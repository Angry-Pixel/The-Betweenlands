package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StrollToPoi extends Behavior<PathfinderMob> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private final float speedModifier;
   private long nextOkStartTime;

   public StrollToPoi(MemoryModuleType<GlobalPos> p_24333_, float p_24334_, int p_24335_, int p_24336_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, p_24333_, MemoryStatus.VALUE_PRESENT));
      this.memoryType = p_24333_;
      this.speedModifier = p_24334_;
      this.closeEnoughDist = p_24335_;
      this.maxDistanceFromPoi = p_24336_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24345_, PathfinderMob p_24346_) {
      Optional<GlobalPos> optional = p_24346_.getBrain().getMemory(this.memoryType);
      return optional.isPresent() && p_24345_.dimension() == optional.get().dimension() && optional.get().pos().closerToCenterThan(p_24346_.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel p_24348_, PathfinderMob p_24349_, long p_24350_) {
      if (p_24350_ > this.nextOkStartTime) {
         Brain<?> brain = p_24349_.getBrain();
         Optional<GlobalPos> optional = brain.getMemory(this.memoryType);
         optional.ifPresent((p_24353_) -> {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_24353_.pos(), this.speedModifier, this.closeEnoughDist));
         });
         this.nextOkStartTime = p_24350_ + 80L;
      }

   }
}