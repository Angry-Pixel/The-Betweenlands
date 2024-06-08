package net.minecraft.world.level.storage;

import net.minecraft.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelHeightAccessor;

public interface LevelData {
   int getXSpawn();

   int getYSpawn();

   int getZSpawn();

   float getSpawnAngle();

   long getGameTime();

   long getDayTime();

   boolean isThundering();

   boolean isRaining();

   void setRaining(boolean p_78171_);

   boolean isHardcore();

   GameRules getGameRules();

   Difficulty getDifficulty();

   boolean isDifficultyLocked();

   default void fillCrashReportCategory(CrashReportCategory p_164873_, LevelHeightAccessor p_164874_) {
      p_164873_.setDetail("Level spawn location", () -> {
         return CrashReportCategory.formatLocation(p_164874_, this.getXSpawn(), this.getYSpawn(), this.getZSpawn());
      });
      p_164873_.setDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.getGameTime(), this.getDayTime());
      });
   }
}