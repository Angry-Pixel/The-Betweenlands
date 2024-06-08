package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RootsBlock extends BushBlock {
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   public RootsBlock(BlockBehaviour.Properties p_55912_) {
      super(p_55912_);
   }

   public VoxelShape getShape(BlockState p_55915_, BlockGetter p_55916_, BlockPos p_55917_, CollisionContext p_55918_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_55920_, BlockGetter p_55921_, BlockPos p_55922_) {
      return p_55920_.is(BlockTags.NYLIUM) || p_55920_.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(p_55920_, p_55921_, p_55922_);
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }
}