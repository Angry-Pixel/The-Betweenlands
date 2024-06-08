package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket implements Packet<ServerGamePacketListener>, net.minecraftforge.network.ICustomPacket<ServerboundCustomPayloadPacket> {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private final ResourceLocation identifier;
   private final FriendlyByteBuf data;

   public ServerboundCustomPayloadPacket(ResourceLocation p_133985_, FriendlyByteBuf p_133986_) {
      this.identifier = p_133985_;
      this.data = p_133986_;
   }

   public ServerboundCustomPayloadPacket(FriendlyByteBuf p_179588_) {
      this.identifier = p_179588_.readResourceLocation();
      int i = p_179588_.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new FriendlyByteBuf(p_179588_.readBytes(i));
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   public void write(FriendlyByteBuf p_133994_) {
      p_133994_.writeResourceLocation(this.identifier);
      p_133994_.writeBytes((ByteBuf)this.data.copy()); //This may be access multiple times, from multiple threads, lets be safe like the S->C packet
   }

   public void handle(ServerGamePacketListener p_133992_) {
      p_133992_.handleCustomPayload(this);
      this.data.release();
   }

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return this.data;
   }
}
