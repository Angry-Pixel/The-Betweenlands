package thebetweenlands.common.handler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.BLEnvironmentEvent;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.config.ConfigHandler;

public class EnvironmentEventOverridesHandler {
	private static final AtomicInteger OVERRIDES_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

	public static final int CHECK_INTERVAL_TICKS = 6000;
	public static final int DEFAULT_REMOTE_RESET_TICKS = 9600;

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(ConfigHandler.onlineEnvironmentEventOverrides && !event.getWorld().isRemote && event.getWorld() instanceof WorldServer && event.getWorld().provider instanceof WorldProviderBetweenlands) {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(event.getWorld());
			if(storage != null) {
				BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
				if(registry.isEnabled()) {
					downloadAndCheckStates(new WeakReference<>((WorldServer)event.getWorld()));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(ConfigHandler.onlineEnvironmentEventOverrides && event.phase == Phase.END && !event.world.isRemote && event.world instanceof WorldServer && event.world.provider instanceof WorldProviderBetweenlands && event.world.getWorldInfo().getWorldTotalTime() % CHECK_INTERVAL_TICKS == 0) {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(event.world);
			if(storage != null) {
				BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
				if(registry.isEnabled()) {
					downloadAndCheckStates(new WeakReference<>((WorldServer)event.world));
				}
			}
		}
	}

	public static void downloadAndCheckStates(WeakReference<WorldServer> server) {
		if(ConfigHandler.onlineEnvironmentEventOverrides) {
			new Thread("BL Environment Event Overrides Downloader #" + OVERRIDES_DOWNLOADER_THREAD_ID.getAndIncrement()) {
				@Override
				public void run() {
					try {
						URL url = new URL("https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/environment_event_overrides/overrides.json");
						HttpURLConnection request = null;
						try {
							Proxy proxy = TheBetweenlands.proxy.getNetProxy();
							if(proxy != null) {
								request = (HttpURLConnection) url.openConnection(proxy);
							} else {
								request = (HttpURLConnection) url.openConnection();
							}
							request.setDoInput(true);
							request.setDoOutput(false);
							request.connect();

							if (request.getResponseCode() / 100 == 2) {
								Object content = request.getContent();
								if(content instanceof InputStream) {
									JsonParser parser = new JsonParser();
									JsonElement jsonElement = parser.parse(new InputStreamReader((InputStream) content));
									if(jsonElement.isJsonArray()) {
										JsonArray jsonArray = jsonElement.getAsJsonArray();
										final WorldServer world = server.get();
										if(world != null) {
											world.addScheduledTask(() -> {
												checkStates(world, jsonArray);
											});
										}
									}
								}
							}
						} finally {
							if(request != null) {
								request.disconnect();
							}
						}
					} catch(Exception ex) {
						if(ConfigHandler.debug) TheBetweenlands.logger.catching(ex);
					}
				}
			}.start();
		}
	}

	public static void checkStates(World world, JsonArray json) {
		try {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
			if(storage != null) {
				BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
				if(registry.isEnabled()) {
					Gson gson = new Gson();
					Type stringArrayType = new TypeToken<String[]>() {}.getType();
					for(int i = 0; i < json.size(); i++) {
						try {
							JsonObject element = json.get(i).getAsJsonObject();
							boolean isVersionValid;
							if(element.has("versions")) {
								isVersionValid = false;
								String[] versions = gson.fromJson(element.get("versions"), stringArrayType);
								for(String version : versions) {
									if(ModInfo.VERSION.equals(version)) {
										isVersionValid = true;
										break;
									}
								}
							} else {
								isVersionValid = true;
							}
							if(isVersionValid) {
								class Override {
									boolean value;
									int remoteResetTicks;

									Override(boolean value, int remoteResetTicks) {
										this.value = value;
										this.remoteResetTicks = remoteResetTicks;
									}
								}

								Map<ResourceLocation, Override> map = new HashMap<>();

								JsonArray overrides = element.get("overrides").getAsJsonArray();
								for(int j = 0; j < overrides.size(); j++) {
									JsonObject override = overrides.get(j).getAsJsonObject();
									String id = override.get("id").getAsString();
									boolean value = override.get("value").getAsBoolean();
									int remoteDeactivationTicks = DEFAULT_REMOTE_RESET_TICKS;
									if(override.has("remote_reset_ticks")) {
										remoteDeactivationTicks = Math.max(override.get("remote_reset_ticks").getAsInt(), CHECK_INTERVAL_TICKS + 1200 /*give at least an extra minute time*/);
									}
									map.put(new ResourceLocation(id), new Override(value, remoteDeactivationTicks));
								}

								for(Entry<ResourceLocation, Override> entry : map.entrySet()) {
									ResourceLocation id = entry.getKey();
									Override override = entry.getValue();

									EnvironmentEvent event = registry.getEvent(id);
									if(event instanceof BLEnvironmentEvent) {
										((BLEnvironmentEvent)event).setActiveRemotely(override.value, override.remoteResetTicks);
									}
								}
							}
						} catch(Exception ex) {
							if(ConfigHandler.debug) {
								TheBetweenlands.logger.error("Failed parsing environment event override entry: " + i);
								TheBetweenlands.logger.catching(ex);
							}
						}
					}
				}
			}
		} catch(Exception ex) {
			if(ConfigHandler.debug) TheBetweenlands.logger.catching(ex);
		}
	}
}
