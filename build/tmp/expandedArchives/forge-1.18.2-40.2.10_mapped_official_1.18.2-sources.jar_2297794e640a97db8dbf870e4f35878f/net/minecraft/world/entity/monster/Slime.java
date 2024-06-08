package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class Slime extends Mob implements Enemy {
   private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
   public static final int MIN_SIZE = 1;
   public static final int MAX_SIZE = 127;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround;

   public Slime(EntityType<? extends Slime> p_33588_, Level p_33589_) {
      super(p_33588_, p_33589_);
      this.moveControl = new Slime.SlimeMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Slime.SlimeFloatGoal(this));
      this.goalSelector.addGoal(2, new Slime.SlimeAttackGoal(this));
      this.goalSelector.addGoal(3, new Slime.SlimeRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new Slime.SlimeKeepOnJumpingGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (p_33641_) -> {
         return Math.abs(p_33641_.getY() - this.getY()) <= 4.0D;
      }));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_SIZE, 1);
   }

   protected void setSize(int p_33594_, boolean p_33595_) {
      int i = Mth.clamp(p_33594_, 1, 127);
      this.entityData.set(ID_SIZE, i);
      this.reapplyPosition();
      this.refreshDimensions();
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
      if (p_33595_) {
         this.setHealth(this.getMaxHealth());
      }

      this.xpReward = i;
   }

   public int getSize() {
      return this.entityData.get(ID_SIZE);
   }

   public void addAdditionalSaveData(CompoundTag p_33619_) {
      super.addAdditionalSaveData(p_33619_);
      p_33619_.putInt("Size", this.getSize() - 1);
      p_33619_.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditionalSaveData(CompoundTag p_33607_) {
      this.setSize(p_33607_.getInt("Size") + 1, false);
      super.readAdditionalSaveData(p_33607_);
      this.wasOnGround = p_33607_.getBoolean("wasOnGround");
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.ITEM_SLIME;
   }

   protected boolean shouldDespawnInPeaceful() {
      return this.getSize() > 0;
   }

   public void tick() {
      this.squish += (this.targetSquish - this.squish) * 0.5F;
      this.oSquish = this.squish;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int i = this.getSize();

         if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
         for(int j = 0; j < i * 8; ++j) {
            float f = this.random.nextFloat() * ((float)Math.PI * 2F);
            float f1 = this.random.nextFloat() * 0.5F + 0.5F;
            float f2 = Mth.sin(f) * (float)i * 0.5F * f1;
            float f3 = Mth.cos(f) * (float)i * 0.5F * f1;
            this.level.addParticle(this.getParticleType(), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.targetSquish = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.targetSquish = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.decreaseSquish();
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
      if (ID_SIZE.equals(p_33609_)) {
         this.refreshDimensions();
         this.setYRot(this.yHeadRot);
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(p_33609_);
   }

   public EntityType<? extends Slime> getType() {
      return (EntityType<? extends Slime>)super.getType();
   }

   public void remove(Entity.RemovalReason p_149847_) {
      int i = this.getSize();
      if (!this.level.isClientSide && i > 1 && this.isDeadOrDying()) {
         Component component = this.getCustomName();
         boolean flag = this.isNoAi();
         float f = (float)i / 4.0F;
         int j = i / 2;
         int k = 2 + this.random.nextInt(3);

         for(int l = 0; l < k; ++l) {
            float f1 = ((float)(l % 2) - 0.5F) * f;
            float f2 = ((float)(l / 2) - 0.5F) * f;
            Slime slime = this.getType().create(this.level);
            if (this.isPersistenceRequired()) {
               slime.setPersistenceRequired();
            }

            slime.setCustomName(component);
            slime.setNoAi(flag);
            slime.setInvulnerable(this.isInvulnerable());
            slime.setSize(j, true);
            slime.moveTo(this.getX() + (double)f1, this.getY() + 0.5D, this.getZ() + (double)f2, this.random.nextFloat() * 360.0F, 0.0F);
            this.level.addFreshEntity(slime);
         }
      }

      super.remove(p_149847_);
   }

   public void push(Entity p_33636_) {
      super.push(p_33636_);
      if (p_33636_ instanceof IronGolem && this.isDealsDamage()) {
         this.dealDamage((LivingEntity)p_33636_);
      }

   }

   public void playerTouch(Player p_33611_) {
      if (this.isDealsDamage()) {
         this.dealDamage(p_33611_);
      }

   }

   protected void dealDamage(LivingEntity p_33638_) {
      if (this.isAlive()) {
         int i = this.getSize();
         if (this.distanceToSqr(p_33638_) < 0.6D * (double)i * 0.6D * (double)i && this.hasLineOfSight(p_33638_) && p_33638_.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, p_33638_);
         }
      }

   }

   protected float getStandingEyeHeight(Pose p_33614_, EntityDimensions p_33615_) {
      return 0.625F * p_33615_.height;
   }

   protected boolean isDealsDamage() {
      return !this.isTiny() && this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   protected SoundEvent getHurtSound(DamageSource p_33631_) {
      return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
   }

   protected ResourceLocation getDefaultLootTable() {
      return this.getSize() == 1 ? this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
   }

   public static boolean checkSlimeSpawnRules(EntityType<Slime> p_33621_, LevelAccessor p_33622_, MobSpawnType p_33623_, BlockPos p_33624_, Random p_33625_) {
      if (p_33622_.getDifficulty() != Difficulty.PEACEFUL) {
         if (p_33622_.getBiome(p_33624_).is(Biomes.SWAMP) && p_33624_.getY() > 50 && p_33624_.getY() < 70 && p_33625_.nextFloat() < 0.5F && p_33625_.nextFloat() < p_33622_.getMoonBrightness() && p_33622_.getMaxLocalRawBrightness(p_33624_) <= p_33625_.nextInt(8)) {
            return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
         }

         if (!(p_33622_ instanceof WorldGenLevel)) {
            return false;
         }

         ChunkPos chunkpos = new ChunkPos(p_33624_);
         boolean flag = WorldgenRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((WorldGenLevel)p_33622_).getSeed(), 987234911L).nextInt(10) == 0;
         if (p_33625_.nextInt(10) == 0 && flag && p_33624_.getY() < 40) {
            return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
         }
      }

      return false;
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   protected void jumpFromGround() {
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
      this.hasImpulse = true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, @Nullable SpawnGroupData p_33604_, @Nullable CompoundTag p_33605_) {
      int i = this.random.nextInt(3);
      if (i < 2 && this.random.nextFloat() < 0.5F * p_33602_.getSpecialMultiplier()) {
         ++i;
      }

      int j = 1 << i;
      this.setSize(j, true);
      return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_, p_33605_);
   }

   float getSoundPitch() {
      float f = this.isTiny() ? 1.4F : 0.8F;
      return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
   }

   public EntityDimensions getDimensions(Pose p_33597_) {
      return super.getDimensions(p_33597_).scale(0.255F * (float)this.getSize());
   }

   /**
    * Called when the slime spawns particles on landing, see onUpdate.
    * Return true to prevent the spawning of the default particles.
    */
   protected boolean spawnCustomParticles() { return false; }

   static class SlimeAttackGoal extends Goal {
      private final Slime slime;
      private int growTiredTimer;

      public SlimeAttackGoal(Slime p_33648_) {
         this.slime = p_33648_;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.slime.getTarget();
         if (livingentity == null) {
            return false;
         } else {
            return !this.slime.canAttack(livingentity) ? false : this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
         }
      }

      public void start() {
         this.growTiredTimer = reducedTickDelay(300);
         super.start();
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = this.slime.getTarget();
         if (livingentity == null) {
            return false;
         } else if (!this.slime.canAttack(livingentity)) {
            return false;
         } else {
            return --this.growTiredTimer > 0;
         }
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity livingentity = this.slime.getTarget();
         if (livingentity != null) {
            this.slime.lookAt(livingentity, 10.0F, 10.0F);
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
      }
   }

   static class SlimeFloatGoal extends Goal {
      private final Slime slime;

      public SlimeFloatGoal(Slime p_33655_) {
         this.slime = p_33655_;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         p_33655_.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.slime.getRandom().nextFloat() < 0.8F) {
            this.slime.getJumpControl().jump();
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.2D);
      }
   }

   static class SlimeKeepOnJumpingGoal extends Goal {
      private final Slime slime;

      public SlimeKeepOnJumpingGoal(Slime p_33660_) {
         this.slime = p_33660_;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      public void tick() {
         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.0D);
      }
   }

   static class SlimeMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final Slime slime;
      private boolean isAggressive;

      public SlimeMoveControl(Slime p_33668_) {
         super(p_33668_);
         this.slime = p_33668_;
         this.yRot = 180.0F * p_33668_.getYRot() / (float)Math.PI;
      }

      public void setDirection(float p_33673_, boolean p_33674_) {
         this.yRot = p_33673_;
         this.isAggressive = p_33674_;
      }

      public void setWantedMovement(double p_33671_) {
         this.speedModifier = p_33671_;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      public void tick() {
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
         this.mob.yHeadRot = this.mob.getYRot();
         this.mob.yBodyRot = this.mob.getYRot();
         if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.isOnGround()) {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpControl().jump();
                  if (this.slime.doPlayJumpSound()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                  }
               } else {
                  this.slime.xxa = 0.0F;
                  this.slime.zza = 0.0F;
                  this.mob.setSpeed(0.0F);
               }
            } else {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }

         }
      }
   }

   static class SlimeRandomDirectionGoal extends Goal {
      private final Slime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SlimeRandomDirectionGoal(Slime p_33679_) {
         this.slime = p_33679_;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
            this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
      }
   }
}
