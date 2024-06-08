package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetEntityLookTarget extends Behavior<LivingEntity> {
   private final Predicate<LivingEntity> predicate;
   private final float maxDistSqr;
   private Optional<LivingEntity> nearestEntityMatchingTest = Optional.empty();

   public SetEntityLookTarget(TagKey<EntityType<?>> p_204047_, float p_204048_) {
      this((p_204051_) -> {
         return p_204051_.getType().is(p_204047_);
      }, p_204048_);
   }

   public SetEntityLookTarget(MobCategory p_23897_, float p_23898_) {
      this((p_23923_) -> {
         return p_23897_.equals(p_23923_.getType().getCategory());
      }, p_23898_);
   }

   public SetEntityLookTarget(EntityType<?> p_23894_, float p_23895_) {
      this((p_23911_) -> {
         return p_23894_.equals(p_23911_.getType());
      }, p_23895_);
   }

   public SetEntityLookTarget(float p_23892_) {
      this((p_23913_) -> {
         return true;
      }, p_23892_);
   }

   public SetEntityLookTarget(Predicate<LivingEntity> p_23900_, float p_23901_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.predicate = p_23900_;
      this.maxDistSqr = p_23901_ * p_23901_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23903_, LivingEntity p_23904_) {
      NearestVisibleLivingEntities nearestvisiblelivingentities = p_23904_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
      this.nearestEntityMatchingTest = nearestvisiblelivingentities.findClosest(this.predicate.and((p_186053_) -> {
         return p_186053_.distanceToSqr(p_23904_) <= (double)this.maxDistSqr;
      }));
      return this.nearestEntityMatchingTest.isPresent();
   }

   protected void start(ServerLevel p_23906_, LivingEntity p_23907_, long p_23908_) {
      p_23907_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.nearestEntityMatchingTest.get(), true));
      this.nearestEntityMatchingTest = Optional.empty();
   }
}