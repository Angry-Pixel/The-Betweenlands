package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends BushBlock implements BonemealableBlock {
   public static final int MAX_AGE = 7;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private final StemGrownBlock fruit;
   private final Supplier<Item> seedSupplier;

   public StemBlock(StemGrownBlock p_154728_, Supplier<Item> p_154729_, BlockBehaviour.Properties p_154730_) {
      super(p_154730_);
      this.fruit = p_154728_;
      this.seedSupplier = p_154729_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_57047_, BlockGetter p_57048_, BlockPos p_57049_, CollisionContext p_57050_) {
      return SHAPE_BY_AGE[p_57047_.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState p_57053_, BlockGetter p_57054_, BlockPos p_57055_) {
      return p_57053_.is(Blocks.FARMLAND);
   }

   public void randomTick(BlockState p_57042_, ServerLevel p_57043_, BlockPos p_57044_, Random p_57045_) {
      if (!p_57043_.isAreaLoaded(p_57044_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
      if (p_57043_.getRawBrightness(p_57044_, 0) >= 9) {
         float f = CropBlock.getGrowthSpeed(this, p_57043_, p_57044_);
         if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_57043_, p_57044_, p_57042_, p_57045_.nextInt((int)(25.0F / f) + 1) == 0)) {
            int i = p_57042_.getValue(AGE);
            if (i < 7) {
               p_57043_.setBlock(p_57044_, p_57042_.setValue(AGE, Integer.valueOf(i + 1)), 2);
            } else {
               Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_57045_);
               BlockPos blockpos = p_57044_.relative(direction);
               BlockState blockstate = p_57043_.getBlockState(blockpos.below());
               Block block = blockstate.getBlock();
               if (p_57043_.isEmptyBlock(blockpos) && (blockstate.canSustainPlant(p_57043_, blockpos.below(), Direction.UP, this) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                  p_57043_.setBlockAndUpdate(blockpos, this.fruit.defaultBlockState());
                  p_57043_.setBlockAndUpdate(p_57044_, this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
               }
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_57043_, p_57044_, p_57042_);
         }

      }
   }

   public ItemStack getCloneItemStack(BlockGetter p_57026_, BlockPos p_57027_, BlockState p_57028_) {
      return new ItemStack(this.seedSupplier.get());
   }

   public boolean isValidBonemealTarget(BlockGetter p_57030_, BlockPos p_57031_, BlockState p_57032_, boolean p_57033_) {
      return p_57032_.getValue(AGE) != 7;
   }

   public boolean isBonemealSuccess(Level p_57035_, Random p_57036_, BlockPos p_57037_, BlockState p_57038_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_57021_, Random p_57022_, BlockPos p_57023_, BlockState p_57024_) {
      int i = Math.min(7, p_57024_.getValue(AGE) + Mth.nextInt(p_57021_.random, 2, 5));
      BlockState blockstate = p_57024_.setValue(AGE, Integer.valueOf(i));
      p_57021_.setBlock(p_57023_, blockstate, 2);
      if (i == 7) {
         blockstate.randomTick(p_57021_, p_57023_, p_57021_.random);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57040_) {
      p_57040_.add(AGE);
   }

   public StemGrownBlock getFruit() {
      return this.fruit;
   }

   //FORGE START
   @Override
   public net.minecraftforge.common.PlantType getPlantType(BlockGetter world, BlockPos pos) {
      return net.minecraftforge.common.PlantType.CROP;
   }
}
