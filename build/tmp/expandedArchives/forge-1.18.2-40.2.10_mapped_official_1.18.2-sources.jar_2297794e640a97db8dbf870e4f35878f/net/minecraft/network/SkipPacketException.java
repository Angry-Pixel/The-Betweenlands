package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class SkipPacketException extends EncoderException {
   public SkipPacketException(Throwable p_130563_) {
      super(p_130563_);
   }
}