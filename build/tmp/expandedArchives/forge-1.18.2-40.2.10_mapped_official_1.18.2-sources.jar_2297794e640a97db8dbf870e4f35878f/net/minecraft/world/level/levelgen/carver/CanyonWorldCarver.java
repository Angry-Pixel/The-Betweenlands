package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class CanyonWorldCarver extends WorldCarver<CanyonCarverConfiguration> {
   public CanyonWorldCarver(Codec<CanyonCarverConfiguration> p_64711_) {
      super(p_64711_);
   }

   public boolean isStartChunk(CanyonCarverConfiguration p_159023_, Random p_159024_) {
      return p_159024_.nextFloat() <= p_159023_.probability;
   }

   public boolean carve(CarvingContext p_190611_, CanyonCarverConfiguration p_190612_, ChunkAccess p_190613_, Function<BlockPos, Holder<Biome>> p_190614_, Random p_190615_, Aquifer p_190616_, ChunkPos p_190617_, CarvingMask p_190618_) {
      int i = (this.getRange() * 2 - 1) * 16;
      double d0 = (double)p_190617_.getBlockX(p_190615_.nextInt(16));
      int j = p_190612_.y.sample(p_190615_, p_190611_);
      double d1 = (double)p_190617_.getBlockZ(p_190615_.nextInt(16));
      float f = p_190615_.nextFloat() * ((float)Math.PI * 2F);
      float f1 = p_190612_.verticalRotation.sample(p_190615_);
      double d2 = (double)p_190612_.yScale.sample(p_190615_);
      float f2 = p_190612_.shape.thickness.sample(p_190615_);
      int k = (int)((float)i * p_190612_.shape.distanceFactor.sample(p_190615_));
      int l = 0;
      this.doCarve(p_190611_, p_190612_, p_190613_, p_190614_, p_190615_.nextLong(), p_190616_, d0, (double)j, d1, f2, f, f1, 0, k, d2, p_190618_);
      return true;
   }

   private void doCarve(CarvingContext p_190594_, CanyonCarverConfiguration p_190595_, ChunkAccess p_190596_, Function<BlockPos, Holder<Biome>> p_190597_, long p_190598_, Aquifer p_190599_, double p_190600_, double p_190601_, double p_190602_, float p_190603_, float p_190604_, float p_190605_, int p_190606_, int p_190607_, double p_190608_, CarvingMask p_190609_) {
      Random random = new Random(p_190598_);
      float[] afloat = this.initWidthFactors(p_190594_, p_190595_, random);
      float f = 0.0F;
      float f1 = 0.0F;

      for(int i = p_190606_; i < p_190607_; ++i) {
         double d0 = 1.5D + (double)(Mth.sin((float)i * (float)Math.PI / (float)p_190607_) * p_190603_);
         double d1 = d0 * p_190608_;
         d0 *= (double)p_190595_.shape.horizontalRadiusFactor.sample(random);
         d1 = this.updateVerticalRadius(p_190595_, random, d1, (float)p_190607_, (float)i);
         float f2 = Mth.cos(p_190605_);
         float f3 = Mth.sin(p_190605_);
         p_190600_ += (double)(Mth.cos(p_190604_) * f2);
         p_190601_ += (double)f3;
         p_190602_ += (double)(Mth.sin(p_190604_) * f2);
         p_190605_ *= 0.7F;
         p_190605_ += f1 * 0.05F;
         p_190604_ += f * 0.05F;
         f1 *= 0.8F;
         f *= 0.5F;
         f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            if (!canReach(p_190596_.getPos(), p_190600_, p_190602_, i, p_190607_, p_190603_)) {
               return;
            }

            this.carveEllipsoid(p_190594_, p_190595_, p_190596_, p_190597_, p_190599_, p_190600_, p_190601_, p_190602_, d0, d1, p_190609_, (p_159082_, p_159083_, p_159084_, p_159085_, p_159086_) -> {
               return this.shouldSkip(p_159082_, afloat, p_159083_, p_159084_, p_159085_, p_159086_);
            });
         }
      }

   }

   private float[] initWidthFactors(CarvingContext p_159061_, CanyonCarverConfiguration p_159062_, Random p_159063_) {
      int i = p_159061_.getGenDepth();
      float[] afloat = new float[i];
      float f = 1.0F;

      for(int j = 0; j < i; ++j) {
         if (j == 0 || p_159063_.nextInt(p_159062_.shape.widthSmoothness) == 0) {
            f = 1.0F + p_159063_.nextFloat() * p_159063_.nextFloat();
         }

         afloat[j] = f * f;
      }

      return afloat;
   }

   private double updateVerticalRadius(CanyonCarverConfiguration p_159026_, Random p_159027_, double p_159028_, float p_159029_, float p_159030_) {
      float f = 1.0F - Mth.abs(0.5F - p_159030_ / p_159029_) * 2.0F;
      float f1 = p_159026_.shape.verticalRadiusDefaultFactor + p_159026_.shape.verticalRadiusCenterFactor * f;
      return (double)f1 * p_159028_ * (double)Mth.randomBetween(p_159027_, 0.75F, 1.0F);
   }

   private boolean shouldSkip(CarvingContext p_159074_, float[] p_159075_, double p_159076_, double p_159077_, double p_159078_, int p_159079_) {
      int i = p_159079_ - p_159074_.getMinGenY();
      return (p_159076_ * p_159076_ + p_159078_ * p_159078_) * (double)p_159075_[i - 1] + p_159077_ * p_159077_ / 6.0D >= 1.0D;
   }
}