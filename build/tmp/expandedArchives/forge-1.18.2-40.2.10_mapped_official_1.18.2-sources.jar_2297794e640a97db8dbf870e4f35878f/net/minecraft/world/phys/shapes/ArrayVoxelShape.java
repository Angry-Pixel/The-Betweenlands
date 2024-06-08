package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public class ArrayVoxelShape extends VoxelShape {
   private final DoubleList xs;
   private final DoubleList ys;
   private final DoubleList zs;

   protected ArrayVoxelShape(DiscreteVoxelShape p_82572_, double[] p_82573_, double[] p_82574_, double[] p_82575_) {
      this(p_82572_, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_82573_, p_82572_.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_82574_, p_82572_.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_82575_, p_82572_.getZSize() + 1)));
   }

   ArrayVoxelShape(DiscreteVoxelShape p_82567_, DoubleList p_82568_, DoubleList p_82569_, DoubleList p_82570_) {
      super(p_82567_);
      int i = p_82567_.getXSize() + 1;
      int j = p_82567_.getYSize() + 1;
      int k = p_82567_.getZSize() + 1;
      if (i == p_82568_.size() && j == p_82569_.size() && k == p_82570_.size()) {
         this.xs = p_82568_;
         this.ys = p_82569_;
         this.zs = p_82570_;
      } else {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getCoords(Direction.Axis p_82577_) {
      switch(p_82577_) {
      case X:
         return this.xs;
      case Y:
         return this.ys;
      case Z:
         return this.zs;
      default:
         throw new IllegalArgumentException();
      }
   }
}