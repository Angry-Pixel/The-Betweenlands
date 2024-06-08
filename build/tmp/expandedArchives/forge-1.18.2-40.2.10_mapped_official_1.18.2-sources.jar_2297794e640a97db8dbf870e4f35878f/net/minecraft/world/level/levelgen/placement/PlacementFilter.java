package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public abstract class PlacementFilter extends PlacementModifier {
   public final Stream<BlockPos> getPositions(PlacementContext p_191839_, Random p_191840_, BlockPos p_191841_) {
      return this.shouldPlace(p_191839_, p_191840_, p_191841_) ? Stream.of(p_191841_) : Stream.of();
   }

   protected abstract boolean shouldPlace(PlacementContext p_191835_, Random p_191836_, BlockPos p_191837_);
}