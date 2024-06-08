package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;

public class NoiseChunk implements DensityFunction.ContextProvider, DensityFunction.FunctionContext {
   private final NoiseSettings noiseSettings;
   final int cellCountXZ;
   final int cellCountY;
   final int cellNoiseMinY;
   private final int firstCellX;
   private final int firstCellZ;
   final int firstNoiseX;
   final int firstNoiseZ;
   final List<NoiseChunk.NoiseInterpolator> interpolators;
   final List<NoiseChunk.CacheAllInCell> cellCaches;
   private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();
   private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
   private final Aquifer aquifer;
   private final DensityFunction initialDensityNoJaggedness;
   private final NoiseChunk.BlockStateFiller blockStateRule;
   private final Blender blender;
   private final NoiseChunk.FlatCache blendAlpha;
   private final NoiseChunk.FlatCache blendOffset;
   private final DensityFunctions.BeardifierOrMarker beardifier;
   private long lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
   private Blender.BlendingOutput lastBlendingOutput = new Blender.BlendingOutput(1.0D, 0.0D);
   final int noiseSizeXZ;
   final int cellWidth;
   final int cellHeight;
   boolean interpolating;
   boolean fillingCell;
   private int cellStartBlockX;
   int cellStartBlockY;
   private int cellStartBlockZ;
   int inCellX;
   int inCellY;
   int inCellZ;
   long interpolationCounter;
   long arrayInterpolationCounter;
   int arrayIndex;
   private final DensityFunction.ContextProvider sliceFillingContextProvider = new DensityFunction.ContextProvider() {
      public DensityFunction.FunctionContext forIndex(int p_209253_) {
         NoiseChunk.this.cellStartBlockY = (p_209253_ + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
         ++NoiseChunk.this.interpolationCounter;
         NoiseChunk.this.inCellY = 0;
         NoiseChunk.this.arrayIndex = p_209253_;
         return NoiseChunk.this;
      }

      public void fillAllDirectly(double[] p_209255_, DensityFunction p_209256_) {
         for(int i2 = 0; i2 < NoiseChunk.this.cellCountY + 1; ++i2) {
            NoiseChunk.this.cellStartBlockY = (i2 + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
            ++NoiseChunk.this.interpolationCounter;
            NoiseChunk.this.inCellY = 0;
            NoiseChunk.this.arrayIndex = i2;
            p_209255_[i2] = p_209256_.compute(NoiseChunk.this);
         }

      }
   };

   public static NoiseChunk forChunk(ChunkAccess p_209207_, NoiseRouter p_209208_, Supplier<DensityFunctions.BeardifierOrMarker> p_209209_, NoiseGeneratorSettings p_209210_, Aquifer.FluidPicker p_209211_, Blender p_209212_) {
      ChunkPos chunkpos = p_209207_.getPos();
      NoiseSettings noisesettings = p_209210_.noiseSettings();
      int i = Math.max(noisesettings.minY(), p_209207_.getMinBuildHeight());
      int j = Math.min(noisesettings.minY() + noisesettings.height(), p_209207_.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, noisesettings.getCellHeight());
      int l = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
      return new NoiseChunk(16 / noisesettings.getCellWidth(), l, k, p_209208_, chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), p_209209_.get(), p_209210_, p_209211_, p_209212_);
   }

   public static NoiseChunk forColumn(int p_209195_, int p_209196_, int p_209197_, int p_209198_, NoiseRouter p_209199_, NoiseGeneratorSettings p_209200_, Aquifer.FluidPicker p_209201_) {
      return new NoiseChunk(1, p_209198_, p_209197_, p_209199_, p_209195_, p_209196_, DensityFunctions.BeardifierMarker.INSTANCE, p_209200_, p_209201_, Blender.empty());
   }

   private NoiseChunk(int p_209175_, int p_209176_, int p_209177_, NoiseRouter p_209178_, int p_209179_, int p_209180_, DensityFunctions.BeardifierOrMarker p_209181_, NoiseGeneratorSettings p_209182_, Aquifer.FluidPicker p_209183_, Blender p_209184_) {
      this.noiseSettings = p_209182_.noiseSettings();
      this.cellCountXZ = p_209175_;
      this.cellCountY = p_209176_;
      this.cellNoiseMinY = p_209177_;
      this.cellWidth = this.noiseSettings.getCellWidth();
      this.cellHeight = this.noiseSettings.getCellHeight();
      this.firstCellX = Math.floorDiv(p_209179_, this.cellWidth);
      this.firstCellZ = Math.floorDiv(p_209180_, this.cellWidth);
      this.interpolators = Lists.newArrayList();
      this.cellCaches = Lists.newArrayList();
      this.firstNoiseX = QuartPos.fromBlock(p_209179_);
      this.firstNoiseZ = QuartPos.fromBlock(p_209180_);
      this.noiseSizeXZ = QuartPos.fromBlock(p_209175_ * this.cellWidth);
      this.blender = p_209184_;
      this.beardifier = p_209181_;
      this.blendAlpha = new NoiseChunk.FlatCache(new NoiseChunk.BlendAlpha(), false);
      this.blendOffset = new NoiseChunk.FlatCache(new NoiseChunk.BlendOffset(), false);

      for(int i = 0; i <= this.noiseSizeXZ; ++i) {
         int j = this.firstNoiseX + i;
         int k = QuartPos.toBlock(j);

         for(int l = 0; l <= this.noiseSizeXZ; ++l) {
            int i1 = this.firstNoiseZ + l;
            int j1 = QuartPos.toBlock(i1);
            Blender.BlendingOutput blender$blendingoutput = p_209184_.blendOffsetAndFactor(k, j1);
            this.blendAlpha.values[i][l] = blender$blendingoutput.alpha();
            this.blendOffset.values[i][l] = blender$blendingoutput.blendingOffset();
         }
      }

      if (!p_209182_.isAquifersEnabled()) {
         this.aquifer = Aquifer.createDisabled(p_209183_);
      } else {
         int k1 = SectionPos.blockToSectionCoord(p_209179_);
         int l1 = SectionPos.blockToSectionCoord(p_209180_);
         this.aquifer = Aquifer.create(this, new ChunkPos(k1, l1), p_209178_.barrierNoise(), p_209178_.fluidLevelFloodednessNoise(), p_209178_.fluidLevelSpreadNoise(), p_209178_.lavaNoise(), p_209178_.aquiferPositionalRandomFactory(), p_209177_ * this.cellHeight, p_209176_ * this.cellHeight, p_209183_);
      }

      Builder<NoiseChunk.BlockStateFiller> builder = ImmutableList.builder();
      DensityFunction densityfunction = DensityFunctions.cacheAllInCell(DensityFunctions.add(p_209178_.finalDensity(), DensityFunctions.BeardifierMarker.INSTANCE)).mapAll(this::wrap);
      builder.add((p_209217_) -> {
         return this.aquifer.computeSubstance(p_209217_, densityfunction.compute(p_209217_));
      });
      if (p_209182_.oreVeinsEnabled()) {
         builder.add(OreVeinifier.create(p_209178_.veinToggle().mapAll(this::wrap), p_209178_.veinRidged().mapAll(this::wrap), p_209178_.veinGap().mapAll(this::wrap), p_209178_.oreVeinsPositionalRandomFactory()));
      }

      this.blockStateRule = new MaterialRuleList(builder.build());
      this.initialDensityNoJaggedness = p_209178_.initialDensityWithoutJaggedness().mapAll(this::wrap);
   }

   protected Climate.Sampler cachedClimateSampler(NoiseRouter p_209219_) {
      return new Climate.Sampler(p_209219_.temperature().mapAll(this::wrap), p_209219_.humidity().mapAll(this::wrap), p_209219_.continents().mapAll(this::wrap), p_209219_.erosion().mapAll(this::wrap), p_209219_.depth().mapAll(this::wrap), p_209219_.ridges().mapAll(this::wrap), p_209219_.spawnTarget());
   }

   @Nullable
   protected BlockState getInterpolatedState() {
      return this.blockStateRule.calculate(this);
   }

   public int blockX() {
      return this.cellStartBlockX + this.inCellX;
   }

   public int blockY() {
      return this.cellStartBlockY + this.inCellY;
   }

   public int blockZ() {
      return this.cellStartBlockZ + this.inCellZ;
   }

   public int preliminarySurfaceLevel(int p_198257_, int p_198258_) {
      return this.preliminarySurfaceLevel.computeIfAbsent(ChunkPos.asLong(QuartPos.fromBlock(p_198257_), QuartPos.fromBlock(p_198258_)), this::computePreliminarySurfaceLevel);
   }

   private int computePreliminarySurfaceLevel(long p_198250_) {
      int i = ChunkPos.getX(p_198250_);
      int j = ChunkPos.getZ(p_198250_);
      return (int)NoiseRouterData.computePreliminarySurfaceLevelScanning(this.noiseSettings, this.initialDensityNoJaggedness, QuartPos.toBlock(i), QuartPos.toBlock(j));
   }

   public Blender getBlender() {
      return this.blender;
   }

   private void fillSlice(boolean p_209221_, int p_209222_) {
      this.cellStartBlockX = p_209222_ * this.cellWidth;
      this.inCellX = 0;

      for(int i = 0; i < this.cellCountXZ + 1; ++i) {
         int j = this.firstCellZ + i;
         this.cellStartBlockZ = j * this.cellWidth;
         this.inCellZ = 0;
         ++this.arrayInterpolationCounter;

         for(NoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
            double[] adouble = (p_209221_ ? noisechunk$noiseinterpolator.slice0 : noisechunk$noiseinterpolator.slice1)[i];
            noisechunk$noiseinterpolator.fillArray(adouble, this.sliceFillingContextProvider);
         }
      }

      ++this.arrayInterpolationCounter;
   }

   public void initializeForFirstCellX() {
      if (this.interpolating) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.interpolating = true;
         this.interpolationCounter = 0L;
         this.fillSlice(true, this.firstCellX);
      }
   }

   public void advanceCellX(int p_188750_) {
      this.fillSlice(false, this.firstCellX + p_188750_ + 1);
      this.cellStartBlockX = (this.firstCellX + p_188750_) * this.cellWidth;
   }

   public NoiseChunk forIndex(int p_209240_) {
      int i = Math.floorMod(p_209240_, this.cellWidth);
      int j = Math.floorDiv(p_209240_, this.cellWidth);
      int k = Math.floorMod(j, this.cellWidth);
      int l = this.cellHeight - 1 - Math.floorDiv(j, this.cellWidth);
      this.inCellX = k;
      this.inCellY = l;
      this.inCellZ = i;
      this.arrayIndex = p_209240_;
      return this;
   }

   public void fillAllDirectly(double[] p_209224_, DensityFunction p_209225_) {
      this.arrayIndex = 0;

      for(int i = this.cellHeight - 1; i >= 0; --i) {
         this.inCellY = i;

         for(int j = 0; j < this.cellWidth; ++j) {
            this.inCellX = j;

            for(int k = 0; k < this.cellWidth; ++k) {
               this.inCellZ = k;
               p_209224_[this.arrayIndex++] = p_209225_.compute(this);
            }
         }
      }

   }

   public void selectCellYZ(int p_188811_, int p_188812_) {
      this.interpolators.forEach((p_209205_) -> {
         p_209205_.selectCellYZ(p_188811_, p_188812_);
      });
      this.fillingCell = true;
      this.cellStartBlockY = (p_188811_ + this.cellNoiseMinY) * this.cellHeight;
      this.cellStartBlockZ = (this.firstCellZ + p_188812_) * this.cellWidth;
      ++this.arrayInterpolationCounter;

      for(NoiseChunk.CacheAllInCell noisechunk$cacheallincell : this.cellCaches) {
         noisechunk$cacheallincell.noiseFiller.fillArray(noisechunk$cacheallincell.values, this);
      }

      ++this.arrayInterpolationCounter;
      this.fillingCell = false;
   }

   public void updateForY(int p_209192_, double p_209193_) {
      this.inCellY = p_209192_ - this.cellStartBlockY;
      this.interpolators.forEach((p_209238_) -> {
         p_209238_.updateForY(p_209193_);
      });
   }

   public void updateForX(int p_209231_, double p_209232_) {
      this.inCellX = p_209231_ - this.cellStartBlockX;
      this.interpolators.forEach((p_209229_) -> {
         p_209229_.updateForX(p_209232_);
      });
   }

   public void updateForZ(int p_209242_, double p_209243_) {
      this.inCellZ = p_209242_ - this.cellStartBlockZ;
      ++this.interpolationCounter;
      this.interpolators.forEach((p_209188_) -> {
         p_209188_.updateForZ(p_209243_);
      });
   }

   public void stopInterpolation() {
      if (!this.interpolating) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.interpolating = false;
      }
   }

   public void swapSlices() {
      this.interpolators.forEach(NoiseChunk.NoiseInterpolator::swapSlices);
   }

   public Aquifer aquifer() {
      return this.aquifer;
   }

   Blender.BlendingOutput getOrComputeBlendingOutput(int p_209245_, int p_209246_) {
      long i = ChunkPos.asLong(p_209245_, p_209246_);
      if (this.lastBlendingDataPos == i) {
         return this.lastBlendingOutput;
      } else {
         this.lastBlendingDataPos = i;
         Blender.BlendingOutput blender$blendingoutput = this.blender.blendOffsetAndFactor(p_209245_, p_209246_);
         this.lastBlendingOutput = blender$blendingoutput;
         return blender$blendingoutput;
      }
   }

   protected DensityFunction wrap(DensityFunction p_209214_) {
      return this.wrapped.computeIfAbsent(p_209214_, this::wrapNew);
   }

   private DensityFunction wrapNew(DensityFunction p_209234_) {
      if (p_209234_ instanceof DensityFunctions.Marker) {
         DensityFunctions.Marker densityfunctions$marker = (DensityFunctions.Marker)p_209234_;
         Object object;
         switch(densityfunctions$marker.type()) {
         case Interpolated:
            object = new NoiseChunk.NoiseInterpolator(densityfunctions$marker.wrapped());
            break;
         case FlatCache:
            object = new NoiseChunk.FlatCache(densityfunctions$marker.wrapped(), true);
            break;
         case Cache2D:
            object = new NoiseChunk.Cache2D(densityfunctions$marker.wrapped());
            break;
         case CacheOnce:
            object = new NoiseChunk.CacheOnce(densityfunctions$marker.wrapped());
            break;
         case CacheAllInCell:
            object = new NoiseChunk.CacheAllInCell(densityfunctions$marker.wrapped());
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return (DensityFunction)object;
      } else {
         if (this.blender != Blender.empty()) {
            if (p_209234_ == DensityFunctions.BlendAlpha.INSTANCE) {
               return this.blendAlpha;
            }

            if (p_209234_ == DensityFunctions.BlendOffset.INSTANCE) {
               return this.blendOffset;
            }
         }

         if (p_209234_ == DensityFunctions.BeardifierMarker.INSTANCE) {
            return this.beardifier;
         } else if (p_209234_ instanceof DensityFunctions.HolderHolder) {
            DensityFunctions.HolderHolder densityfunctions$holderholder = (DensityFunctions.HolderHolder)p_209234_;
            return densityfunctions$holderholder.function().value();
         } else {
            return p_209234_;
         }
      }
   }

   class BlendAlpha implements NoiseChunk.NoiseChunkDensityFunction {
      public DensityFunction wrapped() {
         return DensityFunctions.BlendAlpha.INSTANCE;
      }

      public double compute(DensityFunction.FunctionContext p_209264_) {
         return NoiseChunk.this.getOrComputeBlendingOutput(p_209264_.blockX(), p_209264_.blockZ()).alpha();
      }

      public void fillArray(double[] p_209266_, DensityFunction.ContextProvider p_209267_) {
         p_209267_.fillAllDirectly(p_209266_, this);
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return 1.0D;
      }

      public Codec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendAlpha.CODEC;
      }
   }

   class BlendOffset implements NoiseChunk.NoiseChunkDensityFunction {
      public DensityFunction wrapped() {
         return DensityFunctions.BlendOffset.INSTANCE;
      }

      public double compute(DensityFunction.FunctionContext p_209276_) {
         return NoiseChunk.this.getOrComputeBlendingOutput(p_209276_.blockX(), p_209276_.blockZ()).blendingOffset();
      }

      public void fillArray(double[] p_209278_, DensityFunction.ContextProvider p_209279_) {
         p_209279_.fillAllDirectly(p_209278_, this);
      }

      public double minValue() {
         return Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return Double.POSITIVE_INFINITY;
      }

      public Codec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendOffset.CODEC;
      }
   }

   @FunctionalInterface
   public interface BlockStateFiller {
      @Nullable
      BlockState calculate(DensityFunction.FunctionContext p_209283_);
   }

   static class Cache2D implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction function;
      private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
      private double lastValue;

      Cache2D(DensityFunction p_209288_) {
         this.function = p_209288_;
      }

      public double compute(DensityFunction.FunctionContext p_209290_) {
         int i = p_209290_.blockX();
         int j = p_209290_.blockZ();
         long k = ChunkPos.asLong(i, j);
         if (this.lastPos2D == k) {
            return this.lastValue;
         } else {
            this.lastPos2D = k;
            double d0 = this.function.compute(p_209290_);
            this.lastValue = d0;
            return d0;
         }
      }

      public void fillArray(double[] p_209292_, DensityFunction.ContextProvider p_209293_) {
         this.function.fillArray(p_209292_, p_209293_);
      }

      public DensityFunction wrapped() {
         return this.function;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Cache2D;
      }
   }

   class CacheAllInCell implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      final DensityFunction noiseFiller;
      final double[] values;

      CacheAllInCell(DensityFunction p_209301_) {
         this.noiseFiller = p_209301_;
         this.values = new double[NoiseChunk.this.cellWidth * NoiseChunk.this.cellWidth * NoiseChunk.this.cellHeight];
         NoiseChunk.this.cellCaches.add(this);
      }

      public double compute(DensityFunction.FunctionContext p_209303_) {
         if (p_209303_ != NoiseChunk.this) {
            return this.noiseFiller.compute(p_209303_);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            int i = NoiseChunk.this.inCellX;
            int j = NoiseChunk.this.inCellY;
            int k = NoiseChunk.this.inCellZ;
            return i >= 0 && j >= 0 && k >= 0 && i < NoiseChunk.this.cellWidth && j < NoiseChunk.this.cellHeight && k < NoiseChunk.this.cellWidth ? this.values[((NoiseChunk.this.cellHeight - 1 - j) * NoiseChunk.this.cellWidth + i) * NoiseChunk.this.cellWidth + k] : this.noiseFiller.compute(p_209303_);
         }
      }

      public void fillArray(double[] p_209305_, DensityFunction.ContextProvider p_209306_) {
         p_209306_.fillAllDirectly(p_209305_, this);
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheAllInCell;
      }
   }

   class CacheOnce implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction function;
      private long lastCounter;
      private long lastArrayCounter;
      private double lastValue;
      @Nullable
      private double[] lastArray;

      CacheOnce(DensityFunction p_209317_) {
         this.function = p_209317_;
      }

      public double compute(DensityFunction.FunctionContext p_209319_) {
         if (p_209319_ != NoiseChunk.this) {
            return this.function.compute(p_209319_);
         } else if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            return this.lastArray[NoiseChunk.this.arrayIndex];
         } else if (this.lastCounter == NoiseChunk.this.interpolationCounter) {
            return this.lastValue;
         } else {
            this.lastCounter = NoiseChunk.this.interpolationCounter;
            double d0 = this.function.compute(p_209319_);
            this.lastValue = d0;
            return d0;
         }
      }

      public void fillArray(double[] p_209321_, DensityFunction.ContextProvider p_209322_) {
         if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            System.arraycopy(this.lastArray, 0, p_209321_, 0, p_209321_.length);
         } else {
            this.wrapped().fillArray(p_209321_, p_209322_);
            if (this.lastArray != null && this.lastArray.length == p_209321_.length) {
               System.arraycopy(p_209321_, 0, this.lastArray, 0, p_209321_.length);
            } else {
               this.lastArray = (double[])p_209321_.clone();
            }

            this.lastArrayCounter = NoiseChunk.this.arrayInterpolationCounter;
         }
      }

      public DensityFunction wrapped() {
         return this.function;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheOnce;
      }
   }

   class FlatCache implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction noiseFiller;
      final double[][] values;

      FlatCache(DensityFunction p_209330_, boolean p_209331_) {
         this.noiseFiller = p_209330_;
         this.values = new double[NoiseChunk.this.noiseSizeXZ + 1][NoiseChunk.this.noiseSizeXZ + 1];
         if (p_209331_) {
            for(int i = 0; i <= NoiseChunk.this.noiseSizeXZ; ++i) {
               int j = NoiseChunk.this.firstNoiseX + i;
               int k = QuartPos.toBlock(j);

               for(int l = 0; l <= NoiseChunk.this.noiseSizeXZ; ++l) {
                  int i1 = NoiseChunk.this.firstNoiseZ + l;
                  int j1 = QuartPos.toBlock(i1);
                  this.values[i][l] = p_209330_.compute(new DensityFunction.SinglePointContext(k, 0, j1));
               }
            }
         }

      }

      public double compute(DensityFunction.FunctionContext p_209333_) {
         int i = QuartPos.fromBlock(p_209333_.blockX());
         int j = QuartPos.fromBlock(p_209333_.blockZ());
         int k = i - NoiseChunk.this.firstNoiseX;
         int l = j - NoiseChunk.this.firstNoiseZ;
         int i1 = this.values.length;
         return k >= 0 && l >= 0 && k < i1 && l < i1 ? this.values[k][l] : this.noiseFiller.compute(p_209333_);
      }

      public void fillArray(double[] p_209335_, DensityFunction.ContextProvider p_209336_) {
         p_209336_.fillAllDirectly(p_209335_, this);
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.FlatCache;
      }
   }

   interface NoiseChunkDensityFunction extends DensityFunction {
      DensityFunction wrapped();

      default DensityFunction mapAll(DensityFunction.Visitor p_209341_) {
         return this.wrapped().mapAll(p_209341_);
      }

      default double minValue() {
         return this.wrapped().minValue();
      }

      default double maxValue() {
         return this.wrapped().maxValue();
      }
   }

   public class NoiseInterpolator implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      double[][] slice0;
      double[][] slice1;
      private final DensityFunction noiseFiller;
      private double noise000;
      private double noise001;
      private double noise100;
      private double noise101;
      private double noise010;
      private double noise011;
      private double noise110;
      private double noise111;
      private double valueXZ00;
      private double valueXZ10;
      private double valueXZ01;
      private double valueXZ11;
      private double valueZ0;
      private double valueZ1;
      private double value;

      NoiseInterpolator(DensityFunction p_209345_) {
         this.noiseFiller = p_209345_;
         this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         NoiseChunk.this.interpolators.add(this);
      }

      private double[][] allocateSlice(int p_188855_, int p_188856_) {
         int i = p_188856_ + 1;
         int j = p_188855_ + 1;
         double[][] adouble = new double[i][j];

         for(int k = 0; k < i; ++k) {
            adouble[k] = new double[j];
         }

         return adouble;
      }

      void selectCellYZ(int p_188864_, int p_188865_) {
         this.noise000 = this.slice0[p_188865_][p_188864_];
         this.noise001 = this.slice0[p_188865_ + 1][p_188864_];
         this.noise100 = this.slice1[p_188865_][p_188864_];
         this.noise101 = this.slice1[p_188865_ + 1][p_188864_];
         this.noise010 = this.slice0[p_188865_][p_188864_ + 1];
         this.noise011 = this.slice0[p_188865_ + 1][p_188864_ + 1];
         this.noise110 = this.slice1[p_188865_][p_188864_ + 1];
         this.noise111 = this.slice1[p_188865_ + 1][p_188864_ + 1];
      }

      void updateForY(double p_188851_) {
         this.valueXZ00 = Mth.lerp(p_188851_, this.noise000, this.noise010);
         this.valueXZ10 = Mth.lerp(p_188851_, this.noise100, this.noise110);
         this.valueXZ01 = Mth.lerp(p_188851_, this.noise001, this.noise011);
         this.valueXZ11 = Mth.lerp(p_188851_, this.noise101, this.noise111);
      }

      void updateForX(double p_188862_) {
         this.valueZ0 = Mth.lerp(p_188862_, this.valueXZ00, this.valueXZ10);
         this.valueZ1 = Mth.lerp(p_188862_, this.valueXZ01, this.valueXZ11);
      }

      void updateForZ(double p_188867_) {
         this.value = Mth.lerp(p_188867_, this.valueZ0, this.valueZ1);
      }

      public double compute(DensityFunction.FunctionContext p_209347_) {
         if (p_209347_ != NoiseChunk.this) {
            return this.noiseFiller.compute(p_209347_);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            return NoiseChunk.this.fillingCell ? Mth.lerp3((double)NoiseChunk.this.inCellX / (double)NoiseChunk.this.cellWidth, (double)NoiseChunk.this.inCellY / (double)NoiseChunk.this.cellHeight, (double)NoiseChunk.this.inCellZ / (double)NoiseChunk.this.cellWidth, this.noise000, this.noise100, this.noise010, this.noise110, this.noise001, this.noise101, this.noise011, this.noise111) : this.value;
         }
      }

      public void fillArray(double[] p_209349_, DensityFunction.ContextProvider p_209350_) {
         if (NoiseChunk.this.fillingCell) {
            p_209350_.fillAllDirectly(p_209349_, this);
         } else {
            this.wrapped().fillArray(p_209349_, p_209350_);
         }
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      private void swapSlices() {
         double[][] adouble = this.slice0;
         this.slice0 = this.slice1;
         this.slice1 = adouble;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Interpolated;
      }
   }
}