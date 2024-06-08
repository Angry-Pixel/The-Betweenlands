package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Zombie extends Monster {
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
   public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
   public static final int REINFORCEMENT_ATTEMPTS = 50;
   public static final int REINFORCEMENT_RANGE_MAX = 40;
   public static final int REINFORCEMENT_RANGE_MIN = 7;
   private static final float BREAK_DOOR_CHANCE = 0.1F;
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_34284_) -> {
      return p_34284_ == Difficulty.HARD;
   };
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
   private boolean canBreakDoors;
   private int inWaterTime;
   private int conversionTime;

   public Zombie(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
      super(p_34271_, p_34272_);
   }

   public Zombie(Level p_34274_) {
      this(EntityType.ZOMBIE, p_34274_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new Zombie.ZombieAttackTurtleEggGoal(this, 1.0D, 3));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_BABY_ID, false);
      this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
      this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean isUnderWaterConverting() {
      return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean p_34337_) {
      if (this.supportsBreakDoorGoal() && GoalUtils.hasGroundPathNavigation(this)) {
         if (this.canBreakDoors != p_34337_) {
            this.canBreakDoors = p_34337_;
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(p_34337_);
            if (p_34337_) {
               this.goalSelector.addGoal(1, this.breakDoorGoal);
            } else {
               this.goalSelector.removeGoal(this.breakDoorGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.removeGoal(this.breakDoorGoal);
         this.canBreakDoors = false;
      }

   }

   protected boolean supportsBreakDoorGoal() {
      return true;
   }

   public boolean isBaby() {
      return this.getEntityData().get(DATA_BABY_ID);
   }

   protected int getExperienceReward(Player p_34322_) {
      if (this.isBaby()) {
         this.xpReward = (int)((double)this.xpReward * 2.5D);
      }

      return super.getExperienceReward(p_34322_);
   }

   public void setBaby(boolean p_34309_) {
      this.getEntityData().set(DATA_BABY_ID, p_34309_);
      if (this.level != null && !this.level.isClientSide) {
         AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
         attributeinstance.removeModifier(SPEED_MODIFIER_BABY);
         if (p_34309_) {
            attributeinstance.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_34307_) {
      if (DATA_BABY_ID.equals(p_34307_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_34307_);
   }

   protected boolean convertsInWater() {
      return true;
   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;
            if (this.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.DROWNED, (timer) -> this.conversionTime = timer)) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isEyeInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startUnderWaterConversion(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void aiStep() {
      if (this.isAlive()) {
         boolean flag = this.isSunSensitive() && this.isSunBurnTick();
         if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
               if (itemstack.isDamageableItem()) {
                  itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                  if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     this.broadcastBreakEvent(EquipmentSlot.HEAD);
                     this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               flag = false;
            }

            if (flag) {
               this.setSecondsOnFire(8);
            }
         }
      }

      super.aiStep();
   }

   private void startUnderWaterConversion(int p_34279_) {
      this.conversionTime = p_34279_;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.DROWNED);
      if (!this.isSilent()) {
         this.level.levelEvent((Player)null, 1040, this.blockPosition(), 0);
      }

   }

   protected void convertToZombieType(EntityType<? extends Zombie> p_34311_) {
      Zombie zombie = this.convertTo(p_34311_, true);
      if (zombie != null) {
         zombie.handleAttributes(zombie.level.getCurrentDifficultyAt(zombie.blockPosition()).getSpecialMultiplier());
         zombie.setCanBreakDoors(zombie.supportsBreakDoorGoal() && this.canBreakDoors());
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombie);
      }

   }

   protected boolean isSunSensitive() {
      return true;
   }

   public boolean hurt(DamageSource p_34288_, float p_34289_) {
      if (!super.hurt(p_34288_, p_34289_)) {
         return false;
      } else if (!(this.level instanceof ServerLevel)) {
         return false;
      } else {
         ServerLevel serverlevel = (ServerLevel)this.level;
         LivingEntity livingentity = this.getTarget();
         if (livingentity == null && p_34288_.getEntity() instanceof LivingEntity) {
            livingentity = (LivingEntity)p_34288_.getEntity();
         }

            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ());
         net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent event = net.minecraftforge.event.ForgeEventFactory.fireZombieSummonAid(this, level, i, j, k, livingentity, this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).getValue());
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW  ||
            livingentity != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            Zombie zombie = event.getCustomSummonedAid() != null && event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ? event.getCustomSummonedAid() : EntityType.ZOMBIE.create(this.level);

            for(int l = 0; l < 50; ++l) {
               int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               BlockPos blockpos = new BlockPos(i1, j1, k1);
               EntityType<?> entitytype = zombie.getType();
               SpawnPlacements.Type spawnplacements$type = SpawnPlacements.getPlacementType(entitytype);
               if (NaturalSpawner.isSpawnPositionOk(spawnplacements$type, this.level, blockpos, entitytype) && SpawnPlacements.checkSpawnRules(entitytype, serverlevel, MobSpawnType.REINFORCEMENT, blockpos, this.level.random)) {
                  zombie.setPos((double)i1, (double)j1, (double)k1);
                  if (!this.level.hasNearbyAlivePlayer((double)i1, (double)j1, (double)k1, 7.0D) && this.level.isUnobstructed(zombie) && this.level.noCollision(zombie) && !this.level.containsAnyLiquid(zombie.getBoundingBox())) {
                     if (livingentity != null)
                     zombie.setTarget(livingentity);
                     zombie.finalizeSpawn(serverlevel, this.level.getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
                     serverlevel.addFreshEntityWithPassengers(zombie);
                     this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", (double)-0.05F, AttributeModifier.Operation.ADDITION));
                     zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", (double)-0.05F, AttributeModifier.Operation.ADDITION));
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean doHurtTarget(Entity p_34276_) {
      boolean flag = super.doHurtTarget(p_34276_);
      if (flag) {
         float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
            p_34276_.setSecondsOnFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_34327_) {
      return SoundEvents.ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos p_34316_, BlockState p_34317_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_34286_) {
      super.populateDefaultEquipmentSlots(p_34286_);
      if (this.random.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int i = this.random.nextInt(3);
         if (i == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   public void addAdditionalSaveData(CompoundTag p_34319_) {
      super.addAdditionalSaveData(p_34319_);
      p_34319_.putBoolean("IsBaby", this.isBaby());
      p_34319_.putBoolean("CanBreakDoors", this.canBreakDoors());
      p_34319_.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      p_34319_.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   public void readAdditionalSaveData(CompoundTag p_34305_) {
      super.readAdditionalSaveData(p_34305_);
      this.setBaby(p_34305_.getBoolean("IsBaby"));
      this.setCanBreakDoors(p_34305_.getBoolean("CanBreakDoors"));
      this.inWaterTime = p_34305_.getInt("InWaterTime");
      if (p_34305_.contains("DrownedConversionTime", 99) && p_34305_.getInt("DrownedConversionTime") > -1) {
         this.startUnderWaterConversion(p_34305_.getInt("DrownedConversionTime"));
      }

   }

   public void killed(ServerLevel p_34281_, LivingEntity p_34282_) {
      super.killed(p_34281_, p_34282_);
      if ((p_34281_.getDifficulty() == Difficulty.NORMAL || p_34281_.getDifficulty() == Difficulty.HARD) && p_34282_ instanceof Villager && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(p_34282_, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
         if (p_34281_.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return;
         }

         Villager villager = (Villager)p_34282_;
         ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
         zombievillager.finalizeSpawn(p_34281_, p_34281_.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag)null);
         zombievillager.setVillagerData(villager.getVillagerData());
         zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
         zombievillager.setTradeOffers(villager.getOffers().createTag());
         zombievillager.setVillagerXp(villager.getVillagerXp());
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(p_34282_, zombievillager);
         if (!this.isSilent()) {
            p_34281_.levelEvent((Player)null, 1026, this.blockPosition(), 0);
         }
      }

   }

   protected float getStandingEyeHeight(Pose p_34313_, EntityDimensions p_34314_) {
      return this.isBaby() ? 0.93F : 1.74F;
   }

   public boolean canHoldItem(ItemStack p_34332_) {
      return p_34332_.is(Items.EGG) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(p_34332_);
   }

   public boolean wantsToPickUp(ItemStack p_182400_) {
      return p_182400_.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp(p_182400_);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34297_, DifficultyInstance p_34298_, MobSpawnType p_34299_, @Nullable SpawnGroupData p_34300_, @Nullable CompoundTag p_34301_) {
      p_34300_ = super.finalizeSpawn(p_34297_, p_34298_, p_34299_, p_34300_, p_34301_);
      float f = p_34298_.getSpecialMultiplier();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
      if (p_34300_ == null) {
         p_34300_ = new Zombie.ZombieGroupData(getSpawnAsBabyOdds(p_34297_.getRandom()), true);
      }

      if (p_34300_ instanceof Zombie.ZombieGroupData) {
         Zombie.ZombieGroupData zombie$zombiegroupdata = (Zombie.ZombieGroupData)p_34300_;
         if (zombie$zombiegroupdata.isBaby) {
            this.setBaby(true);
            if (zombie$zombiegroupdata.canSpawnJockey) {
               if ((double)p_34297_.getRandom().nextFloat() < 0.05D) {
                  List<Chicken> list = p_34297_.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
                  if (!list.isEmpty()) {
                     Chicken chicken = list.get(0);
                     chicken.setChickenJockey(true);
                     this.startRiding(chicken);
                  }
               } else if ((double)p_34297_.getRandom().nextFloat() < 0.05D) {
                  Chicken chicken1 = EntityType.CHICKEN.create(this.level);
                  chicken1.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                  chicken1.finalizeSpawn(p_34297_, p_34298_, MobSpawnType.JOCKEY, (SpawnGroupData)null, (CompoundTag)null);
                  chicken1.setChickenJockey(true);
                  this.startRiding(chicken1);
                  p_34297_.addFreshEntity(chicken1);
               }
            }
         }

         this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < f * 0.1F);
         this.populateDefaultEquipmentSlots(p_34298_);
         this.populateDefaultEquipmentEnchantments(p_34298_);
      }

      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      this.handleAttributes(f);
      return p_34300_;
   }

   public static boolean getSpawnAsBabyOdds(Random p_34303_) {
      return p_34303_.nextFloat() < net.minecraftforge.common.ForgeConfig.SERVER.zombieBabyChance.get();
   }

   protected void handleAttributes(float p_34340_) {
      this.randomizeReinforcementsChance();
      this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
      double d0 = this.random.nextDouble() * 1.5D * (double)p_34340_;
      if (d0 > 1.0D) {
         this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }

      if (this.random.nextFloat() < p_34340_ * 0.05F) {
         this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
         this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
         this.setCanBreakDoors(this.supportsBreakDoorGoal());
      }

   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * net.minecraftforge.common.ForgeConfig.SERVER.zombieBaseSummonChance.get());
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? 0.0D : -0.45D;
   }

   protected void dropCustomDeathLoot(DamageSource p_34291_, int p_34292_, boolean p_34293_) {
      super.dropCustomDeathLoot(p_34291_, p_34292_, p_34293_);
      Entity entity = p_34291_.getEntity();
      if (entity instanceof Creeper) {
         Creeper creeper = (Creeper)entity;
         if (creeper.canDropMobsSkull()) {
            ItemStack itemstack = this.getSkull();
            if (!itemstack.isEmpty()) {
               creeper.increaseDroppedSkulls();
               this.spawnAtLocation(itemstack);
            }
         }
      }

   }

   protected ItemStack getSkull() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
      ZombieAttackTurtleEggGoal(PathfinderMob p_34344_, double p_34345_, int p_34346_) {
         super(Blocks.TURTLE_EGG, p_34344_, p_34345_, p_34346_);
      }

      public void playDestroyProgressSound(LevelAccessor p_34351_, BlockPos p_34352_) {
         p_34351_.playSound((Player)null, p_34352_, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + Zombie.this.random.nextFloat() * 0.2F);
      }

      public void playBreakSound(Level p_34348_, BlockPos p_34349_) {
         p_34348_.playSound((Player)null, p_34349_, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + p_34348_.random.nextFloat() * 0.2F);
      }

      public double acceptedDistance() {
         return 1.14D;
      }
   }

   public static class ZombieGroupData implements SpawnGroupData {
      public final boolean isBaby;
      public final boolean canSpawnJockey;

      public ZombieGroupData(boolean p_34357_, boolean p_34358_) {
         this.isBaby = p_34357_;
         this.canSpawnJockey = p_34358_;
      }
   }
}
