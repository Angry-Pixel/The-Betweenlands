package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.google.gson.JsonElement;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix4f;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.profiling.ClientMetricsSamplersProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LegacyPackResourcesAdapter;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PackResourcesAdapterV4;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.ReloadableIdSearchTree;
import net.minecraft.client.searchtree.ReloadableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FileZipper;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Minecraft extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, net.minecraftforge.client.extensions.IForgeMinecraft {
   private static Minecraft instance;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;
   private static final int MAX_TICKS_PER_UPDATE = 10;
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("default");
   public static final ResourceLocation UNIFORM_FONT = new ResourceLocation("uniform");
   public static final ResourceLocation ALT_FONT = new ResourceLocation("alt");
   private static final ResourceLocation REGIONAL_COMPLIANCIES = new ResourceLocation("regional_compliancies.json");
   private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private static final Component SOCIAL_INTERACTIONS_NOT_AVAILABLE = new TranslatableComponent("multiplayer.socialInteractions.not_available");
   public static final String UPDATE_DRIVERS_ADVICE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
   private final File resourcePackDirectory;
   private final PropertyMap profileProperties;
   public final TextureManager textureManager;
   private final DataFixer fixerUpper;
   private final VirtualScreen virtualScreen;
   private final Window window;
   private final Timer timer = new Timer(20.0F, 0L);
   private final RenderBuffers renderBuffers;
   public final LevelRenderer levelRenderer;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;
   private final ItemInHandRenderer itemInHandRenderer;
   public final ParticleEngine particleEngine;
   private final SearchRegistry searchRegistry = new SearchRegistry();
   private final User user;
   public final Font font;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference<>();
   public final Gui gui;
   public final Options options;
   private final HotbarManager hotbarManager;
   public final MouseHandler mouseHandler;
   public final KeyboardHandler keyboardHandler;
   public final File gameDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final LevelStorageSource levelSource;
   public final FrameTimer frameTimer = new FrameTimer();
   private final boolean is64bit;
   private final boolean demo;
   private final boolean allowsMultiplayer;
   private final boolean allowsChat;
   private final ReloadableResourceManager resourceManager;
   private final ClientPackSource clientPackSource;
   private final PackRepository resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final ItemColors itemColors;
   private final RenderTarget mainRenderTarget;
   private final SoundManager soundManager;
   private final MusicManager musicManager;
   private final FontManager fontManager;
   private final SplashManager splashManager;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final PeriodicNotificationManager regionalCompliancies = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES, Minecraft::countryEqualsISO3);
   private final MinecraftSessionService minecraftSessionService;
   private final UserApiService userApiService;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRenderDispatcher blockRenderer;
   private final PaintingTextureManager paintingTextures;
   private final MobEffectTextureManager mobEffectTextures;
   private final ToastComponent toast;
   private final Game game = new Game(this);
   private final Tutorial tutorial;
   private final PlayerSocialManager playerSocialManager;
   private final EntityModelSet entityModels;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final UUID deviceSessionId = UUID.randomUUID();
   @Nullable
   public MultiPlayerGameMode gameMode;
   @Nullable
   public ClientLevel level;
   @Nullable
   public LocalPlayer player;
   @Nullable
   private IntegratedServer singleplayerServer;
   @Nullable
   private ServerData currentServer;
   @Nullable
   private Connection pendingConnection;
   private boolean isLocalServer;
   @Nullable
   public Entity cameraEntity;
   @Nullable
   public Entity crosshairPickEntity;
   @Nullable
   public HitResult hitResult;
   private int rightClickDelay;
   protected int missTime;
   private volatile boolean pause;
   private float pausePartialTick;
   private long lastNanoTime = Util.getNanos();
   private long lastTime;
   private int frames;
   public boolean noRender;
   @Nullable
   public Screen screen;
   @Nullable
   private Overlay overlay;
   private boolean connectedToRealms;
   private Thread gameThread;
   private volatile boolean running = true;
   @Nullable
   private Supplier<CrashReport> delayedCrash;
   private static int fps;
   public String fpsString = "";
   public boolean wireframe;
   public boolean chunkPath;
   public boolean chunkVisibility;
   public boolean smartCull = true;
   private boolean windowActive;
   private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
   @Nullable
   private CompletableFuture<Void> pendingReload;
   @Nullable
   private TutorialToast socialInteractionsToast;
   private ProfilerFiller profiler = InactiveProfiler.INSTANCE;
   private int fpsPieRenderTicks;
   private final ContinuousProfiler fpsPieProfiler = new ContinuousProfiler(Util.timeSource, () -> {
      return this.fpsPieRenderTicks;
   });
   @Nullable
   private ProfileResults fpsPieResults;
   private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   private final ResourceLoadStateTracker reloadStateTracker = new ResourceLoadStateTracker();
   private String debugPath = "root";

   public Minecraft(GameConfig p_91084_) {
      super("Client");
      instance = this;
      this.gameDirectory = p_91084_.location.gameDirectory;
      File file1 = p_91084_.location.assetDirectory;
      this.resourcePackDirectory = p_91084_.location.resourcePackDirectory;
      this.launchedVersion = p_91084_.game.launchVersion;
      this.versionType = p_91084_.game.versionType;
      this.profileProperties = p_91084_.user.profileProperties;
      this.clientPackSource = new ClientPackSource(new File(this.gameDirectory, "server-resource-packs"), p_91084_.location.getAssetIndex());
      this.resourcePackRepository = new PackRepository(Minecraft::createClientPackAdapter, this.clientPackSource, new FolderRepositorySource(this.resourcePackDirectory, PackSource.DEFAULT));
      this.proxy = p_91084_.user.proxy;
      YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
      this.minecraftSessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
      this.userApiService = this.createUserApiService(yggdrasilauthenticationservice, p_91084_);
      this.user = p_91084_.user.user;
      LOGGER.info("Setting user: {}", (Object)this.user.getName());
      this.demo = p_91084_.game.demo;
      this.allowsMultiplayer = !p_91084_.game.disableMultiplayer;
      this.allowsChat = !p_91084_.game.disableChat;
      this.is64bit = checkIs64Bit();
      this.singleplayerServer = null;
      String s;
      int i;
      if (this.allowsMultiplayer() && p_91084_.server.hostname != null) {
         s = p_91084_.server.hostname;
         i = p_91084_.server.port;
      } else {
         s = null;
         i = 0;
      }

      KeybindComponent.setKeyResolver(KeyMapping::createNameSupplier);
      this.fixerUpper = DataFixers.getDataFixer();
      this.toast = new ToastComponent(this);
      this.gameThread = Thread.currentThread();
      this.options = new Options(this, this.gameDirectory);
      this.tutorial = new Tutorial(this, this.options);
      this.hotbarManager = new HotbarManager(this.gameDirectory, this.fixerUpper);
      LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
      DisplayData displaydata;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         displaydata = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, p_91084_.display.fullscreenWidth, p_91084_.display.fullscreenHeight, p_91084_.display.isFullscreen);
      } else {
         displaydata = p_91084_.display;
      }

      Util.timeSource = RenderSystem.initBackendSystem();
      if (net.minecraftforge.common.ForgeConfig.CLIENT.forceSystemNanoTime.get()) Util.timeSource = System::nanoTime;
      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(displaydata, this.options.fullscreenVideoModeString, this.createTitle());
      this.setWindowActive(true);
      if (!ON_OSX) {
         try {
            InputStream inputstream = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
            InputStream inputstream1 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
            this.window.setIcon(inputstream, inputstream1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't set icon", (Throwable)ioexception);
         }
      }

      this.window.setFramerateLimit(this.options.framerateLimit);
      this.mouseHandler = new MouseHandler(this);
      this.keyboardHandler = new KeyboardHandler(this);
      this.keyboardHandler.setup(this.window.getWindow());
      RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new MainTarget(this.window.getWidth(), this.window.getHeight());
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mainRenderTarget.clear(ON_OSX);
      this.resourceManager = new ReloadableResourceManager(PackType.CLIENT_RESOURCES);
      net.minecraftforge.client.RecipeBookRegistry.initDefaultValues();
      net.minecraftforge.client.loading.ClientModLoader.begin(this, this.resourcePackRepository, this.resourceManager, this.clientPackSource);
      this.resourcePackRepository.reload();
      this.options.loadSelectedResourcePacks(this.resourcePackRepository);
      this.languageManager = new LanguageManager(this.options.languageCode);
      this.resourceManager.registerReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.skinManager = new SkinManager(this.textureManager, new File(file1, "skins"), this.minecraftSessionService);
      this.levelSource = new LevelStorageSource(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
      this.soundManager = new SoundManager(this.resourceManager, this.options);
      this.resourceManager.registerReloadListener(this.soundManager);
      this.splashManager = new SplashManager(this.user);
      this.resourceManager.registerReloadListener(this.splashManager);
      this.musicManager = new MusicManager(this);
      this.fontManager = new FontManager(this.textureManager);
      this.font = this.fontManager.createFont();
      this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
      this.selectMainFont(this.isEnforceUnicode());
      this.resourceManager.registerReloadListener(new GrassColorReloadListener());
      this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
      this.window.setErrorSection("Startup");
      RenderSystem.setupDefaultState(0, 0, this.window.getWidth(), this.window.getHeight());
      this.window.setErrorSection("Post startup");
      this.blockColors = BlockColors.createDefault();
      this.itemColors = ItemColors.createDefault(this.blockColors);
      this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels);
      this.resourceManager.registerReloadListener(this.modelManager);
      this.entityModels = new EntityModelSet();
      this.resourceManager.registerReloadListener(this.entityModels);
      this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.font, this.entityModels, this::getBlockRenderer);
      this.resourceManager.registerReloadListener(this.blockEntityRenderDispatcher);
      BlockEntityWithoutLevelRenderer blockentitywithoutlevelrenderer = new BlockEntityWithoutLevelRenderer(this.blockEntityRenderDispatcher, this.entityModels);
      this.resourceManager.registerReloadListener(blockentitywithoutlevelrenderer);
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors, blockentitywithoutlevelrenderer);
      this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.font, this.options, this.entityModels);
      this.resourceManager.registerReloadListener(this.entityRenderDispatcher);
      this.itemInHandRenderer = new ItemInHandRenderer(this);
      this.resourceManager.registerReloadListener(this.itemRenderer);
      this.renderBuffers = new RenderBuffers();
      this.gameRenderer = new GameRenderer(this, this.resourceManager, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.gameRenderer);
      this.playerSocialManager = new PlayerSocialManager(this, this.userApiService);
      this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), blockentitywithoutlevelrenderer, this.blockColors);
      this.resourceManager.registerReloadListener(this.blockRenderer);
      this.levelRenderer = new LevelRenderer(this, this.renderBuffers);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.RenderLevelStageEvent.RegisterStageEvent());
      this.resourceManager.registerReloadListener(this.levelRenderer);
      this.createSearchTrees();
      this.resourceManager.registerReloadListener(this.searchRegistry);
      this.particleEngine = new ParticleEngine(this.level, this.textureManager);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.ParticleFactoryRegisterEvent());
      this.resourceManager.registerReloadListener(this.particleEngine);
      this.paintingTextures = new PaintingTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.paintingTextures);
      this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.mobEffectTextures);
      this.gpuWarnlistManager = new GpuWarnlistManager();
      this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
      this.resourceManager.registerReloadListener(this.regionalCompliancies);
      this.gui = new net.minecraftforge.client.gui.ForgeIngameGui(this);
      this.mouseHandler.setup(this.window.getWindow()); //Forge: Moved below ingameGUI setting to prevent NPEs in handeler.
      this.debugRenderer = new DebugRenderer(this);
      RenderSystem.setErrorCallback(this::onFullscreenError);
      if (this.mainRenderTarget.width == this.window.getWidth() && this.mainRenderTarget.height == this.window.getHeight()) {
         if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullScreen();
            this.options.fullscreen = this.window.isFullscreen();
         }
      } else {
         StringBuilder stringbuilder = new StringBuilder("Recovering from unsupported resolution (" + this.window.getWidth() + "x" + this.window.getHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
         if (GlDebug.isDebugEnabled()) {
            stringbuilder.append("\n\nReported GL debug messages:\n").append(String.join("\n", GlDebug.getLastOpenGlDebugMessages()));
         }

         this.window.setWindowed(this.mainRenderTarget.width, this.mainRenderTarget.height);
         TinyFileDialogs.tinyfd_messageBox("Minecraft", stringbuilder.toString(), "ok", "error", false);
      }

      net.minecraftforge.gametest.ForgeGameTestHooks.registerGametests();
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.RegisterClientReloadListenersEvent(this.resourceManager));
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions());
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers());

      this.window.updateVsync(this.options.enableVsync);
      this.window.updateRawMouseInput(this.options.rawMouseInput);
      this.window.setDefaultErrorCallback();
      this.resizeDisplay();
      this.gameRenderer.preloadUiShader(this.getClientPackSource().getVanillaPack());
      LoadingOverlay.registerTextures(this);
      List<PackResources> list = this.resourcePackRepository.openAllSelected();
      this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.INITIAL, list);
      this.setOverlay(new LoadingOverlay(this, this.resourceManager.createReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, list), (p_210745_) -> {
         Util.ifElse(p_210745_, this::rollbackResourcePacks, () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               this.selfTest();
            }

            this.reloadStateTracker.finishReload();
            if (net.minecraftforge.client.loading.ClientModLoader.completeModLoading()) return; // Do not overwrite the error screen
            // FORGE: Move opening initial screen to after startup and events are enabled.
            // Also Fixes MC-145102
            if (s != null) {
                ConnectScreen.startConnecting(new TitleScreen(), this, new ServerAddress(s, i), (ServerData)null);
            } else {
                this.setScreen(new TitleScreen(true));
            }
         });
      }, false));

   }

   private static boolean countryEqualsISO3(Object p_210783_) {
      try {
         return Locale.getDefault().getISO3Country().equals(p_210783_);
      } catch (MissingResourceException missingresourceexception) {
         return false;
      }
   }

   public void updateTitle() {
      this.window.setTitle(this.createTitle());
   }

   private String createTitle() {
      StringBuilder stringbuilder = new StringBuilder("Minecraft");
      if (checkModStatus().shouldReportAsModified()) {
         stringbuilder.append("*");
      }

      stringbuilder.append(" ");
      stringbuilder.append(SharedConstants.getCurrentVersion().getName());
      ClientPacketListener clientpacketlistener = this.getConnection();
      if (clientpacketlistener != null && clientpacketlistener.getConnection().isConnected()) {
         stringbuilder.append(" - ");
         if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
            stringbuilder.append(I18n.get("title.singleplayer"));
         } else if (this.isConnectedToRealms()) {
            stringbuilder.append(I18n.get("title.multiplayer.realms"));
         } else if (this.singleplayerServer == null && (this.currentServer == null || !this.currentServer.isLan())) {
            stringbuilder.append(I18n.get("title.multiplayer.other"));
         } else {
            stringbuilder.append(I18n.get("title.multiplayer.lan"));
         }
      }

      return stringbuilder.toString();
   }

   private UserApiService createUserApiService(YggdrasilAuthenticationService p_193586_, GameConfig p_193587_) {
      try {
         if ("0".equals(p_193587_.user.user.getAccessToken())) // Forge: We use "0" in dev. Short circuit to stop exception spam.
            return UserApiService.OFFLINE;
         return p_193586_.createUserApiService(p_193587_.user.user.getAccessToken());
      } catch (AuthenticationException authenticationexception) {
         LOGGER.error("Failed to verify authentication", (Throwable)authenticationexception);
         return UserApiService.OFFLINE;
      }
   }

   public static ModCheck checkModStatus() {
      return ModCheck.identify("vanilla", ClientBrandRetriever::getClientModName, "Client", Minecraft.class);
   }

   private void rollbackResourcePacks(Throwable p_91240_) {
      if (this.resourcePackRepository.getSelectedPacks().stream().anyMatch(e -> !e.isRequired())) { //Forge: This caused infinite loop if any resource packs are forced. Such as mod resources. So check if we can disable any.
         this.clearResourcePacksOnError(p_91240_, (Component)null);
      } else {
         Util.throwAsRuntime(p_91240_);
      }

   }

   public void clearResourcePacksOnError(Throwable p_91242_, @Nullable Component p_91243_) {
      LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", p_91242_);
      this.reloadStateTracker.startRecovery(p_91242_);
      this.resourcePackRepository.setSelected(Collections.emptyList());
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();
      this.options.save();
      this.reloadResourcePacks(true).thenRun(() -> {
         ToastComponent toastcomponent = this.getToasts();
         SystemToast.addOrUpdate(toastcomponent, SystemToast.SystemToastIds.PACK_LOAD_FAILURE, new TranslatableComponent("resourcePack.load_fail"), p_91243_);
      });
   }

   public void run() {
      this.gameThread = Thread.currentThread();
      if (Runtime.getRuntime().availableProcessors() > 4) {
         this.gameThread.setPriority(10);
      }

      try {
         boolean flag = false;

         while(this.running) {
            if (this.delayedCrash != null) {
               crash(this.delayedCrash.get());
               return;
            }

            try {
               SingleTickProfiler singletickprofiler = SingleTickProfiler.createTickProfiler("Renderer");
               boolean flag1 = this.shouldRenderFpsPie();
               this.profiler = this.constructProfiler(flag1, singletickprofiler);
               this.profiler.startTick();
               this.metricsRecorder.startTick();
               this.runTick(!flag);
               this.metricsRecorder.endTick();
               this.profiler.endTick();
               this.finishProfilers(flag1, singletickprofiler);
            } catch (OutOfMemoryError outofmemoryerror) {
               if (flag) {
                  throw outofmemoryerror;
               }

               this.emergencySave();
               this.setScreen(new OutOfMemoryScreen());
               System.gc();
               LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)outofmemoryerror);
               flag = true;
            }
         }
      } catch (ReportedException reportedexception) {
         this.fillReport(reportedexception.getReport());
         this.emergencySave();
         LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)reportedexception);
         crash(reportedexception.getReport());
      } catch (Throwable throwable) {
         CrashReport crashreport = this.fillReport(new CrashReport("Unexpected error", throwable));
         LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", throwable);
         this.emergencySave();
         crash(crashreport);
      }

   }

   void selectMainFont(boolean p_91337_) {
      this.fontManager.setRenames(p_91337_ ? ImmutableMap.of(DEFAULT_FONT, UNIFORM_FONT) : ImmutableMap.of());
   }

   public void createSearchTrees() {
      ReloadableSearchTree<ItemStack> reloadablesearchtree = new ReloadableSearchTree<>((p_210797_) -> {
         return p_210797_.getTooltipLines((Player)null, TooltipFlag.Default.NORMAL).stream().map((p_210807_) -> {
            return ChatFormatting.stripFormatting(p_210807_.getString()).trim();
         }).filter((p_210809_) -> {
            return !p_210809_.isEmpty();
         });
      }, (p_91317_) -> {
         return Stream.of(Registry.ITEM.getKey(p_91317_.getItem()));
      });
      ReloadableIdSearchTree<ItemStack> reloadableidsearchtree = new ReloadableIdSearchTree<>((p_91121_) -> {
         return p_91121_.getTags().map(TagKey::location);
      });
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : Registry.ITEM) {
         item.fillItemCategory(CreativeModeTab.TAB_SEARCH, nonnulllist);
      }

      nonnulllist.forEach((p_210707_) -> {
         reloadablesearchtree.add(p_210707_);
         reloadableidsearchtree.add(p_210707_);
      });
      ReloadableSearchTree<RecipeCollection> reloadablesearchtree1 = new ReloadableSearchTree<>((p_210778_) -> {
         return p_210778_.getRecipes().stream().flatMap((p_210776_) -> {
            return p_210776_.getResultItem().getTooltipLines((Player)null, TooltipFlag.Default.NORMAL).stream();
         }).map((p_210803_) -> {
            return ChatFormatting.stripFormatting(p_210803_.getString()).trim();
         }).filter((p_210805_) -> {
            return !p_210805_.isEmpty();
         });
      }, (p_210701_) -> {
         return p_210701_.getRecipes().stream().map((p_210681_) -> {
            return Registry.ITEM.getKey(p_210681_.getResultItem().getItem());
         });
      });
      this.searchRegistry.register(SearchRegistry.CREATIVE_NAMES, reloadablesearchtree);
      this.searchRegistry.register(SearchRegistry.CREATIVE_TAGS, reloadableidsearchtree);
      this.searchRegistry.register(SearchRegistry.RECIPE_COLLECTIONS, reloadablesearchtree1);
   }

   private void onFullscreenError(int p_91114_, long p_91115_) {
      this.options.enableVsync = false;
      this.options.save();
   }

   private static boolean checkIs64Bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String s : astring) {
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public RenderTarget getMainRenderTarget() {
      return this.mainRenderTarget;
   }

   public String getLaunchedVersion() {
      return this.launchedVersion;
   }

   public String getVersionType() {
      return this.versionType;
   }

   public void delayCrash(Supplier<CrashReport> p_199936_) {
      this.delayedCrash = p_199936_;
   }

   public static void crash(CrashReport p_91333_) {
      File file1 = new File(getInstance().gameDirectory, "crash-reports");
      File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.realStdoutPrintln(p_91333_.getFriendlyReport());
      if (p_91333_.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + p_91333_.getSaveFile());
         net.minecraftforge.server.ServerLifecycleHooks.handleExit(-1);
      } else if (p_91333_.saveToFile(file2)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         net.minecraftforge.server.ServerLifecycleHooks.handleExit(-1);
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         net.minecraftforge.server.ServerLifecycleHooks.handleExit(-2);
      }

   }

   public boolean isEnforceUnicode() {
      return this.options.forceUnicodeFont;
   }

   public CompletableFuture<Void> reloadResourcePacks() {
      return this.reloadResourcePacks(false);
   }

   private CompletableFuture<Void> reloadResourcePacks(boolean p_168020_) {
      if (this.pendingReload != null) {
         return this.pendingReload;
      } else {
         CompletableFuture<Void> completablefuture = new CompletableFuture<>();
         if (!p_168020_ && this.overlay instanceof LoadingOverlay) {
            this.pendingReload = completablefuture;
            return completablefuture;
         } else {
            this.resourcePackRepository.reload();
            List<PackResources> list = this.resourcePackRepository.openAllSelected();
            if (!p_168020_) {
               this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.MANUAL, list);
            }

            this.setOverlay(new LoadingOverlay(this, this.resourceManager.createReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, list), (p_210750_) -> {
               Util.ifElse(p_210750_, this::rollbackResourcePacks, () -> {
                  this.levelRenderer.allChanged();
                  this.reloadStateTracker.finishReload();
                  completablefuture.complete((Void)null);
               });
            }, true));
            return completablefuture;
         }
      }
   }

   private void selfTest() {
      boolean flag = false;
      BlockModelShaper blockmodelshaper = this.getBlockRenderer().getBlockModelShaper();
      BakedModel bakedmodel = blockmodelshaper.getModelManager().getMissingModel();

      for(Block block : Registry.BLOCK) {
         for(BlockState blockstate : block.getStateDefinition().getPossibleStates()) {
            if (blockstate.getRenderShape() == RenderShape.MODEL) {
               BakedModel bakedmodel1 = blockmodelshaper.getBlockModel(blockstate);
               if (bakedmodel1 == bakedmodel) {
                  LOGGER.debug("Missing model for: {}", (Object)blockstate);
                  flag = true;
               }
            }
         }
      }

      TextureAtlasSprite textureatlassprite1 = bakedmodel.getParticleIcon();

      for(Block block1 : Registry.BLOCK) {
         for(BlockState blockstate1 : block1.getStateDefinition().getPossibleStates()) {
            TextureAtlasSprite textureatlassprite = blockmodelshaper.getParticleIcon(blockstate1);
            if (!blockstate1.isAir() && textureatlassprite == textureatlassprite1) {
               LOGGER.debug("Missing particle icon for: {}", (Object)blockstate1);
               flag = true;
            }
         }
      }

      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : Registry.ITEM) {
         nonnulllist.clear();
         item.fillItemCategory(CreativeModeTab.TAB_SEARCH, nonnulllist);

         for(ItemStack itemstack : nonnulllist) {
            String s = itemstack.getDescriptionId();
            String s1 = (new TranslatableComponent(s)).getString();
            if (s1.toLowerCase(Locale.ROOT).equals(item.getDescriptionId())) {
               LOGGER.debug("Missing translation for: {} {} {}", itemstack, s, itemstack.getItem());
            }
         }
      }

      flag |= MenuScreens.selfTest();
      flag |= EntityRenderers.validateRegistrations();
      if (flag) {
         throw new IllegalStateException("Your game data is foobar, fix the errors above!");
      }
   }

   public LevelStorageSource getLevelSource() {
      return this.levelSource;
   }

   private void openChatScreen(String p_91327_) {
      Minecraft.ChatStatus minecraft$chatstatus = this.getChatStatus();
      if (!minecraft$chatstatus.isChatAllowed(this.isLocalServer())) {
         this.gui.setOverlayMessage(minecraft$chatstatus.getMessage(), false);
      } else {
         this.setScreen(new ChatScreen(p_91327_));
      }

   }

   public void setScreen(@Nullable Screen p_91153_) {
      if (SharedConstants.IS_RUNNING_IN_IDE && Thread.currentThread() != this.gameThread) {
         LOGGER.error("setScreen called from non-game thread");
      }

      if (p_91153_ == null && this.level == null) {
         p_91153_ = new TitleScreen();
      } else if (p_91153_ == null && this.player.isDeadOrDying()) {
         if (this.player.shouldShowDeathScreen()) {
            p_91153_ = new DeathScreen((Component)null, this.level.getLevelData().isHardcore());
         } else {
            this.player.respawn();
         }
      }

      net.minecraftforge.client.ForgeHooksClient.clearGuiLayers(this);
      Screen old = this.screen;
      net.minecraftforge.client.event.ScreenOpenEvent event = new net.minecraftforge.client.event.ScreenOpenEvent(p_91153_);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

      p_91153_ = event.getScreen();
      if (old != null && p_91153_ != old)
         old.removed();

      this.screen = p_91153_;
      BufferUploader.reset();
      if (p_91153_ != null) {
         this.mouseHandler.releaseMouse();
         KeyMapping.releaseAll();
         p_91153_.init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
         this.noRender = false;
      } else {
         this.soundManager.resume();
         this.mouseHandler.grabMouse();
      }

      this.updateTitle();
   }

   public void setOverlay(@Nullable Overlay p_91151_) {
      this.overlay = p_91151_;
   }

   public void destroy() {
      try {
         LOGGER.info("Stopping!");

         try {
            NarratorChatListener.INSTANCE.destroy();
         } catch (Throwable throwable1) {
         }

         try {
            if (this.level != null) {
               this.level.disconnect();
            }

            this.clearLevel();
         } catch (Throwable throwable) {
         }

         if (this.screen != null) {
            this.screen.removed();
         }

         this.close();
      } finally {
         Util.timeSource = System::nanoTime;
         if (this.delayedCrash == null) {
            System.exit(0);
         }

      }

   }

   public void close() {
      try {
         this.regionalCompliancies.close();
         this.modelManager.close();
         this.fontManager.close();
         this.gameRenderer.close();
         this.levelRenderer.close();
         this.soundManager.destroy();
         this.resourcePackRepository.close();
         this.particleEngine.close();
         this.mobEffectTextures.close();
         this.paintingTextures.close();
         this.textureManager.close();
         this.resourceManager.close();
         Util.shutdownExecutors();
      } catch (Throwable throwable) {
         LOGGER.error("Shutdown failure!", throwable);
         throw throwable;
      } finally {
         this.virtualScreen.close();
         this.window.close();
      }

   }

   private void runTick(boolean p_91384_) {
      this.window.setErrorSection("Pre render");
      long i = Util.getNanos();
      if (this.window.shouldClose()) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
         CompletableFuture<Void> completablefuture = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> {
            completablefuture.complete((Void)null);
         });
      }

      Runnable runnable;
      while((runnable = this.progressTasks.poll()) != null) {
         runnable.run();
      }

      if (p_91384_) {
         int j = this.timer.advanceTime(Util.getMillis());
         this.profiler.push("scheduledExecutables");
         this.runAllTasks();
         this.profiler.pop();
         this.profiler.push("tick");

         for(int k = 0; k < Math.min(10, j); ++k) {
            this.profiler.incrementCounter("clientTick");
            this.tick();
         }

         this.profiler.pop();
      }

      this.mouseHandler.turnPlayer();
      this.window.setErrorSection("Render");
      this.profiler.push("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      this.profiler.pop();
      this.profiler.push("render");
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.clear(16640, ON_OSX);
      this.mainRenderTarget.bindWrite(true);
      FogRenderer.setupNoFog();
      this.profiler.push("display");
      RenderSystem.enableTexture();
      RenderSystem.enableCull();
      this.profiler.pop();
      if (!this.noRender) {
         net.minecraftforge.event.ForgeEventFactory.onRenderTickStart(this.pause ? this.pausePartialTick : this.timer.partialTick);
         this.profiler.popPush("gameRenderer");
         this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, i, p_91384_);
         this.profiler.popPush("toasts");
         this.toast.render(new PoseStack());
         this.profiler.pop();
         net.minecraftforge.event.ForgeEventFactory.onRenderTickEnd(this.pause ? this.pausePartialTick : this.timer.partialTick);
      }

      if (this.fpsPieResults != null) {
         this.profiler.push("fpsPie");
         this.renderFpsMeter(new PoseStack(), this.fpsPieResults);
         this.profiler.pop();
      }

      this.profiler.push("blit");
      this.mainRenderTarget.unbindWrite();
      posestack.popPose();
      posestack.pushPose();
      RenderSystem.applyModelViewMatrix();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      this.profiler.popPush("updateDisplay");
      this.window.updateDisplay();
      int i1 = this.getFramerateLimit();
      if ((double)i1 < Option.FRAMERATE_LIMIT.getMaxValue()) {
         RenderSystem.limitDisplayFPS(i1);
      }

      this.profiler.popPush("yield");
      Thread.yield();
      this.profiler.pop();
      this.window.setErrorSection("Post render");
      ++this.frames;
      boolean flag = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
      if (this.pause != flag) {
         if (this.pause) {
            this.pausePartialTick = this.timer.partialTick;
         } else {
            this.timer.partialTick = this.pausePartialTick;
         }

         this.pause = flag;
      }

      long l = Util.getNanos();
      this.frameTimer.logFrameDuration(l - this.lastNanoTime);
      this.lastNanoTime = l;
      this.profiler.push("fpsUpdate");

      while(Util.getMillis() >= this.lastTime + 1000L) {
         fps = this.frames;
         this.fpsString = String.format("%d fps T: %s%s%s%s B: %d", fps, (double)this.options.framerateLimit == Option.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.options.framerateLimit, this.options.enableVsync ? " vsync" : "", this.options.graphicsMode, this.options.renderClouds == CloudStatus.OFF ? "" : (this.options.renderClouds == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius);
         this.lastTime += 1000L;
         this.frames = 0;
      }

      this.profiler.pop();
   }

   private boolean shouldRenderFpsPie() {
      return this.options.renderDebug && this.options.renderDebugCharts && !this.options.hideGui;
   }

   private ProfilerFiller constructProfiler(boolean p_167971_, @Nullable SingleTickProfiler p_167972_) {
      if (!p_167971_) {
         this.fpsPieProfiler.disable();
         if (!this.metricsRecorder.isRecording() && p_167972_ == null) {
            return InactiveProfiler.INSTANCE;
         }
      }

      ProfilerFiller profilerfiller;
      if (p_167971_) {
         if (!this.fpsPieProfiler.isEnabled()) {
            this.fpsPieRenderTicks = 0;
            this.fpsPieProfiler.enable();
         }

         ++this.fpsPieRenderTicks;
         profilerfiller = this.fpsPieProfiler.getFiller();
      } else {
         profilerfiller = InactiveProfiler.INSTANCE;
      }

      if (this.metricsRecorder.isRecording()) {
         profilerfiller = ProfilerFiller.tee(profilerfiller, this.metricsRecorder.getProfiler());
      }

      return SingleTickProfiler.decorateFiller(profilerfiller, p_167972_);
   }

   private void finishProfilers(boolean p_91339_, @Nullable SingleTickProfiler p_91340_) {
      if (p_91340_ != null) {
         p_91340_.endTick();
      }

      if (p_91339_) {
         this.fpsPieResults = this.fpsPieProfiler.getResults();
      } else {
         this.fpsPieResults = null;
      }

      this.profiler = this.fpsPieProfiler.getFiller();
   }

   public void resizeDisplay() {
      int i = this.window.calculateScale(this.options.guiScale, this.isEnforceUnicode());
      this.window.setGuiScale((double)i);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
         net.minecraftforge.client.ForgeHooksClient.resizeGuiLayers(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      RenderTarget rendertarget = this.getMainRenderTarget();
      rendertarget.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
      if (this.gameRenderer != null)
      this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      this.mouseHandler.setIgnoreFirstMove();
   }

   public void cursorEntered() {
      this.mouseHandler.cursorEntered();
   }

   private int getFramerateLimit() {
      return this.level != null || this.screen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
   }

   public void emergencySave() {
      try {
         MemoryReserve.release();
         this.levelRenderer.clear();
      } catch (Throwable throwable1) {
      }

      try {
         System.gc();
         if (this.isLocalServer && this.singleplayerServer != null) {
            this.singleplayerServer.halt(true);
         }

         this.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
      } catch (Throwable throwable) {
      }

      System.gc();
   }

   public boolean debugClientMetricsStart(Consumer<TranslatableComponent> p_167947_) {
      if (this.metricsRecorder.isRecording()) {
         this.debugClientMetricsStop();
         return false;
      } else {
         Consumer<ProfileResults> consumer = (p_210788_) -> {
            int i = p_210788_.getTickDuration();
            double d0 = (double)p_210788_.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
            this.execute(() -> {
               p_167947_.accept(new TranslatableComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d0), i, String.format(Locale.ROOT, "%.2f", (double)i / d0)));
            });
         };
         Consumer<Path> consumer1 = (p_210791_) -> {
            Component component = (new TextComponent(p_210791_.toString())).withStyle(ChatFormatting.UNDERLINE).withStyle((p_210743_) -> {
               return p_210743_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, p_210791_.toFile().getParent()));
            });
            this.execute(() -> {
               p_167947_.accept(new TranslatableComponent("debug.profiling.stop", component));
            });
         };
         SystemReport systemreport = fillSystemReport(new SystemReport(), this, this.languageManager, this.launchedVersion, this.options);
         Consumer<List<Path>> consumer2 = (p_210677_) -> {
            Path path = this.archiveProfilingReport(systemreport, p_210677_);
            consumer1.accept(path);
         };
         Consumer<Path> consumer3;
         if (this.singleplayerServer == null) {
            consumer3 = (p_210760_) -> {
               consumer2.accept(ImmutableList.of(p_210760_));
            };
         } else {
            this.singleplayerServer.fillSystemReport(systemreport);
            CompletableFuture<Path> completablefuture = new CompletableFuture<>();
            CompletableFuture<Path> completablefuture1 = new CompletableFuture<>();
            CompletableFuture.allOf(completablefuture, completablefuture1).thenRunAsync(() -> {
               consumer2.accept(ImmutableList.of(completablefuture.join(), completablefuture1.join()));
            }, Util.ioPool());
            this.singleplayerServer.startRecordingMetrics((p_210679_) -> {
            }, completablefuture1::complete);
            consumer3 = completablefuture::complete;
         }

         this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ClientMetricsSamplersProvider(Util.timeSource, this.levelRenderer), Util.timeSource, Util.ioPool(), new MetricsPersister("client"), (p_210757_) -> {
            this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
            consumer.accept(p_210757_);
         }, consumer3);
         return true;
      }
   }

   private void debugClientMetricsStop() {
      this.metricsRecorder.end();
      if (this.singleplayerServer != null) {
         this.singleplayerServer.finishRecordingMetrics();
      }

   }

   private Path archiveProfilingReport(SystemReport p_167857_, List<Path> p_167858_) {
      String s;
      if (this.isLocalServer()) {
         s = this.getSingleplayerServer().getWorldData().getLevelName();
      } else {
         s = this.getCurrentServer().name;
      }

      Path path;
      try {
         String s1 = String.format("%s-%s-%s", (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()), s, SharedConstants.getCurrentVersion().getId());
         String s2 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, s1, ".zip");
         path = MetricsPersister.PROFILING_RESULTS_DIR.resolve(s2);
      } catch (IOException ioexception1) {
         throw new UncheckedIOException(ioexception1);
      }

      try {
         FileZipper filezipper = new FileZipper(path);

         try {
            filezipper.add(Paths.get("system.txt"), p_167857_.toLineSeparatedString());
            filezipper.add(Paths.get("client").resolve(this.options.getFile().getName()), this.options.dumpOptionsForReport());
            p_167858_.forEach(filezipper::add);
         } catch (Throwable throwable1) {
            try {
               filezipper.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         filezipper.close();
      } finally {
         for(Path path1 : p_167858_) {
            try {
               FileUtils.forceDelete(path1.toFile());
            } catch (IOException ioexception) {
               LOGGER.warn("Failed to delete temporary profiling result {}", path1, ioexception);
            }
         }

      }

      return path;
   }

   public void debugFpsMeterKeyPress(int p_91112_) {
      if (this.fpsPieResults != null) {
         List<ResultField> list = this.fpsPieResults.getTimes(this.debugPath);
         if (!list.isEmpty()) {
            ResultField resultfield = list.remove(0);
            if (p_91112_ == 0) {
               if (!resultfield.name.isEmpty()) {
                  int i = this.debugPath.lastIndexOf(30);
                  if (i >= 0) {
                     this.debugPath = this.debugPath.substring(0, i);
                  }
               }
            } else {
               --p_91112_;
               if (p_91112_ < list.size() && !"unspecified".equals((list.get(p_91112_)).name)) {
                  if (!this.debugPath.isEmpty()) {
                     this.debugPath = this.debugPath + "\u001e";
                  }

                  this.debugPath = this.debugPath + (list.get(p_91112_)).name;
               }
            }

         }
      }
   }

   private void renderFpsMeter(PoseStack p_91141_, ProfileResults p_91142_) {
      List<ResultField> list = p_91142_.getTimes(this.debugPath);
      ResultField resultfield = list.remove(0);
      RenderSystem.clear(256, ON_OSX);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Matrix4f matrix4f = Matrix4f.orthographic(0.0F, (float)this.window.getWidth(), 0.0F, (float)this.window.getHeight(), 1000.0F, 3000.0F);
      RenderSystem.setProjectionMatrix(matrix4f);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.setIdentity();
      posestack.translate(0.0D, 0.0D, -2000.0D);
      RenderSystem.applyModelViewMatrix();
      RenderSystem.lineWidth(1.0F);
      RenderSystem.disableTexture();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      int i = 160;
      int j = this.window.getWidth() - 160 - 10;
      int k = this.window.getHeight() - 320;
      RenderSystem.enableBlend();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      bufferbuilder.vertex((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      tesselator.end();
      RenderSystem.disableBlend();
      double d0 = 0.0D;

      for(ResultField resultfield1 : list) {
         int l = Mth.floor(resultfield1.percentage / 4.0D) + 1;
         bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         int i1 = resultfield1.getColor();
         int j1 = i1 >> 16 & 255;
         int k1 = i1 >> 8 & 255;
         int l1 = i1 & 255;
         bufferbuilder.vertex((double)j, (double)k, 0.0D).color(j1, k1, l1, 255).endVertex();

         for(int i2 = l; i2 >= 0; --i2) {
            float f = (float)((d0 + resultfield1.percentage * (double)i2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
            float f1 = Mth.sin(f) * 160.0F;
            float f2 = Mth.cos(f) * 160.0F * 0.5F;
            bufferbuilder.vertex((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(j1, k1, l1, 255).endVertex();
         }

         tesselator.end();
         bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

         for(int l2 = l; l2 >= 0; --l2) {
            float f3 = (float)((d0 + resultfield1.percentage * (double)l2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
            float f4 = Mth.sin(f3) * 160.0F;
            float f5 = Mth.cos(f3) * 160.0F * 0.5F;
            if (!(f5 > 0.0F)) {
               bufferbuilder.vertex((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
               bufferbuilder.vertex((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
            }
         }

         tesselator.end();
         d0 += resultfield1.percentage;
      }

      DecimalFormat decimalformat = new DecimalFormat("##0.00");
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      RenderSystem.enableTexture();
      String s = ProfileResults.demanglePath(resultfield.name);
      String s1 = "";
      if (!"unspecified".equals(s)) {
         s1 = s1 + "[0] ";
      }

      if (s.isEmpty()) {
         s1 = s1 + "ROOT ";
      } else {
         s1 = s1 + s + " ";
      }

      int k2 = 16777215;
      this.font.drawShadow(p_91141_, s1, (float)(j - 160), (float)(k - 80 - 16), 16777215);
      s1 = decimalformat.format(resultfield.globalPercentage) + "%";
      this.font.drawShadow(p_91141_, s1, (float)(j + 160 - this.font.width(s1)), (float)(k - 80 - 16), 16777215);

      for(int j2 = 0; j2 < list.size(); ++j2) {
         ResultField resultfield2 = list.get(j2);
         StringBuilder stringbuilder = new StringBuilder();
         if ("unspecified".equals(resultfield2.name)) {
            stringbuilder.append("[?] ");
         } else {
            stringbuilder.append("[").append(j2 + 1).append("] ");
         }

         String s2 = stringbuilder.append(resultfield2.name).toString();
         this.font.drawShadow(p_91141_, s2, (float)(j - 160), (float)(k + 80 + j2 * 8 + 20), resultfield2.getColor());
         s2 = decimalformat.format(resultfield2.percentage) + "%";
         this.font.drawShadow(p_91141_, s2, (float)(j + 160 - 50 - this.font.width(s2)), (float)(k + 80 + j2 * 8 + 20), resultfield2.getColor());
         s2 = decimalformat.format(resultfield2.globalPercentage) + "%";
         this.font.drawShadow(p_91141_, s2, (float)(j + 160 - this.font.width(s2)), (float)(k + 80 + j2 * 8 + 20), resultfield2.getColor());
      }

   }

   public void stop() {
      this.running = false;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void pauseGame(boolean p_91359_) {
      if (this.screen == null) {
         boolean flag = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
         if (flag) {
            this.setScreen(new PauseScreen(!p_91359_));
            this.soundManager.pause();
         } else {
            this.setScreen(new PauseScreen(true));
         }

      }
   }

   private void continueAttack(boolean p_91387_) {
      if (!p_91387_) {
         this.missTime = 0;
      }

      if (this.missTime <= 0 && !this.player.isUsingItem()) {
         if (p_91387_ && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)this.hitResult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!this.level.isEmptyBlock(blockpos)) {
               net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) {
                     this.particleEngine.addBlockHitEffects(blockpos, blockhitresult);
                     this.player.swing(InteractionHand.MAIN_HAND);
                  }
                  return;
               }
               Direction direction = blockhitresult.getDirection();
               if (this.gameMode.continueDestroyBlock(blockpos, direction)) {
                  if (inputEvent.shouldSwingHand()) {
                  this.particleEngine.addBlockHitEffects(blockpos, blockhitresult);
                  this.player.swing(InteractionHand.MAIN_HAND);
                  }
               }
            }

         } else {
            this.gameMode.stopDestroyBlock();
         }
      }
   }

   private boolean startAttack() {
      if (this.missTime > 0) {
         return false;
      } else if (this.hitResult == null) {
         LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
         if (this.gameMode.hasMissTime()) {
            this.missTime = 10;
         }

         return false;
      } else if (this.player.isHandsBusy()) {
         return false;
      } else {
         boolean flag = false;
         net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
         HitResult.Type hitType = this.hitResult.getType();
         if(this.hitResult instanceof EntityHitResult entityHit) { // Forge: Perform attack range checks here, instead of in GameRenderer#pick, so extended-reach interactions work.
             if(!player.canHit(entityHit.getEntity(), 0)) hitType = HitResult.Type.MISS; // No padding in client code.
         }
         if (!inputEvent.isCanceled())
         switch(hitType) {
         case ENTITY:
            this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
            break;
         case BLOCK:
            BlockHitResult blockhitresult = (BlockHitResult)this.hitResult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!this.level.isEmptyBlock(blockpos)) {
               this.gameMode.startDestroyBlock(blockpos, blockhitresult.getDirection());
               if (this.level.getBlockState(blockpos).isAir()) {
                  flag = true;
               }
               break;
            }
         case MISS:
            if (this.gameMode.hasMissTime()) {
               this.missTime = 10;
            }

            this.player.resetAttackStrengthTicker();
            net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(this.player);
         }

         if (inputEvent.shouldSwingHand())
         this.player.swing(InteractionHand.MAIN_HAND);
         return flag;
      }
   }

   private void startUseItem() {
      if (!this.gameMode.isDestroying()) {
         this.rightClickDelay = 4;
         if (!this.player.isHandsBusy()) {
            if (this.hitResult == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(InteractionHand interactionhand : InteractionHand.values()) {
               net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, this.options.keyUse, interactionhand);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) this.player.swing(interactionhand);
                  return;
               }
               ItemStack itemstack = this.player.getItemInHand(interactionhand);
               if (this.hitResult != null) {
                  switch(this.hitResult.getType()) {
                  case ENTITY:
                     EntityHitResult entityhitresult = (EntityHitResult)this.hitResult;
                     Entity entity = entityhitresult.getEntity();
                     if (!this.level.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                        return;
                     }

                     if(!this.player.canInteractWith(entityhitresult.getEntity(), 0)) break; //Forge: Entity may be traced via attack range, but the player may not have sufficient reach.  No padding in client code.
                     InteractionResult interactionresult = this.gameMode.interactAt(this.player, entity, entityhitresult, interactionhand);
                     if (!interactionresult.consumesAction()) {
                        interactionresult = this.gameMode.interact(this.player, entity, interactionhand);
                     }

                     if (interactionresult.consumesAction()) {
                        if (interactionresult.shouldSwing()) {
                           if (inputEvent.shouldSwingHand())
                           this.player.swing(interactionhand);
                        }

                        return;
                     }
                     break;
                  case BLOCK:
                     BlockHitResult blockhitresult = (BlockHitResult)this.hitResult;
                     int i = itemstack.getCount();
                     InteractionResult interactionresult1 = this.gameMode.useItemOn(this.player, this.level, interactionhand, blockhitresult);
                     if (interactionresult1.consumesAction()) {
                        if (interactionresult1.shouldSwing()) {
                           if (inputEvent.shouldSwingHand())
                           this.player.swing(interactionhand);
                           if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.gameMode.hasInfiniteItems())) {
                              this.gameRenderer.itemInHandRenderer.itemUsed(interactionhand);
                           }
                        }

                        return;
                     }

                     if (interactionresult1 == InteractionResult.FAIL) {
                        return;
                     }
                  }
               }

               if (itemstack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS))
                  net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, interactionhand);

               if (!itemstack.isEmpty()) {
                  InteractionResult interactionresult2 = this.gameMode.useItem(this.player, this.level, interactionhand);
                  if (interactionresult2.consumesAction()) {
                     if (interactionresult2.shouldSwing()) {
                        this.player.swing(interactionhand);
                     }

                     this.gameRenderer.itemInHandRenderer.itemUsed(interactionhand);
                     return;
                  }
               }
            }

         }
      }
   }

   public MusicManager getMusicManager() {
      return this.musicManager;
   }

   public void tick() {
      if (this.rightClickDelay > 0) {
         --this.rightClickDelay;
      }

      net.minecraftforge.event.ForgeEventFactory.onPreClientTick();

      this.profiler.push("gui");
      this.gui.tick(this.pause);
      this.profiler.pop();
      this.gameRenderer.pick(1.0F);
      this.tutorial.onLookAt(this.level, this.hitResult);
      this.profiler.push("gameMode");
      if (!this.pause && this.level != null) {
         this.gameMode.tick();
      }

      this.profiler.popPush("textures");
      if (this.level != null) {
         this.textureManager.tick();
      }

      if (this.screen == null && this.player != null) {
         if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
            this.setScreen((Screen)null);
         } else if (this.player.isSleeping() && this.level != null) {
            this.setScreen(new InBedChatScreen());
         }
      } else {
         Screen $$4 = this.screen;
         if ($$4 instanceof InBedChatScreen) {
            InBedChatScreen inbedchatscreen = (InBedChatScreen)$$4;
            if (!this.player.isSleeping()) {
               inbedchatscreen.onPlayerWokeUp();
            }
         }
      }

      if (this.screen != null) {
         this.missTime = 10000;
      }

      if (this.screen != null) {
         Screen.wrapScreenError(() -> {
            this.screen.tick();
         }, "Ticking screen", this.screen.getClass().getCanonicalName());
      }

      if (!this.options.renderDebug) {
         this.gui.clearCache();
      }

      if (this.overlay == null && (this.screen == null || this.screen.passEvents)) {
         this.profiler.popPush("Keybindings");
         this.handleKeybinds();
         if (this.missTime > 0) {
            --this.missTime;
         }
      }

      if (this.level != null) {
         this.profiler.popPush("gameRenderer");
         if (!this.pause) {
            this.gameRenderer.tick();
         }

         this.profiler.popPush("levelRenderer");
         if (!this.pause) {
            this.levelRenderer.tick();
         }

         this.profiler.popPush("level");
         if (!this.pause) {
            if (this.level.getSkyFlashTime() > 0) {
               this.level.setSkyFlashTime(this.level.getSkyFlashTime() - 1);
            }

            this.level.tickEntities();
         }
      } else if (this.gameRenderer.currentEffect() != null) {
         this.gameRenderer.shutdownEffect();
      }

      if (!this.pause) {
         this.musicManager.tick();
      }

      this.soundManager.tick(this.pause);
      if (this.level != null) {
         if (!this.pause) {
            if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
               Component component = new TranslatableComponent("tutorial.socialInteractions.title");
               Component component1 = new TranslatableComponent("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
               this.socialInteractionsToast = new TutorialToast(TutorialToast.Icons.SOCIAL_INTERACTIONS, component, component1, true);
               this.tutorial.addTimedToast(this.socialInteractionsToast, 160);
               this.options.joinedFirstServer = true;
               this.options.save();
            }

            this.tutorial.tick();

            net.minecraftforge.event.ForgeEventFactory.onPreWorldTick(this.level, () -> true);
            try {
               this.level.tick(() -> {
                  return true;
               });
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception in world tick");
               if (this.level == null) {
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Affected level");
                  crashreportcategory.setDetail("Problem", "Level is null!");
               } else {
                  this.level.fillReportDetails(crashreport);
               }

               throw new ReportedException(crashreport);
            }
            net.minecraftforge.event.ForgeEventFactory.onPostWorldTick(this.level, () -> true);
         }

         this.profiler.popPush("animateTick");
         if (!this.pause && this.level != null) {
            this.level.animateTick(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
         }

         this.profiler.popPush("particles");
         if (!this.pause) {
            this.particleEngine.tick();
         }
      } else if (this.pendingConnection != null) {
         this.profiler.popPush("pendingConnection");
         this.pendingConnection.tick();
      }

      this.profiler.popPush("keyboard");
      this.keyboardHandler.tick();
      this.profiler.pop();

      net.minecraftforge.event.ForgeEventFactory.onPostClientTick();
   }

   private boolean isMultiplayerServer() {
      return !this.isLocalServer || this.singleplayerServer != null && this.singleplayerServer.isPublished();
   }

   private void handleKeybinds() {
      for(; this.options.keyTogglePerspective.consumeClick(); this.levelRenderer.needsUpdate()) {
         CameraType cameratype = this.options.getCameraType();
         this.options.setCameraType(this.options.getCameraType().cycle());
         if (cameratype.isFirstPerson() != this.options.getCameraType().isFirstPerson()) {
            this.gameRenderer.checkEntityPostEffect(this.options.getCameraType().isFirstPerson() ? this.getCameraEntity() : null);
         }
      }

      while(this.options.keySmoothCamera.consumeClick()) {
         this.options.smoothCamera = !this.options.smoothCamera;
      }

      for(int i = 0; i < 9; ++i) {
         boolean flag = this.options.keySaveHotbarActivator.isDown();
         boolean flag1 = this.options.keyLoadHotbarActivator.isDown();
         if (this.options.keyHotbarSlots[i].consumeClick()) {
            if (this.player.isSpectator()) {
               this.gui.getSpectatorGui().onHotbarSelected(i);
            } else if (!this.player.isCreative() || this.screen != null || !flag1 && !flag) {
               this.player.getInventory().selected = i;
            } else {
               CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, i, flag1, flag);
            }
         }
      }

      while(this.options.keySocialInteractions.consumeClick()) {
         if (!this.isMultiplayerServer()) {
            this.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
            NarratorChatListener.INSTANCE.sayNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
         } else {
            if (this.socialInteractionsToast != null) {
               this.tutorial.removeTimedToast(this.socialInteractionsToast);
               this.socialInteractionsToast = null;
            }

            this.setScreen(new SocialInteractionsScreen());
         }
      }

      while(this.options.keyInventory.consumeClick()) {
         if (this.gameMode.isServerControlledInventory()) {
            this.player.sendOpenInventory();
         } else {
            this.tutorial.onOpenInventory();
            this.setScreen(new InventoryScreen(this.player));
         }
      }

      while(this.options.keyAdvancements.consumeClick()) {
         this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
      }

      while(this.options.keySwapOffhand.consumeClick()) {
         if (!this.player.isSpectator()) {
            this.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         }
      }

      while(this.options.keyDrop.consumeClick()) {
         if (!this.player.isSpectator() && this.player.drop(Screen.hasControlDown())) {
            this.player.swing(InteractionHand.MAIN_HAND);
         }
      }

      while(this.options.keyChat.consumeClick()) {
         this.openChatScreen("");
      }

      if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
         this.openChatScreen("/");
      }

      boolean flag2 = false;
      if (this.player.isUsingItem()) {
         if (!this.options.keyUse.isDown()) {
            this.gameMode.releaseUsingItem(this.player);
         }

         while(this.options.keyAttack.consumeClick()) {
         }

         while(this.options.keyUse.consumeClick()) {
         }

         while(this.options.keyPickItem.consumeClick()) {
         }
      } else {
         while(this.options.keyAttack.consumeClick()) {
            flag2 |= this.startAttack();
         }

         while(this.options.keyUse.consumeClick()) {
            this.startUseItem();
         }

         while(this.options.keyPickItem.consumeClick()) {
            this.pickBlock();
         }
      }

      if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
         this.startUseItem();
      }

      this.continueAttack(this.screen == null && !flag2 && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
   }

   public ClientTelemetryManager createTelemetryManager() {
      return new ClientTelemetryManager(this, this.userApiService, this.user.getXuid(), this.user.getClientId(), this.deviceSessionId);
   }

   public void loadLevel(String p_91201_) {
      this.doLoadLevel(p_91201_, WorldStem.DataPackConfigSupplier::loadFromWorld, WorldStem.WorldDataSupplier::loadFromWorld, false, Minecraft.ExperimentalDialogType.BACKUP, false);
   }

   public void createLevel(String p_205186_, LevelSettings p_205187_, RegistryAccess p_205188_, WorldGenSettings p_205189_) {
      this.doLoadLevel(p_205186_, (p_210684_) -> {
         return p_205187_::getDataPackConfig;
      }, (p_210718_) -> {
         return (p_210712_, p_210713_) -> {
            RegistryAccess.Writable registryaccess$writable = RegistryAccess.builtinCopy();
            DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, p_205188_);
            DynamicOps<JsonElement> dynamicops1 = RegistryOps.createAndLoad(JsonOps.INSTANCE, registryaccess$writable, p_210712_);
            DataResult<WorldGenSettings> dataresult = WorldGenSettings.CODEC.encodeStart(dynamicops, p_205189_).setLifecycle(Lifecycle.stable()).flatMap((p_210687_) -> {
               return WorldGenSettings.CODEC.parse(dynamicops1, p_210687_);
            });
            WorldGenSettings worldgensettings = dataresult.getOrThrow(false, Util.prefix("Error reading worldgen settings after loading data packs: ", LOGGER::error));
            return Pair.of(new PrimaryLevelData(p_205187_, worldgensettings, dataresult.lifecycle()), registryaccess$writable.freeze());
         };
      }, false, Minecraft.ExperimentalDialogType.CREATE, true);
   }

   private void doLoadLevel(String p_205206_, Function<LevelStorageSource.LevelStorageAccess, WorldStem.DataPackConfigSupplier> p_205207_, Function<LevelStorageSource.LevelStorageAccess, WorldStem.WorldDataSupplier> p_205208_, boolean p_205209_, Minecraft.ExperimentalDialogType p_205210_, boolean creating) {
      LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess;
      try {
         levelstoragesource$levelstorageaccess = this.levelSource.createAccess(p_205206_);
      } catch (IOException ioexception2) {
         LOGGER.warn("Failed to read level {} data", p_205206_, ioexception2);
         SystemToast.onWorldAccessFailure(this, p_205206_);
         this.setScreen((Screen)null);
         return;
      }

      PackRepository packrepository = createPackRepository(levelstoragesource$levelstorageaccess);

      WorldStem worldstem;
      try {
         if (!creating) levelstoragesource$levelstorageaccess.readAdditionalLevelSaveData();
         worldstem = this.makeWorldStem(packrepository, p_205209_, p_205207_.apply(levelstoragesource$levelstorageaccess), p_205208_.apply(levelstoragesource$levelstorageaccess));
      } catch (Exception exception) {
         LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
         this.setScreen(new DatapackLoadFailureScreen(() -> {
            this.doLoadLevel(p_205206_, p_205207_, p_205208_, true, p_205210_, creating);
         }));

         try {
            packrepository.close();
            levelstoragesource$levelstorageaccess.close();
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to unlock access to level {}", p_205206_, ioexception);
         }

         return;
      }

      WorldData worlddata = worldstem.worldData();
      boolean flag = worlddata.worldGenSettings().isOldCustomizedWorld();
      boolean flag1 = worlddata.worldGenSettingsLifecycle() != Lifecycle.stable();
      //Skip confirmation if it has been done already for this world
      boolean skipConfirmation = worlddata instanceof PrimaryLevelData pld && pld.hasConfirmedExperimentalWarning();
      if (skipConfirmation || p_205210_ == Minecraft.ExperimentalDialogType.NONE || !flag && !flag1) {
         this.clearLevel();
         this.progressListener.set((StoringChunkProgressListener)null);

         try {
            RegistryAccess.Frozen registryaccess$frozen = worldstem.registryAccess();
            levelstoragesource$levelstorageaccess.saveDataTag(registryaccess$frozen, worlddata); //TODO-PATCHING: Figure out what registry access needs to be passed in here.
            worldstem.updateGlobals();
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            GameProfileCache gameprofilecache = new GameProfileCache(gameprofilerepository, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
            gameprofilecache.setExecutor(this);
            SkullBlockEntity.setup(gameprofilecache, minecraftsessionservice, this);
            GameProfileCache.setUsesAuthentication(false);
            this.singleplayerServer = MinecraftServer.spin((p_210695_) -> {
               return new IntegratedServer(p_210695_, this, levelstoragesource$levelstorageaccess, packrepository, worldstem, minecraftsessionservice, gameprofilerepository, gameprofilecache, (p_210795_) -> { //TODO-PATCHING: Handle the registry freeze delay again.
                  StoringChunkProgressListener storingchunkprogresslistener = new StoringChunkProgressListener(p_210795_ + 0);
                  this.progressListener.set(storingchunkprogresslistener);
                  return ProcessorChunkProgressListener.createStarted(storingchunkprogresslistener, this.progressTasks::add);
               });
            });
            this.isLocalServer = true;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Starting integrated server");
            crashreportcategory.setDetail("Level ID", p_205206_);
            crashreportcategory.setDetail("Level Name", worlddata.getLevelName());
            throw new ReportedException(crashreport);
         }

         while(this.progressListener.get() == null) {
            Thread.yield();
         }

         LevelLoadingScreen levelloadingscreen = new LevelLoadingScreen(this.progressListener.get());
         this.setScreen(levelloadingscreen);
         this.profiler.push("waitForServer");

         while(!this.singleplayerServer.isReady()) {
            levelloadingscreen.tick();
            this.runTick(false);

            try {
               Thread.sleep(16L);
            } catch (InterruptedException interruptedexception) {
            }

            if (this.delayedCrash != null) {
               crash(this.delayedCrash.get());
               return;
            }
         }

         this.profiler.pop();
         SocketAddress socketaddress = this.singleplayerServer.getConnection().startMemoryChannel();
         Connection connection = Connection.connectToLocalServer(socketaddress);
         connection.setListener(new ClientHandshakePacketListenerImpl(connection, this, (Screen)null, (p_210793_) -> {
         }));
         connection.send(new ClientIntentionPacket(socketaddress.toString(), 0, ConnectionProtocol.LOGIN));
         com.mojang.authlib.GameProfile gameProfile = this.getUser().getGameProfile();
         if (!this.getUser().hasCachedProperties()) {
            gameProfile = minecraftSessionService.fillProfileProperties(gameProfile, true); //Forge: Fill profile properties upon game load. Fixes MC-52974.
            this.getUser().setProperties(gameProfile.getProperties());
         }
         connection.send(new ServerboundHelloPacket(gameProfile));
         this.pendingConnection = connection;
      } else {
         if (flag) //FORGE: For legacy world options, let vanilla handle it.
         this.displayExperimentalConfirmationDialog(p_205210_, p_205206_, flag, () -> {
            this.doLoadLevel(p_205206_, p_205207_, p_205208_, p_205209_, Minecraft.ExperimentalDialogType.NONE, creating);
         });
         else net.minecraftforge.client.ForgeHooksClient.createWorldConfirmationScreen(levelSource, p_205206_, creating, p_205208_, wds -> () -> this.doLoadLevel(p_205206_, p_205207_, wds, p_205209_, ExperimentalDialogType.NONE, creating));
         worldstem.close();

         try {
            packrepository.close();
            levelstoragesource$levelstorageaccess.close();
         } catch (IOException ioexception1) {
            LOGGER.warn("Failed to unlock access to level {}", p_205206_, ioexception1);
         }

      }
   }

   private void displayExperimentalConfirmationDialog(Minecraft.ExperimentalDialogType p_91144_, String p_91145_, boolean p_91146_, Runnable p_91147_) {
      if (p_91144_ == Minecraft.ExperimentalDialogType.BACKUP) {
         Component component;
         Component component1;
         if (p_91146_) {
            component = new TranslatableComponent("selectWorld.backupQuestion.customized");
            component1 = new TranslatableComponent("selectWorld.backupWarning.customized");
         } else {
            component = new TranslatableComponent("selectWorld.backupQuestion.experimental");
            component1 = new TranslatableComponent("selectWorld.backupWarning.experimental");
         }

         this.setScreen(new BackupConfirmScreen((Screen)null, (p_210729_, p_210730_) -> {
            if (p_210729_) {
               EditWorldScreen.makeBackupAndShowToast(this.levelSource, p_91145_);
            }

            p_91147_.run();
         }, component, component1, false));
      } else {
         this.setScreen(new ConfirmScreen((p_210725_) -> {
            if (p_210725_) {
               p_91147_.run();
            } else {
               this.setScreen((Screen)null);

               try {
                  LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.levelSource.createAccess(p_91145_);

                  try {
                     levelstoragesource$levelstorageaccess.deleteLevel();
                  } catch (Throwable throwable1) {
                     if (levelstoragesource$levelstorageaccess != null) {
                        try {
                           levelstoragesource$levelstorageaccess.close();
                        } catch (Throwable throwable) {
                           throwable1.addSuppressed(throwable);
                        }
                     }

                     throw throwable1;
                  }

                  if (levelstoragesource$levelstorageaccess != null) {
                     levelstoragesource$levelstorageaccess.close();
                  }
               } catch (IOException ioexception) {
                  SystemToast.onWorldDeleteFailure(this, p_91145_);
                  LOGGER.error("Failed to delete world {}", p_91145_, ioexception);
               }
            }

         }, new TranslatableComponent("selectWorld.backupQuestion.experimental"), new TranslatableComponent("selectWorld.backupWarning.experimental"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
      }

   }

   public WorldStem makeWorldStem(LevelStorageSource.LevelStorageAccess p_205153_, boolean p_205154_) throws ExecutionException, InterruptedException {
      PackRepository packrepository = createPackRepository(p_205153_);
      return this.makeWorldStem(packrepository, p_205154_, WorldStem.DataPackConfigSupplier.loadFromWorld(p_205153_), WorldStem.WorldDataSupplier.loadFromWorld(p_205153_));
   }

   public WorldStem makeWorldStem(PackRepository p_205126_, boolean p_205127_, WorldStem.DataPackConfigSupplier p_205128_, WorldStem.WorldDataSupplier p_205129_) throws InterruptedException, ExecutionException {
      try {
         WorldStem.InitConfig worldstem$initconfig = new WorldStem.InitConfig(p_205126_, Commands.CommandSelection.INTEGRATED, 2, p_205127_);
         CompletableFuture<WorldStem> completablefuture = WorldStem.load(worldstem$initconfig, p_205128_, p_205129_, Util.backgroundExecutor(), this);
         this.managedBlock(completablefuture::isDone);
         return completablefuture.get();
      } catch (ExecutionException | InterruptedException interruptedexception) {
         p_205126_.close();
         throw interruptedexception;
      }
   }

   private static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess p_205143_) {
      return new PackRepository(PackType.SERVER_DATA, new ServerPacksSource(), new FolderRepositorySource(p_205143_.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD));
   }

   public void setLevel(ClientLevel p_91157_) {
      if (level != null) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(level));
      ProgressScreen progressscreen = new ProgressScreen(true);
      progressscreen.progressStartNoAbort(new TranslatableComponent("connect.joining"));
      this.updateScreenAndTick(progressscreen);
      this.level = p_91157_;
      this.updateLevelInEngines(p_91157_);
      if (!this.isLocalServer) {
         AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy);
         MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
         GameProfileCache gameprofilecache = new GameProfileCache(gameprofilerepository, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
         gameprofilecache.setExecutor(this);
         SkullBlockEntity.setup(gameprofilecache, minecraftsessionservice, this);
         GameProfileCache.setUsesAuthentication(false);
      }

   }

   public void clearLevel() {
      this.clearLevel(new ProgressScreen(true));
   }

   public void clearLevel(Screen p_91321_) {
      ClientPacketListener clientpacketlistener = this.getConnection();
      if (clientpacketlistener != null) {
         this.dropAllTasks();
         clientpacketlistener.cleanup();
      }

      this.playerSocialManager.stopOnlineMode();
      IntegratedServer integratedserver = this.singleplayerServer;
      this.singleplayerServer = null;
      this.gameRenderer.resetData();
      net.minecraftforge.client.ForgeHooksClient.firePlayerLogout(this.gameMode, this.player);
      this.gameMode = null;
      NarratorChatListener.INSTANCE.clear();
      this.updateScreenAndTick(p_91321_);
      if (this.level != null) {
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(level));
         if (integratedserver != null) {
            this.profiler.push("waitForServer");

            while(!integratedserver.isShutdown()) {
               this.runTick(false);
            }

            this.profiler.pop();
         }

         this.clientPackSource.clearServerPack();
         this.gui.onDisconnected();
         this.currentServer = null;
         this.isLocalServer = false;
         net.minecraftforge.client.ForgeHooksClient.handleClientLevelClosing(level);
         this.game.onLeaveGameSession();
      }

      this.level = null;
      this.updateLevelInEngines((ClientLevel)null);
      this.player = null;
      SkullBlockEntity.clear();
   }

   private void updateScreenAndTick(Screen p_91363_) {
      this.profiler.push("forcedTick");
      this.soundManager.stop();
      this.cameraEntity = null;
      this.pendingConnection = null;
      this.setScreen(p_91363_);
      this.runTick(false);
      this.profiler.pop();
   }

   public void forceSetScreen(Screen p_91347_) {
      this.profiler.push("forcedTick");
      this.setScreen(p_91347_);
      this.runTick(false);
      this.profiler.pop();
   }

   private void updateLevelInEngines(@Nullable ClientLevel p_91325_) {
      this.levelRenderer.setLevel(p_91325_);
      this.particleEngine.setLevel(p_91325_);
      this.blockEntityRenderDispatcher.setLevel(p_91325_);
      this.updateTitle();
   }

   public boolean allowsMultiplayer() {
      return this.allowsMultiplayer && this.userApiService.properties().flag(UserFlag.SERVERS_ALLOWED);
   }

   public boolean allowsRealms() {
      return this.userApiService.properties().flag(UserFlag.REALMS_ALLOWED);
   }

   public boolean isBlocked(UUID p_91247_) {
      if (this.getChatStatus().isChatAllowed(false)) {
         return this.playerSocialManager.shouldHideMessageFrom(p_91247_);
      } else {
         return (this.player == null || !p_91247_.equals(this.player.getUUID())) && !p_91247_.equals(Util.NIL_UUID);
      }
   }

   public Minecraft.ChatStatus getChatStatus() {
      if (this.options.chatVisibility == ChatVisiblity.HIDDEN) {
         return Minecraft.ChatStatus.DISABLED_BY_OPTIONS;
      } else if (!this.allowsChat) {
         return Minecraft.ChatStatus.DISABLED_BY_LAUNCHER;
      } else {
         return !this.userApiService.properties().flag(UserFlag.CHAT_ALLOWED) ? Minecraft.ChatStatus.DISABLED_BY_PROFILE : Minecraft.ChatStatus.ENABLED;
      }
   }

   public final boolean isDemo() {
      return this.demo;
   }

   @Nullable
   public ClientPacketListener getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean renderNames() {
      return !instance.options.hideGui;
   }

   public static boolean useFancyGraphics() {
      return instance.options.graphicsMode.getId() >= GraphicsStatus.FANCY.getId();
   }

   public static boolean useShaderTransparency() {
      return !instance.gameRenderer.isPanoramicMode() && instance.options.graphicsMode.getId() >= GraphicsStatus.FABULOUS.getId();
   }

   public static boolean useAmbientOcclusion() {
      return instance.options.ambientOcclusion != AmbientOcclusionStatus.OFF;
   }

   private void pickBlock() {
      if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
          if (!net.minecraftforge.client.ForgeHooksClient.onClickInput(2, this.options.keyPickItem, InteractionHand.MAIN_HAND).isCanceled())
              net.minecraftforge.common.ForgeHooks.onPickBlock(this.hitResult, this.player, this.level);
          // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
      }
   }

   public ItemStack addCustomNbtData(ItemStack p_91123_, BlockEntity p_91124_) {
      CompoundTag compoundtag = p_91124_.saveWithFullMetadata();
      if (p_91123_.getItem() instanceof PlayerHeadItem && compoundtag.contains("SkullOwner")) {
         CompoundTag compoundtag2 = compoundtag.getCompound("SkullOwner");
         p_91123_.getOrCreateTag().put("SkullOwner", compoundtag2);
         return p_91123_;
      } else {
         BlockItem.setBlockEntityData(p_91123_, p_91124_.getType(), compoundtag);
         CompoundTag compoundtag1 = new CompoundTag();
         ListTag listtag = new ListTag();
         listtag.add(StringTag.valueOf("\"(+NBT)\""));
         compoundtag1.put("Lore", listtag);
         p_91123_.addTagElement("display", compoundtag1);
         return p_91123_;
      }
   }

   public CrashReport fillReport(CrashReport p_91355_) {
      SystemReport systemreport = p_91355_.getSystemReport();
      fillSystemReport(systemreport, this, this.languageManager, this.launchedVersion, this.options);
      if (this.level != null) {
         this.level.fillReportDetails(p_91355_);
      }

      if (this.singleplayerServer != null) {
         this.singleplayerServer.fillSystemReport(systemreport);
      }

      this.reloadStateTracker.fillCrashReport(p_91355_);
      return p_91355_;
   }

   public static void fillReport(@Nullable Minecraft p_167873_, @Nullable LanguageManager p_167874_, String p_167875_, @Nullable Options p_167876_, CrashReport p_167877_) {
      SystemReport systemreport = p_167877_.getSystemReport();
      fillSystemReport(systemreport, p_167873_, p_167874_, p_167875_, p_167876_);
   }

   private static SystemReport fillSystemReport(SystemReport p_167851_, @Nullable Minecraft p_167852_, @Nullable LanguageManager p_167853_, String p_167854_, Options p_167855_) {
      p_167851_.setDetail("Launched Version", () -> {
         return p_167854_;
      });
      p_167851_.setDetail("Backend library", RenderSystem::getBackendDescription);
      p_167851_.setDetail("Backend API", RenderSystem::getApiDescription);
      p_167851_.setDetail("Window size", () -> {
         return p_167852_ != null ? p_167852_.window.getWidth() + "x" + p_167852_.window.getHeight() : "<not initialized>";
      });
      p_167851_.setDetail("GL Caps", RenderSystem::getCapsString);
      p_167851_.setDetail("GL debug messages", () -> {
         return GlDebug.isDebugEnabled() ? String.join("\n", GlDebug.getLastOpenGlDebugMessages()) : "<disabled>";
      });
      p_167851_.setDetail("Using VBOs", () -> {
         return "Yes";
      });
      p_167851_.setDetail("Is Modded", () -> {
         return checkModStatus().fullDescription();
      });
      p_167851_.setDetail("Type", "Client (map_client.txt)");
      if (p_167855_ != null) {
         if (instance != null) {
            String s = instance.getGpuWarnlistManager().getAllWarnings();
            if (s != null) {
               p_167851_.setDetail("GPU Warnings", s);
            }
         }

         p_167851_.setDetail("Graphics mode", p_167855_.graphicsMode.toString());
         p_167851_.setDetail("Resource Packs", () -> {
            StringBuilder stringbuilder = new StringBuilder();

            for(String s1 : p_167855_.resourcePacks) {
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(s1);
               if (p_167855_.incompatibleResourcePacks.contains(s1)) {
                  stringbuilder.append(" (incompatible)");
               }
            }

            return stringbuilder.toString();
         });
      }

      if (p_167853_ != null) {
         p_167851_.setDetail("Current Language", () -> {
            return p_167853_.getSelected().toString();
         });
      }

      p_167851_.setDetail("CPU", GlUtil::getCpuInfo);
      return p_167851_;
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public CompletableFuture<Void> delayTextureReload() {
      return this.submit(() -> this.reloadResourcePacks()).thenCompose((p_210747_) -> {
         return p_210747_;
      });
   }

   public void setCurrentServer(@Nullable ServerData p_91159_) {
      this.currentServer = p_91159_;
   }

   @Nullable
   public ServerData getCurrentServer() {
      return this.currentServer;
   }

   public boolean isLocalServer() {
      return this.isLocalServer;
   }

   public boolean hasSingleplayerServer() {
      return this.isLocalServer && this.singleplayerServer != null;
   }

   @Nullable
   public IntegratedServer getSingleplayerServer() {
      return this.singleplayerServer;
   }

   public User getUser() {
      return this.user;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameprofile = this.getMinecraftSessionService().fillProfileProperties(this.user.getGameProfile(), false);
         this.profileProperties.putAll(gameprofile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public PackRepository getResourcePackRepository() {
      return this.resourcePackRepository;
   }

   public ClientPackSource getClientPackSource() {
      return this.clientPackSource;
   }

   public File getResourcePackDirectory() {
      return this.resourcePackDirectory;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation p_91259_) {
      return this.modelManager.getAtlas(p_91259_)::getSprite;
   }

   public boolean is64Bit() {
      return this.is64bit;
   }

   public boolean isPaused() {
      return this.pause;
   }

   public GpuWarnlistManager getGpuWarnlistManager() {
      return this.gpuWarnlistManager;
   }

   public SoundManager getSoundManager() {
      return this.soundManager;
   }

   public Music getSituationalMusic() {
      if (this.screen instanceof WinScreen) {
         return Musics.CREDITS;
      } else if (this.player != null) {
         if (this.player.level.dimension() == Level.END) {
            return this.gui.getBossOverlay().shouldPlayMusic() ? Musics.END_BOSS : Musics.END;
         } else {
            Holder<Biome> holder = this.player.level.getBiome(this.player.blockPosition());
            Biome.BiomeCategory biome$biomecategory = Biome.getBiomeCategory(holder);
            if (!this.musicManager.isPlayingMusic(Musics.UNDER_WATER) && (!this.player.isUnderWater() || biome$biomecategory != Biome.BiomeCategory.OCEAN && biome$biomecategory != Biome.BiomeCategory.RIVER)) {
               return this.player.level.dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly ? Musics.CREATIVE : holder.value().getBackgroundMusic().orElse(Musics.GAME);
            } else {
               return Musics.UNDER_WATER;
            }
         }
      } else {
         return Musics.MENU;
      }
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.minecraftSessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getCameraEntity() {
      return this.cameraEntity;
   }

   public void setCameraEntity(Entity p_91119_) {
      this.cameraEntity = p_91119_;
      this.gameRenderer.checkEntityPostEffect(p_91119_);
   }

   public boolean shouldEntityAppearGlowing(Entity p_91315_) {
      return p_91315_.isCurrentlyGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && p_91315_.getType() == EntityType.PLAYER;
   }

   protected Thread getRunningThread() {
      return this.gameThread;
   }

   protected Runnable wrapRunnable(Runnable p_91376_) {
      return p_91376_;
   }

   protected boolean shouldRun(Runnable p_91365_) {
      return true;
   }

   public BlockRenderDispatcher getBlockRenderer() {
      return this.blockRenderer;
   }

   public EntityRenderDispatcher getEntityRenderDispatcher() {
      return this.entityRenderDispatcher;
   }

   public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
      return this.blockEntityRenderDispatcher;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public ItemInHandRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public <T> MutableSearchTree<T> getSearchTree(SearchRegistry.Key<T> p_91172_) {
      return this.searchRegistry.getTree(p_91172_);
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean p_91373_) {
      this.connectedToRealms = p_91373_;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public float getFrameTime() {
      return this.timer.partialTick;
   }

   public float getDeltaFrameTime() {
      return this.timer.tickDelta;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean showOnlyReducedInfo() {
      return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo;
   }

   public ToastComponent getToasts() {
      return this.toast;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isWindowActive() {
      return this.windowActive;
   }

   public HotbarManager getHotbarManager() {
      return this.hotbarManager;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public PaintingTextureManager getPaintingTextures() {
      return this.paintingTextures;
   }

   public MobEffectTextureManager getMobEffectTextures() {
      return this.mobEffectTextures;
   }

   public void setWindowActive(boolean p_91261_) {
      this.windowActive = p_91261_;
   }

   public Component grabPanoramixScreenshot(File p_167900_, int p_167901_, int p_167902_) {
      int i = this.window.getWidth();
      int j = this.window.getHeight();
      RenderTarget rendertarget = new TextureTarget(p_167901_, p_167902_, true, ON_OSX);
      float f = this.player.getXRot();
      float f1 = this.player.getYRot();
      float f2 = this.player.xRotO;
      float f3 = this.player.yRotO;
      this.gameRenderer.setRenderBlockOutline(false);

      TranslatableComponent translatablecomponent;
      try {
         this.gameRenderer.setPanoramicMode(true);
         this.levelRenderer.graphicsChanged();
         this.window.setWidth(p_167901_);
         this.window.setHeight(p_167902_);

         for(int k = 0; k < 6; ++k) {
            switch(k) {
            case 0:
               this.player.setYRot(f1);
               this.player.setXRot(0.0F);
               break;
            case 1:
               this.player.setYRot((f1 + 90.0F) % 360.0F);
               this.player.setXRot(0.0F);
               break;
            case 2:
               this.player.setYRot((f1 + 180.0F) % 360.0F);
               this.player.setXRot(0.0F);
               break;
            case 3:
               this.player.setYRot((f1 - 90.0F) % 360.0F);
               this.player.setXRot(0.0F);
               break;
            case 4:
               this.player.setYRot(f1);
               this.player.setXRot(-90.0F);
               break;
            case 5:
            default:
               this.player.setYRot(f1);
               this.player.setXRot(90.0F);
            }

            this.player.yRotO = this.player.getYRot();
            this.player.xRotO = this.player.getXRot();
            rendertarget.bindWrite(true);
            this.gameRenderer.renderLevel(1.0F, 0L, new PoseStack());

            try {
               Thread.sleep(10L);
            } catch (InterruptedException interruptedexception) {
            }

            Screenshot.grab(p_167900_, "panorama_" + k + ".png", rendertarget, (p_210769_) -> {
            });
         }

         Component component = (new TextComponent(p_167900_.getName())).withStyle(ChatFormatting.UNDERLINE).withStyle((p_210781_) -> {
            return p_210781_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, p_167900_.getAbsolutePath()));
         });
         return new TranslatableComponent("screenshot.success", component);
      } catch (Exception exception) {
         LOGGER.error("Couldn't save image", (Throwable)exception);
         translatablecomponent = new TranslatableComponent("screenshot.failure", exception.getMessage());
      } finally {
         this.player.setXRot(f);
         this.player.setYRot(f1);
         this.player.xRotO = f2;
         this.player.yRotO = f3;
         this.gameRenderer.setRenderBlockOutline(true);
         this.window.setWidth(i);
         this.window.setHeight(j);
         rendertarget.destroyBuffers();
         this.gameRenderer.setPanoramicMode(false);
         this.levelRenderer.graphicsChanged();
         this.getMainRenderTarget().bindWrite(true);
      }

      return translatablecomponent;
   }

   private Component grabHugeScreenshot(File p_167904_, int p_167905_, int p_167906_, int p_167907_, int p_167908_) {
      try {
         ByteBuffer bytebuffer = GlUtil.allocateMemory(p_167905_ * p_167906_ * 3);
         Screenshot screenshot = new Screenshot(p_167904_, p_167907_, p_167908_, p_167906_);
         float f = (float)p_167907_ / (float)p_167905_;
         float f1 = (float)p_167908_ / (float)p_167906_;
         float f2 = f > f1 ? f : f1;

         for(int i = (p_167908_ - 1) / p_167906_ * p_167906_; i >= 0; i -= p_167906_) {
            for(int j = 0; j < p_167907_; j += p_167905_) {
               RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
               float f3 = (float)(p_167907_ - p_167905_) / 2.0F * 2.0F - (float)(j * 2);
               float f4 = (float)(p_167908_ - p_167906_) / 2.0F * 2.0F - (float)(i * 2);
               f3 /= (float)p_167905_;
               f4 /= (float)p_167906_;
               this.gameRenderer.renderZoomed(f2, f3, f4);
               bytebuffer.clear();
               RenderSystem.pixelStore(3333, 1);
               RenderSystem.pixelStore(3317, 1);
               RenderSystem.readPixels(0, 0, p_167905_, p_167906_, 32992, 5121, bytebuffer);
               screenshot.addRegion(bytebuffer, j, i, p_167905_, p_167906_);
            }

            screenshot.saveRow();
         }

         File file1 = screenshot.close();
         GlUtil.freeMemory(bytebuffer);
         Component component = (new TextComponent(file1.getName())).withStyle(ChatFormatting.UNDERLINE).withStyle((p_210721_) -> {
            return p_210721_.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file1.getAbsolutePath()));
         });
         return new TranslatableComponent("screenshot.success", component);
      } catch (Exception exception) {
         LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
         return new TranslatableComponent("screenshot.failure", exception.getMessage());
      }
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public Game getGame() {
      return this.game;
   }

   @Nullable
   public StoringChunkProgressListener getProgressListener() {
      return this.progressListener.get();
   }

   public SplashManager getSplashManager() {
      return this.splashManager;
   }

   @Nullable
   public Overlay getOverlay() {
      return this.overlay;
   }

   public PlayerSocialManager getPlayerSocialManager() {
      return this.playerSocialManager;
   }

   public boolean renderOnThread() {
      return false;
   }

   public Window getWindow() {
      return this.window;
   }

   public RenderBuffers renderBuffers() {
      return this.renderBuffers;
   }

   private static Pack createClientPackAdapter(String p_167934_, Component p_167935_, boolean p_167936_, Supplier<PackResources> p_167937_, PackMetadataSection p_167938_, Pack.Position p_167939_, PackSource p_167940_, boolean hidden) {
      int i = p_167938_.getPackFormat(PackType.CLIENT_RESOURCES);
      Supplier<PackResources> supplier = p_167937_;
      if (i <= 3) {
         supplier = adaptV3(p_167937_);
      }

      if (i <= 4) {
         supplier = adaptV4(supplier);
      }

      return new Pack(p_167934_, p_167935_, p_167936_, supplier, p_167938_, PackType.CLIENT_RESOURCES, p_167939_, p_167940_, hidden);
   }

   private static Supplier<PackResources> adaptV3(Supplier<PackResources> p_91331_) {
      return () -> {
         return new LegacyPackResourcesAdapter(p_91331_.get(), LegacyPackResourcesAdapter.V3);
      };
   }

   private static Supplier<PackResources> adaptV4(Supplier<PackResources> p_91353_) {
      return () -> {
         return new PackResourcesAdapterV4(p_91353_.get());
      };
   }

   public void updateMaxMipLevel(int p_91313_) {
      this.modelManager.updateMaxMipLevel(p_91313_);
   }

   public ItemColors getItemColors() {
      return this.itemColors;
   }

   public SearchRegistry getSearchTreeManager() {
      return this.searchRegistry;
   }

   public EntityModelSet getEntityModels() {
      return this.entityModels;
   }

   public boolean isTextFilteringEnabled() {
      return this.userApiService.properties().flag(UserFlag.PROFANITY_FILTER_ENABLED);
   }

   public void prepareForMultiplayer() {
      this.playerSocialManager.startOnlineMode();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ChatStatus {
      ENABLED(TextComponent.EMPTY) {
         public boolean isChatAllowed(boolean p_168045_) {
            return true;
         }
      },
      DISABLED_BY_OPTIONS((new TranslatableComponent("chat.disabled.options")).withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean p_168051_) {
            return false;
         }
      },
      DISABLED_BY_LAUNCHER((new TranslatableComponent("chat.disabled.launcher")).withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean p_168057_) {
            return p_168057_;
         }
      },
      DISABLED_BY_PROFILE((new TranslatableComponent("chat.disabled.profile")).withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean p_168063_) {
            return p_168063_;
         }
      };

      private final Component message;

      ChatStatus(Component p_168033_) {
         this.message = p_168033_;
      }

      public Component getMessage() {
         return this.message;
      }

      public abstract boolean isChatAllowed(boolean p_168035_);
   }

   @OnlyIn(Dist.CLIENT)
   static enum ExperimentalDialogType {
      NONE,
      CREATE,
      BACKUP;
   }
}
