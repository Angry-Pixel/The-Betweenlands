package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerClosePacket implements Packet<ServerGamePacketListener> {
   private final int containerId;

   public ServerboundContainerClosePacket(int p_133970_) {
      this.containerId = p_133970_;
   }

   public void handle(ServerGamePacketListener p_133976_) {
      p_133976_.handleContainerClose(this);
   }

   public ServerboundContainerClosePacket(FriendlyByteBuf p_179584_) {
      this.containerId = p_179584_.readByte();
   }

   public void write(FriendlyByteBuf p_133978_) {
      p_133978_.writeByte(this.containerId);
   }

   public int getContainerId() {
      return this.containerId;
   }
}