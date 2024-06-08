package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TintedGlassBlock extends AbstractGlassBlock {
   public TintedGlassBlock(BlockBehaviour.Properties p_154822_) {
      super(p_154822_);
   }

   public boolean propagatesSkylightDown(BlockState p_154824_, BlockGetter p_154825_, BlockPos p_154826_) {
      return false;
   }

   public int getLightBlock(BlockState p_154828_, BlockGetter p_154829_, BlockPos p_154830_) {
      return p_154829_.getMaxLightLevel();
   }
}