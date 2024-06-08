package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierBlock extends Block {
   public BarrierBlock(BlockBehaviour.Properties p_49092_) {
      super(p_49092_);
   }

   public boolean propagatesSkylightDown(BlockState p_49100_, BlockGetter p_49101_, BlockPos p_49102_) {
      return true;
   }

   public RenderShape getRenderShape(BlockState p_49098_) {
      return RenderShape.INVISIBLE;
   }

   public float getShadeBrightness(BlockState p_49094_, BlockGetter p_49095_, BlockPos p_49096_) {
      return 1.0F;
   }
}