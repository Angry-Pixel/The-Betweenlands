package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class DirectionalBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   protected DirectionalBlock(BlockBehaviour.Properties p_52591_) {
      super(p_52591_);
   }
}