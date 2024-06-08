package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WeepingVinesFeature extends Feature<NoneFeatureConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public WeepingVinesFeature(Codec<NoneFeatureConfiguration> p_67375_) {
      super(p_67375_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160661_) {
      WorldGenLevel worldgenlevel = p_160661_.level();
      BlockPos blockpos = p_160661_.origin();
      Random random = p_160661_.random();
      if (!worldgenlevel.isEmptyBlock(blockpos)) {
         return false;
      } else {
         BlockState blockstate = worldgenlevel.getBlockState(blockpos.above());
         if (!blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.NETHER_WART_BLOCK)) {
            return false;
         } else {
            this.placeRoofNetherWart(worldgenlevel, random, blockpos);
            this.placeRoofWeepingVines(worldgenlevel, random, blockpos);
            return true;
         }
      }
   }

   private void placeRoofNetherWart(LevelAccessor p_67384_, Random p_67385_, BlockPos p_67386_) {
      p_67384_.setBlock(p_67386_, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 200; ++i) {
         blockpos$mutableblockpos.setWithOffset(p_67386_, p_67385_.nextInt(6) - p_67385_.nextInt(6), p_67385_.nextInt(2) - p_67385_.nextInt(5), p_67385_.nextInt(6) - p_67385_.nextInt(6));
         if (p_67384_.isEmptyBlock(blockpos$mutableblockpos)) {
            int j = 0;

            for(Direction direction : DIRECTIONS) {
               BlockState blockstate = p_67384_.getBlockState(blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, direction));
               if (blockstate.is(Blocks.NETHERRACK) || blockstate.is(Blocks.NETHER_WART_BLOCK)) {
                  ++j;
               }

               if (j > 1) {
                  break;
               }
            }

            if (j == 1) {
               p_67384_.setBlock(blockpos$mutableblockpos, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
            }
         }
      }

   }

   private void placeRoofWeepingVines(LevelAccessor p_67400_, Random p_67401_, BlockPos p_67402_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 100; ++i) {
         blockpos$mutableblockpos.setWithOffset(p_67402_, p_67401_.nextInt(8) - p_67401_.nextInt(8), p_67401_.nextInt(2) - p_67401_.nextInt(7), p_67401_.nextInt(8) - p_67401_.nextInt(8));
         if (p_67400_.isEmptyBlock(blockpos$mutableblockpos)) {
            BlockState blockstate = p_67400_.getBlockState(blockpos$mutableblockpos.above());
            if (blockstate.is(Blocks.NETHERRACK) || blockstate.is(Blocks.NETHER_WART_BLOCK)) {
               int j = Mth.nextInt(p_67401_, 1, 8);
               if (p_67401_.nextInt(6) == 0) {
                  j *= 2;
               }

               if (p_67401_.nextInt(5) == 0) {
                  j = 1;
               }

               int k = 17;
               int l = 25;
               placeWeepingVinesColumn(p_67400_, p_67401_, blockpos$mutableblockpos, j, 17, 25);
            }
         }
      }

   }

   public static void placeWeepingVinesColumn(LevelAccessor p_67377_, Random p_67378_, BlockPos.MutableBlockPos p_67379_, int p_67380_, int p_67381_, int p_67382_) {
      for(int i = 0; i <= p_67380_; ++i) {
         if (p_67377_.isEmptyBlock(p_67379_)) {
            if (i == p_67380_ || !p_67377_.isEmptyBlock(p_67379_.below())) {
               p_67377_.setBlock(p_67379_, Blocks.WEEPING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Integer.valueOf(Mth.nextInt(p_67378_, p_67381_, p_67382_))), 2);
               break;
            }

            p_67377_.setBlock(p_67379_, Blocks.WEEPING_VINES_PLANT.defaultBlockState(), 2);
         }

         p_67379_.move(Direction.DOWN);
      }

   }
}