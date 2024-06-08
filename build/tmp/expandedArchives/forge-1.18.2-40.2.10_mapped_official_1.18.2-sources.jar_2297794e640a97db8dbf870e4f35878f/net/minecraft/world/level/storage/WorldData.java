package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public interface WorldData {
   int ANVIL_VERSION_ID = 19133;
   int MCREGION_VERSION_ID = 19132;

   DataPackConfig getDataPackConfig();

   void setDataPackConfig(DataPackConfig p_78634_);

   boolean wasModded();

   Set<String> getKnownServerBrands();

   void setModdedInfo(String p_78638_, boolean p_78639_);

   default void fillCrashReportCategory(CrashReportCategory p_78640_) {
      p_78640_.setDetail("Known server brands", () -> {
         return String.join(", ", this.getKnownServerBrands());
      });
      p_78640_.setDetail("Level was modded", () -> {
         return Boolean.toString(this.wasModded());
      });
      p_78640_.setDetail("Level storage version", () -> {
         int i = this.getVersion();
         return String.format("0x%05X - %s", i, this.getStorageVersionName(i));
      });
   }

   default String getStorageVersionName(int p_78647_) {
      switch(p_78647_) {
      case 19132:
         return "McRegion";
      case 19133:
         return "Anvil";
      default:
         return "Unknown?";
      }
   }

   @Nullable
   CompoundTag getCustomBossEvents();

   void setCustomBossEvents(@Nullable CompoundTag p_78643_);

   ServerLevelData overworldData();

   LevelSettings getLevelSettings();

   CompoundTag createTag(RegistryAccess p_78636_, @Nullable CompoundTag p_78637_);

   boolean isHardcore();

   int getVersion();

   String getLevelName();

   GameType getGameType();

   void setGameType(GameType p_78635_);

   boolean getAllowCommands();

   Difficulty getDifficulty();

   void setDifficulty(Difficulty p_78633_);

   boolean isDifficultyLocked();

   void setDifficultyLocked(boolean p_78645_);

   GameRules getGameRules();

   @Nullable
   CompoundTag getLoadedPlayerTag();

   CompoundTag endDragonFightData();

   void setEndDragonFightData(CompoundTag p_78641_);

   WorldGenSettings worldGenSettings();

   Lifecycle worldGenSettingsLifecycle();
}