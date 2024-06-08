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
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;

public class TwistingVinesFeature extends Feature<TwistingVinesConfig> {
   public TwistingVinesFeature(Codec<TwistingVinesConfig> p_67292_) {
      super(p_67292_);
   }

   public boolean place(FeaturePlaceContext<TwistingVinesConfig> p_160558_) {
      WorldGenLevel worldgenlevel = p_160558_.level();
      BlockPos blockpos = p_160558_.origin();
      if (isInvalidPlacementLocation(worldgenlevel, blockpos)) {
         return false;
      } else {
         Random random = p_160558_.random();
         TwistingVinesConfig twistingvinesconfig = p_160558_.config();
         int i = twistingvinesconfig.spreadWidth();
         int j = twistingvinesconfig.spreadHeight();
         int k = twistingvinesconfig.maxHeight();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int l = 0; l < i * i; ++l) {
            blockpos$mutableblockpos.set(blockpos).move(Mth.nextInt(random, -i, i), Mth.nextInt(random, -j, j), Mth.nextInt(random, -i, i));
            if (findFirstAirBlockAboveGround(worldgenlevel, blockpos$mutableblockpos) && !isInvalidPlacementLocation(worldgenlevel, blockpos$mutableblockpos)) {
               int i1 = Mth.nextInt(random, 1, k);
               if (random.nextInt(6) == 0) {
                  i1 *= 2;
               }

               if (random.nextInt(5) == 0) {
                  i1 = 1;
               }

               int j1 = 17;
               int k1 = 25;
               placeWeepingVinesColumn(worldgenlevel, random, blockpos$mutableblockpos, i1, 17, 25);
            }
         }

         return true;
      }
   }

   private static boolean findFirstAirBlockAboveGround(LevelAccessor p_67294_, BlockPos.MutableBlockPos p_67295_) {
      do {
         p_67295_.move(0, -1, 0);
         if (p_67294_.isOutsideBuildHeight(p_67295_)) {
            return false;
         }
      } while(p_67294_.getBlockState(p_67295_).isAir());

      p_67295_.move(0, 1, 0);
      return true;
   }

   public static void placeWeepingVinesColumn(LevelAccessor p_67300_, Random p_67301_, BlockPos.MutableBlockPos p_67302_, int p_67303_, int p_67304_, int p_67305_) {
      for(int i = 1; i <= p_67303_; ++i) {
         if (p_67300_.isEmptyBlock(p_67302_)) {
            if (i == p_67303_ || !p_67300_.isEmptyBlock(p_67302_.above())) {
               p_67300_.setBlock(p_67302_, Blocks.TWISTING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Integer.valueOf(Mth.nextInt(p_67301_, p_67304_, p_67305_))), 2);
               break;
            }

            p_67300_.setBlock(p_67302_, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
         }

         p_67302_.move(Direction.UP);
      }

   }

   private static boolean isInvalidPlacementLocation(LevelAccessor p_67297_, BlockPos p_67298_) {
      if (!p_67297_.isEmptyBlock(p_67298_)) {
         return true;
      } else {
         BlockState blockstate = p_67297_.getBlockState(p_67298_.below());
         return !blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.WARPED_NYLIUM) && !blockstate.is(Blocks.WARPED_WART_BLOCK);
      }
   }
}