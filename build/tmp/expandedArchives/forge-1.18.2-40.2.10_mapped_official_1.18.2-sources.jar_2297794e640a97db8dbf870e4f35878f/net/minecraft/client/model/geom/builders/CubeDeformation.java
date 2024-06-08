package net.minecraft.client.model.geom.builders;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CubeDeformation {
   public static final CubeDeformation NONE = new CubeDeformation(0.0F);
   final float growX;
   final float growY;
   final float growZ;

   public CubeDeformation(float p_171466_, float p_171467_, float p_171468_) {
      this.growX = p_171466_;
      this.growY = p_171467_;
      this.growZ = p_171468_;
   }

   public CubeDeformation(float p_171464_) {
      this(p_171464_, p_171464_, p_171464_);
   }

   public CubeDeformation extend(float p_171470_) {
      return new CubeDeformation(this.growX + p_171470_, this.growY + p_171470_, this.growZ + p_171470_);
   }

   public CubeDeformation extend(float p_171472_, float p_171473_, float p_171474_) {
      return new CubeDeformation(this.growX + p_171472_, this.growY + p_171473_, this.growZ + p_171474_);
   }
}