package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundMoveVehiclePacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;

   public ClientboundMoveVehiclePacket(Entity p_132584_) {
      this.x = p_132584_.getX();
      this.y = p_132584_.getY();
      this.z = p_132584_.getZ();
      this.yRot = p_132584_.getYRot();
      this.xRot = p_132584_.getXRot();
   }

   public ClientboundMoveVehiclePacket(FriendlyByteBuf p_179007_) {
      this.x = p_179007_.readDouble();
      this.y = p_179007_.readDouble();
      this.z = p_179007_.readDouble();
      this.yRot = p_179007_.readFloat();
      this.xRot = p_179007_.readFloat();
   }

   public void write(FriendlyByteBuf p_132593_) {
      p_132593_.writeDouble(this.x);
      p_132593_.writeDouble(this.y);
      p_132593_.writeDouble(this.z);
      p_132593_.writeFloat(this.yRot);
      p_132593_.writeFloat(this.xRot);
   }

   public void handle(ClientGamePacketListener p_132590_) {
      p_132590_.handleMoveVehicle(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}