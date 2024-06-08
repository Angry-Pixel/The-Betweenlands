package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class BlendingData {
   private static final double BLENDING_DENSITY_FACTOR = 0.1D;
   protected static final LevelHeightAccessor AREA_WITH_OLD_GENERATION = new LevelHeightAccessor() {
      public int getHeight() {
         return 256;
      }

      public int getMinBuildHeight() {
         return 0;
      }
   };
   protected static final int CELL_WIDTH = 4;
   protected static final int CELL_HEIGHT = 8;
   protected static final int CELL_RATIO = 2;
   private static final int CELLS_PER_SECTION_Y = 2;
   private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
   private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
   private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
   private static final int CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
   private static final int CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
   private static final int CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
   private static final int CELL_HORIZONTAL_FLOOR_COUNT = QUARTS_PER_SECTION + 1;
   private static final List<Block> SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
   protected static final double NO_VALUE = Double.MAX_VALUE;
   private final boolean oldNoise;
   private boolean hasCalculatedData;
   private final double[] heights;
   private final List<Holder<Biome>> biomes;
   private final transient double[][] densities;
   private final transient double[] floorDensities;
   private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
   public static final Codec<BlendingData> CODEC = RecordCodecBuilder.<BlendingData>create((p_190309_) -> {
      return p_190309_.group(Codec.BOOL.fieldOf("old_noise").forGetter(BlendingData::oldNoise), DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter((p_190346_) -> {
         return DoubleStream.of(p_190346_.heights).anyMatch((p_190279_) -> {
            return p_190279_ != Double.MAX_VALUE;
         }) ? Optional.of(p_190346_.heights) : Optional.empty();
      })).apply(p_190309_, BlendingData::new);
   }).comapFlatMap(BlendingData::validateArraySize, Function.identity());

   private static DataResult<BlendingData> validateArraySize(BlendingData p_190321_) {
      return p_190321_.heights.length != CELL_COLUMN_COUNT ? DataResult.error("heights has to be of length " + CELL_COLUMN_COUNT) : DataResult.success(p_190321_);
   }

   private BlendingData(boolean p_190275_, Optional<double[]> p_190276_) {
      this.oldNoise = p_190275_;
      this.heights = p_190276_.orElse(Util.make(new double[CELL_COLUMN_COUNT], (p_190323_) -> {
         Arrays.fill(p_190323_, Double.MAX_VALUE);
      }));
      this.densities = new double[CELL_COLUMN_COUNT][];
      this.floorDensities = new double[CELL_HORIZONTAL_FLOOR_COUNT * CELL_HORIZONTAL_FLOOR_COUNT];
      ObjectArrayList<Holder<Biome>> objectarraylist = new ObjectArrayList<>(CELL_COLUMN_COUNT);
      objectarraylist.size(CELL_COLUMN_COUNT);
      this.biomes = objectarraylist;
   }

   public boolean oldNoise() {
      return this.oldNoise;
   }

   @Nullable
   public static BlendingData getOrUpdateBlendingData(WorldGenRegion p_190305_, int p_190306_, int p_190307_) {
      ChunkAccess chunkaccess = p_190305_.getChunk(p_190306_, p_190307_);
      BlendingData blendingdata = chunkaccess.getBlendingData();
      if (blendingdata != null && blendingdata.oldNoise()) {
         blendingdata.calculateData(chunkaccess, sideByGenerationAge(p_190305_, p_190306_, p_190307_, false));
         return blendingdata;
      } else {
         return null;
      }
   }

   public static Set<Direction8> sideByGenerationAge(WorldGenLevel p_197066_, int p_197067_, int p_197068_, boolean p_197069_) {
      Set<Direction8> set = EnumSet.noneOf(Direction8.class);

      for(Direction8 direction8 : Direction8.values()) {
         int i = p_197067_;
         int j = p_197068_;

         for(Direction direction : direction8.getDirections()) {
            i += direction.getStepX();
            j += direction.getStepZ();
         }

         if (p_197066_.getChunk(i, j).isOldNoiseGeneration() == p_197069_) {
            set.add(direction8);
         }
      }

      return set;
   }

   private void calculateData(ChunkAccess p_190318_, Set<Direction8> p_190319_) {
      if (!this.hasCalculatedData) {
         Arrays.fill(this.floorDensities, 1.0D);
         if (p_190319_.contains(Direction8.NORTH) || p_190319_.contains(Direction8.WEST) || p_190319_.contains(Direction8.NORTH_WEST)) {
            this.addValuesForColumn(getInsideIndex(0, 0), p_190318_, 0, 0);
         }

         if (p_190319_.contains(Direction8.NORTH)) {
            for(int i = 1; i < QUARTS_PER_SECTION; ++i) {
               this.addValuesForColumn(getInsideIndex(i, 0), p_190318_, 4 * i, 0);
            }
         }

         if (p_190319_.contains(Direction8.WEST)) {
            for(int j = 1; j < QUARTS_PER_SECTION; ++j) {
               this.addValuesForColumn(getInsideIndex(0, j), p_190318_, 0, 4 * j);
            }
         }

         if (p_190319_.contains(Direction8.EAST)) {
            for(int k = 1; k < QUARTS_PER_SECTION; ++k) {
               this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, k), p_190318_, 15, 4 * k);
            }
         }

         if (p_190319_.contains(Direction8.SOUTH)) {
            for(int l = 0; l < QUARTS_PER_SECTION; ++l) {
               this.addValuesForColumn(getOutsideIndex(l, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), p_190318_, 4 * l, 15);
            }
         }

         if (p_190319_.contains(Direction8.EAST) && p_190319_.contains(Direction8.NORTH_EAST)) {
            this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), p_190318_, 15, 0);
         }

         if (p_190319_.contains(Direction8.EAST) && p_190319_.contains(Direction8.SOUTH) && p_190319_.contains(Direction8.SOUTH_EAST)) {
            this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), p_190318_, 15, 15);
         }

         this.hasCalculatedData = true;
      }
   }

   private void addValuesForColumn(int p_190300_, ChunkAccess p_190301_, int p_190302_, int p_190303_) {
      if (this.heights[p_190300_] == Double.MAX_VALUE) {
         this.heights[p_190300_] = (double)getHeightAtXZ(p_190301_, p_190302_, p_190303_);
      }

      this.densities[p_190300_] = getDensityColumn(p_190301_, p_190302_, p_190303_, Mth.floor(this.heights[p_190300_]));
      this.biomes.set(p_190300_, p_190301_.getNoiseBiome(QuartPos.fromBlock(p_190302_), QuartPos.fromBlock(Mth.floor(this.heights[p_190300_])), QuartPos.fromBlock(p_190303_)));
   }

   private static int getHeightAtXZ(ChunkAccess p_190311_, int p_190312_, int p_190313_) {
      int i;
      if (p_190311_.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
         i = Math.min(p_190311_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, p_190312_, p_190313_) + 1, AREA_WITH_OLD_GENERATION.getMaxBuildHeight());
      } else {
         i = AREA_WITH_OLD_GENERATION.getMaxBuildHeight();
      }

      int j = AREA_WITH_OLD_GENERATION.getMinBuildHeight();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_190312_, i, p_190313_);

      while(blockpos$mutableblockpos.getY() > j) {
         blockpos$mutableblockpos.move(Direction.DOWN);
         if (SURFACE_BLOCKS.contains(p_190311_.getBlockState(blockpos$mutableblockpos).getBlock())) {
            return blockpos$mutableblockpos.getY();
         }
      }

      return j;
   }

   private static double read1(ChunkAccess p_198298_, BlockPos.MutableBlockPos p_198299_) {
      return isGround(p_198298_, p_198299_.move(Direction.DOWN)) ? 1.0D : -1.0D;
   }

   private static double read7(ChunkAccess p_198301_, BlockPos.MutableBlockPos p_198302_) {
      double d0 = 0.0D;

      for(int i = 0; i < 7; ++i) {
         d0 += read1(p_198301_, p_198302_);
      }

      return d0;
   }

   private static double[] getDensityColumn(ChunkAccess p_198293_, int p_198294_, int p_198295_, int p_198296_) {
      double[] adouble = new double[cellCountPerColumn()];
      Arrays.fill(adouble, -1.0D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_198294_, AREA_WITH_OLD_GENERATION.getMaxBuildHeight(), p_198295_);
      double d0 = read7(p_198293_, blockpos$mutableblockpos);

      for(int i = adouble.length - 2; i >= 0; --i) {
         double d1 = read1(p_198293_, blockpos$mutableblockpos);
         double d2 = read7(p_198293_, blockpos$mutableblockpos);
         adouble[i] = (d0 + d1 + d2) / 15.0D;
         d0 = d2;
      }

      int j = Mth.intFloorDiv(p_198296_, 8);
      if (j >= 1 && j < adouble.length) {
         double d4 = ((double)p_198296_ + 0.5D) % 8.0D / 8.0D;
         double d5 = (1.0D - d4) / d4;
         double d3 = Math.max(d5, 1.0D) * 0.25D;
         adouble[j] = -d5 / d3;
         adouble[j - 1] = 1.0D / d3;
      }

      return adouble;
   }

   private static boolean isGround(ChunkAccess p_190315_, BlockPos p_190316_) {
      BlockState blockstate = p_190315_.getBlockState(p_190316_);
      if (blockstate.isAir()) {
         return false;
      } else if (blockstate.is(BlockTags.LEAVES)) {
         return false;
      } else if (blockstate.is(BlockTags.LOGS)) {
         return false;
      } else if (!blockstate.is(Blocks.BROWN_MUSHROOM_BLOCK) && !blockstate.is(Blocks.RED_MUSHROOM_BLOCK)) {
         return !blockstate.getCollisionShape(p_190315_, p_190316_).isEmpty();
      } else {
         return false;
      }
   }

   protected double getHeight(int p_190286_, int p_190287_, int p_190288_) {
      if (p_190286_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && p_190288_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return p_190286_ != 0 && p_190288_ != 0 ? Double.MAX_VALUE : this.heights[getInsideIndex(p_190286_, p_190288_)];
      } else {
         return this.heights[getOutsideIndex(p_190286_, p_190288_)];
      }
   }

   private static double getDensity(@Nullable double[] p_190325_, int p_190326_) {
      if (p_190325_ == null) {
         return Double.MAX_VALUE;
      } else {
         int i = p_190326_ - getColumnMinY();
         return i >= 0 && i < p_190325_.length ? p_190325_[i] * 0.1D : Double.MAX_VALUE;
      }
   }

   protected double getDensity(int p_190334_, int p_190335_, int p_190336_) {
      if (p_190335_ == getMinY()) {
         return this.floorDensities[this.getFloorIndex(p_190334_, p_190336_)] * 0.1D;
      } else if (p_190334_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && p_190336_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return p_190334_ != 0 && p_190336_ != 0 ? Double.MAX_VALUE : getDensity(this.densities[getInsideIndex(p_190334_, p_190336_)], p_190335_);
      } else {
         return getDensity(this.densities[getOutsideIndex(p_190334_, p_190336_)], p_190335_);
      }
   }

   protected void iterateBiomes(int p_202278_, int p_202279_, BlendingData.BiomeConsumer p_202280_) {
      for(int i = 0; i < this.biomes.size(); ++i) {
         Holder<Biome> holder = this.biomes.get(i);
         if (holder != null) {
            p_202280_.consume(p_202278_ + getX(i), p_202279_ + getZ(i), holder);
         }
      }

   }

   protected void iterateHeights(int p_190296_, int p_190297_, BlendingData.HeightConsumer p_190298_) {
      for(int i = 0; i < this.heights.length; ++i) {
         double d0 = this.heights[i];
         if (d0 != Double.MAX_VALUE) {
            p_190298_.consume(p_190296_ + getX(i), p_190297_ + getZ(i), d0);
         }
      }

   }

   protected void iterateDensities(int p_190290_, int p_190291_, int p_190292_, int p_190293_, BlendingData.DensityConsumer p_190294_) {
      int i = getColumnMinY();
      int j = Math.max(0, p_190292_ - i);
      int k = Math.min(cellCountPerColumn(), p_190293_ - i);

      for(int l = 0; l < this.densities.length; ++l) {
         double[] adouble = this.densities[l];
         if (adouble != null) {
            int i1 = p_190290_ + getX(l);
            int j1 = p_190291_ + getZ(l);

            for(int k1 = j; k1 < k; ++k1) {
               p_190294_.consume(i1, k1 + i, j1, adouble[k1] * 0.1D);
            }
         }
      }

   }

   private int getFloorIndex(int p_190283_, int p_190284_) {
      return p_190283_ * CELL_HORIZONTAL_FLOOR_COUNT + p_190284_;
   }

   private static int cellCountPerColumn() {
      return AREA_WITH_OLD_GENERATION.getSectionsCount() * 2;
   }

   private static int getColumnMinY() {
      return getMinY() + 1;
   }

   private static int getMinY() {
      return AREA_WITH_OLD_GENERATION.getMinSection() * 2;
   }

   private static int getInsideIndex(int p_190331_, int p_190332_) {
      return CELL_HORIZONTAL_MAX_INDEX_INSIDE - p_190331_ + p_190332_;
   }

   private static int getOutsideIndex(int p_190351_, int p_190352_) {
      return CELL_COLUMN_INSIDE_COUNT + p_190351_ + CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - p_190352_;
   }

   private static int getX(int p_190349_) {
      if (p_190349_ < CELL_COLUMN_INSIDE_COUNT) {
         return zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_INSIDE - p_190349_);
      } else {
         int i = p_190349_ - CELL_COLUMN_INSIDE_COUNT;
         return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - i);
      }
   }

   private static int getZ(int p_190355_) {
      if (p_190355_ < CELL_COLUMN_INSIDE_COUNT) {
         return zeroIfNegative(p_190355_ - CELL_HORIZONTAL_MAX_INDEX_INSIDE);
      } else {
         int i = p_190355_ - CELL_COLUMN_INSIDE_COUNT;
         return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(i - CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
      }
   }

   private static int zeroIfNegative(int p_190357_) {
      return p_190357_ & ~(p_190357_ >> 31);
   }

   protected interface BiomeConsumer {
      void consume(int p_204674_, int p_204675_, Holder<Biome> p_204676_);
   }

   protected interface DensityConsumer {
      void consume(int p_190362_, int p_190363_, int p_190364_, double p_190365_);
   }

   protected interface HeightConsumer {
      void consume(int p_190367_, int p_190368_, double p_190369_);
   }
}