package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientGamePacketListener> {
   private final long id;

   public ClientboundKeepAlivePacket(long p_132212_) {
      this.id = p_132212_;
   }

   public ClientboundKeepAlivePacket(FriendlyByteBuf p_178895_) {
      this.id = p_178895_.readLong();
   }

   public void write(FriendlyByteBuf p_132221_) {
      p_132221_.writeLong(this.id);
   }

   public void handle(ClientGamePacketListener p_132218_) {
      p_132218_.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}