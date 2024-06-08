package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TryFindWater extends Behavior<PathfinderMob> {
   private final int range;
   private final float speedModifier;
   private long nextOkStartTime;

   public TryFindWater(int p_148002_, float p_148003_) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
      this.range = p_148002_;
      this.speedModifier = p_148003_;
   }

   protected void stop(ServerLevel p_148015_, PathfinderMob p_148016_, long p_148017_) {
      this.nextOkStartTime = p_148017_ + 20L + 2L;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_148012_, PathfinderMob p_148013_) {
      return !p_148013_.level.getFluidState(p_148013_.blockPosition()).is(FluidTags.WATER);
   }

   protected void start(ServerLevel p_148019_, PathfinderMob p_148020_, long p_148021_) {
      if (p_148021_ >= this.nextOkStartTime) {
         BlockPos blockpos = null;
         BlockPos blockpos1 = null;
         BlockPos blockpos2 = p_148020_.blockPosition();

         for(BlockPos blockpos3 : BlockPos.withinManhattan(blockpos2, this.range, this.range, this.range)) {
            if (blockpos3.getX() != blockpos2.getX() || blockpos3.getZ() != blockpos2.getZ()) {
               BlockState blockstate = p_148020_.level.getBlockState(blockpos3.above());
               BlockState blockstate1 = p_148020_.level.getBlockState(blockpos3);
               if (blockstate1.is(Blocks.WATER)) {
                  if (blockstate.isAir()) {
                     blockpos = blockpos3.immutable();
                     break;
                  }

                  if (blockpos1 == null && !blockpos3.closerToCenterThan(p_148020_.position(), 1.5D)) {
                     blockpos1 = blockpos3.immutable();
                  }
               }
            }
         }

         if (blockpos == null) {
            blockpos = blockpos1;
         }

         if (blockpos != null) {
            this.nextOkStartTime = p_148021_ + 40L;
            BehaviorUtils.setWalkAndLookTargetMemories(p_148020_, blockpos, this.speedModifier, 0);
         }

      }
   }
}