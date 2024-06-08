package net.minecraft.world.level.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
   public GlazedTerracottaBlock(BlockBehaviour.Properties p_53677_) {
      super(p_53677_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53681_) {
      p_53681_.add(FACING);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53679_) {
      return this.defaultBlockState().setValue(FACING, p_53679_.getHorizontalDirection().getOpposite());
   }

   public PushReaction getPistonPushReaction(BlockState p_53683_) {
      return PushReaction.PUSH_ONLY;
   }
}