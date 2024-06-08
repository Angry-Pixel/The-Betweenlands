package com.mojang.realmsclient.exception;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsHttpException extends RuntimeException {
   public RealmsHttpException(String p_87771_, Exception p_87772_) {
      super(p_87771_, p_87772_);
   }
}