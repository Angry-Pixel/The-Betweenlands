package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
   public static final double MAGICAL_QUANTIZATION = 8000.0D;
   private final int id;
   private final UUID uuid;
   private final double x;
   private final double y;
   private final double z;
   private final int xa;
   private final int ya;
   private final int za;
   private final int xRot;
   private final int yRot;
   private final EntityType<?> type;
   private final int data;
   public static final double LIMIT = 3.9D;

   public ClientboundAddEntityPacket(int p_131470_, UUID p_131471_, double p_131472_, double p_131473_, double p_131474_, float p_131475_, float p_131476_, EntityType<?> p_131477_, int p_131478_, Vec3 p_131479_) {
      this.id = p_131470_;
      this.uuid = p_131471_;
      this.x = p_131472_;
      this.y = p_131473_;
      this.z = p_131474_;
      this.xRot = Mth.floor(p_131475_ * 256.0F / 360.0F);
      this.yRot = Mth.floor(p_131476_ * 256.0F / 360.0F);
      this.type = p_131477_;
      this.data = p_131478_;
      this.xa = (int)(Mth.clamp(p_131479_.x, -3.9D, 3.9D) * 8000.0D);
      this.ya = (int)(Mth.clamp(p_131479_.y, -3.9D, 3.9D) * 8000.0D);
      this.za = (int)(Mth.clamp(p_131479_.z, -3.9D, 3.9D) * 8000.0D);
   }

   public ClientboundAddEntityPacket(Entity p_131481_) {
      this(p_131481_, 0);
   }

   public ClientboundAddEntityPacket(Entity p_131483_, int p_131484_) {
      this(p_131483_.getId(), p_131483_.getUUID(), p_131483_.getX(), p_131483_.getY(), p_131483_.getZ(), p_131483_.getXRot(), p_131483_.getYRot(), p_131483_.getType(), p_131484_, p_131483_.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(Entity p_131486_, EntityType<?> p_131487_, int p_131488_, BlockPos p_131489_) {
      this(p_131486_.getId(), p_131486_.getUUID(), (double)p_131489_.getX(), (double)p_131489_.getY(), (double)p_131489_.getZ(), p_131486_.getXRot(), p_131486_.getYRot(), p_131487_, p_131488_, p_131486_.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(FriendlyByteBuf p_178562_) {
      this.id = p_178562_.readVarInt();
      this.uuid = p_178562_.readUUID();
      this.type = Registry.ENTITY_TYPE.byId(p_178562_.readVarInt());
      this.x = p_178562_.readDouble();
      this.y = p_178562_.readDouble();
      this.z = p_178562_.readDouble();
      this.xRot = p_178562_.readByte();
      this.yRot = p_178562_.readByte();
      this.data = p_178562_.readInt();
      this.xa = p_178562_.readShort();
      this.ya = p_178562_.readShort();
      this.za = p_178562_.readShort();
   }

   public void write(FriendlyByteBuf p_131498_) {
      p_131498_.writeVarInt(this.id);
      p_131498_.writeUUID(this.uuid);
      p_131498_.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      p_131498_.writeDouble(this.x);
      p_131498_.writeDouble(this.y);
      p_131498_.writeDouble(this.z);
      p_131498_.writeByte(this.xRot);
      p_131498_.writeByte(this.yRot);
      p_131498_.writeInt(this.data);
      p_131498_.writeShort(this.xa);
      p_131498_.writeShort(this.ya);
      p_131498_.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener p_131495_) {
      p_131495_.handleAddEntity(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
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

   public double getXa() {
      return (double)this.xa / 8000.0D;
   }

   public double getYa() {
      return (double)this.ya / 8000.0D;
   }

   public double getZa() {
      return (double)this.za / 8000.0D;
   }

   public int getxRot() {
      return this.xRot;
   }

   public int getyRot() {
      return this.yRot;
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }
}