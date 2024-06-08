package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Material;

public class IcebergFeature extends Feature<BlockStateConfiguration> {
   public IcebergFeature(Codec<BlockStateConfiguration> p_66017_) {
      super(p_66017_);
   }

   public boolean place(FeaturePlaceContext<BlockStateConfiguration> p_159884_) {
      BlockPos blockpos = p_159884_.origin();
      WorldGenLevel worldgenlevel = p_159884_.level();
      blockpos = new BlockPos(blockpos.getX(), p_159884_.chunkGenerator().getSeaLevel(), blockpos.getZ());
      Random random = p_159884_.random();
      boolean flag = random.nextDouble() > 0.7D;
      BlockState blockstate = (p_159884_.config()).state;
      double d0 = random.nextDouble() * 2.0D * Math.PI;
      int i = 11 - random.nextInt(5);
      int j = 3 + random.nextInt(3);
      boolean flag1 = random.nextDouble() > 0.7D;
      int k = 11;
      int l = flag1 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
      if (!flag1 && random.nextDouble() > 0.9D) {
         l += random.nextInt(19) + 7;
      }

      int i1 = Math.min(l + random.nextInt(11), 18);
      int j1 = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
      int k1 = flag1 ? i : 11;

      for(int l1 = -k1; l1 < k1; ++l1) {
         for(int i2 = -k1; i2 < k1; ++i2) {
            for(int j2 = 0; j2 < l; ++j2) {
               int k2 = flag1 ? this.heightDependentRadiusEllipse(j2, l, j1) : this.heightDependentRadiusRound(random, j2, l, j1);
               if (flag1 || l1 < k2) {
                  this.generateIcebergBlock(worldgenlevel, random, blockpos, l, l1, j2, i2, k2, k1, flag1, j, d0, flag, blockstate);
               }
            }
         }
      }

      this.smooth(worldgenlevel, blockpos, j1, l, flag1, i);

      for(int i3 = -k1; i3 < k1; ++i3) {
         for(int j3 = -k1; j3 < k1; ++j3) {
            for(int k3 = -1; k3 > -i1; --k3) {
               int l3 = flag1 ? Mth.ceil((float)k1 * (1.0F - (float)Math.pow((double)k3, 2.0D) / ((float)i1 * 8.0F))) : k1;
               int l2 = this.heightDependentRadiusSteep(random, -k3, i1, j1);
               if (i3 < l2) {
                  this.generateIcebergBlock(worldgenlevel, random, blockpos, i1, i3, k3, j3, l2, l3, flag1, j, d0, flag, blockstate);
               }
            }
         }
      }

      boolean flag2 = flag1 ? random.nextDouble() > 0.1D : random.nextDouble() > 0.7D;
      if (flag2) {
         this.generateCutOut(random, worldgenlevel, j1, l, blockpos, flag1, i, d0, j);
      }

      return true;
   }

   private void generateCutOut(Random p_66100_, LevelAccessor p_66101_, int p_66102_, int p_66103_, BlockPos p_66104_, boolean p_66105_, int p_66106_, double p_66107_, int p_66108_) {
      int i = p_66100_.nextBoolean() ? -1 : 1;
      int j = p_66100_.nextBoolean() ? -1 : 1;
      int k = p_66100_.nextInt(Math.max(p_66102_ / 2 - 2, 1));
      if (p_66100_.nextBoolean()) {
         k = p_66102_ / 2 + 1 - p_66100_.nextInt(Math.max(p_66102_ - p_66102_ / 2 - 1, 1));
      }

      int l = p_66100_.nextInt(Math.max(p_66102_ / 2 - 2, 1));
      if (p_66100_.nextBoolean()) {
         l = p_66102_ / 2 + 1 - p_66100_.nextInt(Math.max(p_66102_ - p_66102_ / 2 - 1, 1));
      }

      if (p_66105_) {
         k = l = p_66100_.nextInt(Math.max(p_66106_ - 5, 1));
      }

      BlockPos blockpos = new BlockPos(i * k, 0, j * l);
      double d0 = p_66105_ ? p_66107_ + (Math.PI / 2D) : p_66100_.nextDouble() * 2.0D * Math.PI;

      for(int i1 = 0; i1 < p_66103_ - 3; ++i1) {
         int j1 = this.heightDependentRadiusRound(p_66100_, i1, p_66103_, p_66102_);
         this.carve(j1, i1, p_66104_, p_66101_, false, d0, blockpos, p_66106_, p_66108_);
      }

      for(int k1 = -1; k1 > -p_66103_ + p_66100_.nextInt(5); --k1) {
         int l1 = this.heightDependentRadiusSteep(p_66100_, -k1, p_66103_, p_66102_);
         this.carve(l1, k1, p_66104_, p_66101_, true, d0, blockpos, p_66106_, p_66108_);
      }

   }

   private void carve(int p_66036_, int p_66037_, BlockPos p_66038_, LevelAccessor p_66039_, boolean p_66040_, double p_66041_, BlockPos p_66042_, int p_66043_, int p_66044_) {
      int i = p_66036_ + 1 + p_66043_ / 3;
      int j = Math.min(p_66036_ - 3, 3) + p_66044_ / 2 - 1;

      for(int k = -i; k < i; ++k) {
         for(int l = -i; l < i; ++l) {
            double d0 = this.signedDistanceEllipse(k, l, p_66042_, i, j, p_66041_);
            if (d0 < 0.0D) {
               BlockPos blockpos = p_66038_.offset(k, p_66037_, l);
               BlockState blockstate = p_66039_.getBlockState(blockpos);
               if (isIcebergState(blockstate) || blockstate.is(Blocks.SNOW_BLOCK)) {
                  if (p_66040_) {
                     this.setBlock(p_66039_, blockpos, Blocks.WATER.defaultBlockState());
                  } else {
                     this.setBlock(p_66039_, blockpos, Blocks.AIR.defaultBlockState());
                     this.removeFloatingSnowLayer(p_66039_, blockpos);
                  }
               }
            }
         }
      }

   }

   private void removeFloatingSnowLayer(LevelAccessor p_66049_, BlockPos p_66050_) {
      if (p_66049_.getBlockState(p_66050_.above()).is(Blocks.SNOW)) {
         this.setBlock(p_66049_, p_66050_.above(), Blocks.AIR.defaultBlockState());
      }

   }

   private void generateIcebergBlock(LevelAccessor p_66059_, Random p_66060_, BlockPos p_66061_, int p_66062_, int p_66063_, int p_66064_, int p_66065_, int p_66066_, int p_66067_, boolean p_66068_, int p_66069_, double p_66070_, boolean p_66071_, BlockState p_66072_) {
      double d0 = p_66068_ ? this.signedDistanceEllipse(p_66063_, p_66065_, BlockPos.ZERO, p_66067_, this.getEllipseC(p_66064_, p_66062_, p_66069_), p_66070_) : this.signedDistanceCircle(p_66063_, p_66065_, BlockPos.ZERO, p_66066_, p_66060_);
      if (d0 < 0.0D) {
         BlockPos blockpos = p_66061_.offset(p_66063_, p_66064_, p_66065_);
         double d1 = p_66068_ ? -0.5D : (double)(-6 - p_66060_.nextInt(3));
         if (d0 > d1 && p_66060_.nextDouble() > 0.9D) {
            return;
         }

         this.setIcebergBlock(blockpos, p_66059_, p_66060_, p_66062_ - p_66064_, p_66062_, p_66068_, p_66071_, p_66072_);
      }

   }

   private void setIcebergBlock(BlockPos p_66086_, LevelAccessor p_66087_, Random p_66088_, int p_66089_, int p_66090_, boolean p_66091_, boolean p_66092_, BlockState p_66093_) {
      BlockState blockstate = p_66087_.getBlockState(p_66086_);
      if (blockstate.getMaterial() == Material.AIR || blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.ICE) || blockstate.is(Blocks.WATER)) {
         boolean flag = !p_66091_ || p_66088_.nextDouble() > 0.05D;
         int i = p_66091_ ? 3 : 2;
         if (p_66092_ && !blockstate.is(Blocks.WATER) && (double)p_66089_ <= (double)p_66088_.nextInt(Math.max(1, p_66090_ / i)) + (double)p_66090_ * 0.6D && flag) {
            this.setBlock(p_66087_, p_66086_, Blocks.SNOW_BLOCK.defaultBlockState());
         } else {
            this.setBlock(p_66087_, p_66086_, p_66093_);
         }
      }

   }

   private int getEllipseC(int p_66019_, int p_66020_, int p_66021_) {
      int i = p_66021_;
      if (p_66019_ > 0 && p_66020_ - p_66019_ <= 3) {
         i = p_66021_ - (4 - (p_66020_ - p_66019_));
      }

      return i;
   }

   private double signedDistanceCircle(int p_66030_, int p_66031_, BlockPos p_66032_, int p_66033_, Random p_66034_) {
      float f = 10.0F * Mth.clamp(p_66034_.nextFloat(), 0.2F, 0.8F) / (float)p_66033_;
      return (double)f + Math.pow((double)(p_66030_ - p_66032_.getX()), 2.0D) + Math.pow((double)(p_66031_ - p_66032_.getZ()), 2.0D) - Math.pow((double)p_66033_, 2.0D);
   }

   private double signedDistanceEllipse(int p_66023_, int p_66024_, BlockPos p_66025_, int p_66026_, int p_66027_, double p_66028_) {
      return Math.pow(((double)(p_66023_ - p_66025_.getX()) * Math.cos(p_66028_) - (double)(p_66024_ - p_66025_.getZ()) * Math.sin(p_66028_)) / (double)p_66026_, 2.0D) + Math.pow(((double)(p_66023_ - p_66025_.getX()) * Math.sin(p_66028_) + (double)(p_66024_ - p_66025_.getZ()) * Math.cos(p_66028_)) / (double)p_66027_, 2.0D) - 1.0D;
   }

   private int heightDependentRadiusRound(Random p_66095_, int p_66096_, int p_66097_, int p_66098_) {
      float f = 3.5F - p_66095_.nextFloat();
      float f1 = (1.0F - (float)Math.pow((double)p_66096_, 2.0D) / ((float)p_66097_ * f)) * (float)p_66098_;
      if (p_66097_ > 15 + p_66095_.nextInt(5)) {
         int i = p_66096_ < 3 + p_66095_.nextInt(6) ? p_66096_ / 2 : p_66096_;
         f1 = (1.0F - (float)i / ((float)p_66097_ * f * 0.4F)) * (float)p_66098_;
      }

      return Mth.ceil(f1 / 2.0F);
   }

   private int heightDependentRadiusEllipse(int p_66110_, int p_66111_, int p_66112_) {
      float f = 1.0F;
      float f1 = (1.0F - (float)Math.pow((double)p_66110_, 2.0D) / ((float)p_66111_ * 1.0F)) * (float)p_66112_;
      return Mth.ceil(f1 / 2.0F);
   }

   private int heightDependentRadiusSteep(Random p_66114_, int p_66115_, int p_66116_, int p_66117_) {
      float f = 1.0F + p_66114_.nextFloat() / 2.0F;
      float f1 = (1.0F - (float)p_66115_ / ((float)p_66116_ * f)) * (float)p_66117_;
      return Mth.ceil(f1 / 2.0F);
   }

   private static boolean isIcebergState(BlockState p_159886_) {
      return p_159886_.is(Blocks.PACKED_ICE) || p_159886_.is(Blocks.SNOW_BLOCK) || p_159886_.is(Blocks.BLUE_ICE);
   }

   private boolean belowIsAir(BlockGetter p_66046_, BlockPos p_66047_) {
      return p_66046_.getBlockState(p_66047_.below()).getMaterial() == Material.AIR;
   }

   private void smooth(LevelAccessor p_66052_, BlockPos p_66053_, int p_66054_, int p_66055_, boolean p_66056_, int p_66057_) {
      int i = p_66056_ ? p_66057_ : p_66054_ / 2;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            for(int l = 0; l <= p_66055_; ++l) {
               BlockPos blockpos = p_66053_.offset(j, l, k);
               BlockState blockstate = p_66052_.getBlockState(blockpos);
               if (isIcebergState(blockstate) || blockstate.is(Blocks.SNOW)) {
                  if (this.belowIsAir(p_66052_, blockpos)) {
                     this.setBlock(p_66052_, blockpos, Blocks.AIR.defaultBlockState());
                     this.setBlock(p_66052_, blockpos.above(), Blocks.AIR.defaultBlockState());
                  } else if (isIcebergState(blockstate)) {
                     BlockState[] ablockstate = new BlockState[]{p_66052_.getBlockState(blockpos.west()), p_66052_.getBlockState(blockpos.east()), p_66052_.getBlockState(blockpos.north()), p_66052_.getBlockState(blockpos.south())};
                     int i1 = 0;

                     for(BlockState blockstate1 : ablockstate) {
                        if (!isIcebergState(blockstate1)) {
                           ++i1;
                        }
                     }

                     if (i1 >= 3) {
                        this.setBlock(p_66052_, blockpos, Blocks.AIR.defaultBlockState());
                     }
                  }
               }
            }
         }
      }

   }
}