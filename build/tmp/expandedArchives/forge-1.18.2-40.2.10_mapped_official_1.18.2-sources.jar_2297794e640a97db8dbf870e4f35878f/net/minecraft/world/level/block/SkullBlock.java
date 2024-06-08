package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
   public static final int MAX = 15;
   private static final int ROTATIONS = 16;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

   public SkullBlock(SkullBlock.Type p_56318_, BlockBehaviour.Properties p_56319_) {
      super(p_56318_, p_56319_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
      return SHAPE;
   }

   public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
      return Shapes.empty();
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_56321_) {
      return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)(p_56321_.getRotation() * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public BlockState rotate(BlockState p_56326_, Rotation p_56327_) {
      return p_56326_.setValue(ROTATION, Integer.valueOf(p_56327_.rotate(p_56326_.getValue(ROTATION), 16)));
   }

   public BlockState mirror(BlockState p_56323_, Mirror p_56324_) {
      return p_56323_.setValue(ROTATION, Integer.valueOf(p_56324_.mirror(p_56323_.getValue(ROTATION), 16)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56329_) {
      p_56329_.add(ROTATION);
   }

   public interface Type {
   }

   public static enum Types implements SkullBlock.Type {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }
}