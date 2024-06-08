package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SerializableUUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static final String PLAYER = "Player";
   protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
   private LevelSettings settings;
   private final WorldGenSettings worldGenSettings;
   private final Lifecycle worldGenSettingsLifecycle;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private float spawnAngle;
   private long gameTime;
   private long dayTime;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   @Nullable
   private CompoundTag loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Settings worldBorder;
   private CompoundTag endDragonFightData;
   @Nullable
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final TimerQueue<MinecraftServer> scheduledEvents;
   private boolean confirmedExperimentalWarning = false;

   private PrimaryLevelData(@Nullable DataFixer p_164942_, int p_164943_, @Nullable CompoundTag p_164944_, boolean p_164945_, int p_164946_, int p_164947_, int p_164948_, float p_164949_, long p_164950_, long p_164951_, int p_164952_, int p_164953_, int p_164954_, boolean p_164955_, int p_164956_, boolean p_164957_, boolean p_164958_, boolean p_164959_, WorldBorder.Settings p_164960_, int p_164961_, int p_164962_, @Nullable UUID p_164963_, Set<String> p_164964_, TimerQueue<MinecraftServer> p_164965_, @Nullable CompoundTag p_164966_, CompoundTag p_164967_, LevelSettings p_164968_, WorldGenSettings p_164969_, Lifecycle p_164970_) {
      this.fixerUpper = p_164942_;
      this.wasModded = p_164945_;
      this.xSpawn = p_164946_;
      this.ySpawn = p_164947_;
      this.zSpawn = p_164948_;
      this.spawnAngle = p_164949_;
      this.gameTime = p_164950_;
      this.dayTime = p_164951_;
      this.version = p_164952_;
      this.clearWeatherTime = p_164953_;
      this.rainTime = p_164954_;
      this.raining = p_164955_;
      this.thunderTime = p_164956_;
      this.thundering = p_164957_;
      this.initialized = p_164958_;
      this.difficultyLocked = p_164959_;
      this.worldBorder = p_164960_;
      this.wanderingTraderSpawnDelay = p_164961_;
      this.wanderingTraderSpawnChance = p_164962_;
      this.wanderingTraderId = p_164963_;
      this.knownServerBrands = p_164964_;
      this.loadedPlayerTag = p_164944_;
      this.playerDataVersion = p_164943_;
      this.scheduledEvents = p_164965_;
      this.customBossEvents = p_164966_;
      this.endDragonFightData = p_164967_;
      this.settings = p_164968_.withLifecycle(p_164970_);
      if (p_164970_ == Lifecycle.stable()) //Reset to unconfirmed if the lifecycle goes back to stable
         this.confirmedExperimentalWarning = false;
      this.worldGenSettings = p_164969_;
      this.worldGenSettingsLifecycle = p_164970_;
   }

   public PrimaryLevelData(LevelSettings p_78470_, WorldGenSettings p_78471_, Lifecycle p_78472_) {
      this((DataFixer)null, SharedConstants.getCurrentVersion().getWorldVersion(), (CompoundTag)null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS), (CompoundTag)null, new CompoundTag(), p_78470_.copy(), p_78471_, p_78472_);
   }

   public static PrimaryLevelData parse(Dynamic<Tag> p_78531_, DataFixer p_78532_, int p_78533_, @Nullable CompoundTag p_78534_, LevelSettings p_78535_, LevelVersion p_78536_, WorldGenSettings p_78537_, Lifecycle p_78538_) {
      long i = p_78531_.get("Time").asLong(0L);
      CompoundTag compoundtag = (CompoundTag)p_78531_.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> {
         return p_78531_.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
      });
      return new PrimaryLevelData(p_78532_, p_78533_, p_78534_, p_78531_.get("WasModded").asBoolean(false), p_78531_.get("SpawnX").asInt(0), p_78531_.get("SpawnY").asInt(0), p_78531_.get("SpawnZ").asInt(0), p_78531_.get("SpawnAngle").asFloat(0.0F), i, p_78531_.get("DayTime").asLong(i), p_78536_.levelDataVersion(), p_78531_.get("clearWeatherTime").asInt(0), p_78531_.get("rainTime").asInt(0), p_78531_.get("raining").asBoolean(false), p_78531_.get("thunderTime").asInt(0), p_78531_.get("thundering").asBoolean(false), p_78531_.get("initialized").asBoolean(true), p_78531_.get("DifficultyLocked").asBoolean(false), WorldBorder.Settings.read(p_78531_, WorldBorder.DEFAULT_SETTINGS), p_78531_.get("WanderingTraderSpawnDelay").asInt(0), p_78531_.get("WanderingTraderSpawnChance").asInt(0), p_78531_.get("WanderingTraderId").read(SerializableUUID.CODEC).result().orElse((UUID)null), p_78531_.get("ServerBrands").asStream().flatMap((p_78529_) -> {
         return p_78529_.asString().result().stream();
      }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new TimerQueue<>(TimerCallbacks.SERVER_CALLBACKS, p_78531_.get("ScheduledEvents").asStream()), (CompoundTag)p_78531_.get("CustomBossEvents").orElseEmptyMap().getValue(), compoundtag, p_78535_, p_78537_, p_78538_).withConfirmedWarning(p_78538_ != Lifecycle.stable() && p_78531_.get("confirmedExperimentalSettings").asBoolean(false));
   }

   public CompoundTag createTag(RegistryAccess p_78543_, @Nullable CompoundTag p_78544_) {
      this.updatePlayerTag();
      if (p_78544_ == null) {
         p_78544_ = this.loadedPlayerTag;
      }

      CompoundTag compoundtag = new CompoundTag();
      this.setTagData(p_78543_, compoundtag, p_78544_);
      return compoundtag;
   }

   private void setTagData(RegistryAccess p_78546_, CompoundTag p_78547_, @Nullable CompoundTag p_78548_) {
      ListTag listtag = new ListTag();
      this.knownServerBrands.stream().map(StringTag::valueOf).forEach(listtag::add);
      p_78547_.put("ServerBrands", listtag);
      p_78547_.putBoolean("WasModded", this.wasModded);
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("Name", SharedConstants.getCurrentVersion().getName());
      compoundtag.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      compoundtag.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      compoundtag.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
      p_78547_.put("Version", compoundtag);
      p_78547_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_78546_);
      WorldGenSettings.CODEC.encodeStart(dynamicops, this.worldGenSettings).resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).ifPresent((p_78574_) -> {
         p_78547_.put("WorldGenSettings", p_78574_);
      });
      p_78547_.putInt("GameType", this.settings.gameType().getId());
      p_78547_.putInt("SpawnX", this.xSpawn);
      p_78547_.putInt("SpawnY", this.ySpawn);
      p_78547_.putInt("SpawnZ", this.zSpawn);
      p_78547_.putFloat("SpawnAngle", this.spawnAngle);
      p_78547_.putLong("Time", this.gameTime);
      p_78547_.putLong("DayTime", this.dayTime);
      p_78547_.putLong("LastPlayed", Util.getEpochMillis());
      p_78547_.putString("LevelName", this.settings.levelName());
      p_78547_.putInt("version", 19133);
      p_78547_.putInt("clearWeatherTime", this.clearWeatherTime);
      p_78547_.putInt("rainTime", this.rainTime);
      p_78547_.putBoolean("raining", this.raining);
      p_78547_.putInt("thunderTime", this.thunderTime);
      p_78547_.putBoolean("thundering", this.thundering);
      p_78547_.putBoolean("hardcore", this.settings.hardcore());
      p_78547_.putBoolean("allowCommands", this.settings.allowCommands());
      p_78547_.putBoolean("initialized", this.initialized);
      this.worldBorder.write(p_78547_);
      p_78547_.putByte("Difficulty", (byte)this.settings.difficulty().getId());
      p_78547_.putBoolean("DifficultyLocked", this.difficultyLocked);
      p_78547_.put("GameRules", this.settings.gameRules().createTag());
      p_78547_.put("DragonFight", this.endDragonFightData);
      if (p_78548_ != null) {
         p_78547_.put("Player", p_78548_);
      }

      DataPackConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataPackConfig()).result().ifPresent((p_78560_) -> {
         p_78547_.put("DataPacks", p_78560_);
      });
      if (this.customBossEvents != null) {
         p_78547_.put("CustomBossEvents", this.customBossEvents);
      }

      p_78547_.put("ScheduledEvents", this.scheduledEvents.store());
      p_78547_.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      p_78547_.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         p_78547_.putUUID("WanderingTraderId", this.wanderingTraderId);
      }

      p_78547_.putString("forgeLifecycle", net.minecraftforge.common.ForgeHooks.encodeLifecycle(this.settings.getLifecycle()));
      p_78547_.putBoolean("confirmedExperimentalSettings", this.confirmedExperimentalWarning);
   }

   public int getXSpawn() {
      return this.xSpawn;
   }

   public int getYSpawn() {
      return this.ySpawn;
   }

   public int getZSpawn() {
      return this.zSpawn;
   }

   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   private void updatePlayerTag() {
      if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
               throw (NullPointerException)Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }

            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   public CompoundTag getLoadedPlayerTag() {
      this.updatePlayerTag();
      return this.loadedPlayerTag;
   }

   public void setXSpawn(int p_78565_) {
      this.xSpawn = p_78565_;
   }

   public void setYSpawn(int p_78579_) {
      this.ySpawn = p_78579_;
   }

   public void setZSpawn(int p_78584_) {
      this.zSpawn = p_78584_;
   }

   public void setSpawnAngle(float p_78515_) {
      this.spawnAngle = p_78515_;
   }

   public void setGameTime(long p_78519_) {
      this.gameTime = p_78519_;
   }

   public void setDayTime(long p_78567_) {
      this.dayTime = p_78567_;
   }

   public void setSpawn(BlockPos p_78540_, float p_78541_) {
      this.xSpawn = p_78540_.getX();
      this.ySpawn = p_78540_.getY();
      this.zSpawn = p_78540_.getZ();
      this.spawnAngle = p_78541_;
   }

   public String getLevelName() {
      return this.settings.levelName();
   }

   public int getVersion() {
      return this.version;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int p_78517_) {
      this.clearWeatherTime = p_78517_;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean p_78562_) {
      this.thundering = p_78562_;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int p_78589_) {
      this.thunderTime = p_78589_;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean p_78576_) {
      this.raining = p_78576_;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int p_78592_) {
      this.rainTime = p_78592_;
   }

   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(GameType p_78525_) {
      this.settings = this.settings.withGameType(p_78525_);
   }

   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   public boolean getAllowCommands() {
      return this.settings.allowCommands();
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean p_78581_) {
      this.initialized = p_78581_;
   }

   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   public WorldBorder.Settings getWorldBorder() {
      return this.worldBorder;
   }

   public void setWorldBorder(WorldBorder.Settings p_78527_) {
      this.worldBorder = p_78527_;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   public void setDifficulty(Difficulty p_78521_) {
      this.settings = this.settings.withDifficulty(p_78521_);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean p_78586_) {
      this.difficultyLocked = p_78586_;
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(CrashReportCategory p_164972_, LevelHeightAccessor p_164973_) {
      ServerLevelData.super.fillCrashReportCategory(p_164972_, p_164973_);
      WorldData.super.fillCrashReportCategory(p_164972_);
   }

   public WorldGenSettings worldGenSettings() {
      return this.worldGenSettings;
   }

   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   public CompoundTag endDragonFightData() {
      return this.endDragonFightData;
   }

   public void setEndDragonFightData(CompoundTag p_78557_) {
      this.endDragonFightData = p_78557_;
   }

   public DataPackConfig getDataPackConfig() {
      return this.settings.getDataPackConfig();
   }

   public void setDataPackConfig(DataPackConfig p_78523_) {
      this.settings = this.settings.withDataPackConfig(p_78523_);
   }

   @Nullable
   public CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundTag p_78571_) {
      this.customBossEvents = p_78571_;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int p_78595_) {
      this.wanderingTraderSpawnDelay = p_78595_;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int p_78598_) {
      this.wanderingTraderSpawnChance = p_78598_;
   }

   @Nullable
   public UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   public void setWanderingTraderId(UUID p_78553_) {
      this.wanderingTraderId = p_78553_;
   }

   public void setModdedInfo(String p_78550_, boolean p_78551_) {
      this.knownServerBrands.add(p_78550_);
      this.wasModded |= p_78551_;
   }

   public boolean wasModded() {
      return this.wasModded;
   }

   public Set<String> getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   public ServerLevelData overworldData() {
      return this;
   }

   public LevelSettings getLevelSettings() {
      return this.settings.copy();
   }
   //FORGE
   public boolean hasConfirmedExperimentalWarning() {
      return this.confirmedExperimentalWarning;
   }
   public PrimaryLevelData withConfirmedWarning(boolean confirmedWarning) { //Builder-like to not patch ctor
      this.confirmedExperimentalWarning = confirmedWarning;
      return this;
   }
}
