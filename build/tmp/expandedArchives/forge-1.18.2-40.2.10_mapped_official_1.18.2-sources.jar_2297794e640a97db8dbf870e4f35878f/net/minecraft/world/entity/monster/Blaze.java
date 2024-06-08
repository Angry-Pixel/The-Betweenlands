package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Blaze extends Monster {
   private float allowedHeightOffset = 0.5F;
   private int nextHeightOffsetChangeTick;
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Blaze.class, EntityDataSerializers.BYTE);

   public Blaze(EntityType<? extends Blaze> p_32219_, Level p_32220_) {
      super(p_32219_, p_32220_);
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
      this.xpReward = 10;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new Blaze.BlazeAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.FOLLOW_RANGE, 48.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.BLAZE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_32235_) {
      return SoundEvents.BLAZE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BLAZE_DEATH;
   }

   public float getBrightness() {
      return 1.0F;
   }

   public void aiStep() {
      if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

      if (this.level.isClientSide) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
         }
      }

      super.aiStep();
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   protected void customServerAiStep() {
      --this.nextHeightOffsetChangeTick;
      if (this.nextHeightOffsetChangeTick <= 0) {
         this.nextHeightOffsetChangeTick = 100;
         this.allowedHeightOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;
      }

      LivingEntity livingentity = this.getTarget();
      if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(livingentity)) {
         Vec3 vec3 = this.getDeltaMovement();
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double)0.3F - vec3.y) * (double)0.3F, 0.0D));
         this.hasImpulse = true;
      }

      super.customServerAiStep();
   }

   public boolean causeFallDamage(float p_149683_, float p_149684_, DamageSource p_149685_) {
      return false;
   }

   public boolean isOnFire() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   void setCharged(boolean p_32241_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_32241_) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, b0);
   }

   static class BlazeAttackGoal extends Goal {
      private final Blaze blaze;
      private int attackStep;
      private int attackTime;
      private int lastSeen;

      public BlazeAttackGoal(Blaze p_32247_) {
         this.blaze = p_32247_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.blaze.getTarget();
         return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
      }

      public void start() {
         this.attackStep = 0;
      }

      public void stop() {
         this.blaze.setCharged(false);
         this.lastSeen = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         --this.attackTime;
         LivingEntity livingentity = this.blaze.getTarget();
         if (livingentity != null) {
            boolean flag = this.blaze.getSensing().hasLineOfSight(livingentity);
            if (flag) {
               this.lastSeen = 0;
            } else {
               ++this.lastSeen;
            }

            double d0 = this.blaze.distanceToSqr(livingentity);
            if (d0 < 4.0D) {
               if (!flag) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.blaze.doHurtTarget(livingentity);
               }

               this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
               double d1 = livingentity.getX() - this.blaze.getX();
               double d2 = livingentity.getY(0.5D) - this.blaze.getY(0.5D);
               double d3 = livingentity.getZ() - this.blaze.getZ();
               if (this.attackTime <= 0) {
                  ++this.attackStep;
                  if (this.attackStep == 1) {
                     this.attackTime = 60;
                     this.blaze.setCharged(true);
                  } else if (this.attackStep <= 4) {
                     this.attackTime = 6;
                  } else {
                     this.attackTime = 100;
                     this.attackStep = 0;
                     this.blaze.setCharged(false);
                  }

                  if (this.attackStep > 1) {
                     double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;
                     if (!this.blaze.isSilent()) {
                        this.blaze.level.levelEvent((Player)null, 1018, this.blaze.blockPosition(), 0);
                     }

                     for(int i = 0; i < 1; ++i) {
                        SmallFireball smallfireball = new SmallFireball(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * d4, d2, d3 + this.blaze.getRandom().nextGaussian() * d4);
                        smallfireball.setPos(smallfireball.getX(), this.blaze.getY(0.5D) + 0.5D, smallfireball.getZ());
                        this.blaze.level.addFreshEntity(smallfireball);
                     }
                  }
               }

               this.blaze.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
               this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
      }
   }
}