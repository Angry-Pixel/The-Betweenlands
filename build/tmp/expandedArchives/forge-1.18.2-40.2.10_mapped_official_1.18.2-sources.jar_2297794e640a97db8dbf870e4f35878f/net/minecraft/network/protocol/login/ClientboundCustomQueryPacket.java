package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket implements Packet<ClientLoginPacketListener>, net.minecraftforge.network.ICustomPacket<ClientboundCustomQueryPacket> {
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   private final int transactionId;
   private final ResourceLocation identifier;
   private final FriendlyByteBuf data;

   public ClientboundCustomQueryPacket(int p_179806_, ResourceLocation p_179807_, FriendlyByteBuf p_179808_) {
      this.transactionId = p_179806_;
      this.identifier = p_179807_;
      this.data = p_179808_;
   }

   public ClientboundCustomQueryPacket(FriendlyByteBuf p_179810_) {
      this.transactionId = p_179810_.readVarInt();
      this.identifier = p_179810_.readResourceLocation();
      int i = p_179810_.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.data = new FriendlyByteBuf(p_179810_.readBytes(i));
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void write(FriendlyByteBuf p_134757_) {
      p_134757_.writeVarInt(this.transactionId);
      p_134757_.writeResourceLocation(this.identifier);
      p_134757_.writeBytes(this.data.copy());
   }

   public void handle(ClientLoginPacketListener p_134754_) {
      p_134754_.handleCustomQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return this.data;
   }
}
