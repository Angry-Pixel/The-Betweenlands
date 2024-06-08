package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class GlowLichenBlock extends MultifaceBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public GlowLichenBlock(BlockBehaviour.Properties p_153282_) {
      super(p_153282_);
      this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public static ToIntFunction<BlockState> emission(int p_181223_) {
      return (p_181221_) -> {
         return MultifaceBlock.hasAnyFace(p_181221_) ? p_181223_ : 0;
      };
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
      super.createBlockStateDefinition(p_153309_);
      p_153309_.add(WATERLOGGED);
   }

   public BlockState updateShape(BlockState p_153302_, Direction p_153303_, BlockState p_153304_, LevelAccessor p_153305_, BlockPos p_153306_, BlockPos p_153307_) {
      if (p_153302_.getValue(WATERLOGGED)) {
         p_153305_.scheduleTick(p_153306_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153305_));
      }

      return super.updateShape(p_153302_, p_153303_, p_153304_, p_153305_, p_153306_, p_153307_);
   }

   public boolean canBeReplaced(BlockState p_153299_, BlockPlaceContext p_153300_) {
      return !p_153300_.getItemInHand().is(Items.GLOW_LICHEN) || super.canBeReplaced(p_153299_, p_153300_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_153289_, BlockPos p_153290_, BlockState p_153291_, boolean p_153292_) {
      return Stream.of(DIRECTIONS).anyMatch((p_153316_) -> {
         return this.canSpread(p_153291_, p_153289_, p_153290_, p_153316_.getOpposite());
      });
   }

   public boolean isBonemealSuccess(Level p_153294_, Random p_153295_, BlockPos p_153296_, BlockState p_153297_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_153284_, Random p_153285_, BlockPos p_153286_, BlockState p_153287_) {
      this.spreadFromRandomFaceTowardRandomDirection(p_153287_, p_153284_, p_153286_, p_153285_);
   }

   public FluidState getFluidState(BlockState p_153311_) {
      return p_153311_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153311_);
   }

   public boolean propagatesSkylightDown(BlockState p_181225_, BlockGetter p_181226_, BlockPos p_181227_) {
      return p_181225_.getFluidState().isEmpty();
   }
}