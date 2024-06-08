package net.minecraft.world.entity.animal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Rabbit extends Animal {
   public static final double STROLL_SPEED_MOD = 0.6D;
   public static final double BREED_SPEED_MOD = 0.8D;
   public static final double FOLLOW_SPEED_MOD = 1.0D;
   public static final double FLEE_SPEED_MOD = 2.2D;
   public static final double ATTACK_SPEED_MOD = 1.4D;
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Rabbit.class, EntityDataSerializers.INT);
   public static final int TYPE_BROWN = 0;
   public static final int TYPE_WHITE = 1;
   public static final int TYPE_BLACK = 2;
   public static final int TYPE_WHITE_SPLOTCHED = 3;
   public static final int TYPE_GOLD = 4;
   public static final int TYPE_SALT = 5;
   public static final int TYPE_EVIL = 99;
   private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
   public static final int EVIL_ATTACK_POWER = 8;
   public static final int EVIL_ARMOR_VALUE = 8;
   private static final int MORE_CARROTS_DELAY = 40;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int jumpDelayTicks;
   int moreCarrotTicks;

   public Rabbit(EntityType<? extends Rabbit> p_29656_, Level p_29657_) {
      super(p_29656_, p_29657_);
      this.jumpControl = new Rabbit.RabbitJumpControl(this);
      this.moveControl = new Rabbit.RabbitMoveControl(this);
      this.setSpeedModifier(0.0D);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
      this.goalSelector.addGoal(1, new Rabbit.RabbitPanicGoal(this, 2.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Player.class, 8.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Wolf.class, 10.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Monster.class, 4.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(5, new Rabbit.RaidGardenGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
   }

   protected float getJumpPower() {
      if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.5D))) {
         Path path = this.navigation.getPath();
         if (path != null && !path.isDone()) {
            Vec3 vec3 = path.getNextEntityPos(this);
            if (vec3.y > this.getY() + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0D) {
         double d1 = this.getDeltaMovement().horizontalDistanceSqr();
         if (d1 < 0.01D) {
            this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
         }
      }

      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)1);
      }

   }

   public float getJumpCompletion(float p_29736_) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_29736_) / (float)this.jumpDuration;
   }

   public void setSpeedModifier(double p_29726_) {
      this.getNavigation().setSpeedModifier(p_29726_);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), p_29726_);
   }

   public void setJumping(boolean p_29732_) {
      super.setJumping(p_29732_);
      if (p_29732_) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, 0);
   }

   public void customServerAiStep() {
      if (this.jumpDelayTicks > 0) {
         --this.jumpDelayTicks;
      }

      if (this.moreCarrotTicks > 0) {
         this.moreCarrotTicks -= this.random.nextInt(3);
         if (this.moreCarrotTicks < 0) {
            this.moreCarrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.jumpDelayTicks == 0) {
            LivingEntity livingentity = this.getTarget();
            if (livingentity != null && this.distanceToSqr(livingentity) < 16.0D) {
               this.facePoint(livingentity.getX(), livingentity.getZ());
               this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         Rabbit.RabbitJumpControl rabbit$rabbitjumpcontrol = (Rabbit.RabbitJumpControl)this.jumpControl;
         if (!rabbit$rabbitjumpcontrol.wantJump()) {
            if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
               Path path = this.navigation.getPath();
               Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               if (path != null && !path.isDone()) {
                  vec3 = path.getNextEntityPos(this);
               }

               this.facePoint(vec3.x, vec3.z);
               this.startJumping();
            }
         } else if (!rabbit$rabbitjumpcontrol.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   private void facePoint(double p_29687_, double p_29688_) {
      this.setYRot((float)(Mth.atan2(p_29688_ - this.getZ(), p_29687_ - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
   }

   private void enableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if (this.moveControl.getSpeedModifier() < 2.2D) {
         this.jumpDelayTicks = 10;
      } else {
         this.jumpDelayTicks = 1;
      }

   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
   }

   public void addAdditionalSaveData(CompoundTag p_29697_) {
      super.addAdditionalSaveData(p_29697_);
      p_29697_.putInt("RabbitType", this.getRabbitType());
      p_29697_.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   public void readAdditionalSaveData(CompoundTag p_29684_) {
      super.readAdditionalSaveData(p_29684_);
      this.setRabbitType(p_29684_.getInt("RabbitType"));
      this.moreCarrotTicks = p_29684_.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_29715_) {
      return SoundEvents.RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   public boolean doHurtTarget(Entity p_29659_) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return p_29659_.hurt(DamageSource.mobAttack(this), 8.0F);
      } else {
         return p_29659_.hurt(DamageSource.mobAttack(this), 3.0F);
      }
   }

   public SoundSource getSoundSource() {
      return this.getRabbitType() == 99 ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
   }

   private static boolean isTemptingItem(ItemStack p_149038_) {
      return p_149038_.is(Items.CARROT) || p_149038_.is(Items.GOLDEN_CARROT) || p_149038_.is(Blocks.DANDELION.asItem());
   }

   public Rabbit getBreedOffspring(ServerLevel p_149035_, AgeableMob p_149036_) {
      Rabbit rabbit = EntityType.RABBIT.create(p_149035_);
      int i = this.getRandomRabbitType(p_149035_);
      if (this.random.nextInt(20) != 0) {
         if (p_149036_ instanceof Rabbit && this.random.nextBoolean()) {
            i = ((Rabbit)p_149036_).getRabbitType();
         } else {
            i = this.getRabbitType();
         }
      }

      rabbit.setRabbitType(i);
      return rabbit;
   }

   public boolean isFood(ItemStack p_29729_) {
      return isTemptingItem(p_29729_);
   }

   public int getRabbitType() {
      return this.entityData.get(DATA_TYPE_ID);
   }

   public void setRabbitType(int p_29734_) {
      if (p_29734_ == 99) {
         this.getAttribute(Attributes.ARMOR).setBaseValue(8.0D);
         this.goalSelector.addGoal(4, new Rabbit.EvilRabbitAttackGoal(this));
         this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TranslatableComponent(Util.makeDescriptionId("entity", KILLER_BUNNY)));
         }
      }

      this.entityData.set(DATA_TYPE_ID, p_29734_);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29678_, DifficultyInstance p_29679_, MobSpawnType p_29680_, @Nullable SpawnGroupData p_29681_, @Nullable CompoundTag p_29682_) {
      int i = this.getRandomRabbitType(p_29678_);
      if (p_29681_ instanceof Rabbit.RabbitGroupData) {
         i = ((Rabbit.RabbitGroupData)p_29681_).rabbitType;
      } else {
         p_29681_ = new Rabbit.RabbitGroupData(i);
      }

      this.setRabbitType(i);
      return super.finalizeSpawn(p_29678_, p_29679_, p_29680_, p_29681_, p_29682_);
   }

   private int getRandomRabbitType(LevelAccessor p_29676_) {
      Holder<Biome> holder = p_29676_.getBiome(this.blockPosition());
      int i = this.random.nextInt(100);
      if (holder.value().getPrecipitation() == Biome.Precipitation.SNOW) {
         return i < 80 ? 1 : 3;
      } else if (Biome.getBiomeCategory(holder) == Biome.BiomeCategory.DESERT) {
         return 4;
      } else {
         return i < 50 ? 0 : (i < 90 ? 5 : 2);
      }
   }

   public static boolean checkRabbitSpawnRules(EntityType<Rabbit> p_29699_, LevelAccessor p_29700_, MobSpawnType p_29701_, BlockPos p_29702_, Random p_29703_) {
      return p_29700_.getBlockState(p_29702_.below()).is(BlockTags.RABBITS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_29700_, p_29702_);
   }

   boolean wantsMoreFood() {
      return this.moreCarrotTicks == 0;
   }

   public void handleEntityEvent(byte p_29663_) {
      if (p_29663_ == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(p_29663_);
      }

   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static class EvilRabbitAttackGoal extends MeleeAttackGoal {
      public EvilRabbitAttackGoal(Rabbit p_29738_) {
         super(p_29738_, 1.4D, true);
      }

      protected double getAttackReachSqr(LivingEntity p_29740_) {
         return (double)(4.0F + p_29740_.getBbWidth());
      }
   }

   static class RabbitAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Rabbit rabbit;

      public RabbitAvoidEntityGoal(Rabbit p_29743_, Class<T> p_29744_, float p_29745_, double p_29746_, double p_29747_) {
         super(p_29743_, p_29744_, p_29745_, p_29746_, p_29747_);
         this.rabbit = p_29743_;
      }

      public boolean canUse() {
         return this.rabbit.getRabbitType() != 99 && super.canUse();
      }
   }

   public static class RabbitGroupData extends AgeableMob.AgeableMobGroupData {
      public final int rabbitType;

      public RabbitGroupData(int p_29751_) {
         super(1.0F);
         this.rabbitType = p_29751_;
      }
   }

   public static class RabbitJumpControl extends JumpControl {
      private final Rabbit rabbit;
      private boolean canJump;

      public RabbitJumpControl(Rabbit p_186229_) {
         super(p_186229_);
         this.rabbit = p_186229_;
      }

      public boolean wantJump() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean p_29759_) {
         this.canJump = p_29759_;
      }

      public void tick() {
         if (this.jump) {
            this.rabbit.startJumping();
            this.jump = false;
         }

      }
   }

   static class RabbitMoveControl extends MoveControl {
      private final Rabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveControl(Rabbit p_29766_) {
         super(p_29766_);
         this.rabbit = p_29766_;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.jumping && !((Rabbit.RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0D);
         } else if (this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double p_29769_, double p_29770_, double p_29771_, double p_29772_) {
         if (this.rabbit.isInWater()) {
            p_29772_ = 1.5D;
         }

         super.setWantedPosition(p_29769_, p_29770_, p_29771_, p_29772_);
         if (p_29772_ > 0.0D) {
            this.nextJumpSpeed = p_29772_;
         }

      }
   }

   static class RabbitPanicGoal extends PanicGoal {
      private final Rabbit rabbit;

      public RabbitPanicGoal(Rabbit p_29775_, double p_29776_) {
         super(p_29775_, p_29776_);
         this.rabbit = p_29775_;
      }

      public void tick() {
         super.tick();
         this.rabbit.setSpeedModifier(this.speedModifier);
      }
   }

   static class RaidGardenGoal extends MoveToBlockGoal {
      private final Rabbit rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public RaidGardenGoal(Rabbit p_29782_) {
         super(p_29782_, (double)0.7F, 16);
         this.rabbit = p_29782_;
      }

      public boolean canUse() {
         if (this.nextStartTick <= 0) {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.rabbit.level, this.rabbit)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.wantsMoreFood();
            this.wantsToRaid = true;
         }

         return super.canUse();
      }

      public boolean canContinueToUse() {
         return this.canRaid && super.canContinueToUse();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)this.rabbit.getMaxHeadXRot());
         if (this.isReachedTarget()) {
            Level level = this.rabbit.level;
            BlockPos blockpos = this.blockPos.above();
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (this.canRaid && block instanceof CarrotBlock) {
               int i = blockstate.getValue(CarrotBlock.AGE);
               if (i == 0) {
                  level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 2);
                  level.destroyBlock(blockpos, true, this.rabbit);
               } else {
                  level.setBlock(blockpos, blockstate.setValue(CarrotBlock.AGE, Integer.valueOf(i - 1)), 2);
                  level.levelEvent(2001, blockpos, Block.getId(blockstate));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.canRaid = false;
            this.nextStartTick = 10;
         }

      }

      protected boolean isValidTarget(LevelReader p_29785_, BlockPos p_29786_) {
         BlockState blockstate = p_29785_.getBlockState(p_29786_);
         if (blockstate.is(Blocks.FARMLAND) && this.wantsToRaid && !this.canRaid) {
            blockstate = p_29785_.getBlockState(p_29786_.above());
            if (blockstate.getBlock() instanceof CarrotBlock && ((CarrotBlock)blockstate.getBlock()).isMaxAge(blockstate)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }
}
