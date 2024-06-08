package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSliderButton extends AbstractWidget {
   protected double value;

   public AbstractSliderButton(int p_93579_, int p_93580_, int p_93581_, int p_93582_, Component p_93583_, double p_93584_) {
      super(p_93579_, p_93580_, p_93581_, p_93582_, p_93583_);
      this.value = p_93584_;
   }

   protected int getYImage(boolean p_93607_) {
      return 0;
   }

   protected MutableComponent createNarrationMessage() {
      return new TranslatableComponent("gui.narrate.slider", this.getMessage());
   }

   public void updateNarration(NarrationElementOutput p_168798_) {
      p_168798_.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            p_168798_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.slider.usage.focused"));
         } else {
            p_168798_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.slider.usage.hovered"));
         }
      }

   }

   protected void renderBg(PoseStack p_93600_, Minecraft p_93601_, int p_93602_, int p_93603_) {
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = (this.isHoveredOrFocused() ? 2 : 1) * 20;
      this.blit(p_93600_, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
      this.blit(p_93600_, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
   }

   public void onClick(double p_93588_, double p_93589_) {
      this.setValueFromMouse(p_93588_);
   }

   public boolean keyPressed(int p_93596_, int p_93597_, int p_93598_) {
      boolean flag = p_93596_ == 263;
      if (flag || p_93596_ == 262) {
         float f = flag ? -1.0F : 1.0F;
         this.setValue(this.value + (double)(f / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double p_93586_) {
      this.setValue((p_93586_ - (double)(this.x + 4)) / (double)(this.width - 8));
   }

   private void setValue(double p_93612_) {
      double d0 = this.value;
      this.value = Mth.clamp(p_93612_, 0.0D, 1.0D);
      if (d0 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double p_93591_, double p_93592_, double p_93593_, double p_93594_) {
      this.setValueFromMouse(p_93591_);
      super.onDrag(p_93591_, p_93592_, p_93593_, p_93594_);
   }

   public void playDownSound(SoundManager p_93605_) {
   }

   public void onRelease(double p_93609_, double p_93610_) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}