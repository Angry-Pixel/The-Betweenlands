package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CipherBase {
   private final Cipher cipher;
   private byte[] heapIn = new byte[0];
   private byte[] heapOut = new byte[0];

   protected CipherBase(Cipher p_129403_) {
      this.cipher = p_129403_;
   }

   private byte[] bufToByte(ByteBuf p_129405_) {
      int i = p_129405_.readableBytes();
      if (this.heapIn.length < i) {
         this.heapIn = new byte[i];
      }

      p_129405_.readBytes(this.heapIn, 0, i);
      return this.heapIn;
   }

   protected ByteBuf decipher(ChannelHandlerContext p_129410_, ByteBuf p_129411_) throws ShortBufferException {
      int i = p_129411_.readableBytes();
      byte[] abyte = this.bufToByte(p_129411_);
      ByteBuf bytebuf = p_129410_.alloc().heapBuffer(this.cipher.getOutputSize(i));
      bytebuf.writerIndex(this.cipher.update(abyte, 0, i, bytebuf.array(), bytebuf.arrayOffset()));
      return bytebuf;
   }

   protected void encipher(ByteBuf p_129407_, ByteBuf p_129408_) throws ShortBufferException {
      int i = p_129407_.readableBytes();
      byte[] abyte = this.bufToByte(p_129407_);
      int j = this.cipher.getOutputSize(i);
      if (this.heapOut.length < j) {
         this.heapOut = new byte[j];
      }

      p_129408_.writeBytes(this.heapOut, 0, this.cipher.update(abyte, 0, i, this.heapOut));
   }
}