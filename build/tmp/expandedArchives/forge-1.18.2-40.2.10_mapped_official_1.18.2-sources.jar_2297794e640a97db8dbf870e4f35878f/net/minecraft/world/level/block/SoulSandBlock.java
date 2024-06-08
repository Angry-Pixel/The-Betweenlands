package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulSandBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public SoulSandBlock(BlockBehaviour.Properties p_56672_) {
      super(p_56672_);
   }

   public VoxelShape getCollisionShape(BlockState p_56702_, BlockGetter p_56703_, BlockPos p_56704_, CollisionContext p_56705_) {
      return SHAPE;
   }

   public VoxelShape getBlockSupportShape(BlockState p_56707_, BlockGetter p_56708_, BlockPos p_56709_) {
      return Shapes.block();
   }

   public VoxelShape getVisualShape(BlockState p_56684_, BlockGetter p_56685_, BlockPos p_56686_, CollisionContext p_56687_) {
      return Shapes.block();
   }

   public void tick(BlockState p_56674_, ServerLevel p_56675_, BlockPos p_56676_, Random p_56677_) {
      BubbleColumnBlock.updateColumn(p_56675_, p_56676_.above(), p_56674_);
   }

   public BlockState updateShape(BlockState p_56689_, Direction p_56690_, BlockState p_56691_, LevelAccessor p_56692_, BlockPos p_56693_, BlockPos p_56694_) {
      if (p_56690_ == Direction.UP && p_56691_.is(Blocks.WATER)) {
         p_56692_.scheduleTick(p_56693_, this, 20);
      }

      return super.updateShape(p_56689_, p_56690_, p_56691_, p_56692_, p_56693_, p_56694_);
   }

   public void onPlace(BlockState p_56696_, Level p_56697_, BlockPos p_56698_, BlockState p_56699_, boolean p_56700_) {
      p_56697_.scheduleTick(p_56698_, this, 20);
   }

   public boolean isPathfindable(BlockState p_56679_, BlockGetter p_56680_, BlockPos p_56681_, PathComputationType p_56682_) {
      return false;
   }
}