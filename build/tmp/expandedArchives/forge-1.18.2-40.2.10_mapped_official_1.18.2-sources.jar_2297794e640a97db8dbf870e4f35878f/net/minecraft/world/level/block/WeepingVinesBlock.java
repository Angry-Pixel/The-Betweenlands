package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingVinesBlock extends GrowingPlantHeadBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public WeepingVinesBlock(BlockBehaviour.Properties p_154966_) {
      super(p_154966_, Direction.DOWN, SHAPE, false, 0.1D);
   }

   protected int getBlocksToGrowWhenBonemealed(Random p_154968_) {
      return NetherVines.getBlocksToGrowWhenBonemealed(p_154968_);
   }

   protected Block getBodyBlock() {
      return Blocks.WEEPING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState p_154971_) {
      return NetherVines.isValidGrowthState(p_154971_);
   }
}