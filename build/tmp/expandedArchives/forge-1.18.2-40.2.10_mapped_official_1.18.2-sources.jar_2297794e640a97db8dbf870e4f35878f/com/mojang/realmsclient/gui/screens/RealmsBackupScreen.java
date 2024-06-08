package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
   static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
   static final Component RESTORE_TOOLTIP = new TranslatableComponent("mco.backup.button.restore");
   static final Component HAS_CHANGES_TOOLTIP = new TranslatableComponent("mco.backup.changes.tooltip");
   private static final Component TITLE = new TranslatableComponent("mco.configure.world.backup");
   private static final Component NO_BACKUPS_LABEL = new TranslatableComponent("mco.backup.nobackups");
   static int lastScrollPosition = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   List<Backup> backups = Collections.emptyList();
   @Nullable
   Component toolTip;
   RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
   int selectedBackup = -1;
   private final int slotId;
   private Button downloadButton;
   private Button restoreButton;
   private Button changesButton;
   Boolean noBackups = false;
   final RealmsServer serverData;
   private static final String UPLOADED_KEY = "Uploaded";

   public RealmsBackupScreen(RealmsConfigureWorldScreen p_88126_, RealmsServer p_88127_, int p_88128_) {
      super(new TranslatableComponent("mco.configure.world.backup"));
      this.lastScreen = p_88126_;
      this.serverData = p_88127_;
      this.slotId = p_88128_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
      if (lastScrollPosition != -1) {
         this.backupObjectSelectionList.setScrollAmount((double)lastScrollPosition);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               List<Backup> list = realmsclient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               RealmsBackupScreen.this.minecraft.execute(() -> {
                  RealmsBackupScreen.this.backups = list;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  RealmsBackupScreen.this.backupObjectSelectionList.clear();

                  for(Backup backup : RealmsBackupScreen.this.backups) {
                     RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
                  }

                  RealmsBackupScreen.this.generateChangeList();
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.downloadButton = this.addRenderableWidget(new Button(this.width - 135, row(1), 120, 20, new TranslatableComponent("mco.backup.button.download"), (p_88185_) -> {
         this.downloadClicked();
      }));
      this.restoreButton = this.addRenderableWidget(new Button(this.width - 135, row(3), 120, 20, new TranslatableComponent("mco.backup.button.restore"), (p_88179_) -> {
         this.restoreClicked(this.selectedBackup);
      }));
      this.changesButton = this.addRenderableWidget(new Button(this.width - 135, row(5), 120, 20, new TranslatableComponent("mco.backup.changes.tooltip"), (p_88174_) -> {
         this.minecraft.setScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
         this.selectedBackup = -1;
      }));
      this.addRenderableWidget(new Button(this.width - 100, this.height - 35, 85, 20, CommonComponents.GUI_BACK, (p_88164_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.addWidget(this.backupObjectSelectionList);
      this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
      this.updateButtonStates();
   }

   void generateChangeList() {
      if (this.backups.size() > 1) {
         for(int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup1 = this.backups.get(i + 1);
            if (!backup.metadata.isEmpty() && !backup1.metadata.isEmpty()) {
               for(String s : backup.metadata.keySet()) {
                  if (!s.contains("Uploaded") && backup1.metadata.containsKey(s)) {
                     if (!backup.metadata.get(s).equals(backup1.metadata.get(s))) {
                        this.addToChangeList(backup, s);
                     }
                  } else {
                     this.addToChangeList(backup, s);
                  }
               }
            }
         }

      }
   }

   private void addToChangeList(Backup p_88147_, String p_88148_) {
      if (p_88148_.contains("Uploaded")) {
         String s = DateFormat.getDateTimeInstance(3, 3).format(p_88147_.lastModifiedDate);
         p_88147_.changeList.put(p_88148_, s);
         p_88147_.setUploadedVersion(true);
      } else {
         p_88147_.changeList.put(p_88148_, p_88147_.metadata.get(p_88148_));
      }

   }

   void updateButtonStates() {
      this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
      this.changesButton.visible = this.shouldChangesButtonBeVisible();
   }

   private boolean shouldChangesButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !(this.backups.get(this.selectedBackup)).changeList.isEmpty();
      }
   }

   private boolean shouldRestoreButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !this.serverData.expired;
      }
   }

   public boolean keyPressed(int p_88133_, int p_88134_, int p_88135_) {
      if (p_88133_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_88133_, p_88134_, p_88135_);
      }
   }

   void restoreClicked(int p_88167_) {
      if (p_88167_ >= 0 && p_88167_ < this.backups.size() && !this.serverData.expired) {
         this.selectedBackup = p_88167_;
         Date date = (this.backups.get(p_88167_)).lastModifiedDate;
         String s = DateFormat.getDateTimeInstance(3, 3).format(date);
         String s1 = RealmsUtil.convertToAgePresentationFromInstant(date);
         Component component = new TranslatableComponent("mco.configure.world.restore.question.line1", s, s1);
         Component component1 = new TranslatableComponent("mco.configure.world.restore.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_88187_) -> {
            if (p_88187_) {
               this.restore();
            } else {
               this.selectedBackup = -1;
               this.minecraft.setScreen(this);
            }

         }, RealmsLongConfirmationScreen.Type.Warning, component, component1, true));
      }

   }

   private void downloadClicked() {
      Component component = new TranslatableComponent("mco.configure.world.restore.download.question.line1");
      Component component1 = new TranslatableComponent("mco.configure.world.restore.download.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_88181_) -> {
         if (p_88181_) {
            this.downloadWorldData();
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, component, component1, true));
   }

   private void downloadWorldData() {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
   }

   private void restore() {
      Backup backup = this.backups.get(this.selectedBackup);
      this.selectedBackup = -1;
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.lastScreen)));
   }

   public void render(PoseStack p_88137_, int p_88138_, int p_88139_, float p_88140_) {
      this.toolTip = null;
      this.renderBackground(p_88137_);
      this.backupObjectSelectionList.render(p_88137_, p_88138_, p_88139_, p_88140_);
      drawCenteredString(p_88137_, this.font, this.title, this.width / 2, 12, 16777215);
      this.font.draw(p_88137_, TITLE, (float)((this.width - 150) / 2 - 90), 20.0F, 10526880);
      if (this.noBackups) {
         this.font.draw(p_88137_, NO_BACKUPS_LABEL, 20.0F, (float)(this.height / 2 - 10), 16777215);
      }

      this.downloadButton.active = !this.noBackups;
      super.render(p_88137_, p_88138_, p_88139_, p_88140_);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(p_88137_, this.toolTip, p_88138_, p_88139_);
      }

   }

   protected void renderMousehoverTooltip(PoseStack p_88142_, @Nullable Component p_88143_, int p_88144_, int p_88145_) {
      if (p_88143_ != null) {
         int i = p_88144_ + 12;
         int j = p_88145_ - 12;
         int k = this.font.width(p_88143_);
         this.fillGradient(p_88142_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_88142_, p_88143_, (float)i, (float)j, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionList extends RealmsObjectSelectionList<RealmsBackupScreen.Entry> {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
      }

      public void addEntry(Backup p_88235_) {
         this.addEntry(RealmsBackupScreen.this.new Entry(p_88235_));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.getFocused() == this;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground(PoseStack p_88233_) {
         RealmsBackupScreen.this.renderBackground(p_88233_);
      }

      public boolean mouseClicked(double p_88221_, double p_88222_, int p_88223_) {
         if (p_88223_ != 0) {
            return false;
         } else if (p_88221_ < (double)this.getScrollbarPosition() && p_88222_ >= (double)this.y0 && p_88222_ <= (double)this.y1) {
            int i = this.width / 2 - 92;
            int j = this.width;
            int k = (int)Math.floor(p_88222_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount();
            int l = k / this.itemHeight;
            if (p_88221_ >= (double)i && p_88221_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_88221_, p_88222_, this.width);
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width - 5;
      }

      public void itemClicked(int p_88227_, int p_88228_, double p_88229_, double p_88230_, int p_88231_) {
         int i = this.width - 35;
         int j = p_88228_ * this.itemHeight + 36 - (int)this.getScrollAmount();
         int k = i + 10;
         int l = j - 3;
         if (p_88229_ >= (double)i && p_88229_ <= (double)(i + 9) && p_88230_ >= (double)j && p_88230_ <= (double)(j + 9)) {
            if (!(RealmsBackupScreen.this.backups.get(p_88228_)).changeList.isEmpty()) {
               RealmsBackupScreen.this.selectedBackup = -1;
               RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
               this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.backups.get(p_88228_)));
            }
         } else if (p_88229_ >= (double)k && p_88229_ < (double)(k + 13) && p_88230_ >= (double)l && p_88230_ < (double)(l + 15)) {
            RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
            RealmsBackupScreen.this.restoreClicked(p_88228_);
         }

      }

      public void selectItem(int p_88225_) {
         super.selectItem(p_88225_);
         this.selectInviteListItem(p_88225_);
      }

      public void selectInviteListItem(int p_88242_) {
         RealmsBackupScreen.this.selectedBackup = p_88242_;
         RealmsBackupScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsBackupScreen.Entry p_88237_) {
         super.setSelected(p_88237_);
         RealmsBackupScreen.this.selectedBackup = this.children().indexOf(p_88237_);
         RealmsBackupScreen.this.updateButtonStates();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class Entry extends ObjectSelectionList.Entry<RealmsBackupScreen.Entry> {
      private final Backup backup;

      public Entry(Backup p_88250_) {
         this.backup = p_88250_;
      }

      public void render(PoseStack p_88258_, int p_88259_, int p_88260_, int p_88261_, int p_88262_, int p_88263_, int p_88264_, int p_88265_, boolean p_88266_, float p_88267_) {
         this.renderBackupItem(p_88258_, this.backup, p_88261_ - 40, p_88260_, p_88264_, p_88265_);
      }

      private void renderBackupItem(PoseStack p_88269_, Backup p_88270_, int p_88271_, int p_88272_, int p_88273_, int p_88274_) {
         int i = p_88270_.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.font.draw(p_88269_, "Backup (" + RealmsUtil.convertToAgePresentationFromInstant(p_88270_.lastModifiedDate) + ")", (float)(p_88271_ + 40), (float)(p_88272_ + 1), i);
         RealmsBackupScreen.this.font.draw(p_88269_, this.getMediumDatePresentation(p_88270_.lastModifiedDate), (float)(p_88271_ + 40), (float)(p_88272_ + 12), 5000268);
         int j = RealmsBackupScreen.this.width - 175;
         int k = -3;
         int l = j - 10;
         int i1 = 0;
         if (!RealmsBackupScreen.this.serverData.expired) {
            this.drawRestore(p_88269_, j, p_88272_ + -3, p_88273_, p_88274_);
         }

         if (!p_88270_.changeList.isEmpty()) {
            this.drawInfo(p_88269_, l, p_88272_ + 0, p_88273_, p_88274_);
         }

      }

      private String getMediumDatePresentation(Date p_88276_) {
         return DateFormat.getDateTimeInstance(3, 3).format(p_88276_);
      }

      private void drawRestore(PoseStack p_88252_, int p_88253_, int p_88254_, int p_88255_, int p_88256_) {
         boolean flag = p_88255_ >= p_88253_ && p_88255_ <= p_88253_ + 12 && p_88256_ >= p_88254_ && p_88256_ <= p_88254_ + 14 && p_88256_ < RealmsBackupScreen.this.height - 15 && p_88256_ > 32;
         RenderSystem.setShaderTexture(0, RealmsBackupScreen.RESTORE_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         p_88252_.pushPose();
         p_88252_.scale(0.5F, 0.5F, 0.5F);
         float f = flag ? 28.0F : 0.0F;
         GuiComponent.blit(p_88252_, p_88253_ * 2, p_88254_ * 2, 0.0F, f, 23, 28, 23, 56);
         p_88252_.popPose();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.RESTORE_TOOLTIP;
         }

      }

      private void drawInfo(PoseStack p_88278_, int p_88279_, int p_88280_, int p_88281_, int p_88282_) {
         boolean flag = p_88281_ >= p_88279_ && p_88281_ <= p_88279_ + 8 && p_88282_ >= p_88280_ && p_88282_ <= p_88280_ + 8 && p_88282_ < RealmsBackupScreen.this.height - 15 && p_88282_ > 32;
         RenderSystem.setShaderTexture(0, RealmsBackupScreen.PLUS_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         p_88278_.pushPose();
         p_88278_.scale(0.5F, 0.5F, 0.5F);
         float f = flag ? 15.0F : 0.0F;
         GuiComponent.blit(p_88278_, p_88279_ * 2, p_88280_ * 2, 0.0F, f, 15, 15, 15, 30);
         p_88278_.popPose();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.HAS_CHANGES_TOOLTIP;
         }

      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", this.backup.lastModifiedDate.toString());
      }
   }
}