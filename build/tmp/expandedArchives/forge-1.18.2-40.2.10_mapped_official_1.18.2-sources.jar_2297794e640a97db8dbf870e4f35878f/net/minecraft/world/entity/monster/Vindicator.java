package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Vindicator extends AbstractIllager {
   private static final String TAG_JOHNNY = "Johnny";
   static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_34082_) -> {
      return p_34082_ == Difficulty.NORMAL || p_34082_ == Difficulty.HARD;
   };
   boolean isJohnny;

   public Vindicator(EntityType<? extends Vindicator> p_34074_, Level p_34075_) {
      super(p_34074_, p_34075_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Vindicator.VindicatorBreakDoorGoal(this));
      this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new Vindicator.VindicatorMeleeAttackGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
      this.targetSelector.addGoal(4, new Vindicator.VindicatorJohnnyAttackGoal(this));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   protected void customServerAiStep() {
      if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
         boolean flag = ((ServerLevel)this.level).isRaided(this.blockPosition());
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(flag);
      }

      super.customServerAiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.35F).add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ATTACK_DAMAGE, 5.0D);
   }

   public void addAdditionalSaveData(CompoundTag p_34100_) {
      super.addAdditionalSaveData(p_34100_);
      if (this.isJohnny) {
         p_34100_.putBoolean("Johnny", true);
      }

   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isAggressive()) {
         return AbstractIllager.IllagerArmPose.ATTACKING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public void readAdditionalSaveData(CompoundTag p_34094_) {
      super.readAdditionalSaveData(p_34094_);
      if (p_34094_.contains("Johnny", 99)) {
         this.isJohnny = p_34094_.getBoolean("Johnny");
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.VINDICATOR_CELEBRATE;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34088_, DifficultyInstance p_34089_, MobSpawnType p_34090_, @Nullable SpawnGroupData p_34091_, @Nullable CompoundTag p_34092_) {
      SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34088_, p_34089_, p_34090_, p_34091_, p_34092_);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.populateDefaultEquipmentSlots(p_34089_);
      this.populateDefaultEquipmentEnchantments(p_34089_);
      return spawngroupdata;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_34084_) {
      if (this.getCurrentRaid() == null) {
         this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
      }

   }

   public boolean isAlliedTo(Entity p_34110_) {
      if (super.isAlliedTo(p_34110_)) {
         return true;
      } else if (p_34110_ instanceof LivingEntity && ((LivingEntity)p_34110_).getMobType() == MobType.ILLAGER) {
         return this.getTeam() == null && p_34110_.getTeam() == null;
      } else {
         return false;
      }
   }

   public void setCustomName(@Nullable Component p_34096_) {
      super.setCustomName(p_34096_);
      if (!this.isJohnny && p_34096_ != null && p_34096_.getString().equals("Johnny")) {
         this.isJohnny = true;
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_34103_) {
      return SoundEvents.VINDICATOR_HURT;
   }

   public void applyRaidBuffs(int p_34079_, boolean p_34080_) {
      ItemStack itemstack = new ItemStack(Items.IRON_AXE);
      Raid raid = this.getCurrentRaid();
      int i = 1;
      if (p_34079_ > raid.getNumGroups(Difficulty.NORMAL)) {
         i = 2;
      }

      boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
      if (flag) {
         Map<Enchantment, Integer> map = Maps.newHashMap();
         map.put(Enchantments.SHARPNESS, i);
         EnchantmentHelper.setEnchantments(map, itemstack);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
   }

   static class VindicatorBreakDoorGoal extends BreakDoorGoal {
      public VindicatorBreakDoorGoal(Mob p_34112_) {
         super(p_34112_, 6, Vindicator.DOOR_BREAKING_PREDICATE);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canContinueToUse() {
         Vindicator vindicator = (Vindicator)this.mob;
         return vindicator.hasActiveRaid() && super.canContinueToUse();
      }

      public boolean canUse() {
         Vindicator vindicator = (Vindicator)this.mob;
         return vindicator.hasActiveRaid() && vindicator.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public VindicatorJohnnyAttackGoal(Vindicator p_34117_) {
         super(p_34117_, LivingEntity.class, 0, true, true, LivingEntity::attackable);
      }

      public boolean canUse() {
         return ((Vindicator)this.mob).isJohnny && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   class VindicatorMeleeAttackGoal extends MeleeAttackGoal {
      public VindicatorMeleeAttackGoal(Vindicator p_34123_) {
         super(p_34123_, 1.0D, false);
      }

      protected double getAttackReachSqr(LivingEntity p_34125_) {
         if (this.mob.getVehicle() instanceof Ravager) {
            float f = this.mob.getVehicle().getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + p_34125_.getBbWidth());
         } else {
            return super.getAttackReachSqr(p_34125_);
         }
      }
   }
}