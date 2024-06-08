package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;

public class SliceShape extends VoxelShape {
   private final VoxelShape delegate;
   private final Direction.Axis axis;
   private static final DoubleList SLICE_COORDS = new CubePointRange(1);

   public SliceShape(VoxelShape p_83173_, Direction.Axis p_83174_, int p_83175_) {
      super(makeSlice(p_83173_.shape, p_83174_, p_83175_));
      this.delegate = p_83173_;
      this.axis = p_83174_;
   }

   private static DiscreteVoxelShape makeSlice(DiscreteVoxelShape p_83177_, Direction.Axis p_83178_, int p_83179_) {
      return new SubShape(p_83177_, p_83178_.choose(p_83179_, 0, 0), p_83178_.choose(0, p_83179_, 0), p_83178_.choose(0, 0, p_83179_), p_83178_.choose(p_83179_ + 1, p_83177_.xSize, p_83177_.xSize), p_83178_.choose(p_83177_.ySize, p_83179_ + 1, p_83177_.ySize), p_83178_.choose(p_83177_.zSize, p_83177_.zSize, p_83179_ + 1));
   }

   protected DoubleList getCoords(Direction.Axis p_83181_) {
      return p_83181_ == this.axis ? SLICE_COORDS : this.delegate.getCoords(p_83181_);
   }
}