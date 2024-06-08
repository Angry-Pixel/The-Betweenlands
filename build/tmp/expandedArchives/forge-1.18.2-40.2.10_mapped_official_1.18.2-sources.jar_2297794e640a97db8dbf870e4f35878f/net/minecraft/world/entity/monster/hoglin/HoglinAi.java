package net.minecraft.world.entity.monster.hoglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.BecomePassiveIfMemoryPresent;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

public class HoglinAi {
   public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
   public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
   private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
   private static final int ATTACK_DURATION = 200;
   private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_IDLING = 8;
   private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_RETREATING = 15;
   private static final int ATTACK_INTERVAL = 40;
   private static final int BABY_ATTACK_INTERVAL = 15;
   private static final int REPELLENT_PACIFY_TIME = 200;
   private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
   private static final float SPEED_MULTIPLIER_WHEN_AVOIDING_REPELLENT = 1.0F;
   private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.3F;
   private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.6F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.4F;
   private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 0.6F;

   protected static Brain<?> makeBrain(Brain<Hoglin> p_34576_) {
      initCoreActivity(p_34576_);
      initIdleActivity(p_34576_);
      initFightActivity(p_34576_);
      initRetreatActivity(p_34576_);
      p_34576_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_34576_.setDefaultActivity(Activity.IDLE);
      p_34576_.useDefaultActivity();
      return p_34576_;
   }

   private static void initCoreActivity(Brain<Hoglin> p_34592_) {
      p_34592_.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
   }

   private static void initIdleActivity(Brain<Hoglin> p_34602_) {
      p_34602_.addActivity(Activity.IDLE, 10, ImmutableList.<net.minecraft.world.entity.ai.behavior.Behavior<? super Hoglin>>of(new BecomePassiveIfMemoryPresent(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalMakeLove(EntityType.HOGLIN, 0.6F), SetWalkTargetAwayFrom.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), new StartAttacking<Hoglin>(HoglinAi::findNearestValidAttackTarget), new RunIf<Hoglin>(Hoglin::isAdult, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)), new RunSometimes<LivingEntity>(new SetEntityLookTarget(8.0F), UniformInt.of(30, 60)), new BabyFollowAdult(ADULT_FOLLOW_RANGE, 0.6F), createIdleMovementBehaviors()));
   }

   private static void initFightActivity(Brain<Hoglin> p_34609_) {
      p_34609_.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.<net.minecraft.world.entity.ai.behavior.Behavior<? super Hoglin>>of(new BecomePassiveIfMemoryPresent(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalMakeLove(EntityType.HOGLIN, 0.6F), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new RunIf<>(Hoglin::isAdult, new MeleeAttack(40)), new RunIf<>(AgeableMob::isBaby, new MeleeAttack(15)), new StopAttackingIfTargetInvalid(), new EraseMemoryIf<Hoglin>(HoglinAi::isBreeding, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
   }

   private static void initRetreatActivity(Brain<Hoglin> p_34616_) {
      p_34616_.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false), createIdleMovementBehaviors(), new RunSometimes<LivingEntity>(new SetEntityLookTarget(8.0F), UniformInt.of(30, 60)), new EraseMemoryIf<Hoglin>(HoglinAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
   }

   private static RunOne<Hoglin> createIdleMovementBehaviors() {
      return new RunOne<>(ImmutableList.of(Pair.of(new RandomStroll(0.4F), 2), Pair.of(new SetWalkTargetFromLookTarget(0.4F, 3), 2), Pair.of(new DoNothing(30, 60), 1)));
   }

   protected static void updateActivity(Hoglin p_34578_) {
      Brain<Hoglin> brain = p_34578_.getBrain();
      Activity activity = brain.getActiveNonCoreActivity().orElse((Activity)null);
      brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
      Activity activity1 = brain.getActiveNonCoreActivity().orElse((Activity)null);
      if (activity != activity1) {
         getSoundForCurrentActivity(p_34578_).ifPresent(p_34578_::playSound);
      }

      p_34578_.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   protected static void onHitTarget(Hoglin p_34580_, LivingEntity p_34581_) {
      if (!p_34580_.isBaby()) {
         if (p_34581_.getType() == EntityType.PIGLIN && piglinsOutnumberHoglins(p_34580_)) {
            setAvoidTarget(p_34580_, p_34581_);
            broadcastRetreat(p_34580_, p_34581_);
         } else {
            broadcastAttackTarget(p_34580_, p_34581_);
         }
      }
   }

   private static void broadcastRetreat(Hoglin p_34606_, LivingEntity p_34607_) {
      getVisibleAdultHoglins(p_34606_).forEach((p_34590_) -> {
         retreatFromNearestTarget(p_34590_, p_34607_);
      });
   }

   private static void retreatFromNearestTarget(Hoglin p_34613_, LivingEntity p_34614_) {
      Brain<Hoglin> brain = p_34613_.getBrain();
      LivingEntity $$2 = BehaviorUtils.getNearestTarget(p_34613_, brain.getMemory(MemoryModuleType.AVOID_TARGET), p_34614_);
      $$2 = BehaviorUtils.getNearestTarget(p_34613_, brain.getMemory(MemoryModuleType.ATTACK_TARGET), $$2);
      setAvoidTarget(p_34613_, $$2);
   }

   private static void setAvoidTarget(Hoglin p_34620_, LivingEntity p_34621_) {
      p_34620_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      p_34620_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_34620_.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, p_34621_, (long)RETREAT_DURATION.sample(p_34620_.level.random));
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Hoglin p_34611_) {
      return !isPacified(p_34611_) && !isBreeding(p_34611_) ? p_34611_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
   }

   static boolean isPosNearNearestRepellent(Hoglin p_34586_, BlockPos p_34587_) {
      Optional<BlockPos> optional = p_34586_.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT);
      return optional.isPresent() && optional.get().closerThan(p_34587_, 8.0D);
   }

   private static boolean wantsToStopFleeing(Hoglin p_34618_) {
      return p_34618_.isAdult() && !piglinsOutnumberHoglins(p_34618_);
   }

   private static boolean piglinsOutnumberHoglins(Hoglin p_34623_) {
      if (p_34623_.isBaby()) {
         return false;
      } else {
         int i = p_34623_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
         int j = p_34623_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
         return i > j;
      }
   }

   protected static void wasHurtBy(Hoglin p_34596_, LivingEntity p_34597_) {
      Brain<Hoglin> brain = p_34596_.getBrain();
      brain.eraseMemory(MemoryModuleType.PACIFIED);
      brain.eraseMemory(MemoryModuleType.BREED_TARGET);
      if (p_34596_.isBaby()) {
         retreatFromNearestTarget(p_34596_, p_34597_);
      } else {
         maybeRetaliate(p_34596_, p_34597_);
      }
   }

   private static void maybeRetaliate(Hoglin p_34625_, LivingEntity p_34626_) {
      if (!p_34625_.getBrain().isActive(Activity.AVOID) || p_34626_.getType() != EntityType.PIGLIN) {
         if (p_34626_.getType() != EntityType.HOGLIN) {
            if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(p_34625_, p_34626_, 4.0D)) {
               if (Sensor.isEntityAttackable(p_34625_, p_34626_)) {
                  setAttackTarget(p_34625_, p_34626_);
                  broadcastAttackTarget(p_34625_, p_34626_);
               }
            }
         }
      }
   }

   private static void setAttackTarget(Hoglin p_34630_, LivingEntity p_34631_) {
      Brain<Hoglin> brain = p_34630_.getBrain();
      brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      brain.eraseMemory(MemoryModuleType.BREED_TARGET);
      brain.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, p_34631_, 200L);
   }

   private static void broadcastAttackTarget(Hoglin p_34635_, LivingEntity p_34636_) {
      getVisibleAdultHoglins(p_34635_).forEach((p_34574_) -> {
         setAttackTargetIfCloserThanCurrent(p_34574_, p_34636_);
      });
   }

   private static void setAttackTargetIfCloserThanCurrent(Hoglin p_34640_, LivingEntity p_34641_) {
      if (!isPacified(p_34640_)) {
         Optional<LivingEntity> optional = p_34640_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
         LivingEntity livingentity = BehaviorUtils.getNearestTarget(p_34640_, optional, p_34641_);
         setAttackTarget(p_34640_, livingentity);
      }
   }

   public static Optional<SoundEvent> getSoundForCurrentActivity(Hoglin p_34594_) {
      return p_34594_.getBrain().getActiveNonCoreActivity().map((p_34600_) -> {
         return getSoundForActivity(p_34594_, p_34600_);
      });
   }

   private static SoundEvent getSoundForActivity(Hoglin p_34583_, Activity p_34584_) {
      if (p_34584_ != Activity.AVOID && !p_34583_.isConverting()) {
         if (p_34584_ == Activity.FIGHT) {
            return SoundEvents.HOGLIN_ANGRY;
         } else {
            return isNearRepellent(p_34583_) ? SoundEvents.HOGLIN_RETREAT : SoundEvents.HOGLIN_AMBIENT;
         }
      } else {
         return SoundEvents.HOGLIN_RETREAT;
      }
   }

   private static List<Hoglin> getVisibleAdultHoglins(Hoglin p_34628_) {
      return p_34628_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
   }

   private static boolean isNearRepellent(Hoglin p_34633_) {
      return p_34633_.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
   }

   private static boolean isBreeding(Hoglin p_34638_) {
      return p_34638_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }

   protected static boolean isPacified(Hoglin p_34604_) {
      return p_34604_.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED);
   }
}