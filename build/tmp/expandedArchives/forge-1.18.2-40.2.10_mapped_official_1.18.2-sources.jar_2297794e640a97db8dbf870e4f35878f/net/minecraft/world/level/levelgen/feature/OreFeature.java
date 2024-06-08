package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class OreFeature extends Feature<OreConfiguration> {
   public OreFeature(Codec<OreConfiguration> p_66531_) {
      super(p_66531_);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> p_160177_) {
      Random random = p_160177_.random();
      BlockPos blockpos = p_160177_.origin();
      WorldGenLevel worldgenlevel = p_160177_.level();
      OreConfiguration oreconfiguration = p_160177_.config();
      float f = random.nextFloat() * (float)Math.PI;
      float f1 = (float)oreconfiguration.size / 8.0F;
      int i = Mth.ceil(((float)oreconfiguration.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double d0 = (double)blockpos.getX() + Math.sin((double)f) * (double)f1;
      double d1 = (double)blockpos.getX() - Math.sin((double)f) * (double)f1;
      double d2 = (double)blockpos.getZ() + Math.cos((double)f) * (double)f1;
      double d3 = (double)blockpos.getZ() - Math.cos((double)f) * (double)f1;
      int j = 2;
      double d4 = (double)(blockpos.getY() + random.nextInt(3) - 2);
      double d5 = (double)(blockpos.getY() + random.nextInt(3) - 2);
      int k = blockpos.getX() - Mth.ceil(f1) - i;
      int l = blockpos.getY() - 2 - i;
      int i1 = blockpos.getZ() - Mth.ceil(f1) - i;
      int j1 = 2 * (Mth.ceil(f1) + i);
      int k1 = 2 * (2 + i);

      for(int l1 = k; l1 <= k + j1; ++l1) {
         for(int i2 = i1; i2 <= i1 + j1; ++i2) {
            if (l <= worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, l1, i2)) {
               return this.doPlace(worldgenlevel, random, oreconfiguration, d0, d1, d2, d3, d4, d5, k, l, i1, j1, k1);
            }
         }
      }

      return false;
   }

   protected boolean doPlace(WorldGenLevel p_66533_, Random p_66534_, OreConfiguration p_66535_, double p_66536_, double p_66537_, double p_66538_, double p_66539_, double p_66540_, double p_66541_, int p_66542_, int p_66543_, int p_66544_, int p_66545_, int p_66546_) {
      int i = 0;
      BitSet bitset = new BitSet(p_66545_ * p_66546_ * p_66545_);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int j = p_66535_.size;
      double[] adouble = new double[j * 4];

      for(int k = 0; k < j; ++k) {
         float f = (float)k / (float)j;
         double d0 = Mth.lerp((double)f, p_66536_, p_66537_);
         double d1 = Mth.lerp((double)f, p_66540_, p_66541_);
         double d2 = Mth.lerp((double)f, p_66538_, p_66539_);
         double d3 = p_66534_.nextDouble() * (double)j / 16.0D;
         double d4 = ((double)(Mth.sin((float)Math.PI * f) + 1.0F) * d3 + 1.0D) / 2.0D;
         adouble[k * 4 + 0] = d0;
         adouble[k * 4 + 1] = d1;
         adouble[k * 4 + 2] = d2;
         adouble[k * 4 + 3] = d4;
      }

      for(int l3 = 0; l3 < j - 1; ++l3) {
         if (!(adouble[l3 * 4 + 3] <= 0.0D)) {
            for(int i4 = l3 + 1; i4 < j; ++i4) {
               if (!(adouble[i4 * 4 + 3] <= 0.0D)) {
                  double d8 = adouble[l3 * 4 + 0] - adouble[i4 * 4 + 0];
                  double d10 = adouble[l3 * 4 + 1] - adouble[i4 * 4 + 1];
                  double d12 = adouble[l3 * 4 + 2] - adouble[i4 * 4 + 2];
                  double d14 = adouble[l3 * 4 + 3] - adouble[i4 * 4 + 3];
                  if (d14 * d14 > d8 * d8 + d10 * d10 + d12 * d12) {
                     if (d14 > 0.0D) {
                        adouble[i4 * 4 + 3] = -1.0D;
                     } else {
                        adouble[l3 * 4 + 3] = -1.0D;
                     }
                  }
               }
            }
         }
      }

      BulkSectionAccess bulksectionaccess = new BulkSectionAccess(p_66533_);

      try {
         for(int j4 = 0; j4 < j; ++j4) {
            double d9 = adouble[j4 * 4 + 3];
            if (!(d9 < 0.0D)) {
               double d11 = adouble[j4 * 4 + 0];
               double d13 = adouble[j4 * 4 + 1];
               double d15 = adouble[j4 * 4 + 2];
               int k4 = Math.max(Mth.floor(d11 - d9), p_66542_);
               int l = Math.max(Mth.floor(d13 - d9), p_66543_);
               int i1 = Math.max(Mth.floor(d15 - d9), p_66544_);
               int j1 = Math.max(Mth.floor(d11 + d9), k4);
               int k1 = Math.max(Mth.floor(d13 + d9), l);
               int l1 = Math.max(Mth.floor(d15 + d9), i1);

               for(int i2 = k4; i2 <= j1; ++i2) {
                  double d5 = ((double)i2 + 0.5D - d11) / d9;
                  if (d5 * d5 < 1.0D) {
                     for(int j2 = l; j2 <= k1; ++j2) {
                        double d6 = ((double)j2 + 0.5D - d13) / d9;
                        if (d5 * d5 + d6 * d6 < 1.0D) {
                           for(int k2 = i1; k2 <= l1; ++k2) {
                              double d7 = ((double)k2 + 0.5D - d15) / d9;
                              if (d5 * d5 + d6 * d6 + d7 * d7 < 1.0D && !p_66533_.isOutsideBuildHeight(j2)) {
                                 int l2 = i2 - p_66542_ + (j2 - p_66543_) * p_66545_ + (k2 - p_66544_) * p_66545_ * p_66546_;
                                 if (!bitset.get(l2)) {
                                    bitset.set(l2);
                                    blockpos$mutableblockpos.set(i2, j2, k2);
                                    if (p_66533_.ensureCanWrite(blockpos$mutableblockpos)) {
                                       LevelChunkSection levelchunksection = bulksectionaccess.getSection(blockpos$mutableblockpos);
                                       if (levelchunksection != null) {
                                          int i3 = SectionPos.sectionRelative(i2);
                                          int j3 = SectionPos.sectionRelative(j2);
                                          int k3 = SectionPos.sectionRelative(k2);
                                          BlockState blockstate = levelchunksection.getBlockState(i3, j3, k3);

                                          for(OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : p_66535_.targetStates) {
                                             if (canPlaceOre(blockstate, bulksectionaccess::getBlockState, p_66534_, p_66535_, oreconfiguration$targetblockstate, blockpos$mutableblockpos)) {
                                                levelchunksection.setBlockState(i3, j3, k3, oreconfiguration$targetblockstate.state, false);
                                                ++i;
                                                break;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable throwable1) {
         try {
            bulksectionaccess.close();
         } catch (Throwable throwable) {
            throwable1.addSuppressed(throwable);
         }

         throw throwable1;
      }

      bulksectionaccess.close();
      return i > 0;
   }

   public static boolean canPlaceOre(BlockState p_160170_, Function<BlockPos, BlockState> p_160171_, Random p_160172_, OreConfiguration p_160173_, OreConfiguration.TargetBlockState p_160174_, BlockPos.MutableBlockPos p_160175_) {
      if (!p_160174_.target.test(p_160170_, p_160172_)) {
         return false;
      } else if (shouldSkipAirCheck(p_160172_, p_160173_.discardChanceOnAirExposure)) {
         return true;
      } else {
         return !isAdjacentToAir(p_160171_, p_160175_);
      }
   }

   protected static boolean shouldSkipAirCheck(Random p_160179_, float p_160180_) {
      if (p_160180_ <= 0.0F) {
         return true;
      } else if (p_160180_ >= 1.0F) {
         return false;
      } else {
         return p_160179_.nextFloat() >= p_160180_;
      }
   }
}