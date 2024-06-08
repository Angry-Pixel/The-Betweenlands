package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final CipherBase cipher;

   public CipherDecoder(Cipher p_129414_) {
      this.cipher = new CipherBase(p_129414_);
   }

   protected void decode(ChannelHandlerContext p_129416_, ByteBuf p_129417_, List<Object> p_129418_) throws Exception {
      p_129418_.add(this.cipher.decipher(p_129416_, p_129417_));
   }
}