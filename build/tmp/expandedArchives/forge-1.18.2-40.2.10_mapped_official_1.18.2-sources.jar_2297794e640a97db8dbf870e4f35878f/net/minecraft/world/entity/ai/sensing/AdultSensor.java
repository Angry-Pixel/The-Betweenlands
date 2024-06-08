package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class AdultSensor extends Sensor<AgeableMob> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerLevel p_148248_, AgeableMob p_148249_) {
      p_148249_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((p_186145_) -> {
         this.setNearestVisibleAdult(p_148249_, p_186145_);
      });
   }

   private void setNearestVisibleAdult(AgeableMob p_186141_, NearestVisibleLivingEntities p_186142_) {
      Optional<AgeableMob> optional = p_186142_.findClosest((p_148254_) -> {
         return p_148254_.getType() == p_186141_.getType() && !p_148254_.isBaby();
      }).map(AgeableMob.class::cast);
      p_186141_.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
   }
}