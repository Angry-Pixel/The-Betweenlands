package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KelpBlock extends GrowingPlantHeadBlock implements LiquidBlockContainer {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   private static final double GROW_PER_TICK_PROBABILITY = 0.14D;

   public KelpBlock(BlockBehaviour.Properties p_54300_) {
      super(p_54300_, Direction.UP, SHAPE, true, 0.14D);
   }

   protected boolean canGrowInto(BlockState p_54321_) {
      return p_54321_.is(Blocks.WATER);
   }

   protected Block getBodyBlock() {
      return Blocks.KELP_PLANT;
   }

   protected boolean canAttachTo(BlockState p_153455_) {
      return !p_153455_.is(Blocks.MAGMA_BLOCK);
   }

   public boolean canPlaceLiquid(BlockGetter p_54304_, BlockPos p_54305_, BlockState p_54306_, Fluid p_54307_) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor p_54309_, BlockPos p_54310_, BlockState p_54311_, FluidState p_54312_) {
      return false;
   }

   protected int getBlocksToGrowWhenBonemealed(Random p_54314_) {
      return 1;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_54302_) {
      FluidState fluidstate = p_54302_.getLevel().getFluidState(p_54302_.getClickedPos());
      return fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(p_54302_) : null;
   }

   public FluidState getFluidState(BlockState p_54319_) {
      return Fluids.WATER.getSource(false);
   }
}