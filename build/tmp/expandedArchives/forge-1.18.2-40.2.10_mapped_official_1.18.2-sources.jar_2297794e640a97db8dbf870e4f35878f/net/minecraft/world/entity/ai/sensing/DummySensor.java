package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class DummySensor extends Sensor<LivingEntity> {
   protected void doTick(ServerLevel p_26638_, LivingEntity p_26639_) {
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of();
   }
}