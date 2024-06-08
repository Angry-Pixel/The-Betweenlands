package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafBlock extends HorizontalDirectionalBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final EnumProperty<Tilt> TILT = BlockStateProperties.TILT;
   private static final int NO_TICK = -1;
   private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE = Util.make(new Object2IntArrayMap<>(), (p_152305_) -> {
      p_152305_.defaultReturnValue(-1);
      p_152305_.put(Tilt.UNSTABLE, 10);
      p_152305_.put(Tilt.PARTIAL, 10);
      p_152305_.put(Tilt.FULL, 100);
   });
   private static final int MAX_GEN_HEIGHT = 5;
   private static final int STEM_WIDTH = 6;
   private static final int ENTITY_DETECTION_MIN_Y = 11;
   private static final int LOWEST_LEAF_TOP = 13;
   private static final Map<Tilt, VoxelShape> LEAF_SHAPES = ImmutableMap.of(Tilt.NONE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.UNSTABLE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.PARTIAL, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D), Tilt.FULL, Shapes.empty());
   private static final VoxelShape STEM_SLICER = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final Map<Direction, VoxelShape> STEM_SHAPES = ImmutableMap.of(Direction.NORTH, Shapes.joinUnoptimized(BigDripleafStemBlock.NORTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.SOUTH, Shapes.joinUnoptimized(BigDripleafStemBlock.SOUTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.EAST, Shapes.joinUnoptimized(BigDripleafStemBlock.EAST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.WEST, Shapes.joinUnoptimized(BigDripleafStemBlock.WEST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST));
   private final Map<BlockState, VoxelShape> shapesCache;

   public BigDripleafBlock(BlockBehaviour.Properties p_152214_) {
      super(p_152214_);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, Direction.NORTH).setValue(TILT, Tilt.NONE));
      this.shapesCache = this.getShapeForEachState(BigDripleafBlock::calculateShape);
   }

   private static VoxelShape calculateShape(BlockState p_152318_) {
      return Shapes.or(LEAF_SHAPES.get(p_152318_.getValue(TILT)), STEM_SHAPES.get(p_152318_.getValue(FACING)));
   }

   public static void placeWithRandomHeight(LevelAccessor p_152247_, Random p_152248_, BlockPos p_152249_, Direction p_152250_) {
      int i = Mth.nextInt(p_152248_, 2, 5);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_152249_.mutable();
      int j = 0;

      while(j < i && canPlaceAt(p_152247_, blockpos$mutableblockpos, p_152247_.getBlockState(blockpos$mutableblockpos))) {
         ++j;
         blockpos$mutableblockpos.move(Direction.UP);
      }

      int k = p_152249_.getY() + j - 1;
      blockpos$mutableblockpos.setY(p_152249_.getY());

      while(blockpos$mutableblockpos.getY() < k) {
         BigDripleafStemBlock.place(p_152247_, blockpos$mutableblockpos, p_152247_.getFluidState(blockpos$mutableblockpos), p_152250_);
         blockpos$mutableblockpos.move(Direction.UP);
      }

      place(p_152247_, blockpos$mutableblockpos, p_152247_.getFluidState(blockpos$mutableblockpos), p_152250_);
   }

   private static boolean canReplace(BlockState p_152320_) {
      return p_152320_.isAir() || p_152320_.is(Blocks.WATER) || p_152320_.is(Blocks.SMALL_DRIPLEAF);
   }

   protected static boolean canPlaceAt(LevelHeightAccessor p_152252_, BlockPos p_152253_, BlockState p_152254_) {
      return !p_152252_.isOutsideBuildHeight(p_152253_) && canReplace(p_152254_);
   }

   protected static boolean place(LevelAccessor p_152242_, BlockPos p_152243_, FluidState p_152244_, Direction p_152245_) {
      BlockState blockstate = Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(p_152244_.isSourceOfType(Fluids.WATER))).setValue(FACING, p_152245_);
      return p_152242_.setBlock(p_152243_, blockstate, 3);
   }

   public void onProjectileHit(Level p_152228_, BlockState p_152229_, BlockHitResult p_152230_, Projectile p_152231_) {
      this.setTiltAndScheduleTick(p_152229_, p_152228_, p_152230_.getBlockPos(), Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
   }

   public FluidState getFluidState(BlockState p_152312_) {
      return p_152312_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152312_);
   }

   public boolean canSurvive(BlockState p_152289_, LevelReader p_152290_, BlockPos p_152291_) {
      BlockPos blockpos = p_152291_.below();
      BlockState blockstate = p_152290_.getBlockState(blockpos);
      return blockstate.is(this) || blockstate.is(Blocks.BIG_DRIPLEAF_STEM) || blockstate.is(BlockTags.BIG_DRIPLEAF_PLACEABLE);
   }

   public BlockState updateShape(BlockState p_152293_, Direction p_152294_, BlockState p_152295_, LevelAccessor p_152296_, BlockPos p_152297_, BlockPos p_152298_) {
      if (p_152294_ == Direction.DOWN && !p_152293_.canSurvive(p_152296_, p_152297_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_152293_.getValue(WATERLOGGED)) {
            p_152296_.scheduleTick(p_152297_, Fluids.WATER, Fluids.WATER.getTickDelay(p_152296_));
         }

         return p_152294_ == Direction.UP && p_152295_.is(this) ? Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf(p_152293_) : super.updateShape(p_152293_, p_152294_, p_152295_, p_152296_, p_152297_, p_152298_);
      }
   }

   public boolean isValidBonemealTarget(BlockGetter p_152223_, BlockPos p_152224_, BlockState p_152225_, boolean p_152226_) {
      BlockState blockstate = p_152223_.getBlockState(p_152224_.above());
      return canReplace(blockstate);
   }

   public boolean isBonemealSuccess(Level p_152237_, Random p_152238_, BlockPos p_152239_, BlockState p_152240_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_152216_, Random p_152217_, BlockPos p_152218_, BlockState p_152219_) {
      BlockPos blockpos = p_152218_.above();
      BlockState blockstate = p_152216_.getBlockState(blockpos);
      if (canPlaceAt(p_152216_, blockpos, blockstate)) {
         Direction direction = p_152219_.getValue(FACING);
         BigDripleafStemBlock.place(p_152216_, p_152218_, p_152219_.getFluidState(), direction);
         place(p_152216_, blockpos, blockstate.getFluidState(), direction);
      }

   }

   public void entityInside(BlockState p_152266_, Level p_152267_, BlockPos p_152268_, Entity p_152269_) {
      if (!p_152267_.isClientSide) {
         if (p_152266_.getValue(TILT) == Tilt.NONE && canEntityTilt(p_152268_, p_152269_) && !p_152267_.hasNeighborSignal(p_152268_)) {
            this.setTiltAndScheduleTick(p_152266_, p_152267_, p_152268_, Tilt.UNSTABLE, (SoundEvent)null);
         }

      }
   }

   public void tick(BlockState p_152256_, ServerLevel p_152257_, BlockPos p_152258_, Random p_152259_) {
      if (p_152257_.hasNeighborSignal(p_152258_)) {
         resetTilt(p_152256_, p_152257_, p_152258_);
      } else {
         Tilt tilt = p_152256_.getValue(TILT);
         if (tilt == Tilt.UNSTABLE) {
            this.setTiltAndScheduleTick(p_152256_, p_152257_, p_152258_, Tilt.PARTIAL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.PARTIAL) {
            this.setTiltAndScheduleTick(p_152256_, p_152257_, p_152258_, Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.FULL) {
            resetTilt(p_152256_, p_152257_, p_152258_);
         }

      }
   }

   public void neighborChanged(BlockState p_152271_, Level p_152272_, BlockPos p_152273_, Block p_152274_, BlockPos p_152275_, boolean p_152276_) {
      if (p_152272_.hasNeighborSignal(p_152273_)) {
         resetTilt(p_152271_, p_152272_, p_152273_);
      }

   }

   private static void playTiltSound(Level p_152233_, BlockPos p_152234_, SoundEvent p_152235_) {
      float f = Mth.randomBetween(p_152233_.random, 0.8F, 1.2F);
      p_152233_.playSound((Player)null, p_152234_, p_152235_, SoundSource.BLOCKS, 1.0F, f);
   }

   private static boolean canEntityTilt(BlockPos p_152302_, Entity p_152303_) {
      return p_152303_.isOnGround() && p_152303_.position().y > (double)((float)p_152302_.getY() + 0.6875F);
   }

   private void setTiltAndScheduleTick(BlockState p_152283_, Level p_152284_, BlockPos p_152285_, Tilt p_152286_, @Nullable SoundEvent p_152287_) {
      setTilt(p_152283_, p_152284_, p_152285_, p_152286_);
      if (p_152287_ != null) {
         playTiltSound(p_152284_, p_152285_, p_152287_);
      }

      int i = DELAY_UNTIL_NEXT_TILT_STATE.getInt(p_152286_);
      if (i != -1) {
         p_152284_.scheduleTick(p_152285_, this, i);
      }

   }

   private static void resetTilt(BlockState p_152314_, Level p_152315_, BlockPos p_152316_) {
      setTilt(p_152314_, p_152315_, p_152316_, Tilt.NONE);
      if (p_152314_.getValue(TILT) != Tilt.NONE) {
         playTiltSound(p_152315_, p_152316_, SoundEvents.BIG_DRIPLEAF_TILT_UP);
      }

   }

   private static void setTilt(BlockState p_152278_, Level p_152279_, BlockPos p_152280_, Tilt p_152281_) {
      p_152279_.setBlock(p_152280_, p_152278_.setValue(TILT, p_152281_), 2);
      if (p_152281_.causesVibration()) {
         p_152279_.gameEvent(GameEvent.BLOCK_CHANGE, p_152280_);
      }

   }

   public VoxelShape getCollisionShape(BlockState p_152307_, BlockGetter p_152308_, BlockPos p_152309_, CollisionContext p_152310_) {
      return LEAF_SHAPES.get(p_152307_.getValue(TILT));
   }

   public VoxelShape getShape(BlockState p_152261_, BlockGetter p_152262_, BlockPos p_152263_, CollisionContext p_152264_) {
      return this.shapesCache.get(p_152261_);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_152221_) {
      BlockState blockstate = p_152221_.getLevel().getBlockState(p_152221_.getClickedPos().below());
      FluidState fluidstate = p_152221_.getLevel().getFluidState(p_152221_.getClickedPos());
      boolean flag = blockstate.is(Blocks.BIG_DRIPLEAF) || blockstate.is(Blocks.BIG_DRIPLEAF_STEM);
      return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.isSourceOfType(Fluids.WATER))).setValue(FACING, flag ? blockstate.getValue(FACING) : p_152221_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152300_) {
      p_152300_.add(WATERLOGGED, FACING, TILT);
   }
}