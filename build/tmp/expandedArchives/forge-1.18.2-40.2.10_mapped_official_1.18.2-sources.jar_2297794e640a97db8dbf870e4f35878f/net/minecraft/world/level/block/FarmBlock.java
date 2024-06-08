package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock extends Block {
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   public static final int MAX_MOISTURE = 7;

   public FarmBlock(BlockBehaviour.Properties p_53247_) {
      super(p_53247_);
      this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, Integer.valueOf(0)));
   }

   public BlockState updateShape(BlockState p_53276_, Direction p_53277_, BlockState p_53278_, LevelAccessor p_53279_, BlockPos p_53280_, BlockPos p_53281_) {
      if (p_53277_ == Direction.UP && !p_53276_.canSurvive(p_53279_, p_53280_)) {
         p_53279_.scheduleTick(p_53280_, this, 1);
      }

      return super.updateShape(p_53276_, p_53277_, p_53278_, p_53279_, p_53280_, p_53281_);
   }

   public boolean canSurvive(BlockState p_53272_, LevelReader p_53273_, BlockPos p_53274_) {
      BlockState blockstate = p_53273_.getBlockState(p_53274_.above());
      return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53249_) {
      return !this.defaultBlockState().canSurvive(p_53249_.getLevel(), p_53249_.getClickedPos()) ? Blocks.DIRT.defaultBlockState() : super.getStateForPlacement(p_53249_);
   }

   public boolean useShapeForLightOcclusion(BlockState p_53295_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_53290_, BlockGetter p_53291_, BlockPos p_53292_, CollisionContext p_53293_) {
      return SHAPE;
   }

   public void tick(BlockState p_53262_, ServerLevel p_53263_, BlockPos p_53264_, Random p_53265_) {
      if (!p_53262_.canSurvive(p_53263_, p_53264_)) {
         turnToDirt(p_53262_, p_53263_, p_53264_);
      }

   }

   public void randomTick(BlockState p_53285_, ServerLevel p_53286_, BlockPos p_53287_, Random p_53288_) {
      int i = p_53285_.getValue(MOISTURE);
      if (!isNearWater(p_53286_, p_53287_) && !p_53286_.isRainingAt(p_53287_.above())) {
         if (i > 0) {
            p_53286_.setBlock(p_53287_, p_53285_.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
         } else if (!isUnderCrops(p_53286_, p_53287_)) {
            turnToDirt(p_53285_, p_53286_, p_53287_);
         }
      } else if (i < 7) {
         p_53286_.setBlock(p_53287_, p_53285_.setValue(MOISTURE, Integer.valueOf(7)), 2);
      }

   }

   public void fallOn(Level p_153227_, BlockState p_153228_, BlockPos p_153229_, Entity p_153230_, float p_153231_) {
      if (!p_153227_.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(p_153227_, p_153229_, Blocks.DIRT.defaultBlockState(), p_153231_, p_153230_)) { // Forge: Move logic to Entity#canTrample
         turnToDirt(p_153228_, p_153227_, p_153229_);
      }

      super.fallOn(p_153227_, p_153228_, p_153229_, p_153230_, p_153231_);
   }

   public static void turnToDirt(BlockState p_53297_, Level p_53298_, BlockPos p_53299_) {
      p_53298_.setBlockAndUpdate(p_53299_, pushEntitiesUp(p_53297_, Blocks.DIRT.defaultBlockState(), p_53298_, p_53299_));
   }

   private static boolean isUnderCrops(BlockGetter p_53251_, BlockPos p_53252_) {
      BlockState plant = p_53251_.getBlockState(p_53252_.above());
      BlockState state = p_53251_.getBlockState(p_53252_);
      return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(p_53251_, p_53252_, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
   }

   private static boolean isNearWater(LevelReader p_53259_, BlockPos p_53260_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(p_53260_.offset(-4, 0, -4), p_53260_.offset(4, 1, 4))) {
         if (p_53259_.getFluidState(blockpos).is(FluidTags.WATER)) {
            return true;
         }
      }

      return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(p_53259_, p_53260_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53283_) {
      p_53283_.add(MOISTURE);
   }

   public boolean isPathfindable(BlockState p_53267_, BlockGetter p_53268_, BlockPos p_53269_, PathComputationType p_53270_) {
      return false;
   }
}
