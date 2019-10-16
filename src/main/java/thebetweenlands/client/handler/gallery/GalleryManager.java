package thebetweenlands.client.handler.gallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.Minecraft;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import thebetweenlands.client.render.sprite.TextureGalleryEntry;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;

public final class GalleryManager {
	private static final ExecutorService DOWNLOADER = Executors.newFixedThreadPool(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("BL Gallery Downloader #" + GALLERY_DOWNLOADER_THREAD_ID.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final AtomicInteger GALLERY_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

	public static final Gson GSON = new Gson();
	public static final Type STRING_ARRAY_TYPE = new TypeToken<String[]>() {}.getType();

	public static final GalleryManager INSTANCE = new GalleryManager();

	private Map<String, GalleryEntry> entries = new HashMap<>();

	private volatile boolean registerNewTextures = false;

	private GalleryManager() {

	}

	@SubscribeEvent
	public static void onClientTickEvent(ClientTickEvent event) {
		if(GalleryManager.INSTANCE.registerNewTextures) {
			GalleryManager.INSTANCE.registerNewTextures = false;
			GalleryManager.INSTANCE.registerGlTextures();
		}
	}

	public Map<String, GalleryEntry> getEntries() {
		return this.entries;
	}

	private synchronized void loadLocalIndex(File folder) {
		File file = new File(folder, "index.json");

		if(file.exists()) {
			try(InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				JsonParser parser = new JsonParser();

				final JsonElement json = parser.parse(reader);

				synchronized(this.entries) {
					this.entries = this.parseLocalIndex(folder, json);
				}
			} catch(Exception ex) {
				if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed loading local gallery index", ex);
			}
		}
	}

	private synchronized void saveLocalIndex(File folder) {
		File file = new File(folder, "index.json");

		file.delete();

		try(FileWriter fileWriter = new FileWriter(file)) {
			JsonArray indexArr = new JsonArray();

			synchronized(this.entries) {
				for(GalleryEntry entry : this.entries.values()) {
					indexArr.add(this.writeEntry(entry));
				}
			}

			GSON.toJson(indexArr, fileWriter);
		} catch (IOException e) {
			if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed saving local gallery index", e);
		}
	}

	public void checkAndUpdate(File folder) {
		this.loadLocalIndex(folder);

		if(BetweenlandsConfig.GENERAL.onlineGallery) {
			final Proxy proxy = TheBetweenlands.proxy.getNetProxy();
			DOWNLOADER.submit(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL("https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery/index.json");
						HttpURLConnection request = null;
						try {
							request = GalleryManager.this.createHttpConnection(url, proxy);
							request.setRequestProperty("Content-Type", "application/json; charset=utf-8");
							request.connect();

							if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
								JsonParser parser = new JsonParser();
								final JsonElement jsonElement = parser.parse(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
								GalleryManager.this.updateLocalIndexFromOnline(folder, jsonElement, proxy);
							}
						} finally {
							if(request != null) {
								request.disconnect();
							}
						}
					} catch(Exception ex) {
						if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed downloading online gallery data", ex);
					}

					GalleryManager.this.registerNewTextures = true;
				}
			});
		} else {
			GalleryManager.this.registerNewTextures = true;
		}
	}

	private HttpURLConnection createHttpConnection(URL url, @Nullable Proxy proxy) throws IOException {
		HttpURLConnection request = null;
		if(proxy != null) {
			request = (HttpURLConnection) url.openConnection(proxy);
		} else {
			request = (HttpURLConnection) url.openConnection();
		}
		request.setDoInput(true);
		request.setDoOutput(false);
		return request;
	}

	private void updateLocalIndexFromOnline(File folder, JsonElement json, @Nullable Proxy proxy) {
		Map<String, GalleryEntry> onlineEntries = this.parseOnlineIndex(folder, json);

		Map<String, GalleryEntry> newLocalEntries = new HashMap<>();

		boolean changed = false;

		Map<String, GalleryEntry> localEntriesCopy = new HashMap<>();
		synchronized(this.entries) {
			localEntriesCopy.putAll(this.entries);
		}

		for(GalleryEntry localEntry : localEntriesCopy.values()) {
			GalleryEntry onlineEntry = onlineEntries.get(localEntry.getUrl());

			String localSha256 = localEntry.getLocalSha256();

			if(onlineEntry == null || localSha256 == null || !localSha256.equals(onlineEntry.getSha256())) {
				if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Removing gallery entry '" + localEntry.getSha256() + "'/'" + localEntry.getUrl() + "' because it is no longer used or the local hash (" + localSha256 + ") does not match");
				this.deleteLocalPicture(folder, localEntry);
				changed = true;
			} else {
				newLocalEntries.put(onlineEntry.getUrl(), onlineEntry);
			}
		}

		for(GalleryEntry onlineEntry : onlineEntries.values()) {
			if(!newLocalEntries.containsKey(onlineEntry.getUrl())) {
				try {
					if(this.downloadPicture(folder, onlineEntry, proxy)) {
						newLocalEntries.put(onlineEntry.getUrl(), onlineEntry);
						changed = true;
					} else {
						if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed downloading gallery picture '" + onlineEntry.getUrl() + "'");
					}
				} catch(Exception ex) {
					if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed downloading gallery picture '" + onlineEntry.getUrl() + "'", ex);
				}
			}
		}

		synchronized(this.entries) {
			this.entries = newLocalEntries;
		}

		if(changed) {
			this.saveLocalIndex(folder);
		}
	}

	private File getPictureFile(File folder, GalleryEntry entry) {
		return this.getPictureFile(folder, entry.getSha256());
	}

	private File getPictureFile(File folder, String sha256) {
		return new File(folder, sha256 + ".png");
	}

	private boolean downloadPicture(File folder, GalleryEntry entry, @Nullable Proxy proxy) throws IOException {
		if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Downloading gallery picture '" + entry.getSha256() + "'/'" + entry.getUrl() + "'");

		URL url = new URL(entry.getUrl());
		HttpURLConnection request = null;
		try {
			request = GalleryManager.this.createHttpConnection(url, proxy);
			request.connect();

			if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Files.copy(request.getInputStream(), this.getPictureFile(folder, entry).toPath(), StandardCopyOption.REPLACE_EXISTING);
				return true;
			}
		} finally {
			if(request != null) {
				request.disconnect();
			}
		}

		return false;
	}

	private void deleteLocalPicture(File folder, GalleryEntry entry) {
		try {
			File file = this.getPictureFile(folder, entry);
			if(file.exists()) {
				file.delete();
			}
		} catch(Exception ex) {
			if(BetweenlandsConfig.DEBUG.debug) {
				TheBetweenlands.logger.error("Failed deleting gallery picture", ex);
			}
		}
	}

	private void registerGlTextures() {
		for(GalleryEntry entry : this.entries.values()) {
			Minecraft.getMinecraft().getTextureManager().loadTexture(entry.getLocation(), new TextureGalleryEntry(entry));
			if(BetweenlandsConfig.DEBUG.debug) {
				TheBetweenlands.logger.info("Loaded gallery picture '" + entry.getSha256() + "'/'" + entry.getUrl() + "'/'" + entry.getLocation() + "'");
			}
		}
	}

	private Map<String, GalleryEntry> parseLocalIndex(File folder, JsonElement json) {
		Map<String, GalleryEntry> entries = new HashMap<>();

		if(json.isJsonArray()) {
			JsonArray index = json.getAsJsonArray();
			for(int j = 0; j < index.size(); j++) {
				try {
					JsonObject entryJson = index.get(j).getAsJsonObject();
					GalleryEntry entry = this.parseEntry(folder, entryJson);
					entries.put(entry.getUrl(), entry);
				} catch(Exception ex) {
					if(BetweenlandsConfig.DEBUG.debug) {
						TheBetweenlands.logger.error("Failed parsing local gallery index entry: " + j, ex);
					}
				}
			}
		}

		return entries;
	}

	private Map<String, GalleryEntry> parseOnlineIndex(File folder, JsonElement json) {
		Map<String, GalleryEntry> entries = new HashMap<>();

		if(json.isJsonArray()) {
			JsonArray jsonArr = json.getAsJsonArray();
			for(int i = 0; i < jsonArr.size(); i++) {
				try {
					JsonObject element = jsonArr.get(i).getAsJsonObject();
					boolean isVersionValid;
					if(element.has("versions")) {
						isVersionValid = false;
						String[] versions = GSON.fromJson(element.get("versions"), STRING_ARRAY_TYPE);
						for(String version : versions) {
							if(ModInfo.GALLERY_VERSION.equals(version)) {
								isVersionValid = true;
								break;
							}
						}
					} else {
						isVersionValid = true;
					}
					if(isVersionValid) {
						JsonArray index = element.get("index").getAsJsonArray();
						for(int j = 0; j < index.size(); j++) {
							try {
								JsonObject entryJson = index.get(j).getAsJsonObject();
								GalleryEntry entry = this.parseEntry(folder, entryJson);
								entries.put(entry.getUrl(), entry);
							} catch(Exception ex) {
								if(BetweenlandsConfig.DEBUG.debug) {
									TheBetweenlands.logger.error("Failed parsing online gallery index entry: " + j, ex);
								}
							}
						}
					}
				} catch(Exception ex) {
					if(BetweenlandsConfig.DEBUG.debug) {
						TheBetweenlands.logger.error("Failed parsing gallery version entry: " + i, ex);
					}
				}
			}
		}

		return entries;
	}

	private JsonObject writeEntry(GalleryEntry entry) {
		JsonObject json = new JsonObject();
		json.add("sha256", new JsonPrimitive(entry.getSha256()));
		json.add("url", new JsonPrimitive(entry.getUrl()));
		json.add("title", new JsonPrimitive(entry.getTitle()));
		json.add("author", new JsonPrimitive(entry.getAuthor()));
		if(entry.getDescription() != null) {
			json.add("description", new JsonPrimitive(entry.getDescription()));
		}
		if(entry.getSourceUrl() != null) {
			json.add("source_url", new JsonPrimitive(entry.getSourceUrl()));
		}
		return json;
	}

	private GalleryEntry parseEntry(File folder, JsonObject json) {
		String sha256 = JsonUtils.getString(json, "sha256");
		String url = JsonUtils.getString(json, "url");
		String title = JsonUtils.getString(json, "title");
		String author = JsonUtils.getString(json, "author");
		String description = json.has("description") ? JsonUtils.getString(json, "description") : null;
		String sourceUrl = json.has("source_url") ? JsonUtils.getString(json, "source_url") : null;
		return new GalleryEntry(sha256, url, title, author, description, sourceUrl, this.getPictureFile(folder, sha256));
	}
}
