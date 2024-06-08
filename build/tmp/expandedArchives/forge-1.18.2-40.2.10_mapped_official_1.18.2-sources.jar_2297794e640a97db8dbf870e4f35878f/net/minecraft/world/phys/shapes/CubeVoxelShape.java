package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public final class CubeVoxelShape extends VoxelShape {
   protected CubeVoxelShape(DiscreteVoxelShape p_82765_) {
      super(p_82765_);
   }

   protected DoubleList getCoords(Direction.Axis p_82767_) {
      return new CubePointRange(this.shape.getSize(p_82767_));
   }

   protected int findIndex(Direction.Axis p_82769_, double p_82770_) {
      int i = this.shape.getSize(p_82769_);
      return Mth.floor(Mth.clamp(p_82770_ * (double)i, -1.0D, (double)i));
   }
}