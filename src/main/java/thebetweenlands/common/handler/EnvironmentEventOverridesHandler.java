package thebetweenlands.common.handler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IRemotelyControllableEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.config.ConfigHandler;

public class EnvironmentEventOverridesHandler {
	private static final AtomicInteger OVERRIDES_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

	public static final int CHECK_INTERVAL_TICKS = 6000; //5 min.
	public static final int FAILED_RECHECK_INTERVAL_TICKS = 1200; //1 min.
	public static final int DEFAULT_REMOTE_RESET_TICKS = 3600; //3 min.

	private static int remoteDataRecheckTicks = 0;

	private static volatile int failedDownloadAttempts = 0;

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(ConfigHandler.onlineEnvironmentEventOverrides && event.phase == Phase.END && !event.world.isRemote && event.world instanceof WorldServer && event.world.provider.getDimension() == ConfigHandler.dimensionId) {
			if(failedDownloadAttempts > 0 && failedDownloadAttempts <= 2 && remoteDataRecheckTicks > FAILED_RECHECK_INTERVAL_TICKS) {
				remoteDataRecheckTicks = FAILED_RECHECK_INTERVAL_TICKS;
			}
			remoteDataRecheckTicks--;
			if(remoteDataRecheckTicks <= 0) {
				remoteDataRecheckTicks = CHECK_INTERVAL_TICKS;
				BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(event.world);
				if(storage != null) {
					BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
					if(registry.isEnabled()) {
						downloadAndCheckStates(new WeakReference<>((WorldServer)event.world));
					}
				}
			}
		}
	}

	/**
	 * Returns whether the remote data is available. If this returns false
	 * the remote reset ticks should start counting down
	 * @return
	 */
	public static boolean isRemoteDataAvailable() {
		return failedDownloadAttempts <= 2;
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
									final JsonElement jsonElement = parser.parse(new InputStreamReader((InputStream) content));
									failedDownloadAttempts = 0;
									final WorldServer world = server.get();
									if(world != null) {
										world.addScheduledTask(() -> {
											checkStates(world, jsonElement);
										});
									}
								}
							}
						} finally {
							if(request != null) {
								request.disconnect();
							}
						}
					} catch(Exception ex) {
						failedDownloadAttempts++;
						if(ConfigHandler.debug) TheBetweenlands.logger.info("Failed downloading remote environment event overrides", ex);
					}
				}
			}.start();
		}
	}

	public static void checkStates(World world, JsonElement json) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
		if(storage != null) {
			BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
			if(registry.isEnabled()) {
				Set<ResourceLocation> updatedEvents = new HashSet<>();

				if(json.isJsonArray()) {
					JsonArray jsonArr = json.getAsJsonArray();
					Gson gson = new Gson();
					Type stringArrayType = new TypeToken<String[]>() {}.getType();
					for(int i = 0; i < jsonArr.size(); i++) {
						try {
							JsonObject element = jsonArr.get(i).getAsJsonObject();
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
								JsonArray overrides = element.get("overrides").getAsJsonArray();
								for(int j = 0; j < overrides.size(); j++) {
									JsonObject override = overrides.get(j).getAsJsonObject();
									ResourceLocation id = new ResourceLocation(override.get("id").getAsString());
									boolean value = override.get("value").getAsBoolean();
									int remoteResetTicks = DEFAULT_REMOTE_RESET_TICKS;
									if(override.has("remote_reset_ticks")) {
										remoteResetTicks = override.get("remote_reset_ticks").getAsInt();
									}
									IEnvironmentEvent event = registry.getEvent(id);
									if(event instanceof IRemotelyControllableEnvironmentEvent) {
										IRemotelyControllableEnvironmentEvent controllable = (IRemotelyControllableEnvironmentEvent) event;
										if(controllable.isRemotelyControllable()) {
											controllable.updateStateFromRemote(value, remoteResetTicks, override);
											updatedEvents.add(id);
										}
									}
								}
							}
						} catch(Exception ex) {
							if(ConfigHandler.debug) {
								TheBetweenlands.logger.error("Failed updating environment event override entry: " + i, ex);
							}
						}
					}
				}

				for(Entry<ResourceLocation, IEnvironmentEvent> entry : registry.getEvents().entrySet()) {
					IEnvironmentEvent event = entry.getValue();
					if(event instanceof IRemotelyControllableEnvironmentEvent) {
						IRemotelyControllableEnvironmentEvent controllable = (IRemotelyControllableEnvironmentEvent) event;
						if(controllable.isRemotelyControllable() && !updatedEvents.contains(entry.getKey())) {
							boolean wasStateFromRemote = controllable.isCurrentStateFromRemote();
							controllable.updateNoStateFromRemote();
							if(wasStateFromRemote) {
								controllable.resetStateFromRemote();
							}
						}
					}
				}
			}
		}
	}
}
