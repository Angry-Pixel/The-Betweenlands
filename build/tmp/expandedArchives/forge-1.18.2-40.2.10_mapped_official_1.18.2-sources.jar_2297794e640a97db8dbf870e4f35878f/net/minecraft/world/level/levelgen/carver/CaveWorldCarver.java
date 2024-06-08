package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class CaveWorldCarver extends WorldCarver<CaveCarverConfiguration> {
   public CaveWorldCarver(Codec<CaveCarverConfiguration> p_159194_) {
      super(p_159194_);
   }

   public boolean isStartChunk(CaveCarverConfiguration p_159263_, Random p_159264_) {
      return p_159264_.nextFloat() <= p_159263_.probability;
   }

   public boolean carve(CarvingContext p_190704_, CaveCarverConfiguration p_190705_, ChunkAccess p_190706_, Function<BlockPos, Holder<Biome>> p_190707_, Random p_190708_, Aquifer p_190709_, ChunkPos p_190710_, CarvingMask p_190711_) {
      int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
      int j = p_190708_.nextInt(p_190708_.nextInt(p_190708_.nextInt(this.getCaveBound()) + 1) + 1);

      for(int k = 0; k < j; ++k) {
         double d0 = (double)p_190710_.getBlockX(p_190708_.nextInt(16));
         double d1 = (double)p_190705_.y.sample(p_190708_, p_190704_);
         double d2 = (double)p_190710_.getBlockZ(p_190708_.nextInt(16));
         double d3 = (double)p_190705_.horizontalRadiusMultiplier.sample(p_190708_);
         double d4 = (double)p_190705_.verticalRadiusMultiplier.sample(p_190708_);
         double d5 = (double)p_190705_.floorLevel.sample(p_190708_);
         WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
            return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
         };
         int l = 1;
         if (p_190708_.nextInt(4) == 0) {
            double d6 = (double)p_190705_.yScale.sample(p_190708_);
            float f1 = 1.0F + p_190708_.nextFloat() * 6.0F;
            this.createRoom(p_190704_, p_190705_, p_190706_, p_190707_, p_190709_, d0, d1, d2, f1, d6, p_190711_, worldcarver$carveskipchecker);
            l += p_190708_.nextInt(4);
         }

         for(int k1 = 0; k1 < l; ++k1) {
            float f = p_190708_.nextFloat() * ((float)Math.PI * 2F);
            float f3 = (p_190708_.nextFloat() - 0.5F) / 4.0F;
            float f2 = this.getThickness(p_190708_);
            int i1 = i - p_190708_.nextInt(i / 4);
            int j1 = 0;
            this.createTunnel(p_190704_, p_190705_, p_190706_, p_190707_, p_190708_.nextLong(), p_190709_, d0, d1, d2, d3, d4, f2, f, f3, 0, i1, this.getYScale(), p_190711_, worldcarver$carveskipchecker);
         }
      }

      return true;
   }

   protected int getCaveBound() {
      return 15;
   }

   protected float getThickness(Random p_64834_) {
      float f = p_64834_.nextFloat() * 2.0F + p_64834_.nextFloat();
      if (p_64834_.nextInt(10) == 0) {
         f *= p_64834_.nextFloat() * p_64834_.nextFloat() * 3.0F + 1.0F;
      }

      return f;
   }

   protected double getYScale() {
      return 1.0D;
   }

   protected void createRoom(CarvingContext p_190691_, CaveCarverConfiguration p_190692_, ChunkAccess p_190693_, Function<BlockPos, Holder<Biome>> p_190694_, Aquifer p_190695_, double p_190696_, double p_190697_, double p_190698_, float p_190699_, double p_190700_, CarvingMask p_190701_, WorldCarver.CarveSkipChecker p_190702_) {
      double d0 = 1.5D + (double)(Mth.sin(((float)Math.PI / 2F)) * p_190699_);
      double d1 = d0 * p_190700_;
      this.carveEllipsoid(p_190691_, p_190692_, p_190693_, p_190694_, p_190695_, p_190696_ + 1.0D, p_190697_, p_190698_, d0, d1, p_190701_, p_190702_);
   }

   protected void createTunnel(CarvingContext p_190671_, CaveCarverConfiguration p_190672_, ChunkAccess p_190673_, Function<BlockPos, Holder<Biome>> p_190674_, long p_190675_, Aquifer p_190676_, double p_190677_, double p_190678_, double p_190679_, double p_190680_, double p_190681_, float p_190682_, float p_190683_, float p_190684_, int p_190685_, int p_190686_, double p_190687_, CarvingMask p_190688_, WorldCarver.CarveSkipChecker p_190689_) {
      Random random = new Random(p_190675_);
      int i = random.nextInt(p_190686_ / 2) + p_190686_ / 4;
      boolean flag = random.nextInt(6) == 0;
      float f = 0.0F;
      float f1 = 0.0F;

      for(int j = p_190685_; j < p_190686_; ++j) {
         double d0 = 1.5D + (double)(Mth.sin((float)Math.PI * (float)j / (float)p_190686_) * p_190682_);
         double d1 = d0 * p_190687_;
         float f2 = Mth.cos(p_190684_);
         p_190677_ += (double)(Mth.cos(p_190683_) * f2);
         p_190678_ += (double)Mth.sin(p_190684_);
         p_190679_ += (double)(Mth.sin(p_190683_) * f2);
         p_190684_ *= flag ? 0.92F : 0.7F;
         p_190684_ += f1 * 0.1F;
         p_190683_ += f * 0.1F;
         f1 *= 0.9F;
         f *= 0.75F;
         f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (j == i && p_190682_ > 1.0F) {
            this.createTunnel(p_190671_, p_190672_, p_190673_, p_190674_, random.nextLong(), p_190676_, p_190677_, p_190678_, p_190679_, p_190680_, p_190681_, random.nextFloat() * 0.5F + 0.5F, p_190683_ - ((float)Math.PI / 2F), p_190684_ / 3.0F, j, p_190686_, 1.0D, p_190688_, p_190689_);
            this.createTunnel(p_190671_, p_190672_, p_190673_, p_190674_, random.nextLong(), p_190676_, p_190677_, p_190678_, p_190679_, p_190680_, p_190681_, random.nextFloat() * 0.5F + 0.5F, p_190683_ + ((float)Math.PI / 2F), p_190684_ / 3.0F, j, p_190686_, 1.0D, p_190688_, p_190689_);
            return;
         }

         if (random.nextInt(4) != 0) {
            if (!canReach(p_190673_.getPos(), p_190677_, p_190679_, j, p_190686_, p_190682_)) {
               return;
            }

            this.carveEllipsoid(p_190671_, p_190672_, p_190673_, p_190674_, p_190676_, p_190677_, p_190678_, p_190679_, d0 * p_190680_, d1 * p_190681_, p_190688_, p_190689_);
         }
      }

   }

   private static boolean shouldSkip(double p_159196_, double p_159197_, double p_159198_, double p_159199_) {
      if (p_159197_ <= p_159199_) {
         return true;
      } else {
         return p_159196_ * p_159196_ + p_159197_ * p_159197_ + p_159198_ * p_159198_ >= 1.0D;
      }
   }
}