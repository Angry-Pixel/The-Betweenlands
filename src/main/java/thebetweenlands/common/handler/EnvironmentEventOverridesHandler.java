package thebetweenlands.common.handler;

import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IRemotelyControllableEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EnvironmentEventOverridesHandler {
	private static final ExecutorService DOWNLOADER = Executors.newFixedThreadPool(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("BL Environment Event Overrides Downloader #" + OVERRIDES_DOWNLOADER_THREAD_ID.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final AtomicInteger OVERRIDES_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

	private static int remoteDataRecheckTicks = 0;

	private static volatile int failedDownloadAttempts = 0;

	private static volatile boolean overrideStatesDownloaded = false;
	private static List<OverrideState> overrideStates = new ArrayList<>();

	public static final Gson GSON = new Gson();
	public static final Type STRING_ARRAY_TYPE = new TypeToken<String[]>() {}.getType();

	public static class OverrideState {
		public final Optional<Set<String>> dimensions;
		public final ResourceLocation eventId;
		public final int remoteResetTicks;
		public final boolean value;
		public final ImmutableMap<String, String> data;

		public OverrideState(Optional<Set<String>> dimensions, ResourceLocation eventId, int remoteResetTicks, boolean value, ImmutableMap<String, String> data) {
			this.dimensions = dimensions;
			this.eventId = eventId;
			this.remoteResetTicks = remoteResetTicks;
			this.value = value;
			this.data = data;
		}
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if(BetweenlandsConfig.EVENT_OVERRIDES.onlineEnvironmentEventOverrides) {
				if(failedDownloadAttempts > 0 && failedDownloadAttempts <= BetweenlandsConfig.EVENT_OVERRIDES.failedRecheckCount && remoteDataRecheckTicks > BetweenlandsConfig.EVENT_OVERRIDES.failedRecheckInterval * 20) {
					remoteDataRecheckTicks = BetweenlandsConfig.EVENT_OVERRIDES.failedRecheckInterval * 20;
				}
				remoteDataRecheckTicks--;
				if(remoteDataRecheckTicks <= 0) {
					remoteDataRecheckTicks = BetweenlandsConfig.EVENT_OVERRIDES.checkInterval * 20;

					List<WeakReference<World>> worldRefs = new ArrayList<>();
					for(World world : server.worlds) {
						worldRefs.add(new WeakReference<>(world));
					}

					downloadAndCheckStates(new WeakReference<>(server), worldRefs);
				}
			} else {
				if(!overrideStates.isEmpty()) {
					overrideStates.clear();
					for(World world : server.worlds) {
						updateWorldEventOverrideStates(world);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(!event.getWorld().isRemote && overrideStatesDownloaded) {
			updateWorldEventOverrideStates(event.getWorld());
		}
	}

	/**
	 * Returns whether the remote data is available. If this returns false
	 * the remote reset ticks should start counting down
	 * @return
	 */
	public static boolean isRemoteDataAvailable() {
		return failedDownloadAttempts <= BetweenlandsConfig.EVENT_OVERRIDES.failedRecheckCount;
	}

	public static void downloadAndCheckStates(final WeakReference<IThreadListener> schedulerRef, final List<WeakReference<World>> worldRefs) {
		if(BetweenlandsConfig.EVENT_OVERRIDES.onlineEnvironmentEventOverrides) {
			final Proxy proxy = TheBetweenlands.proxy.getNetProxy();
			DOWNLOADER.submit(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL("https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/environment_event_overrides/overrides.json");
						HttpURLConnection request = null;
						try {
							if(proxy != null) {
								request = (HttpURLConnection) url.openConnection(proxy);
							} else {
								request = (HttpURLConnection) url.openConnection();
							}
							request.setDoInput(true);
							request.setDoOutput(false);
							request.setRequestProperty("Content-Type", "application/json; charset=utf-8");
							request.connect();

							if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
								JsonParser parser = new JsonParser();
								final JsonElement jsonElement = parser.parse(new InputStreamReader(request.getInputStream()));
								failedDownloadAttempts = 0;
								IThreadListener scheduler = schedulerRef.get();
								if(scheduler != null) {
									List<OverrideState> states = parseStates(jsonElement);
									scheduler.addScheduledTask(() -> {
										overrideStates = states;
										for(WeakReference<World> worldRef : worldRefs) {
											final World world = worldRef.get();
											if(world != null) {
												updateWorldEventOverrideStates(world);
											}
										}
										overrideStatesDownloaded = true;
									});
								}
							}
						} finally {
							if(request != null) {
								request.disconnect();
							}
						}
					} catch(Exception ex) {
						failedDownloadAttempts++;
						if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info("Failed downloading remote environment event overrides", ex);
					}
				}
			});
		}
	}

	/**
	 * Parses environment event overrides from the json
	 * @param json
	 * @return
	 */
	public static List<OverrideState> parseStates(JsonElement json) {
		List<OverrideState> states = new ArrayList<>();

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
							try {
								JsonObject override = overrides.get(j).getAsJsonObject();
								ResourceLocation id = new ResourceLocation(override.get("id").getAsString());
								boolean value = override.get("value").getAsBoolean();
								int remoteResetTicks = BetweenlandsConfig.EVENT_OVERRIDES.defaultRemoteResetTime * 20;
								if(override.has("remote_reset_ticks")) {
									remoteResetTicks = override.get("remote_reset_ticks").getAsInt();
								}
								Set<String> dimensions = new HashSet<>();
								if(override.has("dimensions")) {
									String[] dimensionsArr = GSON.fromJson(override.get("dimensions"), STRING_ARRAY_TYPE);
									for(String dim : dimensionsArr) {
										dimensions.add(dim);
									}
								} else {
									dimensions.add(TheBetweenlands.DIMENSION_NAME);
								}
								ImmutableMap.Builder<String, String> data = ImmutableMap.builder();
								for(Entry<String, JsonElement> entry : override.entrySet()) {
									String key = entry.getKey();
									if(!key.equals("id") && !key.equals("value") && !key.equals("remote_reset_ticks") && !key.equals("dimensions")) {
										data.put(key, entry.getValue().toString());
									}
								}
								states.add(new OverrideState(dimensions.isEmpty() ? Optional.empty() : Optional.of(dimensions), id, remoteResetTicks, value, data.build()));
							} catch(Exception ex) {
								if(BetweenlandsConfig.DEBUG.debug) {
									TheBetweenlands.logger.error("Failed parsing override entry: " + j, ex);
								}
							}
						}
					}
				} catch(Exception ex) {
					if(BetweenlandsConfig.DEBUG.debug) {
						TheBetweenlands.logger.error("Failed parsing version entry: " + i, ex);
					}
				}
			}
		}

		return states;
	}

	/**
	 * Updates the world's environment event override states
	 * @param world
	 */
	public static void updateWorldEventOverrideStates(World world) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
		if(storage != null) {
			BLEnvironmentEventRegistry registry = storage.getEnvironmentEventRegistry();
			if(registry.isEnabled()) {
				Set<ResourceLocation> updatedEvents = new HashSet<>();

				for(OverrideState override : overrideStates) {
					if(!override.dimensions.isPresent() || override.dimensions.get().contains(world.provider.getDimensionType().getName())) {
						IEnvironmentEvent event = registry.getEvent(override.eventId);
						if(event instanceof IRemotelyControllableEnvironmentEvent) {
							IRemotelyControllableEnvironmentEvent controllable = (IRemotelyControllableEnvironmentEvent) event;
							if(controllable.isRemotelyControllable()) {
								controllable.updateStateFromRemote(override.value, override.remoteResetTicks, override.data);
								updatedEvents.add(override.eventId);
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
