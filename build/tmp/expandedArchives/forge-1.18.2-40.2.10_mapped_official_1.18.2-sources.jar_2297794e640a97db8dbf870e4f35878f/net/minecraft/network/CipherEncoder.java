package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {
   private final CipherBase cipher;

   public CipherEncoder(Cipher p_129425_) {
      this.cipher = new CipherBase(p_129425_);
   }

   protected void encode(ChannelHandlerContext p_129427_, ByteBuf p_129428_, ByteBuf p_129429_) throws Exception {
      this.cipher.encipher(p_129428_, p_129429_);
   }
}