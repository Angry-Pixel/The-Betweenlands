package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class Squid extends WaterAnimal {
   public float xBodyRot;
   public float xBodyRotO;
   public float zBodyRot;
   public float zBodyRotO;
   public float tentacleMovement;
   public float oldTentacleMovement;
   public float tentacleAngle;
   public float oldTentacleAngle;
   private float speed;
   private float tentacleSpeed;
   private float rotateSpeed;
   private float tx;
   private float ty;
   private float tz;

   public Squid(EntityType<? extends Squid> p_29953_, Level p_29954_) {
      super(p_29953_, p_29954_);
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Squid.SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new Squid.SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D);
   }

   protected float getStandingEyeHeight(Pose p_29975_, EntityDimensions p_29976_) {
      return p_29976_.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_29980_) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.SQUID_SQUIRT;
   }

   public boolean canBeLeashed(Player p_149052_) {
      return !this.isLeashed();
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > (Math.PI * 2D)) {
         if (this.level.isClientSide) {
            this.tentacleMovement = ((float)Math.PI * 2F);
         } else {
            this.tentacleMovement -= ((float)Math.PI * 2F);
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level.broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubble()) {
         if (this.tentacleMovement < (float)Math.PI) {
            float f = this.tentacleMovement / (float)Math.PI;
            this.tentacleAngle = Mth.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25F;
            if ((double)f > 0.75D) {
               this.speed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.speed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.level.isClientSide) {
            this.setDeltaMovement((double)(this.tx * this.speed), (double)(this.ty * this.speed), (double)(this.tz * this.speed));
         }

         Vec3 vec3 = this.getDeltaMovement();
         double d0 = vec3.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2(vec3.x, vec3.z)) * (180F / (float)Math.PI) - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot += (float)Math.PI * this.rotateSpeed * 1.5F;
         this.xBodyRot += (-((float)Mth.atan2(d0, vec3.y)) * (180F / (float)Math.PI) - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * (float)Math.PI * 0.25F;
         if (!this.level.isClientSide) {
            double d1 = this.getDeltaMovement().y;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               d1 = 0.05D * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else if (!this.isNoGravity()) {
               d1 -= 0.08D;
            }

            this.setDeltaMovement(0.0D, d1 * (double)0.98F, 0.0D);
         }

         this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
      }

   }

   public boolean hurt(DamageSource p_29963_, float p_29964_) {
      if (super.hurt(p_29963_, p_29964_) && this.getLastHurtByMob() != null) {
         if (!this.level.isClientSide) {
            this.spawnInk();
         }

         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(Vec3 p_29986_) {
      Vec3 vec3 = p_29986_.xRot(this.xBodyRotO * ((float)Math.PI / 180F));
      return vec3.yRot(-this.yBodyRotO * ((float)Math.PI / 180F));
   }

   private void spawnInk() {
      this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
      Vec3 vec3 = this.rotateVector(new Vec3(0.0D, -1.0D, 0.0D)).add(this.getX(), this.getY(), this.getZ());

      for(int i = 0; i < 30; ++i) {
         Vec3 vec31 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.random.nextFloat() * 0.6D - 0.3D));
         Vec3 vec32 = vec31.scale(0.3D + (double)(this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level).sendParticles(this.getInkParticle(), vec3.x, vec3.y + 0.5D, vec3.z, 0, vec32.x, vec32.y, vec32.z, (double)0.1F);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(Vec3 p_29984_) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void handleEntityEvent(byte p_29957_) {
      if (p_29957_ == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(p_29957_);
      }

   }

   public void setMovementVector(float p_29959_, float p_29960_, float p_29961_) {
      this.tx = p_29959_;
      this.ty = p_29960_;
      this.tz = p_29961_;
   }

   public boolean hasMovementVector() {
      return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
   }

   class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      public boolean canUse() {
         LivingEntity livingentity = Squid.this.getLastHurtByMob();
         if (Squid.this.isInWater() && livingentity != null) {
            return Squid.this.distanceToSqr(livingentity) < 100.0D;
         } else {
            return false;
         }
      }

      public void start() {
         this.fleeTicks = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         ++this.fleeTicks;
         LivingEntity livingentity = Squid.this.getLastHurtByMob();
         if (livingentity != null) {
            Vec3 vec3 = new Vec3(Squid.this.getX() - livingentity.getX(), Squid.this.getY() - livingentity.getY(), Squid.this.getZ() - livingentity.getZ());
            BlockState blockstate = Squid.this.level.getBlockState(new BlockPos(Squid.this.getX() + vec3.x, Squid.this.getY() + vec3.y, Squid.this.getZ() + vec3.z));
            FluidState fluidstate = Squid.this.level.getFluidState(new BlockPos(Squid.this.getX() + vec3.x, Squid.this.getY() + vec3.y, Squid.this.getZ() + vec3.z));
            if (fluidstate.is(FluidTags.WATER) || blockstate.isAir()) {
               double d0 = vec3.length();
               if (d0 > 0.0D) {
                  vec3.normalize();
                  double d1 = 3.0D;
                  if (d0 > 5.0D) {
                     d1 -= (d0 - 5.0D) / 5.0D;
                  }

                  if (d1 > 0.0D) {
                     vec3 = vec3.scale(d1);
                  }
               }

               if (blockstate.isAir()) {
                  vec3 = vec3.subtract(0.0D, vec3.y, 0.0D);
               }

               Squid.this.setMovementVector((float)vec3.x / 20.0F, (float)vec3.y / 20.0F, (float)vec3.z / 20.0F);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level.addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }

   class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(Squid p_30004_) {
         this.squid = p_30004_;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int i = this.squid.getNoActionTime();
         if (i > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
            float f1 = Mth.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
            float f3 = Mth.sin(f) * 0.2F;
            this.squid.setMovementVector(f1, f2, f3);
         }

      }
   }
}