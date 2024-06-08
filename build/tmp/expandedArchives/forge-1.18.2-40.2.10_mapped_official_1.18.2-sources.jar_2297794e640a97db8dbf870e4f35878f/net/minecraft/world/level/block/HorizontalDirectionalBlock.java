package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class HorizontalDirectionalBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected HorizontalDirectionalBlock(BlockBehaviour.Properties p_54120_) {
      super(p_54120_);
   }

   public BlockState rotate(BlockState p_54125_, Rotation p_54126_) {
      return p_54125_.setValue(FACING, p_54126_.rotate(p_54125_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
      return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
   }
}