package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class Turtle extends Animal {
   private static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   public static final Ingredient FOOD_ITEMS = Ingredient.of(Blocks.SEAGRASS.asItem());
   int layEggCounter;
   public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR = (p_30226_) -> {
      return p_30226_.isBaby() && !p_30226_.isInWater();
   };

   public Turtle(EntityType<? extends Turtle> p_30132_, Level p_30133_) {
      super(p_30132_, p_30133_);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_IRON_CLOSED, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, -1.0F);
      this.moveControl = new Turtle.TurtleMoveControl(this);
      this.maxUpStep = 1.0F;
   }

   public void setHomePos(BlockPos p_30220_) {
      this.entityData.set(HOME_POS, p_30220_);
   }

   BlockPos getHomePos() {
      return this.entityData.get(HOME_POS);
   }

   void setTravelPos(BlockPos p_30224_) {
      this.entityData.set(TRAVEL_POS, p_30224_);
   }

   BlockPos getTravelPos() {
      return this.entityData.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return this.entityData.get(HAS_EGG);
   }

   void setHasEgg(boolean p_30235_) {
      this.entityData.set(HAS_EGG, p_30235_);
   }

   public boolean isLayingEgg() {
      return this.entityData.get(LAYING_EGG);
   }

   void setLayingEgg(boolean p_30237_) {
      this.layEggCounter = p_30237_ ? 1 : 0;
      this.entityData.set(LAYING_EGG, p_30237_);
   }

   boolean isGoingHome() {
      return this.entityData.get(GOING_HOME);
   }

   void setGoingHome(boolean p_30239_) {
      this.entityData.set(GOING_HOME, p_30239_);
   }

   boolean isTravelling() {
      return this.entityData.get(TRAVELLING);
   }

   void setTravelling(boolean p_30241_) {
      this.entityData.set(TRAVELLING, p_30241_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(HOME_POS, BlockPos.ZERO);
      this.entityData.define(HAS_EGG, false);
      this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
      this.entityData.define(GOING_HOME, false);
      this.entityData.define(TRAVELLING, false);
      this.entityData.define(LAYING_EGG, false);
   }

   public void addAdditionalSaveData(CompoundTag p_30176_) {
      super.addAdditionalSaveData(p_30176_);
      p_30176_.putInt("HomePosX", this.getHomePos().getX());
      p_30176_.putInt("HomePosY", this.getHomePos().getY());
      p_30176_.putInt("HomePosZ", this.getHomePos().getZ());
      p_30176_.putBoolean("HasEgg", this.hasEgg());
      p_30176_.putInt("TravelPosX", this.getTravelPos().getX());
      p_30176_.putInt("TravelPosY", this.getTravelPos().getY());
      p_30176_.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   public void readAdditionalSaveData(CompoundTag p_30162_) {
      int i = p_30162_.getInt("HomePosX");
      int j = p_30162_.getInt("HomePosY");
      int k = p_30162_.getInt("HomePosZ");
      this.setHomePos(new BlockPos(i, j, k));
      super.readAdditionalSaveData(p_30162_);
      this.setHasEgg(p_30162_.getBoolean("HasEgg"));
      int l = p_30162_.getInt("TravelPosX");
      int i1 = p_30162_.getInt("TravelPosY");
      int j1 = p_30162_.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(l, i1, j1));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30153_, DifficultyInstance p_30154_, MobSpawnType p_30155_, @Nullable SpawnGroupData p_30156_, @Nullable CompoundTag p_30157_) {
      this.setHomePos(this.blockPosition());
      this.setTravelPos(BlockPos.ZERO);
      return super.finalizeSpawn(p_30153_, p_30154_, p_30155_, p_30156_, p_30157_);
   }

   public static boolean checkTurtleSpawnRules(EntityType<Turtle> p_30179_, LevelAccessor p_30180_, MobSpawnType p_30181_, BlockPos p_30182_, Random p_30183_) {
      return p_30182_.getY() < p_30180_.getSeaLevel() + 4 && TurtleEggBlock.onSand(p_30180_, p_30182_) && isBrightEnoughToSpawn(p_30180_, p_30182_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Turtle.TurtlePanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new Turtle.TurtleBreedGoal(this, 1.0D));
      this.goalSelector.addGoal(1, new Turtle.TurtleLayEggGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, FOOD_ITEMS, false));
      this.goalSelector.addGoal(3, new Turtle.TurtleGoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new Turtle.TurtleGoHomeGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new Turtle.TurtleTravelGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new Turtle.TurtleRandomStrollGoal(this, 1.0D, 100));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float p_30192_) {
      super.playSwimSound(p_30192_ * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_30202_) {
      return this.isBaby() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.TURTLE_DEATH_BABY : SoundEvents.TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos p_30173_, BlockState p_30174_) {
      SoundEvent soundevent = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
      this.playSound(soundevent, 0.15F, 1.0F);
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   public float getScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   protected PathNavigation createNavigation(Level p_30171_) {
      return new Turtle.TurtlePathNavigation(this, p_30171_);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_149068_, AgeableMob p_149069_) {
      return EntityType.TURTLE.create(p_149068_);
   }

   public boolean isFood(ItemStack p_30231_) {
      return p_30231_.is(Blocks.SEAGRASS.asItem());
   }

   public float getWalkTargetValue(BlockPos p_30159_, LevelReader p_30160_) {
      if (!this.isGoingHome() && p_30160_.getFluidState(p_30159_).is(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.onSand(p_30160_, p_30159_) ? 10.0F : p_30160_.getBrightness(p_30159_) - 0.5F;
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
         BlockPos blockpos = this.blockPosition();
         if (TurtleEggBlock.onSand(this.level, blockpos)) {
            this.level.levelEvent(2001, blockpos, Block.getId(this.level.getBlockState(blockpos.below())));
         }
      }

   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         this.spawnAtLocation(Items.SCUTE, 1);
      }

   }

   public void travel(Vec3 p_30218_) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(0.1F, p_30218_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerToCenterThan(this.position(), 20.0D))) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_30218_);
      }

   }

   public boolean canBeLeashed(Player p_30151_) {
      return false;
   }

   public void thunderHit(ServerLevel p_30140_, LightningBolt p_30141_) {
      this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   static class TurtleBreedGoal extends BreedGoal {
      private final Turtle turtle;

      TurtleBreedGoal(Turtle p_30244_, double p_30245_) {
         super(p_30244_, p_30245_);
         this.turtle = p_30244_;
      }

      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
      }

      protected void breed() {
         ServerPlayer serverplayer = this.animal.getLoveCause();
         if (serverplayer == null && this.partner.getLoveCause() != null) {
            serverplayer = this.partner.getLoveCause();
         }

         if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, (AgeableMob)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.resetLove();
         this.partner.resetLove();
         Random random = this.animal.getRandom();
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }

      }
   }

   static class TurtleGoHomeGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;
      private int closeToHomeTryTicks;
      private static final int GIVE_UP_TICKS = 600;

      TurtleGoHomeGoal(Turtle p_30253_, double p_30254_) {
         this.turtle = p_30253_;
         this.speedModifier = p_30254_;
      }

      public boolean canUse() {
         if (this.turtle.isBaby()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRandom().nextInt(reducedTickDelay(700)) != 0) {
            return false;
         } else {
            return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 64.0D);
         }
      }

      public void start() {
         this.turtle.setGoingHome(true);
         this.stuck = false;
         this.closeToHomeTryTicks = 0;
      }

      public void stop() {
         this.turtle.setGoingHome(false);
      }

      public boolean canContinueToUse() {
         return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 7.0D) && !this.stuck && this.closeToHomeTryTicks <= this.adjustedTickDelay(600);
      }

      public void tick() {
         BlockPos blockpos = this.turtle.getHomePos();
         boolean flag = blockpos.closerToCenterThan(this.turtle.position(), 16.0D);
         if (flag) {
            ++this.closeToHomeTryTicks;
         }

         if (this.turtle.getNavigation().isDone()) {
            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
            Vec3 vec31 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, vec3, (double)((float)Math.PI / 10F));
            if (vec31 == null) {
               vec31 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, vec3, (double)((float)Math.PI / 2F));
            }

            if (vec31 != null && !flag && !this.turtle.level.getBlockState(new BlockPos(vec31)).is(Blocks.WATER)) {
               vec31 = DefaultRandomPos.getPosTowards(this.turtle, 16, 5, vec3, (double)((float)Math.PI / 2F));
            }

            if (vec31 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
         }

      }
   }

   static class TurtleGoToWaterGoal extends MoveToBlockGoal {
      private static final int GIVE_UP_TICKS = 1200;
      private final Turtle turtle;

      TurtleGoToWaterGoal(Turtle p_30262_, double p_30263_) {
         super(p_30262_, p_30262_.isBaby() ? 2.0D : p_30263_, 24);
         this.turtle = p_30262_;
         this.verticalSearchStart = -1;
      }

      public boolean canContinueToUse() {
         return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
      }

      public boolean canUse() {
         if (this.turtle.isBaby() && !this.turtle.isInWater()) {
            return super.canUse();
         } else {
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.canUse() : false;
         }
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 160 == 0;
      }

      protected boolean isValidTarget(LevelReader p_30270_, BlockPos p_30271_) {
         return p_30270_.getBlockState(p_30271_).is(Blocks.WATER);
      }
   }

   static class TurtleLayEggGoal extends MoveToBlockGoal {
      private final Turtle turtle;

      TurtleLayEggGoal(Turtle p_30276_, double p_30277_) {
         super(p_30276_, p_30277_, 16);
         this.turtle = p_30276_;
      }

      public boolean canUse() {
         return this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0D) ? super.canUse() : false;
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0D);
      }

      public void tick() {
         super.tick();
         BlockPos blockpos = this.turtle.blockPosition();
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            if (this.turtle.layEggCounter < 1) {
               this.turtle.setLayingEgg(true);
            } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
               Level level = this.turtle.level;
               level.playSound((Player)null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
               level.setBlock(this.blockPos.above(), Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1)), 3);
               this.turtle.setHasEgg(false);
               this.turtle.setLayingEgg(false);
               this.turtle.setInLoveTime(600);
            }

            if (this.turtle.isLayingEgg()) {
               ++this.turtle.layEggCounter;
            }
         }

      }

      protected boolean isValidTarget(LevelReader p_30280_, BlockPos p_30281_) {
         return !p_30280_.isEmptyBlock(p_30281_.above()) ? false : TurtleEggBlock.isSand(p_30280_, p_30281_);
      }
   }

   static class TurtleMoveControl extends MoveControl {
      private final Turtle turtle;

      TurtleMoveControl(Turtle p_30286_) {
         super(p_30286_);
         this.turtle = p_30286_;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            if (!this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 16.0D)) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isBaby()) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.operation == MoveControl.Operation.MOVE_TO && !this.turtle.getNavigation().isDone()) {
            double d0 = this.wantedX - this.turtle.getX();
            double d1 = this.wantedY - this.turtle.getY();
            double d2 = this.wantedZ - this.turtle.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 /= d3;
            float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), f, 90.0F));
            this.turtle.yBodyRot = this.turtle.getYRot();
            float f1 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.turtle.setSpeed(Mth.lerp(0.125F, this.turtle.getSpeed(), f1));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, (double)this.turtle.getSpeed() * d1 * 0.1D, 0.0D));
         } else {
            this.turtle.setSpeed(0.0F);
         }
      }
   }

   static class TurtlePanicGoal extends PanicGoal {
      TurtlePanicGoal(Turtle p_30290_, double p_30291_) {
         super(p_30290_, p_30291_);
      }

      public boolean canUse() {
         if (!this.shouldPanic()) {
            return false;
         } else {
            BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 7);
            if (blockpos != null) {
               this.posX = (double)blockpos.getX();
               this.posY = (double)blockpos.getY();
               this.posZ = (double)blockpos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class TurtlePathNavigation extends WaterBoundPathNavigation {
      TurtlePathNavigation(Turtle p_30294_, Level p_30295_) {
         super(p_30294_, p_30295_);
      }

      protected boolean canUpdatePath() {
         return true;
      }

      protected PathFinder createPathFinder(int p_30298_) {
         this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
         return new PathFinder(this.nodeEvaluator, p_30298_);
      }

      public boolean isStableDestination(BlockPos p_30300_) {
         if (this.mob instanceof Turtle) {
            Turtle turtle = (Turtle)this.mob;
            if (turtle.isTravelling()) {
               return this.level.getBlockState(p_30300_).is(Blocks.WATER);
            }
         }

         return !this.level.getBlockState(p_30300_.below()).isAir();
      }
   }

   static class TurtleRandomStrollGoal extends RandomStrollGoal {
      private final Turtle turtle;

      TurtleRandomStrollGoal(Turtle p_30303_, double p_30304_, int p_30305_) {
         super(p_30303_, p_30304_, p_30305_);
         this.turtle = p_30303_;
      }

      public boolean canUse() {
         return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.canUse() : false;
      }
   }

   static class TurtleTravelGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;

      TurtleTravelGoal(Turtle p_30333_, double p_30334_) {
         this.turtle = p_30333_;
         this.speedModifier = p_30334_;
      }

      public boolean canUse() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void start() {
         int i = 512;
         int j = 4;
         Random random = this.turtle.random;
         int k = random.nextInt(1025) - 512;
         int l = random.nextInt(9) - 4;
         int i1 = random.nextInt(1025) - 512;
         if ((double)l + this.turtle.getY() > (double)(this.turtle.level.getSeaLevel() - 1)) {
            l = 0;
         }

         BlockPos blockpos = new BlockPos((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)i1 + this.turtle.getZ());
         this.turtle.setTravelPos(blockpos);
         this.turtle.setTravelling(true);
         this.stuck = false;
      }

      public void tick() {
         if (this.turtle.getNavigation().isDone()) {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.turtle.getTravelPos());
            Vec3 vec31 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, vec3, (double)((float)Math.PI / 10F));
            if (vec31 == null) {
               vec31 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, vec3, (double)((float)Math.PI / 2F));
            }

            if (vec31 != null) {
               int i = Mth.floor(vec31.x);
               int j = Mth.floor(vec31.z);
               int k = 34;
               if (!this.turtle.level.hasChunksAt(i - 34, j - 34, i + 34, j + 34)) {
                  vec31 = null;
               }
            }

            if (vec31 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
         }

      }

      public boolean canContinueToUse() {
         return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void stop() {
         this.turtle.setTravelling(false);
         super.stop();
      }
   }
}