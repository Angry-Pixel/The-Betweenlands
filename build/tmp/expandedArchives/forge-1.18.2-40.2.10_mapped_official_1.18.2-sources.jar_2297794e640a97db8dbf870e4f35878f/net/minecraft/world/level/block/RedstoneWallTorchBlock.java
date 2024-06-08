package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneWallTorchBlock(BlockBehaviour.Properties p_55744_) {
      super(p_55744_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.valueOf(true)));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_55781_, BlockGetter p_55782_, BlockPos p_55783_, CollisionContext p_55784_) {
      return WallTorchBlock.getShape(p_55781_);
   }

   public boolean canSurvive(BlockState p_55762_, LevelReader p_55763_, BlockPos p_55764_) {
      return Blocks.WALL_TORCH.canSurvive(p_55762_, p_55763_, p_55764_);
   }

   public BlockState updateShape(BlockState p_55772_, Direction p_55773_, BlockState p_55774_, LevelAccessor p_55775_, BlockPos p_55776_, BlockPos p_55777_) {
      return Blocks.WALL_TORCH.updateShape(p_55772_, p_55773_, p_55774_, p_55775_, p_55776_, p_55777_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_55746_) {
      BlockState blockstate = Blocks.WALL_TORCH.getStateForPlacement(p_55746_);
      return blockstate == null ? null : this.defaultBlockState().setValue(FACING, blockstate.getValue(FACING));
   }

   public void animateTick(BlockState p_55757_, Level p_55758_, BlockPos p_55759_, Random p_55760_) {
      if (p_55757_.getValue(LIT)) {
         Direction direction = p_55757_.getValue(FACING).getOpposite();
         double d0 = 0.27D;
         double d1 = (double)p_55759_.getX() + 0.5D + (p_55760_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getStepX();
         double d2 = (double)p_55759_.getY() + 0.7D + (p_55760_.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double d3 = (double)p_55759_.getZ() + 0.5D + (p_55760_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getStepZ();
         p_55758_.addParticle(this.flameParticle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean hasNeighborSignal(Level p_55748_, BlockPos p_55749_, BlockState p_55750_) {
      Direction direction = p_55750_.getValue(FACING).getOpposite();
      return p_55748_.hasSignal(p_55749_.relative(direction), direction);
   }

   public int getSignal(BlockState p_55752_, BlockGetter p_55753_, BlockPos p_55754_, Direction p_55755_) {
      return p_55752_.getValue(LIT) && p_55752_.getValue(FACING) != p_55755_ ? 15 : 0;
   }

   public BlockState rotate(BlockState p_55769_, Rotation p_55770_) {
      return Blocks.WALL_TORCH.rotate(p_55769_, p_55770_);
   }

   public BlockState mirror(BlockState p_55766_, Mirror p_55767_) {
      return Blocks.WALL_TORCH.mirror(p_55766_, p_55767_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55779_) {
      p_55779_.add(FACING, LIT);
   }
}