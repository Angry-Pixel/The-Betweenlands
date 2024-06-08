package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Parrot extends ShoulderRidingEntity implements FlyingAnimal {
   private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Parrot.class, EntityDataSerializers.INT);
   private static final Predicate<Mob> NOT_PARROT_PREDICATE = new Predicate<Mob>() {
      public boolean test(@Nullable Mob p_29453_) {
         return p_29453_ != null && Parrot.MOB_SOUND_MAP.containsKey(p_29453_.getType());
      }
   };
   private static final Item POISONOUS_FOOD = Items.COOKIE;
   private static final Set<Item> TAME_FOOD = Sets.newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   private static final int VARIANTS = 5;
   static final Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP = Util.make(Maps.newHashMap(), (p_29398_) -> {
      p_29398_.put(EntityType.BLAZE, SoundEvents.PARROT_IMITATE_BLAZE);
      p_29398_.put(EntityType.CAVE_SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
      p_29398_.put(EntityType.CREEPER, SoundEvents.PARROT_IMITATE_CREEPER);
      p_29398_.put(EntityType.DROWNED, SoundEvents.PARROT_IMITATE_DROWNED);
      p_29398_.put(EntityType.ELDER_GUARDIAN, SoundEvents.PARROT_IMITATE_ELDER_GUARDIAN);
      p_29398_.put(EntityType.ENDER_DRAGON, SoundEvents.PARROT_IMITATE_ENDER_DRAGON);
      p_29398_.put(EntityType.ENDERMITE, SoundEvents.PARROT_IMITATE_ENDERMITE);
      p_29398_.put(EntityType.EVOKER, SoundEvents.PARROT_IMITATE_EVOKER);
      p_29398_.put(EntityType.GHAST, SoundEvents.PARROT_IMITATE_GHAST);
      p_29398_.put(EntityType.GUARDIAN, SoundEvents.PARROT_IMITATE_GUARDIAN);
      p_29398_.put(EntityType.HOGLIN, SoundEvents.PARROT_IMITATE_HOGLIN);
      p_29398_.put(EntityType.HUSK, SoundEvents.PARROT_IMITATE_HUSK);
      p_29398_.put(EntityType.ILLUSIONER, SoundEvents.PARROT_IMITATE_ILLUSIONER);
      p_29398_.put(EntityType.MAGMA_CUBE, SoundEvents.PARROT_IMITATE_MAGMA_CUBE);
      p_29398_.put(EntityType.PHANTOM, SoundEvents.PARROT_IMITATE_PHANTOM);
      p_29398_.put(EntityType.PIGLIN, SoundEvents.PARROT_IMITATE_PIGLIN);
      p_29398_.put(EntityType.PIGLIN_BRUTE, SoundEvents.PARROT_IMITATE_PIGLIN_BRUTE);
      p_29398_.put(EntityType.PILLAGER, SoundEvents.PARROT_IMITATE_PILLAGER);
      p_29398_.put(EntityType.RAVAGER, SoundEvents.PARROT_IMITATE_RAVAGER);
      p_29398_.put(EntityType.SHULKER, SoundEvents.PARROT_IMITATE_SHULKER);
      p_29398_.put(EntityType.SILVERFISH, SoundEvents.PARROT_IMITATE_SILVERFISH);
      p_29398_.put(EntityType.SKELETON, SoundEvents.PARROT_IMITATE_SKELETON);
      p_29398_.put(EntityType.SLIME, SoundEvents.PARROT_IMITATE_SLIME);
      p_29398_.put(EntityType.SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
      p_29398_.put(EntityType.STRAY, SoundEvents.PARROT_IMITATE_STRAY);
      p_29398_.put(EntityType.VEX, SoundEvents.PARROT_IMITATE_VEX);
      p_29398_.put(EntityType.VINDICATOR, SoundEvents.PARROT_IMITATE_VINDICATOR);
      p_29398_.put(EntityType.WITCH, SoundEvents.PARROT_IMITATE_WITCH);
      p_29398_.put(EntityType.WITHER, SoundEvents.PARROT_IMITATE_WITHER);
      p_29398_.put(EntityType.WITHER_SKELETON, SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
      p_29398_.put(EntityType.ZOGLIN, SoundEvents.PARROT_IMITATE_ZOGLIN);
      p_29398_.put(EntityType.ZOMBIE, SoundEvents.PARROT_IMITATE_ZOMBIE);
      p_29398_.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.PARROT_IMITATE_ZOMBIE_VILLAGER);
   });
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   private float flapping = 1.0F;
   private float nextFlap = 1.0F;
   private boolean partyParrot;
   @Nullable
   private BlockPos jukebox;

   public Parrot(EntityType<? extends Parrot> p_29362_, Level p_29363_) {
      super(p_29362_, p_29363_);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29389_, DifficultyInstance p_29390_, MobSpawnType p_29391_, @Nullable SpawnGroupData p_29392_, @Nullable CompoundTag p_29393_) {
      this.setVariant(this.random.nextInt(5));
      if (p_29392_ == null) {
         p_29392_ = new AgeableMob.AgeableMobGroupData(false);
      }

      return super.finalizeSpawn(p_29389_, p_29390_, p_29391_, p_29392_, p_29393_);
   }

   public boolean isBaby() {
      return false;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
      this.goalSelector.addGoal(2, new Parrot.ParrotWanderGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.FLYING_SPEED, (double)0.4F).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
   }

   protected PathNavigation createNavigation(Level p_29417_) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_29417_);
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(true);
      flyingpathnavigation.setCanPassDoors(true);
      return flyingpathnavigation;
   }

   protected float getStandingEyeHeight(Pose p_29411_, EntityDimensions p_29412_) {
      return p_29412_.height * 0.6F;
   }

   public void aiStep() {
      if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46D) || !this.level.getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
         this.partyParrot = false;
         this.jukebox = null;
      }

      if (this.level.random.nextInt(400) == 0) {
         imitateNearbyMobs(this.level, this);
      }

      super.aiStep();
      this.calculateFlapping();
   }

   public void setRecordPlayingNearby(BlockPos p_29395_, boolean p_29396_) {
      this.jukebox = p_29395_;
      this.partyParrot = p_29396_;
   }

   public boolean isPartyParrot() {
      return this.partyParrot;
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed += (float)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3F;
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
   }

   public static boolean imitateNearbyMobs(Level p_29383_, Entity p_29384_) {
      if (p_29384_.isAlive() && !p_29384_.isSilent() && p_29383_.random.nextInt(2) == 0) {
         List<Mob> list = p_29383_.getEntitiesOfClass(Mob.class, p_29384_.getBoundingBox().inflate(20.0D), NOT_PARROT_PREDICATE);
         if (!list.isEmpty()) {
            Mob mob = list.get(p_29383_.random.nextInt(list.size()));
            if (!mob.isSilent()) {
               SoundEvent soundevent = getImitatedSound(mob.getType());
               p_29383_.playSound((Player)null, p_29384_.getX(), p_29384_.getY(), p_29384_.getZ(), soundevent, p_29384_.getSoundSource(), 0.7F, getPitch(p_29383_.random));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public InteractionResult mobInteract(Player p_29414_, InteractionHand p_29415_) {
      ItemStack itemstack = p_29414_.getItemInHand(p_29415_);
      if (!this.isTame() && TAME_FOOD.contains(itemstack.getItem())) {
         if (!p_29414_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         if (!this.isSilent()) {
            this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }

         if (!this.level.isClientSide) {
            if (this.random.nextInt(10) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_29414_)) {
               this.tame(p_29414_);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.level.broadcastEntityEvent(this, (byte)6);
            }
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (itemstack.is(POISONOUS_FOOD)) {
         if (!p_29414_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         this.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
         if (p_29414_.isCreative() || !this.isInvulnerable()) {
            this.hurt(DamageSource.playerAttack(p_29414_), Float.MAX_VALUE);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (!this.isFlying() && this.isTame() && this.isOwnedBy(p_29414_)) {
         if (!this.level.isClientSide) {
            this.setOrderedToSit(!this.isOrderedToSit());
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_29414_, p_29415_);
      }
   }

   public boolean isFood(ItemStack p_29446_) {
      return false;
   }

   public static boolean checkParrotSpawnRules(EntityType<Parrot> p_29424_, LevelAccessor p_29425_, MobSpawnType p_29426_, BlockPos p_29427_, Random p_29428_) {
      return p_29425_.getBlockState(p_29427_.below()).is(BlockTags.PARROTS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_29425_, p_29427_);
   }

   public boolean causeFallDamage(float p_148989_, float p_148990_, DamageSource p_148991_) {
      return false;
   }

   protected void checkFallDamage(double p_29370_, boolean p_29371_, BlockState p_29372_, BlockPos p_29373_) {
   }

   public boolean canMate(Animal p_29381_) {
      return false;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_148993_, AgeableMob p_148994_) {
      return null;
   }

   public boolean doHurtTarget(Entity p_29365_) {
      return p_29365_.hurt(DamageSource.mobAttack(this), 3.0F);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getAmbient(this.level, this.level.random);
   }

   public static SoundEvent getAmbient(Level p_29386_, Random p_29387_) {
      if (p_29386_.getDifficulty() != Difficulty.PEACEFUL && p_29387_.nextInt(1000) == 0) {
         List<EntityType<?>> list = Lists.newArrayList(MOB_SOUND_MAP.keySet());
         return getImitatedSound(list.get(p_29387_.nextInt(list.size())));
      } else {
         return SoundEvents.PARROT_AMBIENT;
      }
   }

   private static SoundEvent getImitatedSound(EntityType<?> p_29409_) {
      return MOB_SOUND_MAP.getOrDefault(p_29409_, SoundEvents.PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource p_29437_) {
      return SoundEvents.PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PARROT_DEATH;
   }

   protected void playStepSound(BlockPos p_29419_, BlockState p_29420_) {
      this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
   }

   protected boolean isFlapping() {
      return this.flyDist > this.nextFlap;
   }

   protected void onFlap() {
      this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
      this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
   }

   public float getVoicePitch() {
      return getPitch(this.random);
   }

   public static float getPitch(Random p_29400_) {
      return (p_29400_.nextFloat() - p_29400_.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   public boolean isPushable() {
      return true;
   }

   protected void doPush(Entity p_29367_) {
      if (!(p_29367_ instanceof Player)) {
         super.doPush(p_29367_);
      }
   }

   public boolean hurt(DamageSource p_29378_, float p_29379_) {
      if (this.isInvulnerableTo(p_29378_)) {
         return false;
      } else {
         if (!this.level.isClientSide) {
            this.setOrderedToSit(false);
         }

         return super.hurt(p_29378_, p_29379_);
      }
   }

   public int getVariant() {
      return Mth.clamp(this.entityData.get(DATA_VARIANT_ID), 0, 4);
   }

   public void setVariant(int p_29449_) {
      this.entityData.set(DATA_VARIANT_ID, p_29449_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT_ID, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_29422_) {
      super.addAdditionalSaveData(p_29422_);
      p_29422_.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag p_29402_) {
      super.readAdditionalSaveData(p_29402_);
      this.setVariant(p_29402_.getInt("Variant"));
   }

   public boolean isFlying() {
      return !this.onGround;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static class ParrotWanderGoal extends WaterAvoidingRandomFlyingGoal {
      public ParrotWanderGoal(PathfinderMob p_186224_, double p_186225_) {
         super(p_186224_, p_186225_);
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vec3 = null;
         if (this.mob.isInWater()) {
            vec3 = LandRandomPos.getPos(this.mob, 15, 15);
         }

         if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3 = this.getTreePos();
         }

         return vec3 == null ? super.getPosition() : vec3;
      }

      @Nullable
      private Vec3 getTreePos() {
         BlockPos blockpos = this.mob.blockPosition();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

         for(BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D), Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D))) {
            if (!blockpos.equals(blockpos1)) {
               BlockState blockstate = this.mob.level.getBlockState(blockpos$mutableblockpos1.setWithOffset(blockpos1, Direction.DOWN));
               boolean flag = blockstate.getBlock() instanceof LeavesBlock || blockstate.is(BlockTags.LOGS);
               if (flag && this.mob.level.isEmptyBlock(blockpos1) && this.mob.level.isEmptyBlock(blockpos$mutableblockpos.setWithOffset(blockpos1, Direction.UP))) {
                  return Vec3.atBottomCenterOf(blockpos1);
               }
            }
         }

         return null;
      }
   }
}
