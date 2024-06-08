package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
   }

   protected void doTick(ServerLevel p_26721_, LivingEntity p_26722_) {
      Brain<?> brain = p_26722_.getBrain();
      List<AbstractPiglin> list = Lists.newArrayList();
      NearestVisibleLivingEntities nearestvisiblelivingentities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
      Optional<Mob> optional = nearestvisiblelivingentities.findClosest((p_186155_) -> {
         return p_186155_ instanceof WitherSkeleton || p_186155_ instanceof WitherBoss;
      }).map(Mob.class::cast);

      for(LivingEntity livingentity : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (livingentity instanceof AbstractPiglin && ((AbstractPiglin)livingentity).isAdult()) {
            list.add((AbstractPiglin)livingentity);
         }
      }

      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
      brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
   }
}