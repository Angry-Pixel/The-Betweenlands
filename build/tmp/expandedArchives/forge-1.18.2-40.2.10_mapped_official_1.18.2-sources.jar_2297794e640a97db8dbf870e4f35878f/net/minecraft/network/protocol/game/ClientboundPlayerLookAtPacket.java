package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final int entity;
   private final EntityAnchorArgument.Anchor fromAnchor;
   private final EntityAnchorArgument.Anchor toAnchor;
   private final boolean atEntity;

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor p_132777_, double p_132778_, double p_132779_, double p_132780_) {
      this.fromAnchor = p_132777_;
      this.x = p_132778_;
      this.y = p_132779_;
      this.z = p_132780_;
      this.entity = 0;
      this.atEntity = false;
      this.toAnchor = null;
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor p_132782_, Entity p_132783_, EntityAnchorArgument.Anchor p_132784_) {
      this.fromAnchor = p_132782_;
      this.entity = p_132783_.getId();
      this.toAnchor = p_132784_;
      Vec3 vec3 = p_132784_.apply(p_132783_);
      this.x = vec3.x;
      this.y = vec3.y;
      this.z = vec3.z;
      this.atEntity = true;
   }

   public ClientboundPlayerLookAtPacket(FriendlyByteBuf p_179146_) {
      this.fromAnchor = p_179146_.readEnum(EntityAnchorArgument.Anchor.class);
      this.x = p_179146_.readDouble();
      this.y = p_179146_.readDouble();
      this.z = p_179146_.readDouble();
      this.atEntity = p_179146_.readBoolean();
      if (this.atEntity) {
         this.entity = p_179146_.readVarInt();
         this.toAnchor = p_179146_.readEnum(EntityAnchorArgument.Anchor.class);
      } else {
         this.entity = 0;
         this.toAnchor = null;
      }

   }

   public void write(FriendlyByteBuf p_132795_) {
      p_132795_.writeEnum(this.fromAnchor);
      p_132795_.writeDouble(this.x);
      p_132795_.writeDouble(this.y);
      p_132795_.writeDouble(this.z);
      p_132795_.writeBoolean(this.atEntity);
      if (this.atEntity) {
         p_132795_.writeVarInt(this.entity);
         p_132795_.writeEnum(this.toAnchor);
      }

   }

   public void handle(ClientGamePacketListener p_132792_) {
      p_132792_.handleLookAt(this);
   }

   public EntityAnchorArgument.Anchor getFromAnchor() {
      return this.fromAnchor;
   }

   @Nullable
   public Vec3 getPosition(Level p_132786_) {
      if (this.atEntity) {
         Entity entity = p_132786_.getEntity(this.entity);
         return entity == null ? new Vec3(this.x, this.y, this.z) : this.toAnchor.apply(entity);
      } else {
         return new Vec3(this.x, this.y, this.z);
      }
   }
}