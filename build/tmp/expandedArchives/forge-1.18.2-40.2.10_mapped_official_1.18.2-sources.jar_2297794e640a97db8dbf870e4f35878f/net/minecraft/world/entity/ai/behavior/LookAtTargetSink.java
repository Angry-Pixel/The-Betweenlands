package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class LookAtTargetSink extends Behavior<Mob> {
   public LookAtTargetSink(int p_23478_, int p_23479_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), p_23478_, p_23479_);
   }

   protected boolean canStillUse(ServerLevel p_23481_, Mob p_23482_, long p_23483_) {
      return p_23482_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((p_23497_) -> {
         return p_23497_.isVisibleBy(p_23482_);
      }).isPresent();
   }

   protected void stop(ServerLevel p_23492_, Mob p_23493_, long p_23494_) {
      p_23493_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel p_23503_, Mob p_23504_, long p_23505_) {
      p_23504_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((p_23486_) -> {
         p_23504_.getLookControl().setLookAt(p_23486_.currentPosition());
      });
   }
}