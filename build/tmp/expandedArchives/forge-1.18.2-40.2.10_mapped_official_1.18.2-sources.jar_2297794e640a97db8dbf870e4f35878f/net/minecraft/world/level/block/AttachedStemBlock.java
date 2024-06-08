package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AttachedStemBlock extends BushBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final float AABB_OFFSET = 2.0F;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
   private final StemGrownBlock fruit;
   private final Supplier<Item> seedSupplier;

   public AttachedStemBlock(StemGrownBlock p_152060_, Supplier<Item> p_152061_, BlockBehaviour.Properties p_152062_) {
      super(p_152062_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
      this.fruit = p_152060_;
      this.seedSupplier = p_152061_;
   }

   public VoxelShape getShape(BlockState p_48858_, BlockGetter p_48859_, BlockPos p_48860_, CollisionContext p_48861_) {
      return AABBS.get(p_48858_.getValue(FACING));
   }

   public BlockState updateShape(BlockState p_48848_, Direction p_48849_, BlockState p_48850_, LevelAccessor p_48851_, BlockPos p_48852_, BlockPos p_48853_) {
      return !p_48850_.is(this.fruit) && p_48849_ == p_48848_.getValue(FACING) ? this.fruit.getStem().defaultBlockState().setValue(StemBlock.AGE, Integer.valueOf(7)) : super.updateShape(p_48848_, p_48849_, p_48850_, p_48851_, p_48852_, p_48853_);
   }

   protected boolean mayPlaceOn(BlockState p_48863_, BlockGetter p_48864_, BlockPos p_48865_) {
      return p_48863_.is(Blocks.FARMLAND);
   }

   public ItemStack getCloneItemStack(BlockGetter p_48838_, BlockPos p_48839_, BlockState p_48840_) {
      return new ItemStack(this.seedSupplier.get());
   }

   public BlockState rotate(BlockState p_48845_, Rotation p_48846_) {
      return p_48845_.setValue(FACING, p_48846_.rotate(p_48845_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_48842_, Mirror p_48843_) {
      return p_48842_.rotate(p_48843_.getRotation(p_48842_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48855_) {
      p_48855_.add(FACING);
   }
}