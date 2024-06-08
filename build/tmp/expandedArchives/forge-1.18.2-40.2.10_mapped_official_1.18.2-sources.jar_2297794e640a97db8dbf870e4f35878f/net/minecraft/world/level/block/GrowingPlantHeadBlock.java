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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantHeadBlock extends GrowingPlantBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
   public static final int MAX_AGE = 25;
   private final double growPerTickProbability;

   protected GrowingPlantHeadBlock(BlockBehaviour.Properties p_53928_, Direction p_53929_, VoxelShape p_53930_, boolean p_53931_, double p_53932_) {
      super(p_53928_, p_53929_, p_53930_, p_53931_);
      this.growPerTickProbability = p_53932_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(LevelAccessor p_53949_) {
      return this.defaultBlockState().setValue(AGE, Integer.valueOf(p_53949_.getRandom().nextInt(25)));
   }

   public boolean isRandomlyTicking(BlockState p_53961_) {
      return p_53961_.getValue(AGE) < 25;
   }

   public void randomTick(BlockState p_53963_, ServerLevel p_53964_, BlockPos p_53965_, Random p_53966_) {
      if (p_53963_.getValue(AGE) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_53964_, p_53965_.relative(this.growthDirection), p_53964_.getBlockState(p_53965_.relative(this.growthDirection)),p_53966_.nextDouble() < this.growPerTickProbability)) {
         BlockPos blockpos = p_53965_.relative(this.growthDirection);
         if (this.canGrowInto(p_53964_.getBlockState(blockpos))) {
            p_53964_.setBlockAndUpdate(blockpos, this.getGrowIntoState(p_53963_, p_53964_.random));
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_53964_, blockpos, p_53964_.getBlockState(blockpos));
         }
      }

   }

   protected BlockState getGrowIntoState(BlockState p_153331_, Random p_153332_) {
      return p_153331_.cycle(AGE);
   }

   public BlockState getMaxAgeState(BlockState p_187439_) {
      return p_187439_.setValue(AGE, Integer.valueOf(25));
   }

   public boolean isMaxAge(BlockState p_187441_) {
      return p_187441_.getValue(AGE) == 25;
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState p_153329_, BlockState p_153330_) {
      return p_153330_;
   }

   public BlockState updateShape(BlockState p_53951_, Direction p_53952_, BlockState p_53953_, LevelAccessor p_53954_, BlockPos p_53955_, BlockPos p_53956_) {
      if (p_53952_ == this.growthDirection.getOpposite() && !p_53951_.canSurvive(p_53954_, p_53955_)) {
         p_53954_.scheduleTick(p_53955_, this, 1);
      }

      if (p_53952_ != this.growthDirection || !p_53953_.is(this) && !p_53953_.is(this.getBodyBlock())) {
         if (this.scheduleFluidTicks) {
            p_53954_.scheduleTick(p_53955_, Fluids.WATER, Fluids.WATER.getTickDelay(p_53954_));
         }

         return super.updateShape(p_53951_, p_53952_, p_53953_, p_53954_, p_53955_, p_53956_);
      } else {
         return this.updateBodyAfterConvertedFromHead(p_53951_, this.getBodyBlock().defaultBlockState());
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53958_) {
      p_53958_.add(AGE);
   }

   public boolean isValidBonemealTarget(BlockGetter p_53939_, BlockPos p_53940_, BlockState p_53941_, boolean p_53942_) {
      return this.canGrowInto(p_53939_.getBlockState(p_53940_.relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(Level p_53944_, Random p_53945_, BlockPos p_53946_, BlockState p_53947_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_53934_, Random p_53935_, BlockPos p_53936_, BlockState p_53937_) {
      BlockPos blockpos = p_53936_.relative(this.growthDirection);
      int i = Math.min(p_53937_.getValue(AGE) + 1, 25);
      int j = this.getBlocksToGrowWhenBonemealed(p_53935_);

      for(int k = 0; k < j && this.canGrowInto(p_53934_.getBlockState(blockpos)); ++k) {
         p_53934_.setBlockAndUpdate(blockpos, p_53937_.setValue(AGE, Integer.valueOf(i)));
         blockpos = blockpos.relative(this.growthDirection);
         i = Math.min(i + 1, 25);
      }

   }

   protected abstract int getBlocksToGrowWhenBonemealed(Random p_53959_);

   protected abstract boolean canGrowInto(BlockState p_53968_);

   protected GrowingPlantHeadBlock getHeadBlock() {
      return this;
   }
}
