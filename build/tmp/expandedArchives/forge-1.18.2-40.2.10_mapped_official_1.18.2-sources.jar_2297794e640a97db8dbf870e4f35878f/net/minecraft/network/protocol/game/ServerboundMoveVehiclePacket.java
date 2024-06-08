package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ServerboundMoveVehiclePacket implements Packet<ServerGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;

   public ServerboundMoveVehiclePacket(Entity p_134192_) {
      this.x = p_134192_.getX();
      this.y = p_134192_.getY();
      this.z = p_134192_.getZ();
      this.yRot = p_134192_.getYRot();
      this.xRot = p_134192_.getXRot();
   }

   public ServerboundMoveVehiclePacket(FriendlyByteBuf p_179700_) {
      this.x = p_179700_.readDouble();
      this.y = p_179700_.readDouble();
      this.z = p_179700_.readDouble();
      this.yRot = p_179700_.readFloat();
      this.xRot = p_179700_.readFloat();
   }

   public void write(FriendlyByteBuf p_134201_) {
      p_134201_.writeDouble(this.x);
      p_134201_.writeDouble(this.y);
      p_134201_.writeDouble(this.z);
      p_134201_.writeFloat(this.yRot);
      p_134201_.writeFloat(this.xRot);
   }

   public void handle(ServerGamePacketListener p_134198_) {
      p_134198_.handleMoveVehicle(this);
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