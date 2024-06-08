package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeagrassBlock extends BushBlock implements BonemealableBlock, LiquidBlockContainer, net.minecraftforge.common.IForgeShearable {
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

   public SeagrassBlock(BlockBehaviour.Properties p_154496_) {
      super(p_154496_);
   }

   public VoxelShape getShape(BlockState p_154525_, BlockGetter p_154526_, BlockPos p_154527_, CollisionContext p_154528_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_154539_, BlockGetter p_154540_, BlockPos p_154541_) {
      return p_154539_.isFaceSturdy(p_154540_, p_154541_, Direction.UP) && !p_154539_.is(Blocks.MAGMA_BLOCK);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_154503_) {
      FluidState fluidstate = p_154503_.getLevel().getFluidState(p_154503_.getClickedPos());
      return fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(p_154503_) : null;
   }

   public BlockState updateShape(BlockState p_154530_, Direction p_154531_, BlockState p_154532_, LevelAccessor p_154533_, BlockPos p_154534_, BlockPos p_154535_) {
      BlockState blockstate = super.updateShape(p_154530_, p_154531_, p_154532_, p_154533_, p_154534_, p_154535_);
      if (!blockstate.isAir()) {
         p_154533_.scheduleTick(p_154534_, Fluids.WATER, Fluids.WATER.getTickDelay(p_154533_));
      }

      return blockstate;
   }

   public boolean isValidBonemealTarget(BlockGetter p_154510_, BlockPos p_154511_, BlockState p_154512_, boolean p_154513_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_154515_, Random p_154516_, BlockPos p_154517_, BlockState p_154518_) {
      return true;
   }

   public FluidState getFluidState(BlockState p_154537_) {
      return Fluids.WATER.getSource(false);
   }

   public void performBonemeal(ServerLevel p_154498_, Random p_154499_, BlockPos p_154500_, BlockState p_154501_) {
      BlockState blockstate = Blocks.TALL_SEAGRASS.defaultBlockState();
      BlockState blockstate1 = blockstate.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
      BlockPos blockpos = p_154500_.above();
      if (p_154498_.getBlockState(blockpos).is(Blocks.WATER)) {
         p_154498_.setBlock(p_154500_, blockstate, 2);
         p_154498_.setBlock(blockpos, blockstate1, 2);
      }

   }

   public boolean canPlaceLiquid(BlockGetter p_154505_, BlockPos p_154506_, BlockState p_154507_, Fluid p_154508_) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor p_154520_, BlockPos p_154521_, BlockState p_154522_, FluidState p_154523_) {
      return false;
   }
}
