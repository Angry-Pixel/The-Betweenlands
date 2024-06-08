package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunOne<E extends LivingEntity> extends GateBehavior<E> {
   public RunOne(List<Pair<Behavior<? super E>, Integer>> p_23832_) {
      this(ImmutableMap.of(), p_23832_);
   }

   public RunOne(Map<MemoryModuleType<?>, MemoryStatus> p_23834_, List<Pair<Behavior<? super E>, Integer>> p_23835_) {
      super(p_23834_, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, p_23835_);
   }
}