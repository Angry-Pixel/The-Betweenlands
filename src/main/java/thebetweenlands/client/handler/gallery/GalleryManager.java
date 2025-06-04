package thebetweenlands.client.handler.gallery;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
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

public class GalleryManager {
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
	public static final Type STRING_ARRAY_TYPE = new TypeToken<String[]>() {
	}.getType();

	public static final GalleryManager INSTANCE = new GalleryManager();

	private Map<String, GalleryEntry> entries = new HashMap<>();

	private GalleryManager() {

	}

	public Map<String, GalleryEntry> getEntries() {
		return this.entries;
	}

	private synchronized void loadLocalIndex(File folder) {
		File file = new File(folder, "index.json");

		if (file.exists()) {
			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				final JsonElement json = JsonParser.parseReader(reader);

				synchronized (this.entries) {
					this.entries = this.parseLocalIndex(folder, json);
				}
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error("Failed loading local gallery index", ex);
			}
		}
	}

	private synchronized void saveLocalIndex(File folder) {
		File file = new File(folder, "index.json");

		file.delete();

		try (FileWriter fileWriter = new FileWriter(file)) {
			JsonArray indexArr = new JsonArray();

			synchronized (this.entries) {
				for (GalleryEntry entry : this.entries.values()) {
					indexArr.add(this.writeEntry(entry));
				}
			}

			GSON.toJson(indexArr, fileWriter);
		} catch (IOException e) {
			TheBetweenlands.LOGGER.error("Failed saving local gallery index", e);
		}
	}

	public void checkAndUpdate(File folder) {
		this.loadLocalIndex(folder);

		if (BetweenlandsConfig.onlineGallery) {
			final Proxy proxy = Minecraft.getInstance().getProxy();
			DOWNLOADER.submit(() -> {
				try {
					TheBetweenlands.LOGGER.info("Updating gallery");

					URL url = URI.create("https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery/index.json").toURL();
					HttpURLConnection request = null;
					try {
						request = GalleryManager.this.createHttpConnection(url, proxy);
						request.setRequestProperty("Content-Type", "application/json; charset=utf-8");
						request.connect();

						if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
							final JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
							GalleryManager.this.updateLocalIndexFromOnline(folder, jsonElement, proxy);
						}
					} finally {
						if (request != null) {
							request.disconnect();
						}
					}
				} catch (Exception ex) {
					TheBetweenlands.LOGGER.error("Failed downloading gallery data", ex);
				}
			});
		}
	}

	private HttpURLConnection createHttpConnection(URL url, @Nullable Proxy proxy) throws IOException {
		HttpURLConnection request;
		if (proxy != null) {
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

		Map<String, GalleryEntry> localEntriesCopy;
		synchronized (this.entries) {
			localEntriesCopy = new HashMap<>(this.entries);
		}

		for (GalleryEntry localEntry : localEntriesCopy.values()) {
			GalleryEntry onlineEntry = onlineEntries.get(localEntry.getUrl());

			String localSha256 = localEntry.getLocalSha256();

			if (onlineEntry == null) {
				//Doesn't exist anymore online, just keep it
				newLocalEntries.put(localEntry.getUrl(), localEntry);
			} else if (localSha256 == null || !localSha256.equals(onlineEntry.getSha256())) {
				TheBetweenlands.LOGGER.info("Removing gallery entry '{}'/'{}' because the local hash ({}) does not match", localEntry.getSha256(), localEntry.getUrl(), localSha256);
				this.deleteLocalPicture(folder, localEntry);
				changed = true;
			} else {
				newLocalEntries.put(onlineEntry.getUrl(), onlineEntry);
			}
		}

		for (GalleryEntry onlineEntry : onlineEntries.values()) {
			if (!newLocalEntries.containsKey(onlineEntry.getUrl())) {
				try {
					if (this.downloadPicture(folder, onlineEntry, proxy)) {
						String localSha256 = onlineEntry.getLocalSha256();

						if (localSha256 != null && !onlineEntry.getSha256().equals(localSha256)) {
							TheBetweenlands.LOGGER.info("Downloaded gallery picture '{}' SHA256 hash does not match (Expected: {} Got: {})! Please report this to the mod authors.", onlineEntry.getUrl(), onlineEntry.getSha256(), localSha256);
						}

						newLocalEntries.put(onlineEntry.getUrl(), onlineEntry);
						changed = true;
					} else {
						TheBetweenlands.LOGGER.error("Failed downloading gallery picture '{}'", onlineEntry.getUrl());
					}
				} catch (Exception ex) {
					TheBetweenlands.LOGGER.error("Failed downloading gallery picture '{}'", onlineEntry.getUrl(), ex);
				}
			}
		}

		synchronized (this.entries) {
			this.entries = newLocalEntries;
		}

		if (changed) {
			this.saveLocalIndex(folder);
		}
	}

	private File getPictureFile(File folder, GalleryEntry entry) {
		return this.getPictureFile(folder, entry.getSha256());
	}

	private File getPictureFile(File folder, String sha256) {
		return new File(folder, sha256.toLowerCase() + ".png");
	}

	private boolean downloadPicture(File folder, GalleryEntry entry, @Nullable Proxy proxy) throws IOException {
		TheBetweenlands.LOGGER.info("Downloading gallery picture '{}'/'{}'", entry.getSha256(), entry.getUrl());

		URL url = URI.create(entry.getUrl()).toURL();
		HttpURLConnection request = null;
		try {
			request = GalleryManager.this.createHttpConnection(url, proxy);
			request.connect();

			if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Files.copy(request.getInputStream(), this.getPictureFile(folder, entry).toPath(), StandardCopyOption.REPLACE_EXISTING);
				return true;
			}
		} finally {
			if (request != null) {
				request.disconnect();
			}
		}

		return false;
	}

	private void deleteLocalPicture(File folder, GalleryEntry entry) {
		try {
			File file = this.getPictureFile(folder, entry);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception ex) {
			TheBetweenlands.LOGGER.error("Failed deleting gallery picture", ex);
		}
	}

	private Map<String, GalleryEntry> parseLocalIndex(File folder, JsonElement json) {
		Map<String, GalleryEntry> entries = new HashMap<>();

		if (json.isJsonArray()) {
			JsonArray index = json.getAsJsonArray();
			for (int j = 0; j < index.size(); j++) {
				try {
					JsonObject entryJson = index.get(j).getAsJsonObject();
					GalleryEntry entry = this.parseEntry(folder, entryJson);
					entries.put(entry.getUrl(), entry);
				} catch (Exception ex) {
					if (BetweenlandsConfig.debug) {
						TheBetweenlands.LOGGER.error("Failed parsing local gallery index entry: {}", j, ex);
					}
				}
			}
		}

		return entries;
	}

	private Map<String, GalleryEntry> parseOnlineIndex(File folder, JsonElement json) {
		Map<String, GalleryEntry> entries = new HashMap<>();

		if (json.isJsonArray()) {
			JsonArray jsonArr = json.getAsJsonArray();
			for (int i = 0; i < jsonArr.size(); i++) {
				try {
					JsonObject element = jsonArr.get(i).getAsJsonObject();
					boolean isVersionValid;
					if (element.has("versions")) {
						isVersionValid = false;
						String[] versions = GSON.fromJson(element.get("versions"), STRING_ARRAY_TYPE);
						for (String version : versions) {
							if (TheBetweenlands.GALLERY_VERSION.equals(version)) {
								isVersionValid = true;
								break;
							}
						}
					} else {
						isVersionValid = true;
					}
					if (isVersionValid) {
						JsonArray index = element.get("index").getAsJsonArray();
						for (int j = 0; j < index.size(); j++) {
							try {
								JsonObject entryJson = index.get(j).getAsJsonObject();
								GalleryEntry entry = this.parseEntry(folder, entryJson);
								entries.put(entry.getUrl(), entry);
							} catch (Exception ex) {
								if (BetweenlandsConfig.debug) {
									TheBetweenlands.LOGGER.error("Failed parsing gallery index entry: {}", j, ex);
								}
							}
						}
					}
				} catch (Exception ex) {
					if (BetweenlandsConfig.debug) {
						TheBetweenlands.LOGGER.error("Failed parsing gallery version entry: {}", i, ex);
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
		if (entry.getDescription() != null) {
			json.add("description", new JsonPrimitive(entry.getDescription()));
		}
		if (entry.getSourceUrl() != null) {
			json.add("source_url", new JsonPrimitive(entry.getSourceUrl()));
		}
		return json;
	}

	private GalleryEntry parseEntry(File folder, JsonObject json) {
		String sha256 = GsonHelper.getAsString(json, "sha256");
		String url = GsonHelper.getAsString(json, "url");
		String title = GsonHelper.getAsString(json, "title");
		String author = GsonHelper.getAsString(json, "author");
		String description = GsonHelper.getAsString(json, "description", null);
		String sourceUrl = GsonHelper.getAsString(json, "source_url", null);
		return new GalleryEntry(sha256, url, title, author, description, sourceUrl, this.getPictureFile(folder, sha256));
	}
}
