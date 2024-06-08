package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock extends Block implements net.minecraftforge.common.IForgeShearable {
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   private static final int TICK_DELAY = 1;

   public LeavesBlock(BlockBehaviour.Properties p_54422_) {
      super(p_54422_);
      this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(7)).setValue(PERSISTENT, Boolean.valueOf(false)));
   }

   public VoxelShape getBlockSupportShape(BlockState p_54456_, BlockGetter p_54457_, BlockPos p_54458_) {
      return Shapes.empty();
   }

   public boolean isRandomlyTicking(BlockState p_54449_) {
      return p_54449_.getValue(DISTANCE) == 7 && !p_54449_.getValue(PERSISTENT);
   }

   public void randomTick(BlockState p_54451_, ServerLevel p_54452_, BlockPos p_54453_, Random p_54454_) {
      if (!p_54451_.getValue(PERSISTENT) && p_54451_.getValue(DISTANCE) == 7) {
         dropResources(p_54451_, p_54452_, p_54453_);
         p_54452_.removeBlock(p_54453_, false);
      }

   }

   public void tick(BlockState p_54426_, ServerLevel p_54427_, BlockPos p_54428_, Random p_54429_) {
      p_54427_.setBlock(p_54428_, updateDistance(p_54426_, p_54427_, p_54428_), 3);
   }

   public int getLightBlock(BlockState p_54460_, BlockGetter p_54461_, BlockPos p_54462_) {
      return 1;
   }

   public BlockState updateShape(BlockState p_54440_, Direction p_54441_, BlockState p_54442_, LevelAccessor p_54443_, BlockPos p_54444_, BlockPos p_54445_) {
      int i = getDistanceAt(p_54442_) + 1;
      if (i != 1 || p_54440_.getValue(DISTANCE) != i) {
         p_54443_.scheduleTick(p_54444_, this, 1);
      }

      return p_54440_;
   }

   private static BlockState updateDistance(BlockState p_54436_, LevelAccessor p_54437_, BlockPos p_54438_) {
      int i = 7;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(p_54438_, direction);
         i = Math.min(i, getDistanceAt(p_54437_.getBlockState(blockpos$mutableblockpos)) + 1);
         if (i == 1) {
            break;
         }
      }

      return p_54436_.setValue(DISTANCE, Integer.valueOf(i));
   }

   private static int getDistanceAt(BlockState p_54464_) {
      if (p_54464_.is(BlockTags.LOGS)) {
         return 0;
      } else {
         return p_54464_.getBlock() instanceof LeavesBlock ? p_54464_.getValue(DISTANCE) : 7;
      }
   }

   public void animateTick(BlockState p_54431_, Level p_54432_, BlockPos p_54433_, Random p_54434_) {
      if (p_54432_.isRainingAt(p_54433_.above())) {
         if (p_54434_.nextInt(15) == 1) {
            BlockPos blockpos = p_54433_.below();
            BlockState blockstate = p_54432_.getBlockState(blockpos);
            if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(p_54432_, blockpos, Direction.UP)) {
               double d0 = (double)p_54433_.getX() + p_54434_.nextDouble();
               double d1 = (double)p_54433_.getY() - 0.05D;
               double d2 = (double)p_54433_.getZ() + p_54434_.nextDouble();
               p_54432_.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54447_) {
      p_54447_.add(DISTANCE, PERSISTENT);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54424_) {
      return updateDistance(this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)), p_54424_.getLevel(), p_54424_.getClickedPos());
   }
}
