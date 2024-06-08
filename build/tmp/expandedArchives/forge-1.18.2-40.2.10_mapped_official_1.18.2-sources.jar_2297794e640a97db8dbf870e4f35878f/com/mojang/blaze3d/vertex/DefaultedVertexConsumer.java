package com.mojang.blaze3d.vertex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DefaultedVertexConsumer implements VertexConsumer {
   protected boolean defaultColorSet;
   protected int defaultR = 255;
   protected int defaultG = 255;
   protected int defaultB = 255;
   protected int defaultA = 255;

   public void defaultColor(int p_85830_, int p_85831_, int p_85832_, int p_85833_) {
      this.defaultR = p_85830_;
      this.defaultG = p_85831_;
      this.defaultB = p_85832_;
      this.defaultA = p_85833_;
      this.defaultColorSet = true;
   }

   public void unsetDefaultColor() {
      this.defaultColorSet = false;
   }
}