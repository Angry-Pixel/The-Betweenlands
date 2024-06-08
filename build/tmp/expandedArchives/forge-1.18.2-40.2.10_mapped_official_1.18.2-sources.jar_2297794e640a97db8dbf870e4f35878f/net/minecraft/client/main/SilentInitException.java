package net.minecraft.client.main;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilentInitException extends RuntimeException {
   public SilentInitException(String p_101979_) {
      super(p_101979_);
   }

   public SilentInitException(String p_170334_, Throwable p_170335_) {
      super(p_170334_, p_170335_);
   }
}