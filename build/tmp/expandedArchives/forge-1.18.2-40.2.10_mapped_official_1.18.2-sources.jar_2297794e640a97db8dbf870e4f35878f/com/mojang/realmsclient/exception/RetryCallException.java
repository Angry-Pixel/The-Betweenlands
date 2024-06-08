package com.mojang.realmsclient.exception;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RetryCallException extends RealmsServiceException {
   public static final int DEFAULT_DELAY = 5;
   public final int delaySeconds;

   public RetryCallException(int p_87789_, int p_87790_) {
      super(p_87790_, "Retry operation");
      if (p_87789_ >= 0 && p_87789_ <= 120) {
         this.delaySeconds = p_87789_;
      } else {
         this.delaySeconds = 5;
      }

   }
}