package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
   private final int compressionThreshold;

   public ClientboundLoginCompressionPacket(int p_134799_) {
      this.compressionThreshold = p_134799_;
   }

   public ClientboundLoginCompressionPacket(FriendlyByteBuf p_179818_) {
      this.compressionThreshold = p_179818_.readVarInt();
   }

   public void write(FriendlyByteBuf p_134808_) {
      p_134808_.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientLoginPacketListener p_134805_) {
      p_134805_.handleCompression(this);
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}