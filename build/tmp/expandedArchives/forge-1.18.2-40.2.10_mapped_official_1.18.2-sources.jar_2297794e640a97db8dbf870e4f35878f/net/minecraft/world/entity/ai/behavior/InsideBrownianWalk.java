package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk extends Behavior<PathfinderMob> {
   private final float speedModifier;

   public InsideBrownianWalk(float p_23209_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = p_23209_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23218_, PathfinderMob p_23219_) {
      return !p_23218_.canSeeSky(p_23219_.blockPosition());
   }

   protected void start(ServerLevel p_23221_, PathfinderMob p_23222_, long p_23223_) {
      BlockPos blockpos = p_23222_.blockPosition();
      List<BlockPos> list = BlockPos.betweenClosedStream(blockpos.offset(-1, -1, -1), blockpos.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
      Collections.shuffle(list);
      Optional<BlockPos> optional = list.stream().filter((p_23230_) -> {
         return !p_23221_.canSeeSky(p_23230_);
      }).filter((p_23237_) -> {
         return p_23221_.loadedAndEntityCanStandOn(p_23237_, p_23222_);
      }).filter((p_23227_) -> {
         return p_23221_.noCollision(p_23222_);
      }).findFirst();
      optional.ifPresent((p_23233_) -> {
         p_23222_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_23233_, this.speedModifier, 0));
      });
   }
}