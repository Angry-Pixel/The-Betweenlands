package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
   private static final Blender EMPTY = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
      public Blender.BlendingOutput blendOffsetAndFactor(int p_209724_, int p_209725_) {
         return new Blender.BlendingOutput(1.0D, 0.0D);
      }

      public double blendDensity(DensityFunction.FunctionContext p_209727_, double p_209728_) {
         return p_209728_;
      }

      public BiomeResolver getBiomeResolver(BiomeResolver p_190232_) {
         return p_190232_;
      }
   };
   private static final NormalNoise SHIFT_NOISE = NormalNoise.create(new XoroshiroRandomSource(42L), BuiltinRegistries.NOISE.getOrThrow(Noises.SHIFT));
   private static final int HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
   private static final int HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(HEIGHT_BLENDING_RANGE_CELLS + 3);
   private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
   private static final int DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
   private static final double OLD_CHUNK_Y_RADIUS = (double)BlendingData.AREA_WITH_OLD_GENERATION.getHeight() / 2.0D;
   private static final double OLD_CHUNK_CENTER_Y = (double)BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight() + OLD_CHUNK_Y_RADIUS;
   private static final double OLD_CHUNK_XZ_RADIUS = 8.0D;
   private final Long2ObjectOpenHashMap<BlendingData> blendingData;
   private final Long2ObjectOpenHashMap<BlendingData> blendingDataForDensityBlending;

   public static Blender empty() {
      return EMPTY;
   }

   public static Blender of(@Nullable WorldGenRegion p_190203_) {
      if (p_190203_ == null) {
         return EMPTY;
      } else {
         Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap = new Long2ObjectOpenHashMap<>();
         Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap1 = new Long2ObjectOpenHashMap<>();
         ChunkPos chunkpos = p_190203_.getCenter();

         for(int i = -HEIGHT_BLENDING_RANGE_CHUNKS; i <= HEIGHT_BLENDING_RANGE_CHUNKS; ++i) {
            for(int j = -HEIGHT_BLENDING_RANGE_CHUNKS; j <= HEIGHT_BLENDING_RANGE_CHUNKS; ++j) {
               int k = chunkpos.x + i;
               int l = chunkpos.z + j;
               BlendingData blendingdata = BlendingData.getOrUpdateBlendingData(p_190203_, k, l);
               if (blendingdata != null) {
                  long2objectopenhashmap.put(ChunkPos.asLong(k, l), blendingdata);
                  if (i >= -DENSITY_BLENDING_RANGE_CHUNKS && i <= DENSITY_BLENDING_RANGE_CHUNKS && j >= -DENSITY_BLENDING_RANGE_CHUNKS && j <= DENSITY_BLENDING_RANGE_CHUNKS) {
                     long2objectopenhashmap1.put(ChunkPos.asLong(k, l), blendingdata);
                  }
               }
            }
         }

         return long2objectopenhashmap.isEmpty() && long2objectopenhashmap1.isEmpty() ? EMPTY : new Blender(long2objectopenhashmap, long2objectopenhashmap1);
      }
   }

   Blender(Long2ObjectOpenHashMap<BlendingData> p_202197_, Long2ObjectOpenHashMap<BlendingData> p_202198_) {
      this.blendingData = p_202197_;
      this.blendingDataForDensityBlending = p_202198_;
   }

   public Blender.BlendingOutput blendOffsetAndFactor(int p_209719_, int p_209720_) {
      int i = QuartPos.fromBlock(p_209719_);
      int j = QuartPos.fromBlock(p_209720_);
      double d0 = this.getBlendingDataValue(i, 0, j, BlendingData::getHeight);
      if (d0 != Double.MAX_VALUE) {
         return new Blender.BlendingOutput(0.0D, heightToOffset(d0));
      } else {
         MutableDouble mutabledouble = new MutableDouble(0.0D);
         MutableDouble mutabledouble1 = new MutableDouble(0.0D);
         MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);
         this.blendingData.forEach((p_202249_, p_202250_) -> {
            p_202250_.iterateHeights(QuartPos.fromSection(ChunkPos.getX(p_202249_)), QuartPos.fromSection(ChunkPos.getZ(p_202249_)), (p_190199_, p_190200_, p_190201_) -> {
               double d3 = Mth.length((double)(i - p_190199_), (double)(j - p_190200_));
               if (!(d3 > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
                  if (d3 < mutabledouble2.doubleValue()) {
                     mutabledouble2.setValue(d3);
                  }

                  double d4 = 1.0D / (d3 * d3 * d3 * d3);
                  mutabledouble1.add(p_190201_ * d4);
                  mutabledouble.add(d4);
               }
            });
         });
         if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
            return new Blender.BlendingOutput(1.0D, 0.0D);
         } else {
            double d1 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
            double d2 = Mth.clamp(mutabledouble2.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);
            d2 = 3.0D * d2 * d2 - 2.0D * d2 * d2 * d2;
            return new Blender.BlendingOutput(d2, heightToOffset(d1));
         }
      }
   }

   private static double heightToOffset(double p_190155_) {
      double d0 = 1.0D;
      double d1 = p_190155_ + 0.5D;
      double d2 = Mth.positiveModulo(d1, 8.0D);
      return 1.0D * (32.0D * (d1 - 128.0D) - 3.0D * (d1 - 120.0D) * d2 + 3.0D * d2 * d2) / (128.0D * (32.0D - 3.0D * d2));
   }

   public double blendDensity(DensityFunction.FunctionContext p_209721_, double p_209722_) {
      int i = QuartPos.fromBlock(p_209721_.blockX());
      int j = p_209721_.blockY() / 8;
      int k = QuartPos.fromBlock(p_209721_.blockZ());
      double d0 = this.getBlendingDataValue(i, j, k, BlendingData::getDensity);
      if (d0 != Double.MAX_VALUE) {
         return d0;
      } else {
         MutableDouble mutabledouble = new MutableDouble(0.0D);
         MutableDouble mutabledouble1 = new MutableDouble(0.0D);
         MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);
         this.blendingDataForDensityBlending.forEach((p_202241_, p_202242_) -> {
            p_202242_.iterateDensities(QuartPos.fromSection(ChunkPos.getX(p_202241_)), QuartPos.fromSection(ChunkPos.getZ(p_202241_)), j - 1, j + 1, (p_202230_, p_202231_, p_202232_, p_202233_) -> {
               double d3 = Mth.length((double)(i - p_202230_), (double)((j - p_202231_) * 2), (double)(k - p_202232_));
               if (!(d3 > 2.0D)) {
                  if (d3 < mutabledouble2.doubleValue()) {
                     mutabledouble2.setValue(d3);
                  }

                  double d4 = 1.0D / (d3 * d3 * d3 * d3);
                  mutabledouble1.add(p_202233_ * d4);
                  mutabledouble.add(d4);
               }
            });
         });
         if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
            return p_209722_;
         } else {
            double d1 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
            double d2 = Mth.clamp(mutabledouble2.doubleValue() / 3.0D, 0.0D, 1.0D);
            return Mth.lerp(d2, d1, p_209722_);
         }
      }
   }

   private double getBlendingDataValue(int p_190175_, int p_190176_, int p_190177_, Blender.CellValueGetter p_190178_) {
      int i = QuartPos.toSection(p_190175_);
      int j = QuartPos.toSection(p_190177_);
      boolean flag = (p_190175_ & 3) == 0;
      boolean flag1 = (p_190177_ & 3) == 0;
      double d0 = this.getBlendingDataValue(p_190178_, i, j, p_190175_, p_190176_, p_190177_);
      if (d0 == Double.MAX_VALUE) {
         if (flag && flag1) {
            d0 = this.getBlendingDataValue(p_190178_, i - 1, j - 1, p_190175_, p_190176_, p_190177_);
         }

         if (d0 == Double.MAX_VALUE) {
            if (flag) {
               d0 = this.getBlendingDataValue(p_190178_, i - 1, j, p_190175_, p_190176_, p_190177_);
            }

            if (d0 == Double.MAX_VALUE && flag1) {
               d0 = this.getBlendingDataValue(p_190178_, i, j - 1, p_190175_, p_190176_, p_190177_);
            }
         }
      }

      return d0;
   }

   private double getBlendingDataValue(Blender.CellValueGetter p_190212_, int p_190213_, int p_190214_, int p_190215_, int p_190216_, int p_190217_) {
      BlendingData blendingdata = this.blendingData.get(ChunkPos.asLong(p_190213_, p_190214_));
      return blendingdata != null ? p_190212_.get(blendingdata, p_190215_ - QuartPos.fromSection(p_190213_), p_190216_, p_190217_ - QuartPos.fromSection(p_190214_)) : Double.MAX_VALUE;
   }

   public BiomeResolver getBiomeResolver(BiomeResolver p_190204_) {
      return (p_204669_, p_204670_, p_204671_, p_204672_) -> {
         Holder<Biome> holder = this.blendBiome(p_204669_, p_204671_);
         return holder == null ? p_190204_.getNoiseBiome(p_204669_, p_204670_, p_204671_, p_204672_) : holder;
      };
   }

   @Nullable
   private Holder<Biome> blendBiome(int p_204665_, int p_204666_) {
      double d0 = (double)p_204665_ + SHIFT_NOISE.getValue((double)p_204665_, 0.0D, (double)p_204666_) * 12.0D;
      double d1 = (double)p_204666_ + SHIFT_NOISE.getValue((double)p_204666_, (double)p_204665_, 0.0D) * 12.0D;
      MutableDouble mutabledouble = new MutableDouble(Double.POSITIVE_INFINITY);
      MutableObject<Holder<Biome>> mutableobject = new MutableObject<>();
      this.blendingData.forEach((p_202218_, p_202219_) -> {
         p_202219_.iterateBiomes(QuartPos.fromSection(ChunkPos.getX(p_202218_)), QuartPos.fromSection(ChunkPos.getZ(p_202218_)), (p_204661_, p_204662_, p_204663_) -> {
            double d3 = Mth.length(d0 - (double)p_204661_, d1 - (double)p_204662_);
            if (!(d3 > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
               if (d3 < mutabledouble.doubleValue()) {
                  mutableobject.setValue(p_204663_);
                  mutabledouble.setValue(d3);
               }

            }
         });
      });
      if (mutabledouble.doubleValue() == Double.POSITIVE_INFINITY) {
         return null;
      } else {
         double d2 = Mth.clamp(mutabledouble.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);
         return d2 > 0.5D ? null : mutableobject.getValue();
      }
   }

   public static void generateBorderTicks(WorldGenRegion p_197032_, ChunkAccess p_197033_) {
      ChunkPos chunkpos = p_197033_.getPos();
      boolean flag = p_197033_.isOldNoiseGeneration();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), 0, chunkpos.getMinBlockZ());
      int i = BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight();
      int j = BlendingData.AREA_WITH_OLD_GENERATION.getMaxBuildHeight() - 1;
      if (flag) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               generateBorderTick(p_197033_, blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l));
               generateBorderTick(p_197033_, blockpos$mutableblockpos.setWithOffset(blockpos, k, i, l));
               generateBorderTick(p_197033_, blockpos$mutableblockpos.setWithOffset(blockpos, k, j, l));
               generateBorderTick(p_197033_, blockpos$mutableblockpos.setWithOffset(blockpos, k, j + 1, l));
            }
         }
      }

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_197032_.getChunk(chunkpos.x + direction.getStepX(), chunkpos.z + direction.getStepZ()).isOldNoiseGeneration() != flag) {
            int i1 = direction == Direction.EAST ? 15 : 0;
            int j1 = direction == Direction.WEST ? 0 : 15;
            int k1 = direction == Direction.SOUTH ? 15 : 0;
            int l1 = direction == Direction.NORTH ? 0 : 15;

            for(int i2 = i1; i2 <= j1; ++i2) {
               for(int j2 = k1; j2 <= l1; ++j2) {
                  int k2 = Math.min(j, p_197033_.getHeight(Heightmap.Types.MOTION_BLOCKING, i2, j2)) + 1;

                  for(int l2 = i; l2 < k2; ++l2) {
                     generateBorderTick(p_197033_, blockpos$mutableblockpos.setWithOffset(blockpos, i2, l2, j2));
                  }
               }
            }
         }
      }

   }

   private static void generateBorderTick(ChunkAccess p_197041_, BlockPos p_197042_) {
      BlockState blockstate = p_197041_.getBlockState(p_197042_);
      if (blockstate.is(BlockTags.LEAVES)) {
         p_197041_.markPosForPostprocessing(p_197042_);
      }

      FluidState fluidstate = p_197041_.getFluidState(p_197042_);
      if (!fluidstate.isEmpty()) {
         p_197041_.markPosForPostprocessing(p_197042_);
      }

   }

   public static void addAroundOldChunksCarvingMaskFilter(WorldGenLevel p_197035_, ProtoChunk p_197036_) {
      ChunkPos chunkpos = p_197036_.getPos();
      Blender.DistanceGetter blender$distancegetter = makeOldChunkDistanceGetter(p_197036_.isOldNoiseGeneration(), BlendingData.sideByGenerationAge(p_197035_, chunkpos.x, chunkpos.z, true));
      if (blender$distancegetter != null) {
         CarvingMask.Mask carvingmask$mask = (p_202262_, p_202263_, p_202264_) -> {
            double d0 = (double)p_202262_ + 0.5D + SHIFT_NOISE.getValue((double)p_202262_, (double)p_202263_, (double)p_202264_) * 4.0D;
            double d1 = (double)p_202263_ + 0.5D + SHIFT_NOISE.getValue((double)p_202263_, (double)p_202264_, (double)p_202262_) * 4.0D;
            double d2 = (double)p_202264_ + 0.5D + SHIFT_NOISE.getValue((double)p_202264_, (double)p_202262_, (double)p_202263_) * 4.0D;
            return blender$distancegetter.getDistance(d0, d1, d2) < 4.0D;
         };
         Stream.of(GenerationStep.Carving.values()).map(p_197036_::getOrCreateCarvingMask).forEach((p_202259_) -> {
            p_202259_.setAdditionalMask(carvingmask$mask);
         });
      }
   }

   @Nullable
   public static Blender.DistanceGetter makeOldChunkDistanceGetter(boolean p_197059_, Set<Direction8> p_197060_) {
      if (!p_197059_ && p_197060_.isEmpty()) {
         return null;
      } else {
         List<Blender.DistanceGetter> list = Lists.newArrayList();
         if (p_197059_) {
            list.add(makeOffsetOldChunkDistanceGetter((Direction8)null));
         }

         p_197060_.forEach((p_202272_) -> {
            list.add(makeOffsetOldChunkDistanceGetter(p_202272_));
         });
         return (p_202267_, p_202268_, p_202269_) -> {
            double d0 = Double.POSITIVE_INFINITY;

            for(Blender.DistanceGetter blender$distancegetter : list) {
               double d1 = blender$distancegetter.getDistance(p_202267_, p_202268_, p_202269_);
               if (d1 < d0) {
                  d0 = d1;
               }
            }

            return d0;
         };
      }
   }

   private static Blender.DistanceGetter makeOffsetOldChunkDistanceGetter(@Nullable Direction8 p_197049_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      if (p_197049_ != null) {
         for(Direction direction : p_197049_.getDirections()) {
            d0 += (double)(direction.getStepX() * 16);
            d1 += (double)(direction.getStepZ() * 16);
         }
      }

      double d3 = d0;
      double d2 = d1;
      return (p_202202_, p_202203_, p_202204_) -> {
         return distanceToCube(p_202202_ - 8.0D - d3, p_202203_ - OLD_CHUNK_CENTER_Y, p_202204_ - 8.0D - d2, 8.0D, OLD_CHUNK_Y_RADIUS, 8.0D);
      };
   }

   private static double distanceToCube(double p_197025_, double p_197026_, double p_197027_, double p_197028_, double p_197029_, double p_197030_) {
      double d0 = Math.abs(p_197025_) - p_197028_;
      double d1 = Math.abs(p_197026_) - p_197029_;
      double d2 = Math.abs(p_197027_) - p_197030_;
      return Mth.length(Math.max(0.0D, d0), Math.max(0.0D, d1), Math.max(0.0D, d2));
   }

   public static record BlendingOutput(double alpha, double blendingOffset) {
   }

   interface CellValueGetter {
      double get(BlendingData p_190234_, int p_190235_, int p_190236_, int p_190237_);
   }

   public interface DistanceGetter {
      double getDistance(double p_197062_, double p_197063_, double p_197064_);
   }
}