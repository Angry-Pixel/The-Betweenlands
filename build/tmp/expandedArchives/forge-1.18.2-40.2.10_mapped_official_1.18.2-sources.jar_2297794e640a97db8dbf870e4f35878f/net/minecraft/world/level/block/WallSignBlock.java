package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSignBlock extends SignBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final float AABB_THICKNESS = 2.0F;
   protected static final float AABB_BOTTOM = 4.5F;
   protected static final float AABB_TOP = 12.5F;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

   public WallSignBlock(BlockBehaviour.Properties p_58068_, WoodType p_58069_) {
      super(p_58068_, p_58069_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_58092_, BlockGetter p_58093_, BlockPos p_58094_, CollisionContext p_58095_) {
      return AABBS.get(p_58092_.getValue(FACING));
   }

   public boolean canSurvive(BlockState p_58073_, LevelReader p_58074_, BlockPos p_58075_) {
      return p_58074_.getBlockState(p_58075_.relative(p_58073_.getValue(FACING).getOpposite())).getMaterial().isSolid();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_58071_) {
      BlockState blockstate = this.defaultBlockState();
      FluidState fluidstate = p_58071_.getLevel().getFluidState(p_58071_.getClickedPos());
      LevelReader levelreader = p_58071_.getLevel();
      BlockPos blockpos = p_58071_.getClickedPos();
      Direction[] adirection = p_58071_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos)) {
               return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_58083_, Direction p_58084_, BlockState p_58085_, LevelAccessor p_58086_, BlockPos p_58087_, BlockPos p_58088_) {
      return p_58084_.getOpposite() == p_58083_.getValue(FACING) && !p_58083_.canSurvive(p_58086_, p_58087_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_58083_, p_58084_, p_58085_, p_58086_, p_58087_, p_58088_);
   }

   public BlockState rotate(BlockState p_58080_, Rotation p_58081_) {
      return p_58080_.setValue(FACING, p_58081_.rotate(p_58080_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_58077_, Mirror p_58078_) {
      return p_58077_.rotate(p_58078_.getRotation(p_58077_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58090_) {
      p_58090_.add(FACING, WATERLOGGED);
   }
}