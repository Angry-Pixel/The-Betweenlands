package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class GlowSquid extends Squid {
   private static final EntityDataAccessor<Integer> DATA_DARK_TICKS_REMAINING = SynchedEntityData.defineId(GlowSquid.class, EntityDataSerializers.INT);

   public GlowSquid(EntityType<? extends GlowSquid> p_147111_, Level p_147112_) {
      super(p_147111_, p_147112_);
   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.GLOW_SQUID_INK;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_DARK_TICKS_REMAINING, 0);
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.GLOW_SQUID_SQUIRT;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GLOW_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_147124_) {
      return SoundEvents.GLOW_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GLOW_SQUID_DEATH;
   }

   public void addAdditionalSaveData(CompoundTag p_147122_) {
      super.addAdditionalSaveData(p_147122_);
      p_147122_.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
   }

   public void readAdditionalSaveData(CompoundTag p_147117_) {
      super.readAdditionalSaveData(p_147117_);
      this.setDarkTicks(p_147117_.getInt("DarkTicksRemaining"));
   }

   public void aiStep() {
      super.aiStep();
      int i = this.getDarkTicksRemaining();
      if (i > 0) {
         this.setDarkTicks(i - 1);
      }

      this.level.addParticle(ParticleTypes.GLOW, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
   }

   public boolean hurt(DamageSource p_147114_, float p_147115_) {
      boolean flag = super.hurt(p_147114_, p_147115_);
      if (flag) {
         this.setDarkTicks(100);
      }

      return flag;
   }

   private void setDarkTicks(int p_147120_) {
      this.entityData.set(DATA_DARK_TICKS_REMAINING, p_147120_);
   }

   public int getDarkTicksRemaining() {
      return this.entityData.get(DATA_DARK_TICKS_REMAINING);
   }

   public static boolean checkGlowSquideSpawnRules(EntityType<? extends LivingEntity> p_186002_, ServerLevelAccessor p_186003_, MobSpawnType p_186004_, BlockPos p_186005_, Random p_186006_) {
      return p_186005_.getY() <= p_186003_.getSeaLevel() - 33 && p_186003_.getRawBrightness(p_186005_, 0) == 0 && p_186003_.getBlockState(p_186005_).is(Blocks.WATER);
   }
}