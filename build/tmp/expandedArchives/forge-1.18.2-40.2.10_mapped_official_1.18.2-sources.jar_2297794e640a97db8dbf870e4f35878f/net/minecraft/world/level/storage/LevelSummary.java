package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary implements Comparable<LevelSummary> {
   private final LevelSettings settings;
   private final LevelVersion levelVersion;
   private final String levelId;
   private final boolean requiresManualConversion;
   private final boolean locked;
   private final File icon;
   @Nullable
   private Component info;

   public LevelSummary(LevelSettings p_78352_, LevelVersion p_78353_, String p_78354_, boolean p_78355_, boolean p_78356_, File p_78357_) {
      this.settings = p_78352_;
      this.levelVersion = p_78353_;
      this.levelId = p_78354_;
      this.locked = p_78356_;
      this.icon = p_78357_;
      this.requiresManualConversion = p_78355_;
   }

   public String getLevelId() {
      return this.levelId;
   }

   public String getLevelName() {
      return StringUtils.isEmpty(this.settings.levelName()) ? this.levelId : this.settings.levelName();
   }

   public File getIcon() {
      return this.icon;
   }

   public boolean requiresManualConversion() {
      return this.requiresManualConversion;
   }

   public long getLastPlayed() {
      return this.levelVersion.lastPlayed();
   }

   public int compareTo(LevelSummary p_78360_) {
      if (this.levelVersion.lastPlayed() < p_78360_.levelVersion.lastPlayed()) {
         return 1;
      } else {
         return this.levelVersion.lastPlayed() > p_78360_.levelVersion.lastPlayed() ? -1 : this.levelId.compareTo(p_78360_.levelId);
      }
   }

   public LevelSettings getSettings() {
      return this.settings;
   }

   public GameType getGameMode() {
      return this.settings.gameType();
   }

   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   public boolean hasCheats() {
      return this.settings.allowCommands();
   }

   public MutableComponent getWorldVersionName() {
      return (MutableComponent)(StringUtil.isNullOrEmpty(this.levelVersion.minecraftVersionName()) ? new TranslatableComponent("selectWorld.versionUnknown") : new TextComponent(this.levelVersion.minecraftVersionName()));
   }

   public LevelVersion levelVersion() {
      return this.levelVersion;
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.backupStatus().shouldBackup();
   }

   public boolean askToOpenWorld() {
      return this.levelVersion.minecraftVersion().getVersion() > SharedConstants.getCurrentVersion().getDataVersion().getVersion();
   }

   public LevelSummary.BackupStatus backupStatus() {
      WorldVersion worldversion = SharedConstants.getCurrentVersion();
      int i = worldversion.getDataVersion().getVersion();
      int j = this.levelVersion.minecraftVersion().getVersion();
      if (!worldversion.isStable() && j < i) {
         return LevelSummary.BackupStatus.UPGRADE_TO_SNAPSHOT;
      } else {
         return j > i ? LevelSummary.BackupStatus.DOWNGRADE : LevelSummary.BackupStatus.NONE;
      }
   }

   public boolean isLocked() {
      return this.locked;
   }

   public boolean isDisabled() {
      if (!this.isLocked() && !this.requiresManualConversion()) {
         return !this.isCompatible();
      } else {
         return true;
      }
   }

   public boolean isCompatible() {
      return SharedConstants.getCurrentVersion().getDataVersion().isCompatible(this.levelVersion.minecraftVersion());
   }

   public Component getInfo() {
      if (this.info == null) {
         this.info = this.createInfo();
      }

      return this.info;
   }

   private Component createInfo() {
      if (this.isLocked()) {
         return (new TranslatableComponent("selectWorld.locked")).withStyle(ChatFormatting.RED);
      } else if (this.requiresManualConversion()) {
         return (new TranslatableComponent("selectWorld.conversion")).withStyle(ChatFormatting.RED);
      } else if (!this.isCompatible()) {
         return (new TranslatableComponent("selectWorld.incompatible_series")).withStyle(ChatFormatting.RED);
      } else {
         MutableComponent mutablecomponent = (MutableComponent)(this.isHardcore() ? (new TextComponent("")).append((new TranslatableComponent("gameMode.hardcore")).withStyle(ChatFormatting.DARK_RED)) : new TranslatableComponent("gameMode." + this.getGameMode().getName()));
         if (this.hasCheats()) {
            mutablecomponent.append(", ").append(new TranslatableComponent("selectWorld.cheats"));
         }

         MutableComponent mutablecomponent1 = this.getWorldVersionName();
         MutableComponent mutablecomponent2 = (new TextComponent(", ")).append(new TranslatableComponent("selectWorld.version")).append(" ");
         if (this.markVersionInList()) {
            mutablecomponent2.append(mutablecomponent1.withStyle(this.askToOpenWorld() ? ChatFormatting.RED : ChatFormatting.ITALIC));
         } else {
            mutablecomponent2.append(mutablecomponent1);
         }

         mutablecomponent.append(mutablecomponent2);
         return mutablecomponent;
      }
   }

   public static enum BackupStatus {
      NONE(false, false, ""),
      DOWNGRADE(true, true, "downgrade"),
      UPGRADE_TO_SNAPSHOT(true, false, "snapshot");

      private final boolean shouldBackup;
      private final boolean severe;
      private final String translationKey;

      private BackupStatus(boolean p_164928_, boolean p_164929_, String p_164930_) {
         this.shouldBackup = p_164928_;
         this.severe = p_164929_;
         this.translationKey = p_164930_;
      }

      public boolean shouldBackup() {
         return this.shouldBackup;
      }

      public boolean isSevere() {
         return this.severe;
      }

      public String getTranslationKey() {
         return this.translationKey;
      }
   }
   public boolean isExperimental() {
      return this.settings.getLifecycle().equals(com.mojang.serialization.Lifecycle.experimental());
   }
}
