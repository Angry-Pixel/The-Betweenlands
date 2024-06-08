package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBodyBlock extends GrowingPlantBlock implements BonemealableBlock {
   protected GrowingPlantBodyBlock(BlockBehaviour.Properties p_53886_, Direction p_53887_, VoxelShape p_53888_, boolean p_53889_) {
      super(p_53886_, p_53887_, p_53888_, p_53889_);
   }

   protected BlockState updateHeadAfterConvertedFromBody(BlockState p_153326_, BlockState p_153327_) {
      return p_153327_;
   }

   public BlockState updateShape(BlockState p_53913_, Direction p_53914_, BlockState p_53915_, LevelAccessor p_53916_, BlockPos p_53917_, BlockPos p_53918_) {
      if (p_53914_ == this.growthDirection.getOpposite() && !p_53913_.canSurvive(p_53916_, p_53917_)) {
         p_53916_.scheduleTick(p_53917_, this, 1);
      }

      GrowingPlantHeadBlock growingplantheadblock = this.getHeadBlock();
      if (p_53914_ == this.growthDirection && !p_53915_.is(this) && !p_53915_.is(growingplantheadblock)) {
         return this.updateHeadAfterConvertedFromBody(p_53913_, growingplantheadblock.getStateForPlacement(p_53916_));
      } else {
         if (this.scheduleFluidTicks) {
            p_53916_.scheduleTick(p_53917_, Fluids.WATER, Fluids.WATER.getTickDelay(p_53916_));
         }

         return super.updateShape(p_53913_, p_53914_, p_53915_, p_53916_, p_53917_, p_53918_);
      }
   }

   public ItemStack getCloneItemStack(BlockGetter p_53896_, BlockPos p_53897_, BlockState p_53898_) {
      return new ItemStack(this.getHeadBlock());
   }

   public boolean isValidBonemealTarget(BlockGetter p_53900_, BlockPos p_53901_, BlockState p_53902_, boolean p_53903_) {
      Optional<BlockPos> optional = this.getHeadPos(p_53900_, p_53901_, p_53902_.getBlock());
      return optional.isPresent() && this.getHeadBlock().canGrowInto(p_53900_.getBlockState(optional.get().relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(Level p_53905_, Random p_53906_, BlockPos p_53907_, BlockState p_53908_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_53891_, Random p_53892_, BlockPos p_53893_, BlockState p_53894_) {
      Optional<BlockPos> optional = this.getHeadPos(p_53891_, p_53893_, p_53894_.getBlock());
      if (optional.isPresent()) {
         BlockState blockstate = p_53891_.getBlockState(optional.get());
         ((GrowingPlantHeadBlock)blockstate.getBlock()).performBonemeal(p_53891_, p_53892_, optional.get(), blockstate);
      }

   }

   private Optional<BlockPos> getHeadPos(BlockGetter p_153323_, BlockPos p_153324_, Block p_153325_) {
      return BlockUtil.getTopConnectedBlock(p_153323_, p_153324_, p_153325_, this.growthDirection, this.getHeadBlock());
   }

   public boolean canBeReplaced(BlockState p_53910_, BlockPlaceContext p_53911_) {
      boolean flag = super.canBeReplaced(p_53910_, p_53911_);
      return flag && p_53911_.getItemInHand().is(this.getHeadBlock().asItem()) ? false : flag;
   }

   protected Block getBodyBlock() {
      return this;
   }
}