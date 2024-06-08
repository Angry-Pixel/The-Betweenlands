package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TOLERANCE_LEVEL_ROTATION = 1;
   private final ServerLevel level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer<Packet<?>> broadcast;
   private long xp;
   private long yp;
   private long zp;
   private int yRotp;
   private int xRotp;
   private int yHeadRotp;
   private Vec3 ap = Vec3.ZERO;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;

   public ServerEntity(ServerLevel p_8528_, Entity p_8529_, int p_8530_, boolean p_8531_, Consumer<Packet<?>> p_8532_) {
      this.level = p_8528_;
      this.broadcast = p_8532_;
      this.entity = p_8529_;
      this.updateInterval = p_8530_;
      this.trackDelta = p_8531_;
      this.updateSentPos();
      this.yRotp = Mth.floor(p_8529_.getYRot() * 256.0F / 360.0F);
      this.xRotp = Mth.floor(p_8529_.getXRot() * 256.0F / 360.0F);
      this.yHeadRotp = Mth.floor(p_8529_.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = p_8529_.isOnGround();
   }

   public void sendChanges() {
      List<Entity> list = this.entity.getPassengers();
      if (!list.equals(this.lastPassengers)) {
         this.lastPassengers = list;
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity instanceof ItemFrame && this.tickCount % 10 == 0) {
         ItemFrame itemframe = (ItemFrame)this.entity;
         ItemStack itemstack = itemframe.getItem();
         Integer integer = MapItem.getMapId(itemstack);
         MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, this.level);
         if (mapitemsaveddata != null) {
               for(ServerPlayer serverplayer : this.level.players()) {
                  mapitemsaveddata.tickCarriedBy(serverplayer, itemstack);
                  Packet<?> packet = mapitemsaveddata.getUpdatePacket(integer, serverplayer);
                  if (packet != null) {
                     serverplayer.connection.send(packet);
                  }
               }
         }

         this.sendDirtyEntityData();
      }



      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         if (this.entity.isPassenger()) {
            int i1 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int l1 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            boolean flag1 = Math.abs(i1 - this.yRotp) >= 1 || Math.abs(l1 - this.xRotp) >= 1;
            if (flag1) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)i1, (byte)l1, this.entity.isOnGround()));
               this.yRotp = i1;
               this.xRotp = l1;
            }

            this.updateSentPos();
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            int l = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int k1 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            Vec3 vec3 = this.entity.position().subtract(ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp));
            boolean flag2 = vec3.lengthSqr() >= (double)7.6293945E-6F;
            Packet<?> packet1 = null;
            boolean flag3 = flag2 || this.tickCount % 60 == 0;
            boolean flag4 = Math.abs(l - this.yRotp) >= 1 || Math.abs(k1 - this.xRotp) >= 1;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long i = ClientboundMoveEntityPacket.entityToPacket(vec3.x);
               long j = ClientboundMoveEntityPacket.entityToPacket(vec3.y);
               long k = ClientboundMoveEntityPacket.entityToPacket(vec3.z);
               boolean flag = i < -32768L || i > 32767L || j < -32768L || j > 32767L || k < -32768L || k > 32767L;
               if (!flag && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.isOnGround()) {
                  if ((!flag3 || !flag4) && !(this.entity instanceof AbstractArrow)) {
                     if (flag3) {
                        packet1 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), this.entity.isOnGround());
                     } else if (flag4) {
                        packet1 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)l, (byte)k1, this.entity.isOnGround());
                     }
                  } else {
                     packet1 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), (byte)l, (byte)k1, this.entity.isOnGround());
                  }
               } else {
                  this.wasOnGround = this.entity.isOnGround();
                  this.teleportDelay = 0;
                  packet1 = new ClientboundTeleportEntityPacket(this.entity);
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vec3 vec31 = this.entity.getDeltaMovement();
               double d0 = vec31.distanceToSqr(this.ap);
               if (d0 > 1.0E-7D || d0 > 0.0D && vec31.lengthSqr() == 0.0D) {
                  this.ap = vec31;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if (packet1 != null) {
               this.broadcast.accept(packet1);
            }

            this.sendDirtyEntityData();
            if (flag3) {
               this.updateSentPos();
            }

            if (flag4) {
               this.yRotp = l;
               this.xRotp = k1;
            }

            this.wasRiding = false;
         }

         int j1 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(j1 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)j1));
            this.yHeadRotp = j1;
         }

         this.entity.hasImpulse = false;
      }

      ++this.tickCount;
      if (this.entity.hurtMarked) {
         this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
         this.entity.hurtMarked = false;
      }

   }

   public void removePairing(ServerPlayer p_8535_) {
      this.entity.stopSeenByPlayer(p_8535_);
      p_8535_.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
      net.minecraftforge.event.ForgeEventFactory.onStopEntityTracking(this.entity, p_8535_);
   }

   public void addPairing(ServerPlayer p_8542_) {
      this.sendPairingData(p_8542_.connection::send);
      this.entity.startSeenByPlayer(p_8542_);
      net.minecraftforge.event.ForgeEventFactory.onStartEntityTracking(this.entity, p_8542_);
   }

   public void sendPairingData(Consumer<Packet<?>> p_8537_) {
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", (Object)this.entity);
      }

      Packet<?> packet = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      p_8537_.accept(packet);
      if (!this.entity.getEntityData().isEmpty()) {
         p_8537_.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.entity.getEntityData(), true));
      }

      boolean flag = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         Collection<AttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
         if (!collection.isEmpty()) {
            p_8537_.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), collection));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            flag = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (flag && !(packet instanceof ClientboundAddMobPacket)) {
         p_8537_.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         List<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

         for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack = ((LivingEntity)this.entity).getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
               list.add(Pair.of(equipmentslot, itemstack.copy()));
            }
         }

         if (!list.isEmpty()) {
            p_8537_.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), list));
         }
      }

      if (this.entity instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)this.entity;

         for(MobEffectInstance mobeffectinstance : livingentity.getActiveEffects()) {
            p_8537_.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), mobeffectinstance));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         p_8537_.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         p_8537_.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof Mob) {
         Mob mob = (Mob)this.entity;
         if (mob.isLeashed()) {
            p_8537_.accept(new ClientboundSetEntityLinkPacket(mob, mob.getLeashHolder()));
         }
      }

   }

   private void sendDirtyEntityData() {
      SynchedEntityData synchedentitydata = this.entity.getEntityData();
      if (synchedentitydata.isDirty()) {
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), synchedentitydata, false));
      }

      if (this.entity instanceof LivingEntity) {
         Set<AttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
         if (!set.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), set));
         }

         set.clear();
      }

   }

   private void updateSentPos() {
      this.xp = ClientboundMoveEntityPacket.entityToPacket(this.entity.getX());
      this.yp = ClientboundMoveEntityPacket.entityToPacket(this.entity.getY());
      this.zp = ClientboundMoveEntityPacket.entityToPacket(this.entity.getZ());
   }

   public Vec3 sentPos() {
      return ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp);
   }

   private void broadcastAndSend(Packet<?> p_8539_) {
      this.broadcast.accept(p_8539_);
      if (this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(p_8539_);
      }

   }
}
