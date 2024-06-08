package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class MinecartTNT extends AbstractMinecart {
   private static final byte EVENT_PRIME = 10;
   private int fuse = -1;

   public MinecartTNT(EntityType<? extends MinecartTNT> p_38649_, Level p_38650_) {
      super(p_38649_, p_38650_);
   }

   public MinecartTNT(Level p_38652_, double p_38653_, double p_38654_, double p_38655_) {
      super(EntityType.TNT_MINECART, p_38652_, p_38653_, p_38654_, p_38655_);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.TNT;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.TNT.defaultBlockState();
   }

   public void tick() {
      super.tick();
      if (this.fuse > 0) {
         --this.fuse;
         this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
      } else if (this.fuse == 0) {
         this.explode(this.getDeltaMovement().horizontalDistanceSqr());
      }

      if (this.horizontalCollision) {
         double d0 = this.getDeltaMovement().horizontalDistanceSqr();
         if (d0 >= (double)0.01F) {
            this.explode(d0);
         }
      }

   }

   public boolean hurt(DamageSource p_38666_, float p_38667_) {
      Entity entity = p_38666_.getDirectEntity();
      if (entity instanceof AbstractArrow) {
         AbstractArrow abstractarrow = (AbstractArrow)entity;
         if (abstractarrow.isOnFire()) {
            this.explode(abstractarrow.getDeltaMovement().lengthSqr());
         }
      }

      return super.hurt(p_38666_, p_38667_);
   }

   public void destroy(DamageSource p_38664_) {
      double d0 = this.getDeltaMovement().horizontalDistanceSqr();
      if (!p_38664_.isFire() && !p_38664_.isExplosion() && !(d0 >= (double)0.01F)) {
         super.destroy(p_38664_);
         if (!p_38664_.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
         }

      } else {
         if (this.fuse < 0) {
            this.primeFuse();
            this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected void explode(double p_38689_) {
      if (!this.level.isClientSide) {
         double d0 = Math.sqrt(p_38689_);
         if (d0 > 5.0D) {
            d0 = 5.0D;
         }

         this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)(4.0D + this.random.nextDouble() * 1.5D * d0), Explosion.BlockInteraction.BREAK);
         this.discard();
      }

   }

   public boolean causeFallDamage(float p_150347_, float p_150348_, DamageSource p_150349_) {
      if (p_150347_ >= 3.0F) {
         float f = p_150347_ / 10.0F;
         this.explode((double)(f * f));
      }

      return super.causeFallDamage(p_150347_, p_150348_, p_150349_);
   }

   public void activateMinecart(int p_38659_, int p_38660_, int p_38661_, boolean p_38662_) {
      if (p_38662_ && this.fuse < 0) {
         this.primeFuse();
      }

   }

   public void handleEntityEvent(byte p_38657_) {
      if (p_38657_ == 10) {
         this.primeFuse();
      } else {
         super.handleEntityEvent(p_38657_);
      }

   }

   public void primeFuse() {
      this.fuse = 80;
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)10);
         if (!this.isSilent()) {
            this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   public int getFuse() {
      return this.fuse;
   }

   public boolean isPrimed() {
      return this.fuse > -1;
   }

   public float getBlockExplosionResistance(Explosion p_38675_, BlockGetter p_38676_, BlockPos p_38677_, BlockState p_38678_, FluidState p_38679_, float p_38680_) {
      return !this.isPrimed() || !p_38678_.is(BlockTags.RAILS) && !p_38676_.getBlockState(p_38677_.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(p_38675_, p_38676_, p_38677_, p_38678_, p_38679_, p_38680_) : 0.0F;
   }

   public boolean shouldBlockExplode(Explosion p_38669_, BlockGetter p_38670_, BlockPos p_38671_, BlockState p_38672_, float p_38673_) {
      return !this.isPrimed() || !p_38672_.is(BlockTags.RAILS) && !p_38670_.getBlockState(p_38671_.above()).is(BlockTags.RAILS) ? super.shouldBlockExplode(p_38669_, p_38670_, p_38671_, p_38672_, p_38673_) : false;
   }

   protected void readAdditionalSaveData(CompoundTag p_38682_) {
      super.readAdditionalSaveData(p_38682_);
      if (p_38682_.contains("TNTFuse", 99)) {
         this.fuse = p_38682_.getInt("TNTFuse");
      }

   }

   protected void addAdditionalSaveData(CompoundTag p_38687_) {
      super.addAdditionalSaveData(p_38687_);
      p_38687_.putInt("TNTFuse", this.fuse);
   }
}