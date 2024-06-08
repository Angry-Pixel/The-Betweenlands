package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LongJumpMidJump;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class GoatAi {
   public static final int RAM_PREPARE_TIME = 20;
   public static final int RAM_MAX_DISTANCE = 7;
   private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
   private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.25F;
   private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
   private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
   private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.25F;
   private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(600, 1200);
   public static final int MAX_LONG_JUMP_HEIGHT = 5;
   public static final int MAX_LONG_JUMP_WIDTH = 5;
   public static final float MAX_JUMP_VELOCITY = 1.5F;
   private static final UniformInt TIME_BETWEEN_RAMS = UniformInt.of(600, 6000);
   private static final UniformInt TIME_BETWEEN_RAMS_SCREAMER = UniformInt.of(100, 300);
   private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((p_149452_) -> {
      return !p_149452_.getType().equals(EntityType.GOAT) && p_149452_.level.getWorldBorder().isWithinBounds(p_149452_.getBoundingBox());
   });
   private static final float SPEED_MULTIPLIER_WHEN_RAMMING = 3.0F;
   public static final int RAM_MIN_DISTANCE = 4;
   public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5F;
   public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0F;

   protected static void initMemories(Goat p_149450_) {
      p_149450_.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(p_149450_.level.random));
      p_149450_.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, TIME_BETWEEN_RAMS.sample(p_149450_.level.random));
   }

   protected static Brain<?> makeBrain(Brain<Goat> p_149448_) {
      initCoreActivity(p_149448_);
      initIdleActivity(p_149448_);
      initLongJumpActivity(p_149448_);
      initRamActivity(p_149448_);
      p_149448_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_149448_.setDefaultActivity(Activity.IDLE);
      p_149448_.useDefaultActivity();
      return p_149448_;
   }

   private static void initCoreActivity(Brain<Goat> p_149454_) {
      p_149454_.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new AnimalPanic(2.0F), new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)));
   }

   private static void initIdleActivity(Brain<Goat> p_149458_) {
      p_149458_.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(0, new AnimalMakeLove(EntityType.GOAT, 1.0F)), Pair.of(1, new FollowTemptation((p_149446_) -> {
         return 1.25F;
      })), Pair.of(2, new BabyFollowAdult<>(ADULT_FOLLOW_RANGE, 1.25F)), Pair.of(3, new RunOne<>(ImmutableList.of(Pair.of(new RandomStroll(1.0F), 2), Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2), Pair.of(new DoNothing(30, 60), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT)));
   }

   private static void initLongJumpActivity(Brain<Goat> p_149462_) {
      p_149462_.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.GOAT_STEP)), Pair.of(1, new LongJumpToRandomPos<>(TIME_BETWEEN_LONG_JUMPS, 5, 5, 1.5F, (p_149476_) -> {
         return p_149476_.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_LONG_JUMP : SoundEvents.GOAT_LONG_JUMP;
      }))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
   }

   private static void initRamActivity(Brain<Goat> p_149466_) {
      p_149466_.addActivityWithConditions(Activity.RAM, ImmutableList.of(Pair.of(0, new RamTarget<>((p_149474_) -> {
         return p_149474_.isScreamingGoat() ? TIME_BETWEEN_RAMS_SCREAMER : TIME_BETWEEN_RAMS;
      }, RAM_TARGET_CONDITIONS, 3.0F, (p_149470_) -> {
         return p_149470_.isBaby() ? 1.0D : 2.5D;
      }, (p_149468_) -> {
         return p_149468_.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT;
      })), Pair.of(1, new PrepareRamNearestTarget<>((p_149464_) -> {
         return p_149464_.isScreamingGoat() ? TIME_BETWEEN_RAMS_SCREAMER.getMinValue() : TIME_BETWEEN_RAMS.getMinValue();
      }, 4, 7, 1.25F, RAM_TARGET_CONDITIONS, 20, (p_149460_) -> {
         return p_149460_.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_PREPARE_RAM : SoundEvents.GOAT_PREPARE_RAM;
      }))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
   }

   public static void updateActivity(Goat p_149456_) {
      p_149456_.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE));
   }

   public static Ingredient getTemptations() {
      return Ingredient.of(Items.WHEAT);
   }
}