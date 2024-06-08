package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderCenterPacket implements Packet<ClientGamePacketListener> {
   private final double newCenterX;
   private final double newCenterZ;

   public ClientboundSetBorderCenterPacket(WorldBorder p_179214_) {
      this.newCenterX = p_179214_.getCenterX();
      this.newCenterZ = p_179214_.getCenterZ();
   }

   public ClientboundSetBorderCenterPacket(FriendlyByteBuf p_179216_) {
      this.newCenterX = p_179216_.readDouble();
      this.newCenterZ = p_179216_.readDouble();
   }

   public void write(FriendlyByteBuf p_179218_) {
      p_179218_.writeDouble(this.newCenterX);
      p_179218_.writeDouble(this.newCenterZ);
   }

   public void handle(ClientGamePacketListener p_179222_) {
      p_179222_.handleSetBorderCenter(this);
   }

   public double getNewCenterZ() {
      return this.newCenterZ;
   }

   public double getNewCenterX() {
      return this.newCenterX;
   }
}