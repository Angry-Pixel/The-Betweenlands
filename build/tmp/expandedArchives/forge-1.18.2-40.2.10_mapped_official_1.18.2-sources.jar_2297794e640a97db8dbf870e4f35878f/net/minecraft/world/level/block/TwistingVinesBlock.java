package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesBlock extends GrowingPlantHeadBlock {
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

   public TwistingVinesBlock(BlockBehaviour.Properties p_154864_) {
      super(p_154864_, Direction.UP, SHAPE, false, 0.1D);
   }

   protected int getBlocksToGrowWhenBonemealed(Random p_154866_) {
      return NetherVines.getBlocksToGrowWhenBonemealed(p_154866_);
   }

   protected Block getBodyBlock() {
      return Blocks.TWISTING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState p_154869_) {
      return NetherVines.isValidGrowthState(p_154869_);
   }
}