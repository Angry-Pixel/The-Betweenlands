package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBlock extends Block {
   protected final Direction growthDirection;
   protected final boolean scheduleFluidTicks;
   protected final VoxelShape shape;

   protected GrowingPlantBlock(BlockBehaviour.Properties p_53863_, Direction p_53864_, VoxelShape p_53865_, boolean p_53866_) {
      super(p_53863_);
      this.growthDirection = p_53864_;
      this.shape = p_53865_;
      this.scheduleFluidTicks = p_53866_;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_53868_) {
      BlockState blockstate = p_53868_.getLevel().getBlockState(p_53868_.getClickedPos().relative(this.growthDirection));
      return !blockstate.is(this.getHeadBlock()) && !blockstate.is(this.getBodyBlock()) ? this.getStateForPlacement(p_53868_.getLevel()) : this.getBodyBlock().defaultBlockState();
   }

   public BlockState getStateForPlacement(LevelAccessor p_53869_) {
      return this.defaultBlockState();
   }

   public boolean canSurvive(BlockState p_53876_, LevelReader p_53877_, BlockPos p_53878_) {
      BlockPos blockpos = p_53878_.relative(this.growthDirection.getOpposite());
      BlockState blockstate = p_53877_.getBlockState(blockpos);
      if (!this.canAttachTo(blockstate)) {
         return false;
      } else {
         return blockstate.is(this.getHeadBlock()) || blockstate.is(this.getBodyBlock()) || blockstate.isFaceSturdy(p_53877_, blockpos, this.growthDirection);
      }
   }

   public void tick(BlockState p_53871_, ServerLevel p_53872_, BlockPos p_53873_, Random p_53874_) {
      if (!p_53871_.canSurvive(p_53872_, p_53873_)) {
         p_53872_.destroyBlock(p_53873_, true);
      }

   }

   protected boolean canAttachTo(BlockState p_153321_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_53880_, BlockGetter p_53881_, BlockPos p_53882_, CollisionContext p_53883_) {
      return this.shape;
   }

   protected abstract GrowingPlantHeadBlock getHeadBlock();

   protected abstract Block getBodyBlock();
}