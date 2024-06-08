package net.minecraft.server.dedicated;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String resourcePack = this.get("resource-pack", "");
   public final boolean requireResourcePack = this.get("require-resource-pack", false);
   public final String resourcePackPrompt = this.get("resource-pack-prompt", "");
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final Difficulty difficulty = this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
   public final GameType gamemode = this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
   public final String levelName = this.get("level-name", "world");
   public final int serverPort = this.get("server-port", 25565);
   @Nullable
   public final Boolean announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
   public final boolean enableQuery = this.get("enable-query", false);
   public final int queryPort = this.get("query.port", 25565);
   public final boolean enableRcon = this.get("enable-rcon", false);
   public final int rconPort = this.get("rcon.port", 25575);
   public final String rconPassword = this.get("rcon.password", "");
   @Nullable
   public final String resourcePackHash = this.getLegacyString("resource-pack-hash");
   public final String resourcePackSha1 = this.get("resource-pack-sha1", "");
   public final boolean hardcore = this.get("hardcore", false);
   public final boolean allowNether = this.get("allow-nether", true);
   public final boolean spawnMonsters = this.get("spawn-monsters", true);
   public final boolean useNativeTransport = this.get("use-native-transport", true);
   public final boolean enableCommandBlock = this.get("enable-command-block", false);
   public final int spawnProtection = this.get("spawn-protection", 16);
   public final int opPermissionLevel = this.get("op-permission-level", 4);
   public final int functionPermissionLevel = this.get("function-permission-level", 2);
   public final long maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
   public final int rateLimitPacketsPerSecond = this.get("rate-limit", 0);
   public final int viewDistance = this.get("view-distance", 10);
   public final int simulationDistance = this.get("simulation-distance", 10);
   public final int maxPlayers = this.get("max-players", 20);
   public final int networkCompressionThreshold = this.get("network-compression-threshold", 256);
   public final boolean broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
   public final boolean broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
   public final int maxWorldSize = this.get("max-world-size", (p_139771_) -> {
      return Mth.clamp(p_139771_, 1, 29999984);
   }, 29999984);
   public final boolean syncChunkWrites = this.get("sync-chunk-writes", true);
   public final boolean enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
   public final boolean enableStatus = this.get("enable-status", true);
   public final boolean hideOnlinePlayers = this.get("hide-online-players", false);
   public final int entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (p_139769_) -> {
      return Mth.clamp(p_139769_, 10, 1000);
   }, 100);
   public final String textFilteringConfig = this.get("text-filtering-config", "");
   public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
   public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList = this.getMutable("white-list", false);
   private final DedicatedServerProperties.WorldGenProperties worldGenProperties = new DedicatedServerProperties.WorldGenProperties(this.get("level-seed", ""), this.get("generator-settings", (p_211543_) -> {
      return GsonHelper.parse(!p_211543_.isEmpty() ? p_211543_ : "{}");
   }, new JsonObject()), this.get("generate-structures", true), this.get("level-type", (p_211541_) -> {
      return p_211541_.toLowerCase(Locale.ROOT);
   }, "default"));
   @Nullable
   private WorldGenSettings worldGenSettings;

   public DedicatedServerProperties(Properties p_180926_) {
      super(p_180926_);
   }

   public static DedicatedServerProperties fromFile(Path p_180930_) {
      return new DedicatedServerProperties(loadFromFile(p_180930_));
   }

   protected DedicatedServerProperties reload(RegistryAccess p_139761_, Properties p_139762_) {
      DedicatedServerProperties dedicatedserverproperties = new DedicatedServerProperties(p_139762_);
      dedicatedserverproperties.getWorldGenSettings(p_139761_);
      return dedicatedserverproperties;
   }

   public WorldGenSettings getWorldGenSettings(RegistryAccess p_180928_) {
      if (this.worldGenSettings == null) {
         this.worldGenSettings = WorldGenSettings.create(p_180928_, this.worldGenProperties);
      }

      return this.worldGenSettings;
   }

   public static record WorldGenProperties(String levelSeed, JsonObject generatorSettings, boolean generateStructures, String levelType) {
   }
}