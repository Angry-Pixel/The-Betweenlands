package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class EndCrystal extends Entity {
   private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
   private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
   public int time;

   public EndCrystal(EntityType<? extends EndCrystal> p_31037_, Level p_31038_) {
      super(p_31037_, p_31038_);
      this.blocksBuilding = true;
      this.time = this.random.nextInt(100000);
   }

   public EndCrystal(Level p_31040_, double p_31041_, double p_31042_, double p_31043_) {
      this(EntityType.END_CRYSTAL, p_31040_);
      this.setPos(p_31041_, p_31042_, p_31043_);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
      this.getEntityData().define(DATA_SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.time;
      if (this.level instanceof ServerLevel) {
         BlockPos blockpos = this.blockPosition();
         if (((ServerLevel)this.level).dragonFight() != null && this.level.getBlockState(blockpos).isAir()) {
            this.level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level, blockpos));
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag p_31062_) {
      if (this.getBeamTarget() != null) {
         p_31062_.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
      }

      p_31062_.putBoolean("ShowBottom", this.showsBottom());
   }

   protected void readAdditionalSaveData(CompoundTag p_31055_) {
      if (p_31055_.contains("BeamTarget", 10)) {
         this.setBeamTarget(NbtUtils.readBlockPos(p_31055_.getCompound("BeamTarget")));
      }

      if (p_31055_.contains("ShowBottom", 1)) {
         this.setShowBottom(p_31055_.getBoolean("ShowBottom"));
      }

   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource p_31050_, float p_31051_) {
      if (this.isInvulnerableTo(p_31050_)) {
         return false;
      } else if (p_31050_.getEntity() instanceof EnderDragon) {
         return false;
      } else {
         if (!this.isRemoved() && !this.level.isClientSide) {
            this.remove(Entity.RemovalReason.KILLED);
            if (!p_31050_.isExplosion()) {
               this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), 6.0F, Explosion.BlockInteraction.DESTROY);
            }

            this.onDestroyedBy(p_31050_);
         }

         return true;
      }
   }

   public void kill() {
      this.onDestroyedBy(DamageSource.GENERIC);
      super.kill();
   }

   private void onDestroyedBy(DamageSource p_31048_) {
      if (this.level instanceof ServerLevel) {
         EndDragonFight enddragonfight = ((ServerLevel)this.level).dragonFight();
         if (enddragonfight != null) {
            enddragonfight.onCrystalDestroyed(this, p_31048_);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos p_31053_) {
      this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(p_31053_));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getEntityData().get(DATA_BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean p_31057_) {
      this.getEntityData().set(DATA_SHOW_BOTTOM, p_31057_);
   }

   public boolean showsBottom() {
      return this.getEntityData().get(DATA_SHOW_BOTTOM);
   }

   public boolean shouldRenderAtSqrDistance(double p_31046_) {
      return super.shouldRenderAtSqrDistance(p_31046_) || this.getBeamTarget() != null;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.END_CRYSTAL);
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}