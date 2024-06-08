package net.minecraft.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtils {
   public static float rotlerpRad(float p_103126_, float p_103127_, float p_103128_) {
      float f;
      for(f = p_103127_ - p_103126_; f < -(float)Math.PI; f += ((float)Math.PI * 2F)) {
      }

      while(f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_103126_ + p_103128_ * f;
   }
}