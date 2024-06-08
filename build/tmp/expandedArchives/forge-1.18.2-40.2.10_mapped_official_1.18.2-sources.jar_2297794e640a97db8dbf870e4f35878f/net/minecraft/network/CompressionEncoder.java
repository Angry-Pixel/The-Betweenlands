package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] encodeBuf = new byte[8192];
   private final Deflater deflater;
   private int threshold;
   private static final boolean DISABLE_PACKET_DEBUG = Boolean.parseBoolean(System.getProperty("forge.disablePacketCompressionDebug", "false"));
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

   public CompressionEncoder(int p_129448_) {
      this.threshold = p_129448_;
      this.deflater = new Deflater();
   }

   protected void encode(ChannelHandlerContext p_129452_, ByteBuf p_129453_, ByteBuf p_129454_) {
      int i = p_129453_.readableBytes();
      FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(p_129454_);
      if (i < this.threshold) {
         friendlybytebuf.writeVarInt(0);
         friendlybytebuf.writeBytes(p_129453_);
      } else {
         if (!DISABLE_PACKET_DEBUG && i > 2097152) {
             p_129453_.markReaderIndex();
             LOGGER.error("Attempted to send packet over maximum protocol size: {} > 2097152\nData:\n{}", i,
                     net.minecraftforge.logging.PacketDump.getContentDump(p_129453_));
             p_129453_.resetReaderIndex();
         }
         byte[] abyte = new byte[i];
         p_129453_.readBytes(abyte);
         friendlybytebuf.writeVarInt(abyte.length);
         this.deflater.setInput(abyte, 0, i);
         this.deflater.finish();

         while(!this.deflater.finished()) {
            int j = this.deflater.deflate(this.encodeBuf);
            friendlybytebuf.writeBytes(this.encodeBuf, 0, j);
         }

         this.deflater.reset();
      }

   }

   public int getThreshold() {
      return this.threshold;
   }

   public void setThreshold(int p_129450_) {
      this.threshold = p_129450_;
   }
}
