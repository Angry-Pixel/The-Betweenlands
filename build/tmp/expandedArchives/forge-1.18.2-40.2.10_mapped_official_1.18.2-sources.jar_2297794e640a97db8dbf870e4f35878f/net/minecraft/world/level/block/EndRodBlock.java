package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class EndRodBlock extends RodBlock {
   public EndRodBlock(BlockBehaviour.Properties p_53085_) {
      super(p_53085_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53087_) {
      Direction direction = p_53087_.getClickedFace();
      BlockState blockstate = p_53087_.getLevel().getBlockState(p_53087_.getClickedPos().relative(direction.getOpposite()));
      return blockstate.is(this) && blockstate.getValue(FACING) == direction ? this.defaultBlockState().setValue(FACING, direction.getOpposite()) : this.defaultBlockState().setValue(FACING, direction);
   }

   public void animateTick(BlockState p_53094_, Level p_53095_, BlockPos p_53096_, Random p_53097_) {
      Direction direction = p_53094_.getValue(FACING);
      double d0 = (double)p_53096_.getX() + 0.55D - (double)(p_53097_.nextFloat() * 0.1F);
      double d1 = (double)p_53096_.getY() + 0.55D - (double)(p_53097_.nextFloat() * 0.1F);
      double d2 = (double)p_53096_.getZ() + 0.55D - (double)(p_53097_.nextFloat() * 0.1F);
      double d3 = (double)(0.4F - (p_53097_.nextFloat() + p_53097_.nextFloat()) * 0.4F);
      if (p_53097_.nextInt(5) == 0) {
         p_53095_.addParticle(ParticleTypes.END_ROD, d0 + (double)direction.getStepX() * d3, d1 + (double)direction.getStepY() * d3, d2 + (double)direction.getStepZ() * d3, p_53097_.nextGaussian() * 0.005D, p_53097_.nextGaussian() * 0.005D, p_53097_.nextGaussian() * 0.005D);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53105_) {
      p_53105_.add(FACING);
   }

   public PushReaction getPistonPushReaction(BlockState p_53112_) {
      return PushReaction.NORMAL;
   }
}