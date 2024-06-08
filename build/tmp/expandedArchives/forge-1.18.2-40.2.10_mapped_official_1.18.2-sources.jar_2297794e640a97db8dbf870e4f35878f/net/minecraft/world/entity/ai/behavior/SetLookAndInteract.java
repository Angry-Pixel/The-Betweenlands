package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetLookAndInteract extends Behavior<LivingEntity> {
   private final EntityType<?> type;
   private final int interactionRangeSqr;
   private final Predicate<LivingEntity> targetFilter;
   private final Predicate<LivingEntity> selfFilter;

   public SetLookAndInteract(EntityType<?> p_23945_, int p_23946_, Predicate<LivingEntity> p_23947_, Predicate<LivingEntity> p_23948_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = p_23945_;
      this.interactionRangeSqr = p_23946_ * p_23946_;
      this.targetFilter = p_23948_;
      this.selfFilter = p_23947_;
   }

   public SetLookAndInteract(EntityType<?> p_23942_, int p_23943_) {
      this(p_23942_, p_23943_, (p_23973_) -> {
         return true;
      }, (p_23971_) -> {
         return true;
      });
   }

   public boolean checkExtraStartConditions(ServerLevel p_23950_, LivingEntity p_23951_) {
      return this.selfFilter.test(p_23951_) && this.getVisibleEntities(p_23951_).contains(this::isMatchingTarget);
   }

   public void start(ServerLevel p_23953_, LivingEntity p_23954_, long p_23955_) {
      super.start(p_23953_, p_23954_, p_23955_);
      Brain<?> brain = p_23954_.getBrain();
      brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((p_186056_) -> {
         return p_186056_.findClosest((p_147899_) -> {
            return p_147899_.distanceToSqr(p_23954_) <= (double)this.interactionRangeSqr && this.isMatchingTarget(p_147899_);
         });
      }).ifPresent((p_186059_) -> {
         brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_186059_);
         brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_186059_, true));
      });
   }

   private boolean isMatchingTarget(LivingEntity p_23957_) {
      return this.type.equals(p_23957_.getType()) && this.targetFilter.test(p_23957_);
   }

   private NearestVisibleLivingEntities getVisibleEntities(LivingEntity p_186061_) {
      return p_186061_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
   }
}