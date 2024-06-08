package net.minecraft.client.model.geom.builders;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UVPair {
   private final float u;
   private final float v;

   public UVPair(float p_171610_, float p_171611_) {
      this.u = p_171610_;
      this.v = p_171611_;
   }

   public float u() {
      return this.u;
   }

   public float v() {
      return this.v;
   }

   public String toString() {
      return "(" + this.u + "," + this.v + ")";
   }
}