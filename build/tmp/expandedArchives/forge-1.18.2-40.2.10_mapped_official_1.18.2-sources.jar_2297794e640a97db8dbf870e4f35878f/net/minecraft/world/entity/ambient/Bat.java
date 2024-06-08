package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bat extends AmbientCreature {
   public static final float FLAP_DEGREES_PER_TICK = 74.48451F;
   public static final int TICKS_PER_FLAP = Mth.ceil(2.4166098F);
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
   private static final int FLAG_RESTING = 1;
   private static final TargetingConditions BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0D);
   @Nullable
   private BlockPos targetPosition;

   public Bat(EntityType<? extends Bat> p_27412_, Level p_27413_) {
      super(p_27412_, p_27413_);
      this.setResting(true);
   }

   public boolean isFlapping() {
      return !this.isResting() && this.tickCount % TICKS_PER_FLAP == 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   public float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_27451_) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity p_27415_) {
   }

   protected void pushEntities() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D);
   }

   public boolean isResting() {
      return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
   }

   public void setResting(boolean p_27457_) {
      byte b0 = this.entityData.get(DATA_ID_FLAGS);
      if (p_27457_) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.isResting()) {
         this.setDeltaMovement(Vec3.ZERO);
         this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0D - (double)this.getBbHeight(), this.getZ());
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      BlockPos blockpos = this.blockPosition();
      BlockPos blockpos1 = blockpos.above();
      if (this.isResting()) {
         boolean flag = this.isSilent();
         if (this.level.getBlockState(blockpos1).isRedstoneConductor(this.level, blockpos)) {
            if (this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               if (!flag) {
                  this.level.levelEvent((Player)null, 1025, blockpos, 0);
               }
            }
         } else {
            this.setResting(false);
            if (!flag) {
               this.level.levelEvent((Player)null, 1025, blockpos, 0);
            }
         }
      } else {
         if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
            this.targetPosition = null;
         }

         if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0D)) {
            this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double d2 = (double)this.targetPosition.getX() + 0.5D - this.getX();
         double d0 = (double)this.targetPosition.getY() + 0.1D - this.getY();
         double d1 = (double)this.targetPosition.getZ() + 0.5D - this.getZ();
         Vec3 vec3 = this.getDeltaMovement();
         Vec3 vec31 = vec3.add((Math.signum(d2) * 0.5D - vec3.x) * (double)0.1F, (Math.signum(d0) * (double)0.7F - vec3.y) * (double)0.1F, (Math.signum(d1) * 0.5D - vec3.z) * (double)0.1F);
         this.setDeltaMovement(vec31);
         float f = (float)(Mth.atan2(vec31.z, vec31.x) * (double)(180F / (float)Math.PI)) - 90.0F;
         float f1 = Mth.wrapDegrees(f - this.getYRot());
         this.zza = 0.5F;
         this.setYRot(this.getYRot() + f1);
         if (this.random.nextInt(100) == 0 && this.level.getBlockState(blockpos1).isRedstoneConductor(this.level, blockpos1)) {
            this.setResting(true);
         }
      }

   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public boolean causeFallDamage(float p_148702_, float p_148703_, DamageSource p_148704_) {
      return false;
   }

   protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean hurt(DamageSource p_27424_, float p_27425_) {
      if (this.isInvulnerableTo(p_27424_)) {
         return false;
      } else {
         if (!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
         }

         return super.hurt(p_27424_, p_27425_);
      }
   }

   public void readAdditionalSaveData(CompoundTag p_27427_) {
      super.readAdditionalSaveData(p_27427_);
      this.entityData.set(DATA_ID_FLAGS, p_27427_.getByte("BatFlags"));
   }

   public void addAdditionalSaveData(CompoundTag p_27443_) {
      super.addAdditionalSaveData(p_27443_);
      p_27443_.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
   }

   public static boolean checkBatSpawnRules(EntityType<Bat> p_27434_, LevelAccessor p_27435_, MobSpawnType p_27436_, BlockPos p_27437_, Random p_27438_) {
      if (p_27437_.getY() >= p_27435_.getSeaLevel()) {
         return false;
      } else {
         int i = p_27435_.getMaxLocalRawBrightness(p_27437_);
         int j = 4;
         if (isHalloween()) {
            j = 7;
         } else if (p_27438_.nextBoolean()) {
            return false;
         }

         return i > p_27438_.nextInt(j) ? false : checkMobSpawnRules(p_27434_, p_27435_, p_27436_, p_27437_, p_27438_);
      }
   }

   private static boolean isHalloween() {
      LocalDate localdate = LocalDate.now();
      int i = localdate.get(ChronoField.DAY_OF_MONTH);
      int j = localdate.get(ChronoField.MONTH_OF_YEAR);
      return j == 10 && i >= 20 || j == 11 && i <= 3;
   }

   protected float getStandingEyeHeight(Pose p_27440_, EntityDimensions p_27441_) {
      return p_27441_.height / 2.0F;
   }
}