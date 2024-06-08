package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
   public static final int MIN_CANDLES = 1;
   public static final int MAX_CANDLES = 4;
   public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
   public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION = (p_152848_) -> {
      return p_152848_.getValue(LIT) ? 3 * p_152848_.getValue(CANDLES) : 0;
   };
   private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = Util.make(() -> {
      Int2ObjectMap<List<Vec3>> int2objectmap = new Int2ObjectOpenHashMap<>();
      int2objectmap.defaultReturnValue(ImmutableList.of());
      int2objectmap.put(1, ImmutableList.of(new Vec3(0.5D, 0.5D, 0.5D)));
      int2objectmap.put(2, ImmutableList.of(new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.625D, 0.5D, 0.44D)));
      int2objectmap.put(3, ImmutableList.of(new Vec3(0.5D, 0.313D, 0.625D), new Vec3(0.375D, 0.44D, 0.5D), new Vec3(0.56D, 0.5D, 0.44D)));
      int2objectmap.put(4, ImmutableList.of(new Vec3(0.44D, 0.313D, 0.56D), new Vec3(0.625D, 0.44D, 0.56D), new Vec3(0.375D, 0.44D, 0.375D), new Vec3(0.56D, 0.5D, 0.375D)));
      return Int2ObjectMaps.unmodifiable(int2objectmap);
   });
   private static final VoxelShape ONE_AABB = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D);
   private static final VoxelShape TWO_AABB = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 6.0D, 9.0D);
   private static final VoxelShape THREE_AABB = Block.box(5.0D, 0.0D, 6.0D, 10.0D, 6.0D, 11.0D);
   private static final VoxelShape FOUR_AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 10.0D);

   public CandleBlock(BlockBehaviour.Properties p_152801_) {
      super(p_152801_);
      this.registerDefaultState(this.stateDefinition.any().setValue(CANDLES, Integer.valueOf(1)).setValue(LIT, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public InteractionResult use(BlockState p_152822_, Level p_152823_, BlockPos p_152824_, Player p_152825_, InteractionHand p_152826_, BlockHitResult p_152827_) {
      if (p_152825_.getAbilities().mayBuild && p_152825_.getItemInHand(p_152826_).isEmpty() && p_152822_.getValue(LIT)) {
         extinguish(p_152825_, p_152822_, p_152823_, p_152824_);
         return InteractionResult.sidedSuccess(p_152823_.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public boolean canBeReplaced(BlockState p_152814_, BlockPlaceContext p_152815_) {
      return !p_152815_.isSecondaryUseActive() && p_152815_.getItemInHand().getItem() == this.asItem() && p_152814_.getValue(CANDLES) < 4 ? true : super.canBeReplaced(p_152814_, p_152815_);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_152803_) {
      BlockState blockstate = p_152803_.getLevel().getBlockState(p_152803_.getClickedPos());
      if (blockstate.is(this)) {
         return blockstate.cycle(CANDLES);
      } else {
         FluidState fluidstate = p_152803_.getLevel().getFluidState(p_152803_.getClickedPos());
         boolean flag = fluidstate.getType() == Fluids.WATER;
         return super.getStateForPlacement(p_152803_).setValue(WATERLOGGED, Boolean.valueOf(flag));
      }
   }

   public BlockState updateShape(BlockState p_152833_, Direction p_152834_, BlockState p_152835_, LevelAccessor p_152836_, BlockPos p_152837_, BlockPos p_152838_) {
      if (p_152833_.getValue(WATERLOGGED)) {
         p_152836_.scheduleTick(p_152837_, Fluids.WATER, Fluids.WATER.getTickDelay(p_152836_));
      }

      return super.updateShape(p_152833_, p_152834_, p_152835_, p_152836_, p_152837_, p_152838_);
   }

   public FluidState getFluidState(BlockState p_152844_) {
      return p_152844_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152844_);
   }

   public VoxelShape getShape(BlockState p_152817_, BlockGetter p_152818_, BlockPos p_152819_, CollisionContext p_152820_) {
      switch(p_152817_.getValue(CANDLES)) {
      case 1:
      default:
         return ONE_AABB;
      case 2:
         return TWO_AABB;
      case 3:
         return THREE_AABB;
      case 4:
         return FOUR_AABB;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152840_) {
      p_152840_.add(CANDLES, LIT, WATERLOGGED);
   }

   public boolean placeLiquid(LevelAccessor p_152805_, BlockPos p_152806_, BlockState p_152807_, FluidState p_152808_) {
      if (!p_152807_.getValue(WATERLOGGED) && p_152808_.getType() == Fluids.WATER) {
         BlockState blockstate = p_152807_.setValue(WATERLOGGED, Boolean.valueOf(true));
         if (p_152807_.getValue(LIT)) {
            extinguish((Player)null, blockstate, p_152805_, p_152806_);
         } else {
            p_152805_.setBlock(p_152806_, blockstate, 3);
         }

         p_152805_.scheduleTick(p_152806_, p_152808_.getType(), p_152808_.getType().getTickDelay(p_152805_));
         return true;
      } else {
         return false;
      }
   }

   public static boolean canLight(BlockState p_152846_) {
      return p_152846_.is(BlockTags.CANDLES, (p_152810_) -> {
         return p_152810_.hasProperty(LIT) && p_152810_.hasProperty(WATERLOGGED);
      }) && !p_152846_.getValue(LIT) && !p_152846_.getValue(WATERLOGGED);
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState p_152812_) {
      return PARTICLE_OFFSETS.get(p_152812_.getValue(CANDLES).intValue());
   }

   protected boolean canBeLit(BlockState p_152842_) {
      return !p_152842_.getValue(WATERLOGGED) && super.canBeLit(p_152842_);
   }

   public boolean canSurvive(BlockState p_152829_, LevelReader p_152830_, BlockPos p_152831_) {
      return Block.canSupportCenter(p_152830_, p_152831_.below(), Direction.UP);
   }
}