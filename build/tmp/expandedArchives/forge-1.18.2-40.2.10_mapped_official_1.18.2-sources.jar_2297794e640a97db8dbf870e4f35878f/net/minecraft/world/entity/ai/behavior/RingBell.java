package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RingBell extends Behavior<LivingEntity> {
   private static final float BELL_RING_CHANCE = 0.95F;
   public static final int RING_BELL_FROM_DISTANCE = 3;

   public RingBell() {
      super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23789_, LivingEntity p_23790_) {
      return p_23789_.random.nextFloat() > 0.95F;
   }

   protected void start(ServerLevel p_23792_, LivingEntity p_23793_, long p_23794_) {
      Brain<?> brain = p_23793_.getBrain();
      BlockPos blockpos = brain.getMemory(MemoryModuleType.MEETING_POINT).get().pos();
      if (blockpos.closerThan(p_23793_.blockPosition(), 3.0D)) {
         BlockState blockstate = p_23792_.getBlockState(blockpos);
         if (blockstate.is(Blocks.BELL)) {
            BellBlock bellblock = (BellBlock)blockstate.getBlock();
            bellblock.attemptToRing(p_23793_, p_23792_, blockpos, (Direction)null);
         }
      }

   }
}