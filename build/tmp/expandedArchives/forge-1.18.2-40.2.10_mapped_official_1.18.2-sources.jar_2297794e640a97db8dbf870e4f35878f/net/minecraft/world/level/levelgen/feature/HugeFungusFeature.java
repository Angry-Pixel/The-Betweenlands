package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Material;

public class HugeFungusFeature extends Feature<HugeFungusConfiguration> {
   private static final float HUGE_PROBABILITY = 0.06F;

   public HugeFungusFeature(Codec<HugeFungusConfiguration> p_65922_) {
      super(p_65922_);
   }

   public boolean place(FeaturePlaceContext<HugeFungusConfiguration> p_159878_) {
      WorldGenLevel worldgenlevel = p_159878_.level();
      BlockPos blockpos = p_159878_.origin();
      Random random = p_159878_.random();
      ChunkGenerator chunkgenerator = p_159878_.chunkGenerator();
      HugeFungusConfiguration hugefungusconfiguration = p_159878_.config();
      Block block = hugefungusconfiguration.validBaseState.getBlock();
      BlockPos blockpos1 = null;
      BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
      if (blockstate.is(block)) {
         blockpos1 = blockpos;
      }

      if (blockpos1 == null) {
         return false;
      } else {
         int i = Mth.nextInt(random, 4, 13);
         if (random.nextInt(12) == 0) {
            i *= 2;
         }

         if (!hugefungusconfiguration.planted) {
            int j = chunkgenerator.getGenDepth();
            if (blockpos1.getY() + i + 1 >= j) {
               return false;
            }
         }

         boolean flag = !hugefungusconfiguration.planted && random.nextFloat() < 0.06F;
         worldgenlevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 4);
         this.placeStem(worldgenlevel, random, hugefungusconfiguration, blockpos1, i, flag);
         this.placeHat(worldgenlevel, random, hugefungusconfiguration, blockpos1, i, flag);
         return true;
      }
   }

   private static boolean isReplaceable(LevelAccessor p_65924_, BlockPos p_65925_, boolean p_65926_) {
      return p_65924_.isStateAtPosition(p_65925_, (p_65966_) -> {
         Material material = p_65966_.getMaterial();
         return p_65966_.getMaterial().isReplaceable() || p_65926_ && material == Material.PLANT;
      });
   }

   private void placeStem(LevelAccessor p_65936_, Random p_65937_, HugeFungusConfiguration p_65938_, BlockPos p_65939_, int p_65940_, boolean p_65941_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockState blockstate = p_65938_.stemState;
      int i = p_65941_ ? 1 : 0;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            boolean flag = p_65941_ && Mth.abs(j) == i && Mth.abs(k) == i;

            for(int l = 0; l < p_65940_; ++l) {
               blockpos$mutableblockpos.setWithOffset(p_65939_, j, l, k);
               if (isReplaceable(p_65936_, blockpos$mutableblockpos, true)) {
                  if (p_65938_.planted) {
                     if (!p_65936_.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                        p_65936_.destroyBlock(blockpos$mutableblockpos, true);
                     }

                     p_65936_.setBlock(blockpos$mutableblockpos, blockstate, 3);
                  } else if (flag) {
                     if (p_65937_.nextFloat() < 0.1F) {
                        this.setBlock(p_65936_, blockpos$mutableblockpos, blockstate);
                     }
                  } else {
                     this.setBlock(p_65936_, blockpos$mutableblockpos, blockstate);
                  }
               }
            }
         }
      }

   }

   private void placeHat(LevelAccessor p_65968_, Random p_65969_, HugeFungusConfiguration p_65970_, BlockPos p_65971_, int p_65972_, boolean p_65973_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      boolean flag = p_65970_.hatState.is(Blocks.NETHER_WART_BLOCK);
      int i = Math.min(p_65969_.nextInt(1 + p_65972_ / 3) + 5, p_65972_);
      int j = p_65972_ - i;

      for(int k = j; k <= p_65972_; ++k) {
         int l = k < p_65972_ - p_65969_.nextInt(3) ? 2 : 1;
         if (i > 8 && k < j + 4) {
            l = 3;
         }

         if (p_65973_) {
            ++l;
         }

         for(int i1 = -l; i1 <= l; ++i1) {
            for(int j1 = -l; j1 <= l; ++j1) {
               boolean flag1 = i1 == -l || i1 == l;
               boolean flag2 = j1 == -l || j1 == l;
               boolean flag3 = !flag1 && !flag2 && k != p_65972_;
               boolean flag4 = flag1 && flag2;
               boolean flag5 = k < j + 3;
               blockpos$mutableblockpos.setWithOffset(p_65971_, i1, k, j1);
               if (isReplaceable(p_65968_, blockpos$mutableblockpos, false)) {
                  if (p_65970_.planted && !p_65968_.getBlockState(blockpos$mutableblockpos.below()).isAir()) {
                     p_65968_.destroyBlock(blockpos$mutableblockpos, true);
                  }

                  if (flag5) {
                     if (!flag3) {
                        this.placeHatDropBlock(p_65968_, p_65969_, blockpos$mutableblockpos, p_65970_.hatState, flag);
                     }
                  } else if (flag3) {
                     this.placeHatBlock(p_65968_, p_65969_, p_65970_, blockpos$mutableblockpos, 0.1F, 0.2F, flag ? 0.1F : 0.0F);
                  } else if (flag4) {
                     this.placeHatBlock(p_65968_, p_65969_, p_65970_, blockpos$mutableblockpos, 0.01F, 0.7F, flag ? 0.083F : 0.0F);
                  } else {
                     this.placeHatBlock(p_65968_, p_65969_, p_65970_, blockpos$mutableblockpos, 5.0E-4F, 0.98F, flag ? 0.07F : 0.0F);
                  }
               }
            }
         }
      }

   }

   private void placeHatBlock(LevelAccessor p_65928_, Random p_65929_, HugeFungusConfiguration p_65930_, BlockPos.MutableBlockPos p_65931_, float p_65932_, float p_65933_, float p_65934_) {
      if (p_65929_.nextFloat() < p_65932_) {
         this.setBlock(p_65928_, p_65931_, p_65930_.decorState);
      } else if (p_65929_.nextFloat() < p_65933_) {
         this.setBlock(p_65928_, p_65931_, p_65930_.hatState);
         if (p_65929_.nextFloat() < p_65934_) {
            tryPlaceWeepingVines(p_65931_, p_65928_, p_65929_);
         }
      }

   }

   private void placeHatDropBlock(LevelAccessor p_65943_, Random p_65944_, BlockPos p_65945_, BlockState p_65946_, boolean p_65947_) {
      if (p_65943_.getBlockState(p_65945_.below()).is(p_65946_.getBlock())) {
         this.setBlock(p_65943_, p_65945_, p_65946_);
      } else if ((double)p_65944_.nextFloat() < 0.15D) {
         this.setBlock(p_65943_, p_65945_, p_65946_);
         if (p_65947_ && p_65944_.nextInt(11) == 0) {
            tryPlaceWeepingVines(p_65945_, p_65943_, p_65944_);
         }
      }

   }

   private static void tryPlaceWeepingVines(BlockPos p_65961_, LevelAccessor p_65962_, Random p_65963_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_65961_.mutable().move(Direction.DOWN);
      if (p_65962_.isEmptyBlock(blockpos$mutableblockpos)) {
         int i = Mth.nextInt(p_65963_, 1, 5);
         if (p_65963_.nextInt(7) == 0) {
            i *= 2;
         }

         int j = 23;
         int k = 25;
         WeepingVinesFeature.placeWeepingVinesColumn(p_65962_, p_65963_, blockpos$mutableblockpos, i, 23, 25);
      }
   }
}