package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RandomSwim;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.TryFindWater;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class AxolotlAi {
   private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
   private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.2F;
   private static final float SPEED_MULTIPLIER_ON_LAND = 0.15F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 0.5F;
   private static final float SPEED_MULTIPLIER_WHEN_CHASING_IN_WATER = 0.6F;
   private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT_IN_WATER = 0.6F;

   protected static Brain<?> makeBrain(Brain<Axolotl> p_149291_) {
      initCoreActivity(p_149291_);
      initIdleActivity(p_149291_);
      initFightActivity(p_149291_);
      initPlayDeadActivity(p_149291_);
      p_149291_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_149291_.setDefaultActivity(Activity.IDLE);
      p_149291_.useDefaultActivity();
      return p_149291_;
   }

   private static void initPlayDeadActivity(Brain<Axolotl> p_149297_) {
      p_149297_.addActivityAndRemoveMemoriesWhenStopped(Activity.PLAY_DEAD, ImmutableList.of(Pair.of(0, new PlayDead()), Pair.of(1, new EraseMemoryIf<>(AxolotlAi::isBreeding, MemoryModuleType.PLAY_DEAD_TICKS))), ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT)), ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
   }

   private static void initFightActivity(Brain<Axolotl> p_149303_) {
      p_149303_.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(new StopAttackingIfTargetInvalid<>(Axolotl::onStopAttacking), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(AxolotlAi::getSpeedModifierChasing), new MeleeAttack(20), new EraseMemoryIf<Axolotl>(AxolotlAi::isBreeding, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
   }

   private static void initCoreActivity(Brain<Axolotl> p_149307_) {
      p_149307_.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(), new ValidatePlayDead(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
   }

   private static void initIdleActivity(Brain<Axolotl> p_149309_) {
      p_149309_.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new AnimalMakeLove(EntityType.AXOLOTL, 0.2F)), Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(new FollowTemptation(AxolotlAi::getSpeedModifier), 1), Pair.of(new BabyFollowAdult<>(ADULT_FOLLOW_RANGE, AxolotlAi::getSpeedModifierFollowingAdult), 1)))), Pair.of(3, new StartAttacking<>(AxolotlAi::findNearestValidAttackTarget)), Pair.of(3, new TryFindWater(6, 0.15F)), Pair.of(4, new GateBehavior<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(Pair.of(new RandomSwim(0.5F), 2), Pair.of(new RandomStroll(0.15F, false), 2), Pair.of(new SetWalkTargetFromLookTarget(AxolotlAi::canSetWalkTargetFromLookTarget, AxolotlAi::getSpeedModifier, 3), 3), Pair.of(new RunIf<>(Entity::isInWaterOrBubble, new DoNothing(30, 60)), 5), Pair.of(new RunIf<>(Entity::isOnGround, new DoNothing(200, 400)), 5))))));
   }

   private static boolean canSetWalkTargetFromLookTarget(LivingEntity p_182381_) {
      Level level = p_182381_.level;
      Optional<PositionTracker> optional = p_182381_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
      if (optional.isPresent()) {
         BlockPos blockpos = optional.get().currentBlockPosition();
         return level.isWaterAt(blockpos) == p_182381_.isInWaterOrBubble();
      } else {
         return false;
      }
   }

   public static void updateActivity(Axolotl p_149293_) {
      Brain<Axolotl> brain = p_149293_.getBrain();
      Activity activity = brain.getActiveNonCoreActivity().orElse((Activity)null);
      if (activity != Activity.PLAY_DEAD) {
         brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
         if (activity == Activity.FIGHT && brain.getActiveNonCoreActivity().orElse((Activity)null) != Activity.FIGHT) {
            brain.setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
         }
      }

   }

   private static float getSpeedModifierChasing(LivingEntity p_149289_) {
      return p_149289_.isInWaterOrBubble() ? 0.6F : 0.15F;
   }

   private static float getSpeedModifierFollowingAdult(LivingEntity p_149295_) {
      return p_149295_.isInWaterOrBubble() ? 0.6F : 0.15F;
   }

   private static float getSpeedModifier(LivingEntity p_149301_) {
      return p_149301_.isInWaterOrBubble() ? 0.5F : 0.15F;
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Axolotl p_149299_) {
      return isBreeding(p_149299_) ? Optional.empty() : p_149299_.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
   }

   private static boolean isBreeding(Axolotl p_149305_) {
      return p_149305_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }

   public static Ingredient getTemptations() {
      return Ingredient.of(ItemTags.AXOLOTL_TEMPT_ITEMS);
   }
}