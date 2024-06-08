package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public abstract class RepeatingPlacement extends PlacementModifier {
   protected abstract int count(Random p_191913_, BlockPos p_191914_);

   public Stream<BlockPos> getPositions(PlacementContext p_191916_, Random p_191917_, BlockPos p_191918_) {
      return IntStream.range(0, this.count(p_191917_, p_191918_)).mapToObj((p_191912_) -> {
         return p_191918_;
      });
   }
}