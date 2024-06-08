package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class Vex extends Monster {
   public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
   public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Vex.class, EntityDataSerializers.BYTE);
   private static final int FLAG_IS_CHARGING = 1;
   @Nullable
   Mob owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean hasLimitedLife;
   private int limitedLifeTicks;

   public Vex(EntityType<? extends Vex> p_33984_, Level p_33985_) {
      super(p_33984_, p_33985_);
      this.moveControl = new Vex.VexMoveControl(this);
      this.xpReward = 3;
   }

   public boolean isFlapping() {
      return this.tickCount % TICKS_PER_FLAP == 0;
   }

   public void move(MoverType p_33997_, Vec3 p_33998_) {
      super.move(p_33997_, p_33998_);
      this.checkInsideBlocks();
   }

   public void tick() {
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
      if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.hurt(DamageSource.STARVE, 1.0F);
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new Vex.VexChargeAttackGoal());
      this.goalSelector.addGoal(8, new Vex.VexRandomMoveGoal());
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new Vex.VexCopyOwnerTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundTag p_34008_) {
      super.readAdditionalSaveData(p_34008_);
      if (p_34008_.contains("BoundX")) {
         this.boundOrigin = new BlockPos(p_34008_.getInt("BoundX"), p_34008_.getInt("BoundY"), p_34008_.getInt("BoundZ"));
      }

      if (p_34008_.contains("LifeTicks")) {
         this.setLimitedLife(p_34008_.getInt("LifeTicks"));
      }

   }

   public void addAdditionalSaveData(CompoundTag p_34015_) {
      super.addAdditionalSaveData(p_34015_);
      if (this.boundOrigin != null) {
         p_34015_.putInt("BoundX", this.boundOrigin.getX());
         p_34015_.putInt("BoundY", this.boundOrigin.getY());
         p_34015_.putInt("BoundZ", this.boundOrigin.getZ());
      }

      if (this.hasLimitedLife) {
         p_34015_.putInt("LifeTicks", this.limitedLifeTicks);
      }

   }

   @Nullable
   public Mob getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos p_34034_) {
      this.boundOrigin = p_34034_;
   }

   private boolean getVexFlag(int p_34011_) {
      int i = this.entityData.get(DATA_FLAGS_ID);
      return (i & p_34011_) != 0;
   }

   private void setVexFlag(int p_33990_, boolean p_33991_) {
      int i = this.entityData.get(DATA_FLAGS_ID);
      if (p_33991_) {
         i |= p_33990_;
      } else {
         i &= ~p_33990_;
      }

      this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setIsCharging(boolean p_34043_) {
      this.setVexFlag(1, p_34043_);
   }

   public void setOwner(Mob p_33995_) {
      this.owner = p_33995_;
   }

   public void setLimitedLife(int p_33988_) {
      this.hasLimitedLife = true;
      this.limitedLifeTicks = p_33988_;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_34023_) {
      return SoundEvents.VEX_HURT;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34002_, DifficultyInstance p_34003_, MobSpawnType p_34004_, @Nullable SpawnGroupData p_34005_, @Nullable CompoundTag p_34006_) {
      this.populateDefaultEquipmentSlots(p_34003_);
      this.populateDefaultEquipmentEnchantments(p_34003_);
      return super.finalizeSpawn(p_34002_, p_34003_, p_34004_, p_34005_, p_34006_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_33993_) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   class VexChargeAttackGoal extends Goal {
      public VexChargeAttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (Vex.this.getTarget() != null && !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(reducedTickDelay(7)) == 0) {
            return Vex.this.distanceToSqr(Vex.this.getTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return Vex.this.getMoveControl().hasWanted() && Vex.this.isCharging() && Vex.this.getTarget() != null && Vex.this.getTarget().isAlive();
      }

      public void start() {
         LivingEntity livingentity = Vex.this.getTarget();
         if (livingentity != null) {
            Vec3 vec3 = livingentity.getEyePosition();
            Vex.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
         }

         Vex.this.setIsCharging(true);
         Vex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
      }

      public void stop() {
         Vex.this.setIsCharging(false);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity livingentity = Vex.this.getTarget();
         if (livingentity != null) {
            if (Vex.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
               Vex.this.doHurtTarget(livingentity);
               Vex.this.setIsCharging(false);
            } else {
               double d0 = Vex.this.distanceToSqr(livingentity);
               if (d0 < 9.0D) {
                  Vec3 vec3 = livingentity.getEyePosition();
                  Vex.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
               }
            }

         }
      }
   }

   class VexCopyOwnerTargetGoal extends TargetGoal {
      private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

      public VexCopyOwnerTargetGoal(PathfinderMob p_34056_) {
         super(p_34056_, false);
      }

      public boolean canUse() {
         return Vex.this.owner != null && Vex.this.owner.getTarget() != null && this.canAttack(Vex.this.owner.getTarget(), this.copyOwnerTargeting);
      }

      public void start() {
         Vex.this.setTarget(Vex.this.owner.getTarget());
         super.start();
      }
   }

   class VexMoveControl extends MoveControl {
      public VexMoveControl(Vex p_34062_) {
         super(p_34062_);
      }

      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vec3 = new Vec3(this.wantedX - Vex.this.getX(), this.wantedY - Vex.this.getY(), this.wantedZ - Vex.this.getZ());
            double d0 = vec3.length();
            if (d0 < Vex.this.getBoundingBox().getSize()) {
               this.operation = MoveControl.Operation.WAIT;
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().scale(0.5D));
            } else {
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
               if (Vex.this.getTarget() == null) {
                  Vec3 vec31 = Vex.this.getDeltaMovement();
                  Vex.this.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
                  Vex.this.yBodyRot = Vex.this.getYRot();
               } else {
                  double d2 = Vex.this.getTarget().getX() - Vex.this.getX();
                  double d1 = Vex.this.getTarget().getZ() - Vex.this.getZ();
                  Vex.this.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                  Vex.this.yBodyRot = Vex.this.getYRot();
               }
            }

         }
      }
   }

   class VexRandomMoveGoal extends Goal {
      public VexRandomMoveGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(reducedTickDelay(7)) == 0;
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void tick() {
         BlockPos blockpos = Vex.this.getBoundOrigin();
         if (blockpos == null) {
            blockpos = Vex.this.blockPosition();
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.offset(Vex.this.random.nextInt(15) - 7, Vex.this.random.nextInt(11) - 5, Vex.this.random.nextInt(15) - 7);
            if (Vex.this.level.isEmptyBlock(blockpos1)) {
               Vex.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
               if (Vex.this.getTarget() == null) {
                  Vex.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}