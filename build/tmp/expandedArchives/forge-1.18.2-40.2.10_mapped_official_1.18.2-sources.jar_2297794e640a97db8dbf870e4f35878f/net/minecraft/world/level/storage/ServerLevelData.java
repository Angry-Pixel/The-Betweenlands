package net.minecraft.world.level.storage;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.TimerQueue;

public interface ServerLevelData extends WritableLevelData {
   String getLevelName();

   void setThundering(boolean p_78623_);

   int getRainTime();

   void setRainTime(int p_78627_);

   void setThunderTime(int p_78626_);

   int getThunderTime();

   default void fillCrashReportCategory(CrashReportCategory p_164976_, LevelHeightAccessor p_164977_) {
      WritableLevelData.super.fillCrashReportCategory(p_164976_, p_164977_);
      p_164976_.setDetail("Level name", this::getLevelName);
      p_164976_.setDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.getAllowCommands());
      });
      p_164976_.setDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering());
      });
   }

   int getClearWeatherTime();

   void setClearWeatherTime(int p_78616_);

   int getWanderingTraderSpawnDelay();

   void setWanderingTraderSpawnDelay(int p_78628_);

   int getWanderingTraderSpawnChance();

   void setWanderingTraderSpawnChance(int p_78629_);

   @Nullable
   UUID getWanderingTraderId();

   void setWanderingTraderId(UUID p_78620_);

   GameType getGameType();

   void setWorldBorder(WorldBorder.Settings p_78619_);

   WorldBorder.Settings getWorldBorder();

   boolean isInitialized();

   void setInitialized(boolean p_78625_);

   boolean getAllowCommands();

   void setGameType(GameType p_78618_);

   TimerQueue<MinecraftServer> getScheduledEvents();

   void setGameTime(long p_78617_);

   void setDayTime(long p_78624_);
}