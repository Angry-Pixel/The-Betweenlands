package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
   private static final Component TEXT_UNKNOWN = new TextComponent("UNKNOWN");
   private final Screen lastScreen;
   final Backup backup;
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen p_88048_, Backup p_88049_) {
      super(new TextComponent("Changes from last backup"));
      this.lastScreen = p_88048_;
      this.backup = p_88049_;
   }

   public void tick() {
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, CommonComponents.GUI_BACK, (p_88066_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.minecraft);
      this.addWidget(this.backupInfoList);
      this.magicalSpecialHackyFocus(this.backupInfoList);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_88051_, int p_88052_, int p_88053_) {
      if (p_88051_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_88051_, p_88052_, p_88053_);
      }
   }

   public void render(PoseStack p_88055_, int p_88056_, int p_88057_, float p_88058_) {
      this.renderBackground(p_88055_);
      this.backupInfoList.render(p_88055_, p_88056_, p_88057_, p_88058_);
      drawCenteredString(p_88055_, this.font, this.title, this.width / 2, 10, 16777215);
      super.render(p_88055_, p_88056_, p_88057_, p_88058_);
   }

   Component checkForSpecificMetadata(String p_88068_, String p_88069_) {
      String s = p_88068_.toLowerCase(Locale.ROOT);
      if (s.contains("game") && s.contains("mode")) {
         return this.gameModeMetadata(p_88069_);
      } else {
         return (Component)(s.contains("game") && s.contains("difficulty") ? this.gameDifficultyMetadata(p_88069_) : new TextComponent(p_88069_));
      }
   }

   private Component gameDifficultyMetadata(String p_88074_) {
      try {
         return RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(p_88074_)).getDisplayName();
      } catch (Exception exception) {
         return TEXT_UNKNOWN;
      }
   }

   private Component gameModeMetadata(String p_88076_) {
      try {
         return RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(p_88076_)).getShortDisplayName();
      } catch (Exception exception) {
         return TEXT_UNKNOWN;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoList extends ObjectSelectionList<RealmsBackupInfoScreen.BackupInfoListEntry> {
      public BackupInfoList(Minecraft p_88082_) {
         super(p_88082_, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
         this.setRenderSelection(false);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((p_88084_, p_88085_) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(p_88084_, p_88085_));
            });
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoListEntry extends ObjectSelectionList.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
      private final String key;
      private final String value;

      public BackupInfoListEntry(String p_88091_, String p_88092_) {
         this.key = p_88091_;
         this.value = p_88092_;
      }

      public void render(PoseStack p_88094_, int p_88095_, int p_88096_, int p_88097_, int p_88098_, int p_88099_, int p_88100_, int p_88101_, boolean p_88102_, float p_88103_) {
         Font font = RealmsBackupInfoScreen.this.minecraft.font;
         GuiComponent.drawString(p_88094_, font, this.key, p_88097_, p_88096_, 10526880);
         GuiComponent.drawString(p_88094_, font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), p_88097_, p_88096_ + 12, 16777215);
      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", this.key + " " + this.value);
      }
   }
}