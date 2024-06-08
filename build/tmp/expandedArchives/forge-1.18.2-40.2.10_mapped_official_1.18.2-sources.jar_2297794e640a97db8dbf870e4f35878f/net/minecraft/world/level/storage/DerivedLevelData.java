package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.TimerQueue;

public class DerivedLevelData implements ServerLevelData {
   private final WorldData worldData;
   private final ServerLevelData wrapped;

   public DerivedLevelData(WorldData p_78079_, ServerLevelData p_78080_) {
      this.worldData = p_78079_;
      this.wrapped = p_78080_;
   }

   public int getXSpawn() {
      return this.wrapped.getXSpawn();
   }

   public int getYSpawn() {
      return this.wrapped.getYSpawn();
   }

   public int getZSpawn() {
      return this.wrapped.getZSpawn();
   }

   public float getSpawnAngle() {
      return this.wrapped.getSpawnAngle();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   public String getLevelName() {
      return this.worldData.getLevelName();
   }

   public int getClearWeatherTime() {
      return this.wrapped.getClearWeatherTime();
   }

   public void setClearWeatherTime(int p_78085_) {
   }

   public boolean isThundering() {
      return this.wrapped.isThundering();
   }

   public int getThunderTime() {
      return this.wrapped.getThunderTime();
   }

   public boolean isRaining() {
      return this.wrapped.isRaining();
   }

   public int getRainTime() {
      return this.wrapped.getRainTime();
   }

   public GameType getGameType() {
      return this.worldData.getGameType();
   }

   public void setXSpawn(int p_78103_) {
   }

   public void setYSpawn(int p_78110_) {
   }

   public void setZSpawn(int p_78115_) {
   }

   public void setSpawnAngle(float p_78083_) {
   }

   public void setGameTime(long p_78087_) {
   }

   public void setDayTime(long p_78105_) {
   }

   public void setSpawn(BlockPos p_78093_, float p_78094_) {
   }

   public void setThundering(boolean p_78100_) {
   }

   public void setThunderTime(int p_78118_) {
   }

   public void setRaining(boolean p_78107_) {
   }

   public void setRainTime(int p_78121_) {
   }

   public void setGameType(GameType p_78089_) {
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public boolean getAllowCommands() {
      return this.worldData.getAllowCommands();
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(boolean p_78112_) {
   }

   public GameRules getGameRules() {
      return this.worldData.getGameRules();
   }

   public WorldBorder.Settings getWorldBorder() {
      return this.wrapped.getWorldBorder();
   }

   public void setWorldBorder(WorldBorder.Settings p_78091_) {
   }

   public Difficulty getDifficulty() {
      return this.worldData.getDifficulty();
   }

   public boolean isDifficultyLocked() {
      return this.worldData.isDifficultyLocked();
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   public int getWanderingTraderSpawnDelay() {
      return 0;
   }

   public void setWanderingTraderSpawnDelay(int p_78124_) {
   }

   public int getWanderingTraderSpawnChance() {
      return 0;
   }

   public void setWanderingTraderSpawnChance(int p_78127_) {
   }

   public UUID getWanderingTraderId() {
      return null;
   }

   public void setWanderingTraderId(UUID p_78096_) {
   }

   public void fillCrashReportCategory(CrashReportCategory p_164852_, LevelHeightAccessor p_164853_) {
      p_164852_.setDetail("Derived", true);
      this.wrapped.fillCrashReportCategory(p_164852_, p_164853_);
   }
}