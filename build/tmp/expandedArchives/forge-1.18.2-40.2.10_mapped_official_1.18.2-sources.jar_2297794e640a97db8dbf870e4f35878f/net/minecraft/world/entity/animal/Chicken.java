package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Chicken extends Animal {
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   private float nextFlap = 1.0F;
   public int eggTime = this.random.nextInt(6000) + 6000;
   public boolean isChickenJockey;

   public Chicken(EntityType<? extends Chicken> p_28236_, Level p_28237_) {
      super(p_28236_, p_28237_);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, FOOD_ITEMS, false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   protected float getStandingEyeHeight(Pose p_28251_, EntityDimensions p_28252_) {
      return this.isBaby() ? p_28252_.height * 0.85F : p_28252_.height * 0.92F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public void aiStep() {
      super.aiStep();
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed += (this.onGround ? -1.0F : 4.0F) * 0.3F;
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping *= 0.9F;
      Vec3 vec3 = this.getDeltaMovement();
      if (!this.onGround && vec3.y < 0.0D) {
         this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
      }

      this.flap += this.flapping * 2.0F;
      if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.eggTime <= 0) {
         this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.spawnAtLocation(Items.EGG);
         this.eggTime = this.random.nextInt(6000) + 6000;
      }

   }

   protected boolean isFlapping() {
      return this.flyDist > this.nextFlap;
   }

   protected void onFlap() {
      this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
   }

   public boolean causeFallDamage(float p_148875_, float p_148876_, DamageSource p_148877_) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.CHICKEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_28262_) {
      return SoundEvents.CHICKEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CHICKEN_DEATH;
   }

   protected void playStepSound(BlockPos p_28254_, BlockState p_28255_) {
      this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
   }

   public Chicken getBreedOffspring(ServerLevel p_148884_, AgeableMob p_148885_) {
      return EntityType.CHICKEN.create(p_148884_);
   }

   public boolean isFood(ItemStack p_28271_) {
      return FOOD_ITEMS.test(p_28271_);
   }

   protected int getExperienceReward(Player p_28259_) {
      return this.isChickenJockey() ? 10 : super.getExperienceReward(p_28259_);
   }

   public void readAdditionalSaveData(CompoundTag p_28243_) {
      super.readAdditionalSaveData(p_28243_);
      this.isChickenJockey = p_28243_.getBoolean("IsChickenJockey");
      if (p_28243_.contains("EggLayTime")) {
         this.eggTime = p_28243_.getInt("EggLayTime");
      }

   }

   public void addAdditionalSaveData(CompoundTag p_28257_) {
      super.addAdditionalSaveData(p_28257_);
      p_28257_.putBoolean("IsChickenJockey", this.isChickenJockey);
      p_28257_.putInt("EggLayTime", this.eggTime);
   }

   public boolean removeWhenFarAway(double p_28266_) {
      return this.isChickenJockey();
   }

   public void positionRider(Entity p_28269_) {
      super.positionRider(p_28269_);
      float f = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
      float f1 = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
      float f2 = 0.1F;
      float f3 = 0.0F;
      p_28269_.setPos(this.getX() + (double)(0.1F * f), this.getY(0.5D) + p_28269_.getMyRidingOffset() + 0.0D, this.getZ() - (double)(0.1F * f1));
      if (p_28269_ instanceof LivingEntity) {
         ((LivingEntity)p_28269_).yBodyRot = this.yBodyRot;
      }

   }

   public boolean isChickenJockey() {
      return this.isChickenJockey;
   }

   public void setChickenJockey(boolean p_28274_) {
      this.isChickenJockey = p_28274_;
   }
}