package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class PiglinBrute extends AbstractPiglin {
   private static final int MAX_HEALTH = 50;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
   private static final int ATTACK_DAMAGE = 7;
   protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBrute>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME);

   public PiglinBrute(EntityType<? extends PiglinBrute> p_35048_, Level p_35049_) {
      super(p_35048_, p_35049_);
      this.xpReward = 20;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, (double)0.35F).add(Attributes.ATTACK_DAMAGE, 7.0D);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_35058_, DifficultyInstance p_35059_, MobSpawnType p_35060_, @Nullable SpawnGroupData p_35061_, @Nullable CompoundTag p_35062_) {
      PiglinBruteAi.initMemories(this);
      this.populateDefaultEquipmentSlots(p_35059_);
      return super.finalizeSpawn(p_35058_, p_35059_, p_35060_, p_35061_, p_35062_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_35053_) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
   }

   protected Brain.Provider<PiglinBrute> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_35064_) {
      return PiglinBruteAi.makeBrain(this, this.brainProvider().makeBrain(p_35064_));
   }

   public Brain<PiglinBrute> getBrain() {
      return (Brain<PiglinBrute>)super.getBrain();
   }

   public boolean canHunt() {
      return false;
   }

   public boolean wantsToPickUp(ItemStack p_35078_) {
      return p_35078_.is(Items.GOLDEN_AXE) ? super.wantsToPickUp(p_35078_) : false;
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("piglinBruteBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      PiglinBruteAi.updateActivity(this);
      PiglinBruteAi.maybePlayActivitySound(this);
      super.customServerAiStep();
   }

   public PiglinArmPose getArmPose() {
      return this.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
   }

   public boolean hurt(DamageSource p_35055_, float p_35056_) {
      boolean flag = super.hurt(p_35055_, p_35056_);
      if (this.level.isClientSide) {
         return false;
      } else {
         if (flag && p_35055_.getEntity() instanceof LivingEntity) {
            PiglinBruteAi.wasHurtBy(this, (LivingEntity)p_35055_.getEntity());
         }

         return flag;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIGLIN_BRUTE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_35072_) {
      return SoundEvents.PIGLIN_BRUTE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_BRUTE_DEATH;
   }

   protected void playStepSound(BlockPos p_35066_, BlockState p_35067_) {
      this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, 1.0F, this.getVoicePitch());
   }

   protected void playConvertedSound() {
      this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch());
   }
}