package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressScreen extends Screen implements ProgressListener {
   @Nullable
   private Component header;
   @Nullable
   private Component stage;
   private int progress;
   private boolean stop;
   private final boolean clearScreenAfterStop;

   public ProgressScreen(boolean p_169364_) {
      super(NarratorChatListener.NO_TITLE);
      this.clearScreenAfterStop = p_169364_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void progressStartNoAbort(Component p_96520_) {
      this.progressStart(p_96520_);
   }

   public void progressStart(Component p_96523_) {
      this.header = p_96523_;
      this.progressStage(new TranslatableComponent("progress.working"));
   }

   public void progressStage(Component p_96525_) {
      this.stage = p_96525_;
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int p_96513_) {
      this.progress = p_96513_;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(PoseStack p_96515_, int p_96516_, int p_96517_, float p_96518_) {
      if (this.stop) {
         if (this.clearScreenAfterStop) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         this.renderBackground(p_96515_);
         if (this.header != null) {
            drawCenteredString(p_96515_, this.font, this.header, this.width / 2, 70, 16777215);
         }

         if (this.stage != null && this.progress != 0) {
            drawCenteredString(p_96515_, this.font, (new TextComponent("")).append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, 16777215);
         }

         super.render(p_96515_, p_96516_, p_96517_, p_96518_);
      }
   }
}