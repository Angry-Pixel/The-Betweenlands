package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetHiddenState extends Behavior<LivingEntity> {
   private static final int HIDE_TIMEOUT = 300;
   private final int closeEnoughDist;
   private final int stayHiddenTicks;
   private int ticksHidden;

   public SetHiddenState(int p_23931_, int p_23932_) {
      super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
      this.stayHiddenTicks = p_23931_ * 20;
      this.ticksHidden = 0;
      this.closeEnoughDist = p_23932_;
   }

   protected void start(ServerLevel p_23934_, LivingEntity p_23935_, long p_23936_) {
      Brain<?> brain = p_23935_.getBrain();
      Optional<Long> optional = brain.getMemory(MemoryModuleType.HEARD_BELL_TIME);
      boolean flag = optional.get() + 300L <= p_23936_;
      if (this.ticksHidden <= this.stayHiddenTicks && !flag) {
         BlockPos blockpos = brain.getMemory(MemoryModuleType.HIDING_PLACE).get().pos();
         if (blockpos.closerThan(p_23935_.blockPosition(), (double)this.closeEnoughDist)) {
            ++this.ticksHidden;
         }

      } else {
         brain.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
         brain.eraseMemory(MemoryModuleType.HIDING_PLACE);
         brain.updateActivityFromSchedule(p_23934_.getDayTime(), p_23934_.getGameTime());
         this.ticksHidden = 0;
      }
   }
}