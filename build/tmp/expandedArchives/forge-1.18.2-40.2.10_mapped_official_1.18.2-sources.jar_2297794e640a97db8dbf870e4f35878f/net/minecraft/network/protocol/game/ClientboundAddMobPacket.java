package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddMobPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final UUID uuid;
   private final int type;
   private final double x;
   private final double y;
   private final double z;
   private final int xd;
   private final int yd;
   private final int zd;
   private final byte yRot;
   private final byte xRot;
   private final byte yHeadRot;

   public ClientboundAddMobPacket(LivingEntity p_131545_) {
      this.id = p_131545_.getId();
      this.uuid = p_131545_.getUUID();
      this.type = Registry.ENTITY_TYPE.getId(p_131545_.getType());
      this.x = p_131545_.getX();
      this.y = p_131545_.getY();
      this.z = p_131545_.getZ();
      this.yRot = (byte)((int)(p_131545_.getYRot() * 256.0F / 360.0F));
      this.xRot = (byte)((int)(p_131545_.getXRot() * 256.0F / 360.0F));
      this.yHeadRot = (byte)((int)(p_131545_.yHeadRot * 256.0F / 360.0F));
      double d0 = 3.9D;
      Vec3 vec3 = p_131545_.getDeltaMovement();
      double d1 = Mth.clamp(vec3.x, -3.9D, 3.9D);
      double d2 = Mth.clamp(vec3.y, -3.9D, 3.9D);
      double d3 = Mth.clamp(vec3.z, -3.9D, 3.9D);
      this.xd = (int)(d1 * 8000.0D);
      this.yd = (int)(d2 * 8000.0D);
      this.zd = (int)(d3 * 8000.0D);
   }

   public ClientboundAddMobPacket(FriendlyByteBuf p_178566_) {
      this.id = p_178566_.readVarInt();
      this.uuid = p_178566_.readUUID();
      this.type = p_178566_.readVarInt();
      this.x = p_178566_.readDouble();
      this.y = p_178566_.readDouble();
      this.z = p_178566_.readDouble();
      this.yRot = p_178566_.readByte();
      this.xRot = p_178566_.readByte();
      this.yHeadRot = p_178566_.readByte();
      this.xd = p_178566_.readShort();
      this.yd = p_178566_.readShort();
      this.zd = p_178566_.readShort();
   }

   public void write(FriendlyByteBuf p_131554_) {
      p_131554_.writeVarInt(this.id);
      p_131554_.writeUUID(this.uuid);
      p_131554_.writeVarInt(this.type);
      p_131554_.writeDouble(this.x);
      p_131554_.writeDouble(this.y);
      p_131554_.writeDouble(this.z);
      p_131554_.writeByte(this.yRot);
      p_131554_.writeByte(this.xRot);
      p_131554_.writeByte(this.yHeadRot);
      p_131554_.writeShort(this.xd);
      p_131554_.writeShort(this.yd);
      p_131554_.writeShort(this.zd);
   }

   public void handle(ClientGamePacketListener p_131551_) {
      p_131551_.handleAddMob(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public int getType() {
      return this.type;
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

   public int getXd() {
      return this.xd;
   }

   public int getYd() {
      return this.yd;
   }

   public int getZd() {
      return this.zd;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public byte getyHeadRot() {
      return this.yHeadRot;
   }
}