package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = new TranslatableComponent("multiplayer.downloadingTerrain");
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 2000L;
   private boolean loadingPacketsReceived = false;
   private boolean oneTickSkipped = false;
   private final long createdAt = System.currentTimeMillis();

   public ReceivingLevelScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(PoseStack p_96530_, int p_96531_, int p_96532_, float p_96533_) {
      this.renderDirtBackground(0);
      drawCenteredString(p_96530_, this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_96530_, p_96531_, p_96532_, p_96533_);
   }

   public void tick() {
      boolean flag = this.oneTickSkipped || System.currentTimeMillis() > this.createdAt + 2000L;
      if (flag && this.minecraft != null && this.minecraft.player != null) {
         BlockPos blockpos = this.minecraft.player.blockPosition();
         boolean flag1 = this.minecraft.level != null && this.minecraft.level.isOutsideBuildHeight(blockpos.getY());
         if (flag1 || this.minecraft.levelRenderer.isChunkCompiled(blockpos)) {
            this.onClose();
         }

         if (this.loadingPacketsReceived) {
            this.oneTickSkipped = true;
         }

      }
   }

   public void loadingPacketsReceived() {
      this.loadingPacketsReceived = true;
   }

   public boolean isPauseScreen() {
      return false;
   }
}