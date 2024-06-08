package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPingPacket implements Packet<ClientGamePacketListener> {
   private final int id;

   public ClientboundPingPacket(int p_179016_) {
      this.id = p_179016_;
   }

   public ClientboundPingPacket(FriendlyByteBuf p_179018_) {
      this.id = p_179018_.readInt();
   }

   public void write(FriendlyByteBuf p_179020_) {
      p_179020_.writeInt(this.id);
   }

   public void handle(ClientGamePacketListener p_179024_) {
      p_179024_.handlePing(this);
   }

   public int getId() {
      return this.id;
   }
}