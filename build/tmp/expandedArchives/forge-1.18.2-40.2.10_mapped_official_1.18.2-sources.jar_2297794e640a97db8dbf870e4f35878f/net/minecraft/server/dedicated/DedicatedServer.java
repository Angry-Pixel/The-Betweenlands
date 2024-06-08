package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.WorldStem;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.network.TextFilterClient;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int CONVERSION_RETRY_DELAY_MS = 5000;
   private static final int CONVERSION_RETRIES = 2;
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   public final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
   @Nullable
   private QueryThreadGs4 queryThreadGs4;
   private final RconConsoleSource rconConsoleSource;
   @Nullable
   private RconThread rconThread;
   private final DedicatedServerSettings settings;
   @Nullable
   private MinecraftServerGui gui;
   @Nullable
   private final TextFilterClient textFilterClient;
   @Nullable
   private final Component resourcePackPrompt;

   public DedicatedServer(Thread p_203713_, LevelStorageSource.LevelStorageAccess p_203714_, PackRepository p_203715_, WorldStem p_203716_, DedicatedServerSettings p_203717_, DataFixer p_203718_, MinecraftSessionService p_203719_, GameProfileRepository p_203720_, GameProfileCache p_203721_, ChunkProgressListenerFactory p_203722_) {
      super(p_203713_, p_203714_, p_203715_, p_203716_, Proxy.NO_PROXY, p_203718_, p_203719_, p_203720_, p_203721_, p_203722_);
      this.settings = p_203717_;
      this.rconConsoleSource = new RconConsoleSource(this);
      this.textFilterClient = TextFilterClient.createFromConfig(p_203717_.getProperties().textFilteringConfig);
      this.resourcePackPrompt = parseResourcePackPrompt(p_203717_);
   }

   public boolean initServer() throws IOException {
      Thread thread = new Thread("Server console handler") {
         public void run() {
            if (net.minecraftforge.server.console.TerminalHandler.handleCommands(DedicatedServer.this)) return;
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            String s1;
            try {
               while(!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (s1 = bufferedreader.readLine()) != null) {
                  DedicatedServer.this.handleConsoleInput(s1, DedicatedServer.this.createCommandSourceStack());
               }
            } catch (IOException ioexception1) {
               DedicatedServer.LOGGER.error("Exception handling console input", (Throwable)ioexception1);
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
      LOGGER.info("Starting minecraft server version {}", (Object)SharedConstants.getCurrentVersion().getName());
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      DedicatedServerProperties dedicatedserverproperties = this.settings.getProperties();
      if (this.isSingleplayer()) {
         this.setLocalIp("127.0.0.1");
      } else {
         this.setUsesAuthentication(dedicatedserverproperties.onlineMode);
         this.setPreventProxyConnections(dedicatedserverproperties.preventProxyConnections);
         this.setLocalIp(dedicatedserverproperties.serverIp);
      }

      this.setPvpAllowed(dedicatedserverproperties.pvp);
      this.setFlightAllowed(dedicatedserverproperties.allowFlight);
      this.setResourcePack(dedicatedserverproperties.resourcePack, this.getPackHash());
      this.setMotd(dedicatedserverproperties.motd);
      super.setPlayerIdleTimeout(dedicatedserverproperties.playerIdleTimeout.get());
      this.setEnforceWhitelist(dedicatedserverproperties.enforceWhitelist);
      this.worldData.setGameType(dedicatedserverproperties.gamemode);
      LOGGER.info("Default game type: {}", (Object)dedicatedserverproperties.gamemode);
      InetAddress inetaddress = null;
      if (!this.getLocalIp().isEmpty()) {
         inetaddress = InetAddress.getByName(this.getLocalIp());
      }

      if (this.getPort() < 0) {
         this.setPort(dedicatedserverproperties.serverPort);
      }

      this.initializeKeyPair();
      LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

      try {
         this.getConnection().startTcpServerListener(inetaddress, this.getPort());
      } catch (IOException ioexception) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: {}", (Object)ioexception.toString());
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.usesAuthentication()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.getProfileCache().save();
      }

      if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         this.setPlayerList(new DedicatedPlayerList(this, this.registryAccess(), this.playerDataStorage));
         long i = Util.getNanos();
         SkullBlockEntity.setup(this.getProfileCache(), this.getSessionService(), this);
         GameProfileCache.setUsesAuthentication(this.usesAuthentication());
         if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
         LOGGER.info("Preparing level \"{}\"", (Object)this.getLevelIdName());
         this.loadLevel();
         long j = Util.getNanos() - i;
         String s = String.format(Locale.ROOT, "%.3fs", (double)j / 1.0E9D);
         LOGGER.info("Done ({})! For help, type \"help\"", (Object)s);
         this.nextTickTime = Util.getMillis(); //Forge: Update server time to prevent watchdog/spaming during long load.
         if (dedicatedserverproperties.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(dedicatedserverproperties.announcePlayerAchievements, this);
         }

         if (dedicatedserverproperties.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
         }

         if (dedicatedserverproperties.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
         }

         if (this.getMaxTickLength() > 0L) {
            Thread thread1 = new Thread(new ServerWatchdog(this));
            thread1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            thread1.setName("Server Watchdog");
            thread1.setDaemon(true);
            thread1.start();
         }

         Items.AIR.fillItemCategory(CreativeModeTab.TAB_SEARCH, NonNullList.create());
         // <3 you Grum for this, saves us ~30 patch files! --^
         if (dedicatedserverproperties.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
            LOGGER.info("JMX monitoring enabled");
         }

         return net.minecraftforge.server.ServerLifecycleHooks.handleServerStarting(this);
      }
   }

   public boolean isSpawningAnimals() {
      return this.getProperties().spawnAnimals && super.isSpawningAnimals();
   }

   public boolean isSpawningMonsters() {
      return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
   }

   public boolean areNpcsEnabled() {
      return this.settings.getProperties().spawnNpcs && super.areNpcsEnabled();
   }

   public String getPackHash() {
      DedicatedServerProperties dedicatedserverproperties = this.settings.getProperties();
      String s;
      if (!dedicatedserverproperties.resourcePackSha1.isEmpty()) {
         s = dedicatedserverproperties.resourcePackSha1;
         if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
         }
      } else if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
         LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
         s = dedicatedserverproperties.resourcePackHash;
      } else {
         s = "";
      }

      if (!s.isEmpty() && !SHA1.matcher(s).matches()) {
         LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
      }

      if (!dedicatedserverproperties.resourcePack.isEmpty() && s.isEmpty()) {
         LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
      }

      return s;
   }

   public DedicatedServerProperties getProperties() {
      return this.settings.getProperties();
   }

   public void forceDifficulty() {
      this.setDifficulty(this.getProperties().difficulty, true);
   }

   public boolean isHardcore() {
      return this.getProperties().hardcore;
   }

   public SystemReport fillServerSystemReport(SystemReport p_142870_) {
      p_142870_.setDetail("Is Modded", () -> {
         return this.getModdedStatus().fullDescription();
      });
      p_142870_.setDetail("Type", () -> {
         return "Dedicated Server (map_server.txt)";
      });
      return p_142870_;
   }

   public void dumpServerProperties(Path p_142872_) throws IOException {
      DedicatedServerProperties dedicatedserverproperties = this.getProperties();
      Writer writer = Files.newBufferedWriter(p_142872_);

      try {
         writer.write(String.format("sync-chunk-writes=%s%n", dedicatedserverproperties.syncChunkWrites));
         writer.write(String.format("gamemode=%s%n", dedicatedserverproperties.gamemode));
         writer.write(String.format("spawn-monsters=%s%n", dedicatedserverproperties.spawnMonsters));
         writer.write(String.format("entity-broadcast-range-percentage=%d%n", dedicatedserverproperties.entityBroadcastRangePercentage));
         writer.write(String.format("max-world-size=%d%n", dedicatedserverproperties.maxWorldSize));
         writer.write(String.format("spawn-npcs=%s%n", dedicatedserverproperties.spawnNpcs));
         writer.write(String.format("view-distance=%d%n", dedicatedserverproperties.viewDistance));
         writer.write(String.format("simulation-distance=%d%n", dedicatedserverproperties.simulationDistance));
         writer.write(String.format("spawn-animals=%s%n", dedicatedserverproperties.spawnAnimals));
         writer.write(String.format("generate-structures=%s%n", dedicatedserverproperties.getWorldGenSettings(this.registryAccess()).generateFeatures()));
         writer.write(String.format("use-native=%s%n", dedicatedserverproperties.useNativeTransport));
         writer.write(String.format("rate-limit=%d%n", dedicatedserverproperties.rateLimitPacketsPerSecond));
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

   public void onServerExit() {
      if (this.textFilterClient != null) {
         this.textFilterClient.close();
      }

      if (this.gui != null) {
         this.gui.close();
      }

      if (this.rconThread != null) {
         this.rconThread.stop();
      }

      if (this.queryThreadGs4 != null) {
         this.queryThreadGs4.stop();
      }

   }

   public void tickChildren(BooleanSupplier p_139661_) {
      super.tickChildren(p_139661_);
      this.handleConsoleInputs();
   }

   public boolean isNetherEnabled() {
      return this.getProperties().allowNether;
   }

   public void handleConsoleInput(String p_139646_, CommandSourceStack p_139647_) {
      this.consoleInput.add(new ConsoleInput(p_139646_, p_139647_));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         ConsoleInput consoleinput = this.consoleInput.remove(0);
         this.getCommands().performCommand(consoleinput.source, consoleinput.msg);
      }

   }

   public boolean isDedicatedServer() {
      return true;
   }

   public int getRateLimitPacketsPerSecond() {
      return this.getProperties().rateLimitPacketsPerSecond;
   }

   public boolean isEpollEnabled() {
      return this.getProperties().useNativeTransport;
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   public boolean isPublished() {
      return true;
   }

   public String getServerIp() {
      return this.getLocalIp();
   }

   public int getServerPort() {
      return this.getPort();
   }

   public String getServerName() {
      return this.getMotd();
   }

   public void showGui() {
      if (this.gui == null) {
         this.gui = MinecraftServerGui.showFrameFor(this);
      }

   }

   public boolean hasGui() {
      return this.gui != null;
   }

   public boolean isCommandBlockEnabled() {
      return this.getProperties().enableCommandBlock;
   }

   public int getSpawnProtectionRadius() {
      return this.getProperties().spawnProtection;
   }

   public boolean isUnderSpawnProtection(ServerLevel p_139630_, BlockPos p_139631_, Player p_139632_) {
      if (p_139630_.dimension() != Level.OVERWORLD) {
         return false;
      } else if (this.getPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerList().isOp(p_139632_.getGameProfile())) {
         return false;
      } else if (this.getSpawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos blockpos = p_139630_.getSharedSpawnPos();
         int i = Mth.abs(p_139631_.getX() - blockpos.getX());
         int j = Mth.abs(p_139631_.getZ() - blockpos.getZ());
         int k = Math.max(i, j);
         return k <= this.getSpawnProtectionRadius();
      }
   }

   public boolean repliesToStatus() {
      return this.getProperties().enableStatus;
   }

   public boolean hidesOnlinePlayers() {
      return this.getProperties().hideOnlinePlayers;
   }

   public int getOperatorUserPermissionLevel() {
      return this.getProperties().opPermissionLevel;
   }

   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   public void setPlayerIdleTimeout(int p_139676_) {
      super.setPlayerIdleTimeout(p_139676_);
      this.settings.update((p_139628_) -> {
         return p_139628_.playerIdleTimeout.update(this.registryAccess(), p_139676_);
      });
   }

   public boolean shouldRconBroadcast() {
      return this.getProperties().broadcastRconToOps;
   }

   public boolean shouldInformAdmins() {
      return this.getProperties().broadcastConsoleToOps;
   }

   public int getAbsoluteMaxWorldSize() {
      return this.getProperties().maxWorldSize;
   }

   public int getCompressionThreshold() {
      return this.getProperties().networkCompressionThreshold;
   }

   protected boolean convertOldUsers() {
      boolean flag = false;

      for(int i = 0; !flag && i <= 2; ++i) {
         if (i > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag = OldUsersConverter.convertUserBanlist(this);
      }

      boolean flag1 = false;

      for(int j = 0; !flag1 && j <= 2; ++j) {
         if (j > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag1 = OldUsersConverter.convertIpBanlist(this);
      }

      boolean flag2 = false;

      for(int k = 0; !flag2 && k <= 2; ++k) {
         if (k > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.waitForRetry();
         }

         flag2 = OldUsersConverter.convertOpsList(this);
      }

      boolean flag3 = false;

      for(int l = 0; !flag3 && l <= 2; ++l) {
         if (l > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag3 = OldUsersConverter.convertWhiteList(this);
      }

      boolean flag4 = false;

      for(int i1 = 0; !flag4 && i1 <= 2; ++i1) {
         if (i1 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.waitForRetry();
         }

         flag4 = OldUsersConverter.convertPlayers(this);
      }

      return flag || flag1 || flag2 || flag3 || flag4;
   }

   private void waitForRetry() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException interruptedexception) {
      }
   }

   public long getMaxTickLength() {
      return this.getProperties().maxTickTime;
   }

   public String getPluginNames() {
      return "";
   }

   public String runCommand(String p_139644_) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> {
         this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), p_139644_);
      });
      return this.rconConsoleSource.getCommandResponse();
   }

   public void storeUsingWhiteList(boolean p_139689_) {
      this.settings.update((p_139650_) -> {
         return p_139650_.whiteList.update(this.registryAccess(), p_139689_);
      });
   }

   public void stopServer() {
      super.stopServer();
      Util.shutdownExecutors();
      SkullBlockEntity.clear();
   }

   public boolean isSingleplayerOwner(GameProfile p_139642_) {
      return false;
   }

   @Override //Forge: Enable formated text for colors in console.
   public void sendMessage(net.minecraft.network.chat.Component message, java.util.UUID p_108776_) {
      LOGGER.info(message.getString());
   }

   public int getScaledTrackingDistance(int p_139659_) {
      return this.getProperties().entityBroadcastRangePercentage * p_139659_ / 100;
   }

   public String getLevelIdName() {
      return this.storageSource.getLevelId();
   }

   public boolean forceSynchronousWrites() {
      return this.settings.getProperties().syncChunkWrites;
   }

   public TextFilter createTextFilterForPlayer(ServerPlayer p_139634_) {
      return this.textFilterClient != null ? this.textFilterClient.createContext(p_139634_.getGameProfile()) : TextFilter.DUMMY;
   }

   public boolean isResourcePackRequired() {
      return this.settings.getProperties().requireResourcePack;
   }

   @Nullable
   public GameType getForcedGameType() {
      return this.settings.getProperties().forceGameMode ? this.worldData.getGameType() : null;
   }

   @Nullable
   private static Component parseResourcePackPrompt(DedicatedServerSettings p_142868_) {
      String s = p_142868_.getProperties().resourcePackPrompt;
      if (!Strings.isNullOrEmpty(s)) {
         try {
            return Component.Serializer.fromJson(s);
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", s, exception);
         }
      }

      return null;
   }

   @Nullable
   public Component getResourcePackPrompt() {
      return this.resourcePackPrompt;
   }
}
