package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BannerBlock extends AbstractBannerBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final Map<DyeColor, Block> BY_COLOR = Maps.newHashMap();
   private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public BannerBlock(DyeColor p_49012_, BlockBehaviour.Properties p_49013_) {
      super(p_49012_, p_49013_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
      BY_COLOR.put(p_49012_, this);
   }

   public boolean canSurvive(BlockState p_49019_, LevelReader p_49020_, BlockPos p_49021_) {
      return p_49020_.getBlockState(p_49021_.below()).getMaterial().isSolid();
   }

   public VoxelShape getShape(BlockState p_49038_, BlockGetter p_49039_, BlockPos p_49040_, CollisionContext p_49041_) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_49017_) {
      return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)((180.0F + p_49017_.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public BlockState updateShape(BlockState p_49029_, Direction p_49030_, BlockState p_49031_, LevelAccessor p_49032_, BlockPos p_49033_, BlockPos p_49034_) {
      return p_49030_ == Direction.DOWN && !p_49029_.canSurvive(p_49032_, p_49033_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_49029_, p_49030_, p_49031_, p_49032_, p_49033_, p_49034_);
   }

   public BlockState rotate(BlockState p_49026_, Rotation p_49027_) {
      return p_49026_.setValue(ROTATION, Integer.valueOf(p_49027_.rotate(p_49026_.getValue(ROTATION), 16)));
   }

   public BlockState mirror(BlockState p_49023_, Mirror p_49024_) {
      return p_49023_.setValue(ROTATION, Integer.valueOf(p_49024_.mirror(p_49023_.getValue(ROTATION), 16)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49036_) {
      p_49036_.add(ROTATION);
   }

   public static Block byColor(DyeColor p_49015_) {
      return BY_COLOR.getOrDefault(p_49015_, Blocks.WHITE_BANNER);
   }
}