package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class Llama extends AbstractChestedHorse implements RangedAttackMob {
   private static final int MAX_STRENGTH = 5;
   private static final int VARIANTS = 4;
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
   private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_SWAG_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   boolean didSpit;
   @Nullable
   private Llama caravanHead;
   @Nullable
   private Llama caravanTail;

   public Llama(EntityType<? extends Llama> p_30750_, Level p_30751_) {
      super(p_30750_, p_30751_);
   }

   public boolean isTraderLlama() {
      return false;
   }

   private void setStrength(int p_30841_) {
      this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, p_30841_)));
   }

   private void setRandomStrength() {
      int i = this.random.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + this.random.nextInt(i));
   }

   public int getStrength() {
      return this.entityData.get(DATA_STRENGTH_ID);
   }

   public void addAdditionalSaveData(CompoundTag p_30793_) {
      super.addAdditionalSaveData(p_30793_);
      p_30793_.putInt("Variant", this.getVariant());
      p_30793_.putInt("Strength", this.getStrength());
      if (!this.inventory.getItem(1).isEmpty()) {
         p_30793_.put("DecorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag p_30780_) {
      this.setStrength(p_30780_.getInt("Strength"));
      super.readAdditionalSaveData(p_30780_);
      this.setVariant(p_30780_.getInt("Variant"));
      if (p_30780_.contains("DecorItem", 10)) {
         this.inventory.setItem(1, ItemStack.of(p_30780_.getCompound("DecorItem")));
      }

      this.updateContainerEquipment();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, (double)2.1F));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25D, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(5, new TemptGoal(this, 1.25D, Ingredient.of(Items.HAY_BLOCK), false));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7D));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new Llama.LlamaHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new Llama.LlamaAttackWolfGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseChestedHorseAttributes().add(Attributes.FOLLOW_RANGE, 40.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_STRENGTH_ID, 0);
      this.entityData.define(DATA_SWAG_ID, -1);
      this.entityData.define(DATA_VARIANT_ID, 0);
   }

   public int getVariant() {
      return Mth.clamp(this.entityData.get(DATA_VARIANT_ID), 0, 3);
   }

   public void setVariant(int p_30839_) {
      this.entityData.set(DATA_VARIANT_ID, p_30839_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
   }

   public void positionRider(Entity p_30830_) {
      if (this.hasPassenger(p_30830_)) {
         float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
         float f1 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
         float f2 = 0.3F;
         p_30830_.setPos(this.getX() + (double)(0.3F * f1), this.getY() + this.getPassengersRidingOffset() + p_30830_.getMyRidingOffset(), this.getZ() - (double)(0.3F * f));
      }
   }

   public double getPassengersRidingOffset() {
      return (double)this.getBbHeight() * 0.6D;
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   public boolean isFood(ItemStack p_30832_) {
      return FOOD_ITEMS.test(p_30832_);
   }

   protected boolean handleEating(Player p_30796_, ItemStack p_30797_) {
      int i = 0;
      int j = 0;
      float f = 0.0F;
      boolean flag = false;
      if (p_30797_.is(Items.WHEAT)) {
         i = 10;
         j = 3;
         f = 2.0F;
      } else if (p_30797_.is(Blocks.HAY_BLOCK.asItem())) {
         i = 90;
         j = 6;
         f = 10.0F;
         if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
            flag = true;
            this.setInLove(p_30796_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isBaby() && i > 0) {
         this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.level.isClientSide) {
            this.ageUp(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.level.isClientSide) {
            this.modifyTemper(j);
         }
      }

      if (flag) {
         this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
         if (!this.isSilent()) {
            SoundEvent soundevent = this.getEatingSound();
            if (soundevent != null) {
               this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
         }
      }

      return flag;
   }

   protected boolean isImmobile() {
      return this.isDeadOrDying() || this.isEating();
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30774_, DifficultyInstance p_30775_, MobSpawnType p_30776_, @Nullable SpawnGroupData p_30777_, @Nullable CompoundTag p_30778_) {
      this.setRandomStrength();
      int i;
      if (p_30777_ instanceof Llama.LlamaGroupData) {
         i = ((Llama.LlamaGroupData)p_30777_).variant;
      } else {
         i = this.random.nextInt(4);
         p_30777_ = new Llama.LlamaGroupData(i);
      }

      this.setVariant(i);
      return super.finalizeSpawn(p_30774_, p_30775_, p_30776_, p_30777_, p_30778_);
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_30803_) {
      return SoundEvents.LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.LLAMA_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.LLAMA_EAT;
   }

   protected void playStepSound(BlockPos p_30790_, BlockState p_30791_) {
      this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public void makeMad() {
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public int getInventoryColumns() {
      return this.getStrength();
   }

   public boolean canWearArmor() {
      return true;
   }

   public boolean isWearingArmor() {
      return !this.inventory.getItem(1).isEmpty();
   }

   public boolean isArmor(ItemStack p_30834_) {
      return p_30834_.is(ItemTags.CARPETS);
   }

   public boolean isSaddleable() {
      return false;
   }

   public void containerChanged(Container p_30760_) {
      DyeColor dyecolor = this.getSwag();
      super.containerChanged(p_30760_);
      DyeColor dyecolor1 = this.getSwag();
      if (this.tickCount > 20 && dyecolor1 != null && dyecolor1 != dyecolor) {
         this.playSound(SoundEvents.LLAMA_SWAG, 0.5F, 1.0F);
      }

   }

   protected void updateContainerEquipment() {
      if (!this.level.isClientSide) {
         super.updateContainerEquipment();
         this.setSwag(getDyeColor(this.inventory.getItem(1)));
      }
   }

   private void setSwag(@Nullable DyeColor p_30772_) {
      this.entityData.set(DATA_SWAG_ID, p_30772_ == null ? -1 : p_30772_.getId());
   }

   @Nullable
   private static DyeColor getDyeColor(ItemStack p_30836_) {
      Block block = Block.byItem(p_30836_.getItem());
      return block instanceof WoolCarpetBlock ? ((WoolCarpetBlock)block).getColor() : null;
   }

   @Nullable
   public DyeColor getSwag() {
      int i = this.entityData.get(DATA_SWAG_ID);
      return i == -1 ? null : DyeColor.byId(i);
   }

   public int getMaxTemper() {
      return 30;
   }

   public boolean canMate(Animal p_30765_) {
      return p_30765_ != this && p_30765_ instanceof Llama && this.canParent() && ((Llama)p_30765_).canParent();
   }

   public Llama getBreedOffspring(ServerLevel p_149545_, AgeableMob p_149546_) {
      Llama llama = this.makeBabyLlama();
      this.setOffspringAttributes(p_149546_, llama);
      Llama llama1 = (Llama)p_149546_;
      int i = this.random.nextInt(Math.max(this.getStrength(), llama1.getStrength())) + 1;
      if (this.random.nextFloat() < 0.03F) {
         ++i;
      }

      llama.setStrength(i);
      llama.setVariant(this.random.nextBoolean() ? this.getVariant() : llama1.getVariant());
      return llama;
   }

   protected Llama makeBabyLlama() {
      return EntityType.LLAMA.create(this.level);
   }

   private void spit(LivingEntity p_30828_) {
      LlamaSpit llamaspit = new LlamaSpit(this.level, this);
      double d0 = p_30828_.getX() - this.getX();
      double d1 = p_30828_.getY(0.3333333333333333D) - llamaspit.getY();
      double d2 = p_30828_.getZ() - this.getZ();
      double d3 = Math.sqrt(d0 * d0 + d2 * d2) * (double)0.2F;
      llamaspit.shoot(d0, d1 + d3, d2, 1.5F, 10.0F);
      if (!this.isSilent()) {
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }

      this.level.addFreshEntity(llamaspit);
      this.didSpit = true;
   }

   void setDidSpit(boolean p_30753_) {
      this.didSpit = p_30753_;
   }

   public boolean causeFallDamage(float p_149538_, float p_149539_, DamageSource p_149540_) {
      int i = this.calculateFallDamage(p_149538_, p_149539_);
      if (i <= 0) {
         return false;
      } else {
         if (p_149538_ >= 6.0F) {
            this.hurt(p_149540_, (float)i);
            if (this.isVehicle()) {
               for(Entity entity : this.getIndirectPassengers()) {
                  entity.hurt(p_149540_, (float)i);
               }
            }
         }

         this.playBlockFallSound();
         return true;
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(Llama p_30767_) {
      this.caravanHead = p_30767_;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public Llama getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   protected void followMommy() {
      if (!this.inCaravan() && this.isBaby()) {
         super.followMommy();
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   public void performRangedAttack(LivingEntity p_30762_, float p_30763_) {
      this.spit(p_30762_);
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, 0.75D * (double)this.getEyeHeight(), (double)this.getBbWidth() * 0.5D);
   }

   static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal<Wolf> {
      public LlamaAttackWolfGoal(Llama p_30843_) {
         super(p_30843_, Wolf.class, 16, false, true, (p_30845_) -> {
            return !((Wolf)p_30845_).isTame();
         });
      }

      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.25D;
      }
   }

   static class LlamaGroupData extends AgeableMob.AgeableMobGroupData {
      public final int variant;

      LlamaGroupData(int p_30849_) {
         super(true);
         this.variant = p_30849_;
      }
   }

   static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
      public LlamaHurtByTargetGoal(Llama p_30854_) {
         super(p_30854_);
      }

      public boolean canContinueToUse() {
         if (this.mob instanceof Llama) {
            Llama llama = (Llama)this.mob;
            if (llama.didSpit) {
               llama.setDidSpit(false);
               return false;
            }
         }

         return super.canContinueToUse();
      }
   }
}