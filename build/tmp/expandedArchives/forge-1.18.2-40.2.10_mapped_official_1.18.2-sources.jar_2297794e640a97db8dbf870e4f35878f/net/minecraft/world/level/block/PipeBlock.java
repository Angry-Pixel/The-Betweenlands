package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock extends Block {
   private static final Direction[] DIRECTIONS = Direction.values();
   public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
   public static final BooleanProperty EAST = BlockStateProperties.EAST;
   public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
   public static final BooleanProperty WEST = BlockStateProperties.WEST;
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (p_55164_) -> {
      p_55164_.put(Direction.NORTH, NORTH);
      p_55164_.put(Direction.EAST, EAST);
      p_55164_.put(Direction.SOUTH, SOUTH);
      p_55164_.put(Direction.WEST, WEST);
      p_55164_.put(Direction.UP, UP);
      p_55164_.put(Direction.DOWN, DOWN);
   }));
   protected final VoxelShape[] shapeByIndex;

   public PipeBlock(float p_55159_, BlockBehaviour.Properties p_55160_) {
      super(p_55160_);
      this.shapeByIndex = this.makeShapes(p_55159_);
   }

   private VoxelShape[] makeShapes(float p_55162_) {
      float f = 0.5F - p_55162_;
      float f1 = 0.5F + p_55162_;
      VoxelShape voxelshape = Block.box((double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F));
      VoxelShape[] avoxelshape = new VoxelShape[DIRECTIONS.length];

      for(int i = 0; i < DIRECTIONS.length; ++i) {
         Direction direction = DIRECTIONS[i];
         avoxelshape[i] = Shapes.box(0.5D + Math.min((double)(-p_55162_), (double)direction.getStepX() * 0.5D), 0.5D + Math.min((double)(-p_55162_), (double)direction.getStepY() * 0.5D), 0.5D + Math.min((double)(-p_55162_), (double)direction.getStepZ() * 0.5D), 0.5D + Math.max((double)p_55162_, (double)direction.getStepX() * 0.5D), 0.5D + Math.max((double)p_55162_, (double)direction.getStepY() * 0.5D), 0.5D + Math.max((double)p_55162_, (double)direction.getStepZ() * 0.5D));
      }

      VoxelShape[] avoxelshape1 = new VoxelShape[64];

      for(int k = 0; k < 64; ++k) {
         VoxelShape voxelshape1 = voxelshape;

         for(int j = 0; j < DIRECTIONS.length; ++j) {
            if ((k & 1 << j) != 0) {
               voxelshape1 = Shapes.or(voxelshape1, avoxelshape[j]);
            }
         }

         avoxelshape1[k] = voxelshape1;
      }

      return avoxelshape1;
   }

   public boolean propagatesSkylightDown(BlockState p_55166_, BlockGetter p_55167_, BlockPos p_55168_) {
      return false;
   }

   public VoxelShape getShape(BlockState p_55170_, BlockGetter p_55171_, BlockPos p_55172_, CollisionContext p_55173_) {
      return this.shapeByIndex[this.getAABBIndex(p_55170_)];
   }

   protected int getAABBIndex(BlockState p_55175_) {
      int i = 0;

      for(int j = 0; j < DIRECTIONS.length; ++j) {
         if (p_55175_.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
            i |= 1 << j;
         }
      }

      return i;
   }
}