package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
   protected abstract boolean isMatchingEntity(LivingEntity p_148292_, LivingEntity p_148293_);

   protected abstract MemoryModuleType<LivingEntity> getMemory();

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(this.getMemory());
   }

   protected void doTick(ServerLevel p_148288_, LivingEntity p_148289_) {
      p_148289_.getBrain().setMemory(this.getMemory(), this.getNearestEntity(p_148289_));
   }

   private Optional<LivingEntity> getNearestEntity(LivingEntity p_148298_) {
      return this.getVisibleEntities(p_148298_).flatMap((p_186153_) -> {
         return p_186153_.findClosest((p_148301_) -> {
            return this.isMatchingEntity(p_148298_, p_148301_);
         });
      });
   }

   protected Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity p_148291_) {
      return p_148291_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}