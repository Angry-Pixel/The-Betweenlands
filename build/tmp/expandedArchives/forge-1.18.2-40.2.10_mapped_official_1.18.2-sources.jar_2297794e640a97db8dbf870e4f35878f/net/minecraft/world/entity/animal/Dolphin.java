package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ConfiguredStructureTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class Dolphin extends WaterAnimal {
   private static final EntityDataAccessor<BlockPos> TREASURE_POS = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> GOT_FISH = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> MOISTNESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
   static final TargetingConditions SWIM_WITH_PLAYER_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
   public static final int TOTAL_AIR_SUPPLY = 4800;
   private static final int TOTAL_MOISTNESS_LEVEL = 2400;
   public static final Predicate<ItemEntity> ALLOWED_ITEMS = (p_28369_) -> {
      return !p_28369_.hasPickUpDelay() && p_28369_.isAlive() && p_28369_.isInWater();
   };

   public Dolphin(EntityType<? extends Dolphin> p_28316_, Level p_28317_) {
      super(p_28316_, p_28317_);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_28332_, DifficultyInstance p_28333_, MobSpawnType p_28334_, @Nullable SpawnGroupData p_28335_, @Nullable CompoundTag p_28336_) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      return super.finalizeSpawn(p_28332_, p_28333_, p_28334_, p_28335_, p_28336_);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected void handleAirSupply(int p_28326_) {
   }

   public void setTreasurePos(BlockPos p_28385_) {
      this.entityData.set(TREASURE_POS, p_28385_);
   }

   public BlockPos getTreasurePos() {
      return this.entityData.get(TREASURE_POS);
   }

   public boolean gotFish() {
      return this.entityData.get(GOT_FISH);
   }

   public void setGotFish(boolean p_28394_) {
      this.entityData.set(GOT_FISH, p_28394_);
   }

   public int getMoistnessLevel() {
      return this.entityData.get(MOISTNESS_LEVEL);
   }

   public void setMoisntessLevel(int p_28344_) {
      this.entityData.set(MOISTNESS_LEVEL, p_28344_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TREASURE_POS, BlockPos.ZERO);
      this.entityData.define(GOT_FISH, false);
      this.entityData.define(MOISTNESS_LEVEL, 2400);
   }

   public void addAdditionalSaveData(CompoundTag p_28364_) {
      super.addAdditionalSaveData(p_28364_);
      p_28364_.putInt("TreasurePosX", this.getTreasurePos().getX());
      p_28364_.putInt("TreasurePosY", this.getTreasurePos().getY());
      p_28364_.putInt("TreasurePosZ", this.getTreasurePos().getZ());
      p_28364_.putBoolean("GotFish", this.gotFish());
      p_28364_.putInt("Moistness", this.getMoistnessLevel());
   }

   public void readAdditionalSaveData(CompoundTag p_28340_) {
      int i = p_28340_.getInt("TreasurePosX");
      int j = p_28340_.getInt("TreasurePosY");
      int k = p_28340_.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(i, j, k));
      super.readAdditionalSaveData(p_28340_);
      this.setGotFish(p_28340_.getBoolean("GotFish"));
      this.setMoisntessLevel(p_28340_.getInt("Moistness"));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new Dolphin.DolphinSwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new Dolphin.DolphinSwimWithPlayerGoal(this, 4.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, (double)1.2F, true));
      this.goalSelector.addGoal(8, new Dolphin.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Guardian.class, 8.0F, 1.0D, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Guardian.class)).setAlertOthers());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)1.2F).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   protected PathNavigation createNavigation(Level p_28362_) {
      return new WaterBoundPathNavigation(this, p_28362_);
   }

   public boolean doHurtTarget(Entity p_28319_) {
      boolean flag = p_28319_.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_28319_);
         this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return flag;
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int p_28389_) {
      return this.getMaxAirSupply();
   }

   protected float getStandingEyeHeight(Pose p_28352_, EntityDimensions p_28353_) {
      return 0.3F;
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   protected boolean canRide(Entity p_28391_) {
      return true;
   }

   public boolean canTakeItem(ItemStack p_28376_) {
      EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(p_28376_);
      if (!this.getItemBySlot(equipmentslot).isEmpty()) {
         return false;
      } else {
         return equipmentslot == EquipmentSlot.MAINHAND && super.canTakeItem(p_28376_);
      }
   }

   protected void pickUpItem(ItemEntity p_28357_) {
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
         ItemStack itemstack = p_28357_.getItem();
         if (this.canHoldItem(itemstack)) {
            this.onItemPickup(p_28357_);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(p_28357_, itemstack.getCount());
            p_28357_.discard();
         }
      }

   }

   public void tick() {
      super.tick();
      if (this.isNoAi()) {
         this.setAirSupply(this.getMaxAirSupply());
      } else {
         if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
         } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
               this.hurt(DamageSource.DRY_OUT, 1.0F);
            }

            if (this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.setYRot(this.random.nextFloat() * 360.0F);
               this.onGround = false;
               this.hasImpulse = true;
            }
         }

         if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vec3 vec3 = this.getViewVector(0.0F);
            float f = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * 0.3F;
            float f1 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * 0.3F;
            float f2 = 1.2F - this.random.nextFloat() * 0.7F;

            for(int i = 0; i < 2; ++i) {
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f2 + (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 + (double)f1, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f2 - (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 - (double)f1, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   public void handleEntityEvent(byte p_28324_) {
      if (p_28324_ == 38) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleEntityEvent(p_28324_);
      }

   }

   private void addParticlesAroundSelf(ParticleOptions p_28338_) {
      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.01D;
         double d1 = this.random.nextGaussian() * 0.01D;
         double d2 = this.random.nextGaussian() * 0.01D;
         this.level.addParticle(p_28338_, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   protected InteractionResult mobInteract(Player p_28359_, InteractionHand p_28360_) {
      ItemStack itemstack = p_28359_.getItemInHand(p_28360_);
      if (!itemstack.isEmpty() && itemstack.is(ItemTags.FISHES)) {
         if (!this.level.isClientSide) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!p_28359_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_28359_, p_28360_);
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_28374_) {
      return SoundEvents.DOLPHIN_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.DOLPHIN_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DOLPHIN_SWIM;
   }

   protected boolean closeToNextPos() {
      BlockPos blockpos = this.getNavigation().getTargetPos();
      return blockpos != null ? blockpos.closerToCenterThan(this.position(), 12.0D) : false;
   }

   public void travel(Vec3 p_28383_) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), p_28383_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_28383_);
      }

   }

   public boolean canBeLeashed(Player p_28330_) {
      return true;
   }

   static class DolphinSwimToTreasureGoal extends Goal {
      private final Dolphin dolphin;
      private boolean stuck;

      DolphinSwimToTreasureGoal(Dolphin p_28402_) {
         this.dolphin = p_28402_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean isInterruptable() {
         return false;
      }

      public boolean canUse() {
         return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
      }

      public boolean canContinueToUse() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         return !(new BlockPos((double)blockpos.getX(), this.dolphin.getY(), (double)blockpos.getZ())).closerToCenterThan(this.dolphin.position(), 4.0D) && !this.stuck && this.dolphin.getAirSupply() >= 100;
      }

      public void start() {
         if (this.dolphin.level instanceof ServerLevel) {
            ServerLevel serverlevel = (ServerLevel)this.dolphin.level;
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos blockpos = this.dolphin.blockPosition();
            BlockPos blockpos1 = serverlevel.findNearestMapFeature(ConfiguredStructureTags.DOLPHIN_LOCATED, blockpos, 50, false);
            if (blockpos1 != null) {
               this.dolphin.setTreasurePos(blockpos1);
               serverlevel.broadcastEntityEvent(this.dolphin, (byte)38);
            } else {
               this.stuck = true;
            }
         }
      }

      public void stop() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         if ((new BlockPos((double)blockpos.getX(), this.dolphin.getY(), (double)blockpos.getZ())).closerToCenterThan(this.dolphin.position(), 4.0D) || this.stuck) {
            this.dolphin.setGotFish(false);
         }

      }

      public void tick() {
         Level level = this.dolphin.level;
         if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
            Vec3 vec3 = Vec3.atCenterOf(this.dolphin.getTreasurePos());
            Vec3 vec31 = DefaultRandomPos.getPosTowards(this.dolphin, 16, 1, vec3, (double)((float)Math.PI / 8F));
            if (vec31 == null) {
               vec31 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 4, vec3, (double)((float)Math.PI / 2F));
            }

            if (vec31 != null) {
               BlockPos blockpos = new BlockPos(vec31);
               if (!level.getFluidState(blockpos).is(FluidTags.WATER) || !level.getBlockState(blockpos).isPathfindable(level, blockpos, PathComputationType.WATER)) {
                  vec31 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 5, vec3, (double)((float)Math.PI / 2F));
               }
            }

            if (vec31 == null) {
               this.stuck = true;
               return;
            }

            this.dolphin.getLookControl().setLookAt(vec31.x, vec31.y, vec31.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            this.dolphin.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, 1.3D);
            if (level.random.nextInt(this.adjustedTickDelay(80)) == 0) {
               level.broadcastEntityEvent(this.dolphin, (byte)38);
            }
         }

      }
   }

   static class DolphinSwimWithPlayerGoal extends Goal {
      private final Dolphin dolphin;
      private final double speedModifier;
      @Nullable
      private Player player;

      DolphinSwimWithPlayerGoal(Dolphin p_28413_, double p_28414_) {
         this.dolphin = p_28413_;
         this.speedModifier = p_28414_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         this.player = this.dolphin.level.getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         if (this.player == null) {
            return false;
         } else {
            return this.player.isSwimming() && this.dolphin.getTarget() != this.player;
         }
      }

      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0D;
      }

      public void start() {
         this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
      }

      public void stop() {
         this.player = null;
         this.dolphin.getNavigation().stop();
      }

      public void tick() {
         this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
         if (this.dolphin.distanceToSqr(this.player) < 6.25D) {
            this.dolphin.getNavigation().stop();
         } else {
            this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
         }

         if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
         }

      }
   }

   class PlayWithItemsGoal extends Goal {
      private int cooldown;

      public boolean canUse() {
         if (this.cooldown > Dolphin.this.tickCount) {
            return false;
         } else {
            List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
            return !list.isEmpty() || !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         }
      }

      public void start() {
         List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         if (!list.isEmpty()) {
            Dolphin.this.getNavigation().moveTo(list.get(0), (double)1.2F);
            Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.cooldown = 0;
      }

      public void stop() {
         ItemStack itemstack = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!itemstack.isEmpty()) {
            this.drop(itemstack);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
         }

      }

      public void tick() {
         List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         ItemStack itemstack = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!itemstack.isEmpty()) {
            this.drop(itemstack);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         } else if (!list.isEmpty()) {
            Dolphin.this.getNavigation().moveTo(list.get(0), (double)1.2F);
         }

      }

      private void drop(ItemStack p_28429_) {
         if (!p_28429_.isEmpty()) {
            double d0 = Dolphin.this.getEyeY() - (double)0.3F;
            ItemEntity itementity = new ItemEntity(Dolphin.this.level, Dolphin.this.getX(), d0, Dolphin.this.getZ(), p_28429_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(Dolphin.this.getUUID());
            float f = 0.3F;
            float f1 = Dolphin.this.random.nextFloat() * ((float)Math.PI * 2F);
            float f2 = 0.02F * Dolphin.this.random.nextFloat();
            itementity.setDeltaMovement((double)(0.3F * -Mth.sin(Dolphin.this.getYRot() * ((float)Math.PI / 180F)) * Mth.cos(Dolphin.this.getXRot() * ((float)Math.PI / 180F)) + Mth.cos(f1) * f2), (double)(0.3F * Mth.sin(Dolphin.this.getXRot() * ((float)Math.PI / 180F)) * 1.5F), (double)(0.3F * Mth.cos(Dolphin.this.getYRot() * ((float)Math.PI / 180F)) * Mth.cos(Dolphin.this.getXRot() * ((float)Math.PI / 180F)) + Mth.sin(f1) * f2));
            Dolphin.this.level.addFreshEntity(itementity);
         }
      }
   }
}