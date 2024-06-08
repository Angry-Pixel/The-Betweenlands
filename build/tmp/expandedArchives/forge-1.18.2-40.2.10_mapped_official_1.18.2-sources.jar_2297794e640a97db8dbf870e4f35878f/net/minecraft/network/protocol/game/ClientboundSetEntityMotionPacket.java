package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final int xa;
   private final int ya;
   private final int za;

   public ClientboundSetEntityMotionPacket(Entity p_133185_) {
      this(p_133185_.getId(), p_133185_.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int p_133182_, Vec3 p_133183_) {
      this.id = p_133182_;
      double d0 = 3.9D;
      double d1 = Mth.clamp(p_133183_.x, -3.9D, 3.9D);
      double d2 = Mth.clamp(p_133183_.y, -3.9D, 3.9D);
      double d3 = Mth.clamp(p_133183_.z, -3.9D, 3.9D);
      this.xa = (int)(d1 * 8000.0D);
      this.ya = (int)(d2 * 8000.0D);
      this.za = (int)(d3 * 8000.0D);
   }

   public ClientboundSetEntityMotionPacket(FriendlyByteBuf p_179294_) {
      this.id = p_179294_.readVarInt();
      this.xa = p_179294_.readShort();
      this.ya = p_179294_.readShort();
      this.za = p_179294_.readShort();
   }

   public void write(FriendlyByteBuf p_133194_) {
      p_133194_.writeVarInt(this.id);
      p_133194_.writeShort(this.xa);
      p_133194_.writeShort(this.ya);
      p_133194_.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener p_133191_) {
      p_133191_.handleSetEntityMotion(this);
   }

   public int getId() {
      return this.id;
   }

   public int getXa() {
      return this.xa;
   }

   public int getYa() {
      return this.ya;
   }

   public int getZa() {
      return this.za;
   }
}