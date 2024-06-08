package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements CommandSource, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA_BRAND = "vanilla";
   private static final float AVERAGE_TICK_TIME_SMOOTHING = 0.8F;
   private static final int TICK_STATS_SPAN = 100;
   public static final int MS_PER_TICK = 50;
   private static final int OVERLOADED_THRESHOLD = 2000;
   private static final int OVERLOADED_WARNING_INTERVAL = 15000;
   public static final String LEVEL_STORAGE_PROTOCOL = "level";
   public static final String LEVEL_STORAGE_SCHEMA = "level://";
   private static final long STATUS_EXPIRE_TIME_NS = 5000000000L;
   private static final int MAX_STATUS_PLAYER_SAMPLE = 12;
   public static final String MAP_RESOURCE_FILE = "resources.zip";
   public static final File USERID_CACHE_FILE = new File("usercache.json");
   public static final int START_CHUNK_RADIUS = 11;
   private static final int START_TICKING_CHUNK_COUNT = 441;
   private static final int AUTOSAVE_INTERVAL = 6000;
   private static final int MAX_TICK_LATENCY = 3;
   public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
   public static final LevelSettings DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackConfig.DEFAULT);
   private static final long DELAYED_TASKS_TICK_EXTENSION = 50L;
   public static final GameProfile ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
   protected final LevelStorageSource.LevelStorageAccess storageSource;
   protected final PlayerDataStorage playerDataStorage;
   private final List<Runnable> tickables = Lists.newArrayList();
   private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   private ProfilerFiller profiler = this.metricsRecorder.getProfiler();
   private Consumer<ProfileResults> onMetricsRecordingStopped = (p_177903_) -> {
      this.stopRecordingMetrics();
   };
   private Consumer<Path> onMetricsRecordingFinished = (p_177954_) -> {
   };
   private boolean willStartRecordingMetrics;
   @Nullable
   private MinecraftServer.TimeProfiler debugCommandProfiler;
   private boolean debugCommandProfilerDelayStart;
   private final ServerConnectionListener connection;
   private final ChunkProgressListenerFactory progressListenerFactory;
   private final ServerStatus status = new ServerStatus();
   private final Random random = new Random();
   private final DataFixer fixerUpper;
   private String localIp;
   private int port = -1;
   private final RegistryAccess.Frozen registryHolder;
   private final Map<ResourceKey<Level>, ServerLevel> levels = Maps.newLinkedHashMap();
   private PlayerList playerList;
   private volatile boolean running = true;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int playerIdleTimeout;
   public final long[] tickTimes = new long[100];
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private String singleplayerName;
   private boolean isDemo;
   private String resourcePack = "";
   private String resourcePackHash = "";
   private volatile boolean isReady;
   private long lastOverloadWarning;
   private final MinecraftSessionService sessionService;
   @Nullable
   private final GameProfileRepository profileRepository;
   @Nullable
   private final GameProfileCache profileCache;
   private long lastServerStatus;
   private final Thread serverThread;
   protected long nextTickTime = Util.getMillis();
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   private final PackRepository packRepository;
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   @Nullable
   private CommandStorage commandStorage;
   private final CustomBossEvents customBossEvents = new CustomBossEvents();
   private final ServerFunctionManager functionManager;
   private final FrameTimer frameTimer = new FrameTimer();
   private boolean enforceWhitelist;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;
   private MinecraftServer.ReloadableResources resources;
   private final StructureManager structureManager;
   protected final WorldData worldData;
   private volatile boolean isSaving;

   public static <S extends MinecraftServer> S spin(Function<Thread, S> p_129873_) {
      AtomicReference<S> atomicreference = new AtomicReference<>();
      Thread thread = new Thread(net.minecraftforge.fml.util.thread.SidedThreadGroups.SERVER, () -> {
         atomicreference.get().runServer();
      }, "Server thread");
      thread.setUncaughtExceptionHandler((p_177909_, p_177910_) -> {
         LOGGER.error("Uncaught exception in server thread", p_177910_);
      });
      if (Runtime.getRuntime().availableProcessors() > 4) {
         thread.setPriority(8);
      }

      S s = p_129873_.apply(thread);
      atomicreference.set(s);
      thread.start();
      return s;
   }

   public MinecraftServer(Thread p_206546_, LevelStorageSource.LevelStorageAccess p_206547_, PackRepository p_206548_, WorldStem p_206549_, Proxy p_206550_, DataFixer p_206551_, @Nullable MinecraftSessionService p_206552_, @Nullable GameProfileRepository p_206553_, @Nullable GameProfileCache p_206554_, ChunkProgressListenerFactory p_206555_) {
      super("Server");
      this.registryHolder = p_206549_.registryAccess();
      this.worldData = p_206549_.worldData();
      this.proxy = p_206550_;
      this.packRepository = p_206548_;
      this.resources = new MinecraftServer.ReloadableResources(p_206549_.resourceManager(), p_206549_.dataPackResources());
      this.sessionService = p_206552_;
      this.profileRepository = p_206553_;
      this.profileCache = p_206554_;
      if (p_206554_ != null) {
         p_206554_.setExecutor(this);
      }

      this.connection = new ServerConnectionListener(this);
      this.progressListenerFactory = p_206555_;
      this.storageSource = p_206547_;
      this.playerDataStorage = p_206547_.createPlayerStorage();
      this.fixerUpper = p_206551_;
      this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
      this.structureManager = new StructureManager(p_206549_.resourceManager(), p_206547_, p_206551_);
      this.serverThread = p_206546_;
      this.executor = Util.backgroundExecutor();
   }

   private void readScoreboard(DimensionDataStorage p_129842_) {
      p_129842_.computeIfAbsent(this.getScoreboard()::createData, this.getScoreboard()::createData, "scoreboard");
   }

   protected abstract boolean initServer() throws IOException;

   protected void loadLevel() {
      if (!JvmProfiler.INSTANCE.isRunning()) {
      }

      boolean flag = false;
      ProfiledDuration profiledduration = JvmProfiler.INSTANCE.onWorldLoadedStarted();
      this.detectBundledResources();
      this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
      ChunkProgressListener chunkprogresslistener = this.progressListenerFactory.create(11);
      this.createLevels(chunkprogresslistener);
      this.forceDifficulty();
      this.prepareLevels(chunkprogresslistener);
      if (profiledduration != null) {
         profiledduration.finish();
      }

      if (flag) {
         try {
            JvmProfiler.INSTANCE.stop();
         } catch (Throwable throwable) {
            LOGGER.warn("Failed to stop JFR profiling", throwable);
         }
      }

   }

   protected void forceDifficulty() {
   }

   protected void createLevels(ChunkProgressListener p_129816_) {
      ServerLevelData serverleveldata = this.worldData.overworldData();
      WorldGenSettings worldgensettings = this.worldData.worldGenSettings();
      boolean flag = worldgensettings.isDebug();
      long i = worldgensettings.seed();
      long j = BiomeManager.obfuscateSeed(i);
      List<CustomSpawner> list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(serverleveldata));
      Registry<LevelStem> registry = worldgensettings.dimensions();
      LevelStem levelstem = registry.get(LevelStem.OVERWORLD);
      ChunkGenerator chunkgenerator;
      Holder<DimensionType> holder;
      if (levelstem == null) {
         holder = this.registryAccess().<DimensionType>registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getOrCreateHolder(DimensionType.OVERWORLD_LOCATION);
         chunkgenerator = WorldGenSettings.makeDefaultOverworld(this.registryAccess(), (new Random()).nextLong());
      } else {
         holder = levelstem.typeHolder();
         chunkgenerator = levelstem.generator();
      }

      ServerLevel serverlevel = new ServerLevel(this, this.executor, this.storageSource, serverleveldata, Level.OVERWORLD, holder, p_129816_, chunkgenerator, flag, j, list, true);
      this.levels.put(Level.OVERWORLD, serverlevel);
      DimensionDataStorage dimensiondatastorage = serverlevel.getDataStorage();
      this.readScoreboard(dimensiondatastorage);
      this.commandStorage = new CommandStorage(dimensiondatastorage);
      WorldBorder worldborder = serverlevel.getWorldBorder();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(levels.get(Level.OVERWORLD)));
      if (!serverleveldata.isInitialized()) {
         try {
            setInitialSpawn(serverlevel, serverleveldata, worldgensettings.generateBonusChest(), flag);
            serverleveldata.setInitialized(true);
            if (flag) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable throwable1) {
            CrashReport crashreport = CrashReport.forThrowable(throwable1, "Exception initializing level");

            try {
               serverlevel.fillReportDetails(crashreport);
            } catch (Throwable throwable) {
            }

            throw new ReportedException(crashreport);
         }

         serverleveldata.setInitialized(true);
      }

      this.getPlayerList().addWorldborderListener(serverlevel);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents());
      }

      for(Entry<ResourceKey<LevelStem>, LevelStem> entry : registry.entrySet()) {
         ResourceKey<LevelStem> resourcekey = entry.getKey();
         if (resourcekey != LevelStem.OVERWORLD) {
            ResourceKey<Level> resourcekey1 = ResourceKey.create(Registry.DIMENSION_REGISTRY, resourcekey.location());
            Holder<DimensionType> holder1 = entry.getValue().typeHolder();
            ChunkGenerator chunkgenerator1 = entry.getValue().generator();
            DerivedLevelData derivedleveldata = new DerivedLevelData(this.worldData, serverleveldata);
            ServerLevel serverlevel1 = new ServerLevel(this, this.executor, this.storageSource, derivedleveldata, resourcekey1, holder1, p_129816_, chunkgenerator1, flag, j, ImmutableList.of(), false);
            worldborder.addListener(new BorderChangeListener.DelegateBorderChangeListener(serverlevel1.getWorldBorder()));
            this.levels.put(resourcekey1, serverlevel1);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(levels.get(resourcekey)));
         }
      }

      worldborder.applySettings(serverleveldata.getWorldBorder());
   }

   private static void setInitialSpawn(ServerLevel p_177897_, ServerLevelData p_177898_, boolean p_177899_, boolean p_177900_) {
      if (p_177900_) {
         p_177898_.setSpawn(BlockPos.ZERO.above(80), 0.0F);
      } else {
         ChunkGenerator chunkgenerator = p_177897_.getChunkSource().getGenerator();
         if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(p_177897_, p_177898_)) return;
         ChunkPos chunkpos = new ChunkPos(chunkgenerator.climateSampler().findSpawnPosition());
         int i = chunkgenerator.getSpawnHeight(p_177897_);
         if (i < p_177897_.getMinBuildHeight()) {
            BlockPos blockpos = chunkpos.getWorldPosition();
            i = p_177897_.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos.getX() + 8, blockpos.getZ() + 8);
         }

         p_177898_.setSpawn(chunkpos.getWorldPosition().offset(8, i, 8), 0.0F);
         int k1 = 0;
         int j = 0;
         int k = 0;
         int l = -1;
         int i1 = 5;

         for(int j1 = 0; j1 < Mth.square(11); ++j1) {
            if (k1 >= -5 && k1 <= 5 && j >= -5 && j <= 5) {
               BlockPos blockpos1 = PlayerRespawnLogic.getSpawnPosInChunk(p_177897_, new ChunkPos(chunkpos.x + k1, chunkpos.z + j));
               if (blockpos1 != null) {
                  p_177898_.setSpawn(blockpos1, 0.0F);
                  break;
               }
            }

            if (k1 == j || k1 < 0 && k1 == -j || k1 > 0 && k1 == 1 - j) {
               int l1 = k;
               k = -l;
               l = l1;
            }

            k1 += k;
            j += l;
         }

         if (p_177899_) {
            ConfiguredFeature<?, ?> configuredfeature = MiscOverworldFeatures.BONUS_CHEST.value();
            configuredfeature.place(p_177897_, chunkgenerator, p_177897_.random, new BlockPos(p_177898_.getXSpawn(), p_177898_.getYSpawn(), p_177898_.getZSpawn()));
         }

      }
   }

   private void setupDebugLevel(WorldData p_129848_) {
      p_129848_.setDifficulty(Difficulty.PEACEFUL);
      p_129848_.setDifficultyLocked(true);
      ServerLevelData serverleveldata = p_129848_.overworldData();
      serverleveldata.setRaining(false);
      serverleveldata.setThundering(false);
      serverleveldata.setClearWeatherTime(1000000000);
      serverleveldata.setDayTime(6000L);
      serverleveldata.setGameType(GameType.SPECTATOR);
   }

   private void prepareLevels(ChunkProgressListener p_129941_) {
      ServerLevel serverlevel = this.overworld();
      LOGGER.info("Preparing start region for dimension {}", (Object)serverlevel.dimension().location());
      BlockPos blockpos = serverlevel.getSharedSpawnPos();
      p_129941_.updateSpawnPos(new ChunkPos(blockpos));
      ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
      serverchunkcache.getLightEngine().setTaskPerBatch(500);
      this.nextTickTime = Util.getMillis();
      serverchunkcache.addRegionTicket(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

      while(serverchunkcache.getTickingGenerated() != 441) {
         this.nextTickTime = Util.getMillis() + 10L;
         this.waitUntilNextTick();
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();

      for(ServerLevel serverlevel1 : this.levels.values()) {
         ForcedChunksSavedData forcedchunkssaveddata = serverlevel1.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
         if (forcedchunkssaveddata != null) {
            LongIterator longiterator = forcedchunkssaveddata.getChunks().iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(i);
               serverlevel1.getChunkSource().updateChunkForced(chunkpos, true);
            }
            net.minecraftforge.common.world.ForgeChunkManager.reinstatePersistentChunks(serverlevel1, forcedchunkssaveddata);
         }
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();
      p_129941_.stop();
      serverchunkcache.getLightEngine().setTaskPerBatch(5);
      this.updateMobSpawningFlags();
   }

   protected void detectBundledResources() {
      File file1 = this.storageSource.getLevelPath(LevelResource.MAP_RESOURCE_FILE).toFile();
      if (file1.isFile()) {
         String s = this.storageSource.getLevelId();

         try {
            this.setResourcePack("level://" + URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) + "/resources.zip", "");
         } catch (UnsupportedEncodingException unsupportedencodingexception) {
            LOGGER.warn("Something went wrong url encoding {}", (Object)s);
         }
      }

   }

   public GameType getDefaultGameType() {
      return this.worldData.getGameType();
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public boolean saveAllChunks(boolean p_129886_, boolean p_129887_, boolean p_129888_) {
      boolean flag = false;

      for(ServerLevel serverlevel : this.getAllLevels()) {
         if (!p_129886_) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverlevel, serverlevel.dimension().location());
         }

         serverlevel.save((ProgressListener)null, p_129887_, serverlevel.noSave && !p_129888_);
         flag = true;
      }

      ServerLevel serverlevel2 = this.overworld();
      ServerLevelData serverleveldata = this.worldData.overworldData();
      serverleveldata.setWorldBorder(serverlevel2.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save());
      this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.getPlayerList().getSingleplayerData());
      if (p_129887_) {
         for(ServerLevel serverlevel1 : this.getAllLevels()) {
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)serverlevel1.getChunkSource().chunkMap.getStorageName());
         }

         LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
      }

      return flag;
   }

   public boolean saveEverything(boolean p_195515_, boolean p_195516_, boolean p_195517_) {
      boolean flag;
      try {
         this.isSaving = true;
         this.getPlayerList().saveAll();
         flag = this.saveAllChunks(p_195515_, p_195516_, p_195517_);
      } finally {
         this.isSaving = false;
      }

      return flag;
   }

   public void close() {
      this.stopServer();
   }

   public void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getConnection() != null) {
         this.getConnection().stop();
      }

      this.isSaving = true;
      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAll();
         this.playerList.removeAll();
      }

      LOGGER.info("Saving worlds");

      for(ServerLevel serverlevel : this.getAllLevels()) {
         if (serverlevel != null) {
            serverlevel.noSave = false;
         }
      }

      while(this.levels.values().stream().anyMatch((p_202480_) -> {
         return p_202480_.getChunkSource().chunkMap.hasWork();
      })) {
         this.nextTickTime = Util.getMillis() + 1L;

         for(ServerLevel serverlevel1 : this.getAllLevels()) {
            serverlevel1.getChunkSource().removeTicketsOnClosing();
            serverlevel1.getChunkSource().tick(() -> {
               return true;
            }, false);
         }

         this.waitUntilNextTick();
      }

      this.saveAllChunks(false, true, false);

      for(ServerLevel serverlevel2 : this.getAllLevels()) {
         if (serverlevel2 != null) {
            try {
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(serverlevel2));
               serverlevel2.close();
            } catch (IOException ioexception1) {
               LOGGER.error("Exception closing the level", (Throwable)ioexception1);
            }
         }
      }

      this.isSaving = false;
      this.resources.close();

      try {
         this.storageSource.close();
      } catch (IOException ioexception) {
         LOGGER.error("Failed to unlock level {}", this.storageSource.getLevelId(), ioexception);
      }

   }

   public String getLocalIp() {
      return this.localIp;
   }

   public void setLocalIp(String p_129914_) {
      this.localIp = p_129914_;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void halt(boolean p_129884_) {
      this.running = false;
      if (p_129884_) {
         try {
            this.serverThread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.error("Error while shutting down", (Throwable)interruptedexception);
         }
      }

   }

   protected void runServer() {
      try {
         if (this.initServer()) {
            net.minecraftforge.server.ServerLifecycleHooks.handleServerStarted(this);
            this.nextTickTime = Util.getMillis();
            this.status.setDescription(new TextComponent(this.motd));
            this.status.setVersion(new ServerStatus.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
            this.updateStatusIcon(this.status);

            while(this.running) {
               long i = Util.getMillis() - this.nextTickTime;
               if (i > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.nextTickTime += j * 50L;
                  this.lastOverloadWarning = this.nextTickTime;
               }

               if (this.debugCommandProfilerDelayStart) {
                  this.debugCommandProfilerDelayStart = false;
                  this.debugCommandProfiler = new MinecraftServer.TimeProfiler(Util.getNanos(), this.tickCount);
               }

               this.nextTickTime += 50L;
               this.startMetricsRecordingTick();
               this.profiler.push("tick");
               this.tickServer(this::haveTime);
               this.profiler.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
               this.waitUntilNextTick();
               this.profiler.pop();
               this.endMetricsRecordingTick();
               this.isReady = true;
               JvmProfiler.INSTANCE.onServerTick(this.averageTickTime);
            }
            net.minecraftforge.server.ServerLifecycleHooks.handleServerStopping(this);
            net.minecraftforge.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         } else {
            net.minecraftforge.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.onServerCrash((CrashReport)null);
         }
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport = constructOrExtractCrashReport(throwable1);
         this.fillSystemReport(crashreport.getSystemReport());
         File file1 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         net.minecraftforge.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         this.onServerCrash(crashreport);
      } finally {
         try {
            this.stopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            if (this.profileCache != null) {
               this.profileCache.clearExecutor();
            }

            net.minecraftforge.server.ServerLifecycleHooks.handleServerStopped(this);
            this.onServerExit();
         }

      }

   }

   private static CrashReport constructOrExtractCrashReport(Throwable p_206569_) {
      ReportedException reportedexception = null;

      for(Throwable throwable = p_206569_; throwable != null; throwable = throwable.getCause()) {
         if (throwable instanceof ReportedException) {
            ReportedException reportedexception1 = (ReportedException)throwable;
            reportedexception = reportedexception1;
         }
      }

      CrashReport crashreport;
      if (reportedexception != null) {
         crashreport = reportedexception.getReport();
         if (reportedexception != p_206569_) {
            crashreport.addCategory("Wrapped in").setDetailError("Wrapping exception", p_206569_);
         }
      } else {
         crashreport = new CrashReport("Exception in server tick loop", p_206569_);
      }

      return crashreport;
   }

   private boolean haveTime() {
      return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> {
         return !this.haveTime();
      });
   }

   protected TickTask wrapRunnable(Runnable p_129852_) {
      return new TickTask(this.tickCount, p_129852_);
   }

   protected boolean shouldRun(TickTask p_129883_) {
      return p_129883_.getTick() + 3 < this.tickCount || this.haveTime();
   }

   public boolean pollTask() {
      boolean flag = this.pollTaskInternal();
      this.mayHaveDelayedTasks = flag;
      return flag;
   }

   private boolean pollTaskInternal() {
      if (super.pollTask()) {
         return true;
      } else {
         if (this.haveTime()) {
            for(ServerLevel serverlevel : this.getAllLevels()) {
               if (serverlevel.getChunkSource().pollTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void doRunTask(TickTask p_129957_) {
      this.getProfiler().incrementCounter("runTask");
      super.doRunTask(p_129957_);
   }

   private void updateStatusIcon(ServerStatus p_129879_) {
      Optional<File> optional = Optional.of(this.getFile("server-icon.png")).filter(File::isFile);
      if (!optional.isPresent()) {
         optional = this.storageSource.getIconFile().map(Path::toFile).filter(File::isFile);
      }

      optional.ifPresent((p_202470_) -> {
         try {
            BufferedImage bufferedimage = ImageIO.read(p_202470_);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            ImageIO.write(bufferedimage, "PNG", bytearrayoutputstream);
            byte[] abyte = Base64.getEncoder().encode(bytearrayoutputstream.toByteArray());
            p_129879_.setFavicon("data:image/png;base64," + new String(abyte, StandardCharsets.UTF_8));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", (Throwable)exception);
         }

      });
   }

   public Optional<Path> getWorldScreenshotFile() {
      return this.storageSource.getIconFile();
   }

   public File getServerDirectory() {
      return new File(".");
   }

   protected void onServerCrash(CrashReport p_129874_) {
   }

   public void onServerExit() {
   }

   public void tickServer(BooleanSupplier p_129871_) {
      long i = Util.getNanos();
      net.minecraftforge.event.ForgeEventFactory.onPreServerTick(p_129871_);
      ++this.tickCount;
      this.tickChildren(p_129871_);
      if (i - this.lastServerStatus >= 5000000000L) {
         this.lastServerStatus = i;
         this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayers(), this.getPlayerCount()));
         if (!this.hidesOnlinePlayers()) {
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getPlayerCount(), 12)];
            int j = Mth.nextInt(this.random, 0, this.getPlayerCount() - agameprofile.length);

            for(int k = 0; k < agameprofile.length; ++k) {
               ServerPlayer serverplayer = this.playerList.getPlayers().get(j + k);
               if (serverplayer.allowsListing()) {
                  agameprofile[k] = serverplayer.getGameProfile();
               } else {
                  agameprofile[k] = ANONYMOUS_PLAYER_PROFILE;
               }
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.status.getPlayers().setSample(agameprofile);
         }
         this.status.invalidateJson();
      }

      if (this.tickCount % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.saveEverything(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("tallying");
      long l = this.tickTimes[this.tickCount % 100] = Util.getNanos() - i;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      long i1 = Util.getNanos();
      this.frameTimer.logFrameDuration(i1 - i);
      this.profiler.pop();
      net.minecraftforge.event.ForgeEventFactory.onPostServerTick(p_129871_);
   }

   public void tickChildren(BooleanSupplier p_129954_) {
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");

      for(ServerLevel serverlevel : this.getWorldArray()) {
         long tickStart = Util.getNanos();
         this.profiler.push(() -> {
            return serverlevel + " " + serverlevel.dimension().location();
         });
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.playerList.broadcastAll(new ClientboundSetTimePacket(serverlevel.getGameTime(), serverlevel.getDayTime(), serverlevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), serverlevel.dimension());
            this.profiler.pop();
         }

         this.profiler.push("tick");
         net.minecraftforge.event.ForgeEventFactory.onPreWorldTick(serverlevel, p_129954_);

         try {
            serverlevel.tick(p_129954_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception ticking world");
            serverlevel.fillReportDetails(crashreport);
            throw new ReportedException(crashreport);
         }
         net.minecraftforge.event.ForgeEventFactory.onPostWorldTick(serverlevel, p_129954_);

         this.profiler.pop();
         this.profiler.pop();
         perWorldTickTimes.computeIfAbsent(serverlevel.dimension(), k -> new long[100])[this.tickCount % 100] = Util.getNanos() - tickStart;
      }

      this.profiler.popPush("connection");
      this.getConnection().tick();
      this.profiler.popPush("players");
      this.playerList.tick();
      if (net.minecraftforge.gametest.ForgeGameTestHooks.isGametestEnabled()) {
         GameTestTicker.SINGLETON.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int i = 0; i < this.tickables.size(); ++i) {
         this.tickables.get(i).run();
      }

      this.profiler.pop();
   }

   public boolean isNetherEnabled() {
      return true;
   }

   public void addTickable(Runnable p_129947_) {
      this.tickables.add(p_129947_);
   }

   protected void setId(String p_129949_) {
      this.serverId = p_129949_;
   }

   public boolean isShutdown() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String p_129972_) {
      return new File(this.getServerDirectory(), p_129972_);
   }

   public final ServerLevel overworld() {
      return this.levels.get(Level.OVERWORLD);
   }

   @Nullable
   public ServerLevel getLevel(ResourceKey<Level> p_129881_) {
      return this.levels.get(p_129881_);
   }

   public Set<ResourceKey<Level>> levelKeys() {
      return this.levels.keySet();
   }

   public Iterable<ServerLevel> getAllLevels() {
      return this.levels.values();
   }

   public String getServerVersion() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public int getPlayerCount() {
      return this.playerList.getPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getPlayerNames() {
      return this.playerList.getPlayerNamesArray();
   }

   @DontObfuscate
   public String getServerModName() {
      return net.minecraftforge.internal.BrandingControl.getServerBranding();
   }

   public SystemReport fillSystemReport(SystemReport p_177936_) {
      p_177936_.setDetail("Server Running", () -> {
         return Boolean.toString(this.running);
      });
      if (this.playerList != null) {
         p_177936_.setDetail("Player Count", () -> {
            return this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      p_177936_.setDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(Pack pack : this.packRepository.getSelectedPacks()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(pack.getId());
            if (!pack.getCompatibility().isCompatible()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      p_177936_.setDetail("World Generation", () -> {
         return this.worldData.worldGenSettingsLifecycle().toString();
      });
      if (this.serverId != null) {
         p_177936_.setDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return this.fillServerSystemReport(p_177936_);
   }

   public abstract SystemReport fillServerSystemReport(SystemReport p_177901_);

   public ModCheck getModdedStatus() {
      return ModCheck.identify("vanilla", this::getServerModName, "Server", MinecraftServer.class);
   }

   public void sendMessage(Component p_129876_, UUID p_129877_) {
      LOGGER.info(p_129876_.getString());
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int p_129802_) {
      this.port = p_129802_;
   }

   public String getSingleplayerName() {
      return this.singleplayerName;
   }

   public void setSingleplayerName(String p_129982_) {
      this.singleplayerName = p_129982_;
   }

   public boolean isSingleplayer() {
      return this.singleplayerName != null;
   }

   protected void initializeKeyPair() {
      LOGGER.info("Generating keypair");

      try {
         this.keyPair = Crypt.generateKeyPair();
      } catch (CryptException cryptexception) {
         throw new IllegalStateException("Failed to generate key pair", cryptexception);
      }
   }

   public void setDifficulty(Difficulty p_129828_, boolean p_129829_) {
      if (p_129829_ || !this.worldData.isDifficultyLocked()) {
         this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : p_129828_);
         this.updateMobSpawningFlags();
         this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
      }
   }

   public int getScaledTrackingDistance(int p_129935_) {
      return p_129935_;
   }

   private void updateMobSpawningFlags() {
      for(ServerLevel serverlevel : this.getAllLevels()) {
         serverlevel.setSpawnSettings(this.isSpawningMonsters(), this.isSpawningAnimals());
      }

   }

   public void setDifficultyLocked(boolean p_129959_) {
      this.worldData.setDifficultyLocked(p_129959_);
      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayer p_129939_) {
      LevelData leveldata = p_129939_.getLevel().getLevelData();
      p_129939_.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
   }

   public boolean isSpawningMonsters() {
      return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean p_129976_) {
      this.isDemo = p_129976_;
   }

   public String getResourcePack() {
      return this.resourcePack;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String p_129854_, String p_129855_) {
      this.resourcePack = p_129854_;
      this.resourcePackHash = p_129855_;
   }

   public abstract boolean isDedicatedServer();

   public abstract int getRateLimitPacketsPerSecond();

   public boolean usesAuthentication() {
      return this.onlineMode;
   }

   public void setUsesAuthentication(boolean p_129986_) {
      this.onlineMode = p_129986_;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_129994_) {
      this.preventProxyConnections = p_129994_;
   }

   public boolean isSpawningAnimals() {
      return true;
   }

   public boolean areNpcsEnabled() {
      return true;
   }

   public abstract boolean isEpollEnabled();

   public boolean isPvpAllowed() {
      return this.pvp;
   }

   public void setPvpAllowed(boolean p_129998_) {
      this.pvp = p_129998_;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setFlightAllowed(boolean p_130000_) {
      this.allowFlight = p_130000_;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(String p_129990_) {
      this.motd = p_129990_;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList p_129824_) {
      this.playerList = p_129824_;
   }

   public abstract boolean isPublished();

   public void setDefaultGameType(GameType p_129832_) {
      this.worldData.setGameType(p_129832_);
   }

   @Nullable
   public ServerConnectionListener getConnection() {
      return this.connection;
   }

   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public boolean publishServer(@Nullable GameType p_129833_, boolean p_129834_, int p_129835_) {
      return false;
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(ServerLevel p_129811_, BlockPos p_129812_, Player p_129813_) {
      return false;
   }

   public boolean repliesToStatus() {
      return true;
   }

   public boolean hidesOnlinePlayers() {
      return false;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int p_129978_) {
      this.playerIdleTimeout = p_129978_;
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getProfileRepository() {
      return this.profileRepository;
   }

   public GameProfileCache getProfileCache() {
      return this.profileCache;
   }

   public ServerStatus getStatus() {
      return this.status;
   }

   public void invalidateStatus() {
      this.lastServerStatus = 0L;
   }

   public int getAbsoluteMaxWorldSize() {
      return 29999984;
   }

   public boolean scheduleExecutables() {
      return super.scheduleExecutables() && !this.isStopped();
   }

   public void executeIfPossible(Runnable p_202482_) {
      if (this.isStopped()) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         super.executeIfPossible(p_202482_);
      }
   }

   public Thread getRunningThread() {
      return this.serverThread;
   }

   public int getCompressionThreshold() {
      return 256;
   }

   public long getNextTickTime() {
      return this.nextTickTime;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public int getSpawnRadius(@Nullable ServerLevel p_129804_) {
      return p_129804_ != null ? p_129804_.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS) : 10;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.resources.managers.getAdvancements();
   }

   public ServerFunctionManager getFunctions() {
      return this.functionManager;
   }

   public CompletableFuture<Void> reloadResources(Collection<String> p_129862_) {
      RegistryAccess.Frozen registryaccess$frozen = this.registryAccess();
      CompletableFuture<Void> completablefuture = CompletableFuture.supplyAsync(() -> {
         return p_129862_.stream().map(this.packRepository::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList());
      }, this).thenCompose((p_212913_) -> {
         CloseableResourceManager closeableresourcemanager = new MultiPackResourceManager(PackType.SERVER_DATA, p_212913_);
         return ReloadableServerResources.loadResources(closeableresourcemanager, registryaccess$frozen, this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this).whenComplete((p_212907_, p_212908_) -> {
            if (p_212908_ != null) {
               closeableresourcemanager.close();
            }

         }).thenApply((p_212904_) -> {
            return new MinecraftServer.ReloadableResources(closeableresourcemanager, p_212904_);
         });
      }).thenAcceptAsync((p_212919_) -> {
         this.resources.close();
         this.resources = p_212919_;
         this.packRepository.setSelected(p_129862_);
         this.worldData.setDataPackConfig(getSelectedPacks(this.packRepository));
         this.resources.managers.updateRegistryTags(this.registryAccess());
         this.getPlayerList().saveAll();
         this.getPlayerList().reloadResources();
         this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
         this.structureManager.onResourceManagerReload(this.resources.resourceManager);
         this.getPlayerList().getPlayers().forEach(this.getPlayerList()::sendPlayerPermissionLevel); //Forge: Fix newly added/modified commands not being sent to the client when commands reload.
      }, this);
      if (this.isSameThread()) {
         this.managedBlock(completablefuture::isDone);
      }

      return completablefuture;
   }

   public static DataPackConfig configurePackRepository(PackRepository p_129820_, DataPackConfig p_129821_, boolean p_129822_) {
      net.minecraftforge.resource.ResourcePackLoader.loadResourcePacks(p_129820_, net.minecraftforge.server.ServerLifecycleHooks::buildPackFinder);
      p_129820_.reload();
      DataPackConfig.DEFAULT.addModPacks(net.minecraftforge.common.ForgeHooks.getModPacks());
      p_129821_.addModPacks(net.minecraftforge.common.ForgeHooks.getModPacks());
      if (p_129822_) {
         p_129820_.setSelected(net.minecraftforge.common.ForgeHooks.getModPacksWithVanilla());
         return new DataPackConfig(net.minecraftforge.common.ForgeHooks.getModPacksWithVanilla(), ImmutableList.of());
      } else {
         Set<String> set = Sets.newLinkedHashSet();

         for(String s : p_129821_.getEnabled()) {
            if (p_129820_.isAvailable(s)) {
               set.add(s);
            } else {
               LOGGER.warn("Missing data pack {}", (Object)s);
            }
         }

         for(Pack pack : p_129820_.getAvailablePacks()) {
            String s1 = pack.getId();
            if (!p_129821_.getDisabled().contains(s1) && !set.contains(s1)) {
               LOGGER.info("Found new data pack {}, loading it automatically", (Object)s1);
               set.add(s1);
            }
         }

         if (set.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            set.add("vanilla");
         }

         p_129820_.setSelected(set);
         return getSelectedPacks(p_129820_);
      }
   }

   private static DataPackConfig getSelectedPacks(PackRepository p_129818_) {
      Collection<String> collection = p_129818_.getSelectedIds();
      List<String> list = ImmutableList.copyOf(collection);
      List<String> list1 = p_129818_.getAvailableIds().stream().filter((p_212916_) -> {
         return !collection.contains(p_212916_);
      }).collect(ImmutableList.toImmutableList());
      return new DataPackConfig(list, list1);
   }

   public void kickUnlistedPlayers(CommandSourceStack p_129850_) {
      if (this.isEnforceWhitelist()) {
         PlayerList playerlist = p_129850_.getServer().getPlayerList();
         UserWhiteList userwhitelist = playerlist.getWhiteList();

         for(ServerPlayer serverplayer : Lists.newArrayList(playerlist.getPlayers())) {
            if (!userwhitelist.isWhiteListed(serverplayer.getGameProfile())) {
               serverplayer.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public PackRepository getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.resources.managers.getCommands();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel serverlevel = this.overworld();
      return new CommandSourceStack(this, serverlevel == null ? Vec3.ZERO : Vec3.atLowerCornerOf(serverlevel.getSharedSpawnPos()), Vec2.ZERO, serverlevel, 4, "Server", new TextComponent("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public abstract boolean shouldInformAdmins();

   public RecipeManager getRecipeManager() {
      return this.resources.managers.getRecipeManager();
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage getCommandStorage() {
      if (this.commandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.commandStorage;
      }
   }

   public LootTables getLootTables() {
      return this.resources.managers.getLootTables();
   }

   public PredicateManager getPredicateManager() {
      return this.resources.managers.getPredicateManager();
   }

   public ItemModifierManager getItemModifierManager() {
      return this.resources.managers.getItemModifierManager();
   }

   public GameRules getGameRules() {
      return this.overworld().getGameRules();
   }

   public CustomBossEvents getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean p_130005_) {
      this.enforceWhitelist = p_130005_;
   }

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public int getProfilePermissions(GameProfile p_129945_) {
      if (this.getPlayerList().isOp(p_129945_)) {
         ServerOpListEntry serveroplistentry = this.getPlayerList().getOps().get(p_129945_);
         if (serveroplistentry != null) {
            return serveroplistentry.getLevel();
         } else if (this.isSingleplayerOwner(p_129945_)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
         } else {
            return this.getOperatorUserPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public abstract boolean isSingleplayerOwner(GameProfile p_129840_);

   private Map<ResourceKey<Level>, long[]> perWorldTickTimes = Maps.newIdentityHashMap();
   @Nullable
   public long[] getTickTime(ResourceKey<Level> dim) {
      return perWorldTickTimes.get(dim);
   }

   @Deprecated //Forge Internal use Only, You can screw up a lot of things if you mess with this map.
   public synchronized Map<ResourceKey<Level>, ServerLevel> forgeGetWorldMap() {
      return this.levels;
   }
   private int worldArrayMarker = 0;
   private int worldArrayLast = -1;
   private ServerLevel[] worldArray;
   @Deprecated //Forge Internal use Only, use to protect against concurrent modifications in the world tick loop.
   public synchronized void markWorldsDirty() {
      worldArrayMarker++;
   }
   private ServerLevel[] getWorldArray() {
      if (worldArrayMarker == worldArrayLast && worldArray != null)
         return worldArray;
      worldArray = this.levels.values().stream().toArray(x -> new ServerLevel[x]);
      worldArrayLast = worldArrayMarker;
      return worldArray;
   }

   public void dumpServerProperties(Path p_177911_) throws IOException {
   }

   private void saveDebugReport(Path p_129860_) {
      Path path = p_129860_.resolve("levels");

      try {
         for(Entry<ResourceKey<Level>, ServerLevel> entry : this.levels.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey().location();
            Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
            Files.createDirectories(path1);
            entry.getValue().saveDebugReport(path1);
         }

         this.dumpGameRules(p_129860_.resolve("gamerules.txt"));
         this.dumpClasspath(p_129860_.resolve("classpath.txt"));
         this.dumpMiscStats(p_129860_.resolve("stats.txt"));
         this.dumpThreads(p_129860_.resolve("threads.txt"));
         this.dumpServerProperties(p_129860_.resolve("server.properties.txt"));
         this.dumpNativeModules(p_129860_.resolve("modules.txt"));
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to save debug report", (Throwable)ioexception);
      }

   }

   private void dumpMiscStats(Path p_129951_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_129951_);

      try {
         writer.write(String.format("pending_tasks: %d\n", this.getPendingTasksCount()));
         writer.write(String.format("average_tick_time: %f\n", this.getAverageTickTime()));
         writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimes)));
         writer.write(String.format("queue: %s\n", Util.backgroundExecutor()));
      } catch (Throwable throwable1) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpGameRules(Path p_129984_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_129984_);

      try {
         final List<String> list = Lists.newArrayList();
         final GameRules gamerules = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> p_195531_, GameRules.Type<T> p_195532_) {
               list.add(String.format("%s=%s\n", p_195531_.getId(), gamerules.<T>getRule(p_195531_)));
            }
         });

         for(String s : list) {
            writer.write(s);
         }
      } catch (Throwable throwable1) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpClasspath(Path p_129992_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_129992_);

      try {
         String s = System.getProperty("java.class.path");
         String s1 = System.getProperty("path.separator");

         for(String s2 : Splitter.on(s1).split(s)) {
            writer.write(s2);
            writer.write("\n");
         }
      } catch (Throwable throwable1) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpThreads(Path p_129996_) throws IOException {
      ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
      Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));
      Writer writer = Files.newBufferedWriter(p_129996_);

      try {
         for(ThreadInfo threadinfo : athreadinfo) {
            writer.write(threadinfo.toString());
            writer.write(10);
         }
      } catch (Throwable throwable1) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpNativeModules(Path p_195522_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_195522_);

      label49: {
         try {
            label50: {
               List<NativeModuleLister.NativeModuleInfo> list;
               try {
                  list = Lists.newArrayList(NativeModuleLister.listModules());
               } catch (Throwable throwable1) {
                  LOGGER.warn("Failed to list native modules", throwable1);
                  break label50;
               }

               list.sort(Comparator.comparing((p_212910_) -> {
                  return p_212910_.name;
               }));
               Iterator $$3 = list.iterator();

               while(true) {
                  if (!$$3.hasNext()) {
                     break label49;
                  }

                  NativeModuleLister.NativeModuleInfo nativemodulelister$nativemoduleinfo = (NativeModuleLister.NativeModuleInfo)$$3.next();
                  writer.write(nativemodulelister$nativemoduleinfo.toString());
                  writer.write(10);
               }
            }
         } catch (Throwable throwable2) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable throwable) {
                  throwable2.addSuppressed(throwable);
               }
            }

            throw throwable2;
         }

         if (writer != null) {
            writer.close();
         }

         return;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void startMetricsRecordingTick() {
      if (this.willStartRecordingMetrics) {
         this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()), Util.timeSource, Util.ioPool(), new MetricsPersister("server"), this.onMetricsRecordingStopped, (p_212927_) -> {
            this.executeBlocking(() -> {
               this.saveDebugReport(p_212927_.resolve("server"));
            });
            this.onMetricsRecordingFinished.accept(p_212927_);
         });
         this.willStartRecordingMetrics = false;
      }

      this.profiler = SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
      this.metricsRecorder.startTick();
      this.profiler.startTick();
   }

   private void endMetricsRecordingTick() {
      this.profiler.endTick();
      this.metricsRecorder.endTick();
   }

   public boolean isRecordingMetrics() {
      return this.metricsRecorder.isRecording();
   }

   public void startRecordingMetrics(Consumer<ProfileResults> p_177924_, Consumer<Path> p_177925_) {
      this.onMetricsRecordingStopped = (p_212922_) -> {
         this.stopRecordingMetrics();
         p_177924_.accept(p_212922_);
      };
      this.onMetricsRecordingFinished = p_177925_;
      this.willStartRecordingMetrics = true;
   }

   public void stopRecordingMetrics() {
      this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   }

   public void finishRecordingMetrics() {
      this.metricsRecorder.end();
   }

   public Path getWorldPath(LevelResource p_129844_) {
      return this.storageSource.getLevelPath(p_129844_);
   }

   public boolean forceSynchronousWrites() {
      return true;
   }

   public StructureManager getStructureManager() {
      return this.structureManager;
   }

   public WorldData getWorldData() {
      return this.worldData;
   }

   public MinecraftServer.ReloadableResources getServerResources() {
       return resources;
   }

   public RegistryAccess.Frozen registryAccess() {
      return this.registryHolder;
   }

   public TextFilter createTextFilterForPlayer(ServerPlayer p_129814_) {
      return TextFilter.DUMMY;
   }

   public boolean isResourcePackRequired() {
      return false;
   }

   public ServerPlayerGameMode createGameModeForPlayer(ServerPlayer p_177934_) {
      return (ServerPlayerGameMode)(this.isDemo() ? new DemoMode(p_177934_) : new ServerPlayerGameMode(p_177934_));
   }

   @Nullable
   public GameType getForcedGameType() {
      return null;
   }

   public ResourceManager getResourceManager() {
      return this.resources.resourceManager;
   }

   @Nullable
   public Component getResourcePackPrompt() {
      return null;
   }

   public boolean isCurrentlySaving() {
      return this.isSaving;
   }

   public boolean isTimeProfilerRunning() {
      return this.debugCommandProfilerDelayStart || this.debugCommandProfiler != null;
   }

   public void startTimeProfiler() {
      this.debugCommandProfilerDelayStart = true;
   }

   public ProfileResults stopTimeProfiler() {
      if (this.debugCommandProfiler == null) {
         return EmptyProfileResults.EMPTY;
      } else {
         ProfileResults profileresults = this.debugCommandProfiler.stop(Util.getNanos(), this.tickCount);
         this.debugCommandProfiler = null;
         return profileresults;
      }
   }

   public static record ReloadableResources(CloseableResourceManager resourceManager, ReloadableServerResources managers) implements AutoCloseable {
      public void close() {
         this.resourceManager.close();
      }
   }

   static class TimeProfiler {
      final long startNanos;
      final int startTick;

      TimeProfiler(long p_177958_, int p_177959_) {
         this.startNanos = p_177958_;
         this.startTick = p_177959_;
      }

      ProfileResults stop(final long p_177961_, final int p_177962_) {
         return new ProfileResults() {
            public List<ResultField> getTimes(String p_177972_) {
               return Collections.emptyList();
            }

            public boolean saveResults(Path p_177974_) {
               return false;
            }

            public long getStartTimeNano() {
               return TimeProfiler.this.startNanos;
            }

            public int getStartTimeTicks() {
               return TimeProfiler.this.startTick;
            }

            public long getEndTimeNano() {
               return p_177961_;
            }

            public int getEndTimeTicks() {
               return p_177962_;
            }

            public String getProfilerResults() {
               return "";
            }
         };
      }
   }
}
