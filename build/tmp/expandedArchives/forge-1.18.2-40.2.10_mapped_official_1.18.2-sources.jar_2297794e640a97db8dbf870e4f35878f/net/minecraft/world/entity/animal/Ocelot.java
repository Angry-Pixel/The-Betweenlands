package net.minecraft.world.entity.animal;

import java.util.Random;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Ocelot extends Animal {
   public static final double CROUCH_SPEED_MOD = 0.6D;
   public static final double WALK_SPEED_MOD = 0.8D;
   public static final double SPRINT_SPEED_MOD = 1.33D;
   private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.SALMON);
   private static final EntityDataAccessor<Boolean> DATA_TRUSTING = SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);
   @Nullable
   private Ocelot.OcelotAvoidEntityGoal<Player> ocelotAvoidPlayersGoal;
   @Nullable
   private Ocelot.OcelotTemptGoal temptGoal;

   public Ocelot(EntityType<? extends Ocelot> p_28987_, Level p_28988_) {
      super(p_28987_, p_28988_);
      this.reassessTrustingGoals();
   }

   boolean isTrusting() {
      return this.entityData.get(DATA_TRUSTING);
   }

   private void setTrusting(boolean p_29046_) {
      this.entityData.set(DATA_TRUSTING, p_29046_);
      this.reassessTrustingGoals();
   }

   public void addAdditionalSaveData(CompoundTag p_29024_) {
      super.addAdditionalSaveData(p_29024_);
      p_29024_.putBoolean("Trusting", this.isTrusting());
   }

   public void readAdditionalSaveData(CompoundTag p_29013_) {
      super.readAdditionalSaveData(p_29013_);
      this.setTrusting(p_29013_.getBoolean("Trusting"));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TRUSTING, false);
   }

   protected void registerGoals() {
      this.temptGoal = new Ocelot.OcelotTemptGoal(this, 0.6D, TEMPT_INGREDIENT, true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(9, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Chicken.class, false));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public void customServerAiStep() {
      if (this.getMoveControl().hasWanted()) {
         double d0 = this.getMoveControl().getSpeedModifier();
         if (d0 == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (d0 == 1.33D) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(Pose.STANDING);
         this.setSprinting(false);
      }

   }

   public boolean removeWhenFarAway(double p_29041_) {
      return !this.isTrusting() && this.tickCount > 2400;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   public boolean causeFallDamage(float p_148949_, float p_148950_, DamageSource p_148951_) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.OCELOT_AMBIENT;
   }

   public int getAmbientSoundInterval() {
      return 900;
   }

   protected SoundEvent getHurtSound(DamageSource p_29035_) {
      return SoundEvents.OCELOT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.OCELOT_DEATH;
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity p_28990_) {
      return p_28990_.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
   }

   public InteractionResult mobInteract(Player p_29021_, InteractionHand p_29022_) {
      ItemStack itemstack = p_29021_.getItemInHand(p_29022_);
      if ((this.temptGoal == null || this.temptGoal.isRunning()) && !this.isTrusting() && this.isFood(itemstack) && p_29021_.distanceToSqr(this) < 9.0D) {
         this.usePlayerItem(p_29021_, p_29022_, itemstack);
         if (!this.level.isClientSide) {
            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_29021_)) {
               this.setTrusting(true);
               this.spawnTrustingParticles(true);
               this.level.broadcastEntityEvent(this, (byte)41);
            } else {
               this.spawnTrustingParticles(false);
               this.level.broadcastEntityEvent(this, (byte)40);
            }
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_29021_, p_29022_);
      }
   }

   public void handleEntityEvent(byte p_28995_) {
      if (p_28995_ == 41) {
         this.spawnTrustingParticles(true);
      } else if (p_28995_ == 40) {
         this.spawnTrustingParticles(false);
      } else {
         super.handleEntityEvent(p_28995_);
      }

   }

   private void spawnTrustingParticles(boolean p_29048_) {
      ParticleOptions particleoptions = ParticleTypes.HEART;
      if (!p_29048_) {
         particleoptions = ParticleTypes.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   protected void reassessTrustingGoals() {
      if (this.ocelotAvoidPlayersGoal == null) {
         this.ocelotAvoidPlayersGoal = new Ocelot.OcelotAvoidEntityGoal<>(this, Player.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.ocelotAvoidPlayersGoal);
      if (!this.isTrusting()) {
         this.goalSelector.addGoal(4, this.ocelotAvoidPlayersGoal);
      }

   }

   public Ocelot getBreedOffspring(ServerLevel p_148956_, AgeableMob p_148957_) {
      return EntityType.OCELOT.create(p_148956_);
   }

   public boolean isFood(ItemStack p_29043_) {
      return TEMPT_INGREDIENT.test(p_29043_);
   }

   public static boolean checkOcelotSpawnRules(EntityType<Ocelot> p_29026_, LevelAccessor p_29027_, MobSpawnType p_29028_, BlockPos p_29029_, Random p_29030_) {
      return p_29030_.nextInt(3) != 0;
   }

   public boolean checkSpawnObstruction(LevelReader p_29005_) {
      if (p_29005_.isUnobstructed(this) && !p_29005_.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = this.blockPosition();
         if (blockpos.getY() < p_29005_.getSeaLevel()) {
            return false;
         }

         BlockState blockstate = p_29005_.getBlockState(blockpos.below());
         if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(BlockTags.LEAVES)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29007_, DifficultyInstance p_29008_, MobSpawnType p_29009_, @Nullable SpawnGroupData p_29010_, @Nullable CompoundTag p_29011_) {
      if (p_29010_ == null) {
         p_29010_ = new AgeableMob.AgeableMobGroupData(1.0F);
      }

      return super.finalizeSpawn(p_29007_, p_29008_, p_29009_, p_29010_, p_29011_);
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public boolean isSteppingCarefully() {
      return this.getPose() == Pose.CROUCHING || super.isSteppingCarefully();
   }

   static class OcelotAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Ocelot ocelot;

      public OcelotAvoidEntityGoal(Ocelot p_29051_, Class<T> p_29052_, float p_29053_, double p_29054_, double p_29055_) {
         super(p_29051_, p_29052_, p_29053_, p_29054_, p_29055_, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
         this.ocelot = p_29051_;
      }

      public boolean canUse() {
         return !this.ocelot.isTrusting() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.ocelot.isTrusting() && super.canContinueToUse();
      }
   }

   static class OcelotTemptGoal extends TemptGoal {
      private final Ocelot ocelot;

      public OcelotTemptGoal(Ocelot p_29060_, double p_29061_, Ingredient p_29062_, boolean p_29063_) {
         super(p_29060_, p_29061_, p_29062_, p_29063_);
         this.ocelot = p_29060_;
      }

      protected boolean canScare() {
         return super.canScare() && !this.ocelot.isTrusting();
      }
   }
}
