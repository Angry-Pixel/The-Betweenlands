package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
   private static final Codec<DensityFunction> CODEC = Registry.DENSITY_FUNCTION_TYPES.byNameCodec().dispatch(DensityFunction::codec, Function.identity());
   protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0D;
   static final Codec<Double> NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0D, 1000000.0D);
   public static final Codec<DensityFunction> DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap((p_208274_) -> {
      return p_208274_.map(DensityFunctions::constant, Function.identity());
   }, (p_208392_) -> {
      if (p_208392_ instanceof DensityFunctions.Constant) {
         DensityFunctions.Constant densityfunctions$constant = (DensityFunctions.Constant)p_208392_;
         return Either.left(densityfunctions$constant.value());
      } else {
         return Either.right(p_208392_);
      }
   });

   public static Codec<? extends DensityFunction> bootstrap(Registry<Codec<? extends DensityFunction>> p_208343_) {
      register(p_208343_, "blend_alpha", DensityFunctions.BlendAlpha.CODEC);
      register(p_208343_, "blend_offset", DensityFunctions.BlendOffset.CODEC);
      register(p_208343_, "beardifier", DensityFunctions.BeardifierMarker.CODEC);
      register(p_208343_, "old_blended_noise", BlendedNoise.CODEC);

      for(DensityFunctions.Marker.Type densityfunctions$marker$type : DensityFunctions.Marker.Type.values()) {
         register(p_208343_, densityfunctions$marker$type.getSerializedName(), densityfunctions$marker$type.codec);
      }

      register(p_208343_, "noise", DensityFunctions.Noise.CODEC);
      register(p_208343_, "end_islands", DensityFunctions.EndIslandDensityFunction.CODEC);
      register(p_208343_, "weird_scaled_sampler", DensityFunctions.WeirdScaledSampler.CODEC);
      register(p_208343_, "shifted_noise", DensityFunctions.ShiftedNoise.CODEC);
      register(p_208343_, "range_choice", DensityFunctions.RangeChoice.CODEC);
      register(p_208343_, "shift_a", DensityFunctions.ShiftA.CODEC);
      register(p_208343_, "shift_b", DensityFunctions.ShiftB.CODEC);
      register(p_208343_, "shift", DensityFunctions.Shift.CODEC);
      register(p_208343_, "blend_density", DensityFunctions.BlendDensity.CODEC);
      register(p_208343_, "clamp", DensityFunctions.Clamp.CODEC);

      for(DensityFunctions.Mapped.Type densityfunctions$mapped$type : DensityFunctions.Mapped.Type.values()) {
         register(p_208343_, densityfunctions$mapped$type.getSerializedName(), densityfunctions$mapped$type.codec);
      }

      register(p_208343_, "slide", DensityFunctions.Slide.CODEC);

      for(DensityFunctions.TwoArgumentSimpleFunction.Type densityfunctions$twoargumentsimplefunction$type : DensityFunctions.TwoArgumentSimpleFunction.Type.values()) {
         register(p_208343_, densityfunctions$twoargumentsimplefunction$type.getSerializedName(), densityfunctions$twoargumentsimplefunction$type.codec);
      }

      register(p_208343_, "spline", DensityFunctions.Spline.CODEC);
      register(p_208343_, "terrain_shaper_spline", DensityFunctions.TerrainShaperSpline.CODEC);
      register(p_208343_, "constant", DensityFunctions.Constant.CODEC);
      return register(p_208343_, "y_clamped_gradient", DensityFunctions.YClampedGradient.CODEC);
   }

   private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> p_208345_, String p_208346_, Codec<? extends DensityFunction> p_208347_) {
      return Registry.register(p_208345_, p_208346_, p_208347_);
   }

   static <A, O> Codec<O> singleArgumentCodec(Codec<A> p_208276_, Function<A, O> p_208277_, Function<O, A> p_208278_) {
      return p_208276_.fieldOf("argument").xmap(p_208277_, p_208278_).codec();
   }

   static <O> Codec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> p_208353_, Function<O, DensityFunction> p_208354_) {
      return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, p_208353_, p_208354_);
   }

   static <O> Codec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> p_208349_, Function<O, DensityFunction> p_208350_, Function<O, DensityFunction> p_208351_) {
      return RecordCodecBuilder.create((p_208359_) -> {
         return p_208359_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(p_208350_), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(p_208351_)).apply(p_208359_, p_208349_);
      });
   }

   static <O> Codec<O> makeCodec(MapCodec<O> p_208280_) {
      return p_208280_.codec();
   }

   private DensityFunctions() {
   }

   public static DensityFunction interpolated(DensityFunction p_208282_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Interpolated, p_208282_);
   }

   public static DensityFunction flatCache(DensityFunction p_208362_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.FlatCache, p_208362_);
   }

   public static DensityFunction cache2d(DensityFunction p_208374_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Cache2D, p_208374_);
   }

   public static DensityFunction cacheOnce(DensityFunction p_208381_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheOnce, p_208381_);
   }

   public static DensityFunction cacheAllInCell(DensityFunction p_208388_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheAllInCell, p_208388_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208337_, @Deprecated double p_208338_, double p_208339_, double p_208340_, double p_208341_) {
      return mapFromUnitTo(new DensityFunctions.Noise(p_208337_, (NormalNoise)null, p_208338_, p_208339_), p_208340_, p_208341_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208332_, double p_208333_, double p_208334_, double p_208335_) {
      return mappedNoise(p_208332_, 1.0D, p_208333_, p_208334_, p_208335_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208328_, double p_208329_, double p_208330_) {
      return mappedNoise(p_208328_, 1.0D, 1.0D, p_208329_, p_208330_);
   }

   public static DensityFunction shiftedNoise2d(DensityFunction p_208297_, DensityFunction p_208298_, double p_208299_, Holder<NormalNoise.NoiseParameters> p_208300_) {
      return new DensityFunctions.ShiftedNoise(p_208297_, zero(), p_208298_, p_208299_, 0.0D, p_208300_, (NormalNoise)null);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208323_) {
      return noise(p_208323_, 1.0D, 1.0D);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208369_, double p_208370_, double p_208371_) {
      return new DensityFunctions.Noise(p_208369_, (NormalNoise)null, p_208370_, p_208371_);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208325_, double p_208326_) {
      return noise(p_208325_, 1.0D, p_208326_);
   }

   public static DensityFunction rangeChoice(DensityFunction p_208288_, double p_208289_, double p_208290_, DensityFunction p_208291_, DensityFunction p_208292_) {
      return new DensityFunctions.RangeChoice(p_208288_, p_208289_, p_208290_, p_208291_, p_208292_);
   }

   public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> p_208367_) {
      return new DensityFunctions.ShiftA(p_208367_, (NormalNoise)null);
   }

   public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> p_208379_) {
      return new DensityFunctions.ShiftB(p_208379_, (NormalNoise)null);
   }

   public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> p_208386_) {
      return new DensityFunctions.Shift(p_208386_, (NormalNoise)null);
   }

   public static DensityFunction blendDensity(DensityFunction p_208390_) {
      return new DensityFunctions.BlendDensity(p_208390_);
   }

   public static DensityFunction endIslands(long p_208272_) {
      return new DensityFunctions.EndIslandDensityFunction(p_208272_);
   }

   public static DensityFunction weirdScaledSampler(DensityFunction p_208316_, Holder<NormalNoise.NoiseParameters> p_208317_, DensityFunctions.WeirdScaledSampler.RarityValueMapper p_208318_) {
      return new DensityFunctions.WeirdScaledSampler(p_208316_, p_208317_, (NormalNoise)null, p_208318_);
   }

   public static DensityFunction slide(NoiseSettings p_208320_, DensityFunction p_208321_) {
      return new DensityFunctions.Slide(p_208320_, p_208321_);
   }

   public static DensityFunction add(DensityFunction p_208294_, DensityFunction p_208295_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.ADD, p_208294_, p_208295_);
   }

   public static DensityFunction mul(DensityFunction p_208364_, DensityFunction p_208365_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MUL, p_208364_, p_208365_);
   }

   public static DensityFunction min(DensityFunction p_208376_, DensityFunction p_208377_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MIN, p_208376_, p_208377_);
   }

   public static DensityFunction max(DensityFunction p_208383_, DensityFunction p_208384_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MAX, p_208383_, p_208384_);
   }

   public static DensityFunction terrainShaperSpline(DensityFunction p_208306_, DensityFunction p_208307_, DensityFunction p_208308_, DensityFunctions.TerrainShaperSpline.SplineType p_208309_, double p_208310_, double p_208311_) {
      return new DensityFunctions.TerrainShaperSpline(p_208306_, p_208307_, p_208308_, (TerrainShaper)null, p_208309_, p_208310_, p_208311_);
   }

   public static DensityFunction zero() {
      return DensityFunctions.Constant.ZERO;
   }

   public static DensityFunction constant(double p_208265_) {
      return new DensityFunctions.Constant(p_208265_);
   }

   public static DensityFunction yClampedGradient(int p_208267_, int p_208268_, double p_208269_, double p_208270_) {
      return new DensityFunctions.YClampedGradient(p_208267_, p_208268_, p_208269_, p_208270_);
   }

   public static DensityFunction map(DensityFunction p_208313_, DensityFunctions.Mapped.Type p_208314_) {
      return DensityFunctions.Mapped.create(p_208314_, p_208313_);
   }

   private static DensityFunction mapFromUnitTo(DensityFunction p_208284_, double p_208285_, double p_208286_) {
      double d0 = (p_208285_ + p_208286_) * 0.5D;
      double d1 = (p_208286_ - p_208285_) * 0.5D;
      return add(constant(d0), mul(constant(d1), p_208284_));
   }

   public static DensityFunction blendAlpha() {
      return DensityFunctions.BlendAlpha.INSTANCE;
   }

   public static DensityFunction blendOffset() {
      return DensityFunctions.BlendOffset.INSTANCE;
   }

   public static DensityFunction lerp(DensityFunction p_208302_, DensityFunction p_208303_, DensityFunction p_208304_) {
      DensityFunction densityfunction = cacheOnce(p_208302_);
      DensityFunction densityfunction1 = add(mul(densityfunction, constant(-1.0D)), constant(1.0D));
      return add(mul(p_208303_, densityfunction1), mul(p_208304_, densityfunction));
   }

   static record Ap2(DensityFunctions.TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements DensityFunctions.TwoArgumentSimpleFunction {
      public double compute(DensityFunction.FunctionContext p_208410_) {
         double d0 = this.argument1.compute(p_208410_);
         double d1;
         switch(this.type) {
         case ADD:
            d1 = d0 + this.argument2.compute(p_208410_);
            break;
         case MAX:
            d1 = d0 > this.argument2.maxValue() ? d0 : Math.max(d0, this.argument2.compute(p_208410_));
            break;
         case MIN:
            d1 = d0 < this.argument2.minValue() ? d0 : Math.min(d0, this.argument2.compute(p_208410_));
            break;
         case MUL:
            d1 = d0 == 0.0D ? 0.0D : d0 * this.argument2.compute(p_208410_);
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return d1;
      }

      public void fillArray(double[] p_208414_, DensityFunction.ContextProvider p_208415_) {
         this.argument1.fillArray(p_208414_, p_208415_);
         switch(this.type) {
         case ADD:
            double[] adouble = new double[p_208414_.length];
            this.argument2.fillArray(adouble, p_208415_);

            for(int k = 0; k < p_208414_.length; ++k) {
               p_208414_[k] += adouble[k];
            }
            break;
         case MAX:
            double d3 = this.argument2.maxValue();

            for(int l = 0; l < p_208414_.length; ++l) {
               double d4 = p_208414_[l];
               p_208414_[l] = d4 > d3 ? d4 : Math.max(d4, this.argument2.compute(p_208415_.forIndex(l)));
            }
            break;
         case MIN:
            double d2 = this.argument2.minValue();

            for(int j = 0; j < p_208414_.length; ++j) {
               double d1 = p_208414_[j];
               p_208414_[j] = d1 < d2 ? d1 : Math.min(d1, this.argument2.compute(p_208415_.forIndex(j)));
            }
            break;
         case MUL:
            for(int i = 0; i < p_208414_.length; ++i) {
               double d0 = p_208414_[i];
               p_208414_[i] = d0 == 0.0D ? 0.0D : d0 * this.argument2.compute(p_208415_.forIndex(i));
            }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208412_) {
         return p_208412_.apply(DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(p_208412_), this.argument2.mapAll(p_208412_)));
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      public DensityFunctions.TwoArgumentSimpleFunction.Type type() {
         return this.type;
      }

      public DensityFunction argument1() {
         return this.argument1;
      }

      public DensityFunction argument2() {
         return this.argument2;
      }
   }

   protected static enum BeardifierMarker implements DensityFunctions.BeardifierOrMarker {
      INSTANCE;

      public double compute(DensityFunction.FunctionContext p_208515_) {
         return 0.0D;
      }

      public void fillArray(double[] p_208517_, DensityFunction.ContextProvider p_208518_) {
         Arrays.fill(p_208517_, 0.0D);
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return 0.0D;
      }
   }

   public interface BeardifierOrMarker extends DensityFunction.SimpleFunction {
      Codec<DensityFunction> CODEC = Codec.unit(DensityFunctions.BeardifierMarker.INSTANCE);

      default Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final Codec<DensityFunction> CODEC = Codec.unit(INSTANCE);

      public double compute(DensityFunction.FunctionContext p_208536_) {
         return 1.0D;
      }

      public void fillArray(double[] p_208538_, DensityFunction.ContextProvider p_208539_) {
         Arrays.fill(p_208538_, 1.0D);
      }

      public double minValue() {
         return 1.0D;
      }

      public double maxValue() {
         return 1.0D;
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static record BlendDensity(DensityFunction input) implements DensityFunctions.TransformerWithContext {
      static final Codec<DensityFunctions.BlendDensity> CODEC = DensityFunctions.singleFunctionArgumentCodec(DensityFunctions.BlendDensity::new, DensityFunctions.BlendDensity::input);

      public double transform(DensityFunction.FunctionContext p_208553_, double p_208554_) {
         return p_208553_.getBlender().blendDensity(p_208553_, p_208554_);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208556_) {
         return p_208556_.apply(new DensityFunctions.BlendDensity(this.input.mapAll(p_208556_)));
      }

      public double minValue() {
         return Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return Double.POSITIVE_INFINITY;
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }
   }

   protected static enum BlendOffset implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final Codec<DensityFunction> CODEC = Codec.unit(INSTANCE);

      public double compute(DensityFunction.FunctionContext p_208573_) {
         return 0.0D;
      }

      public void fillArray(double[] p_208575_, DensityFunction.ContextProvider p_208576_) {
         Arrays.fill(p_208575_, 0.0D);
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return 0.0D;
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctions.PureTransformer {
      private static final MapCodec<DensityFunctions.Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208597_) -> {
         return p_208597_.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(DensityFunctions.Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(DensityFunctions.Clamp::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(DensityFunctions.Clamp::maxValue)).apply(p_208597_, DensityFunctions.Clamp::new);
      });
      public static final Codec<DensityFunctions.Clamp> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double transform(double p_208595_) {
         return Mth.clamp(p_208595_, this.minValue, this.maxValue);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208599_) {
         return new DensityFunctions.Clamp(this.input.mapAll(p_208599_), this.minValue, this.maxValue);
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }
   }

   static record Constant(double value) implements DensityFunction.SimpleFunction {
      static final Codec<DensityFunctions.Constant> CODEC = DensityFunctions.singleArgumentCodec(DensityFunctions.NOISE_VALUE_CODEC, DensityFunctions.Constant::new, DensityFunctions.Constant::value);
      static final DensityFunctions.Constant ZERO = new DensityFunctions.Constant(0.0D);

      public double compute(DensityFunction.FunctionContext p_208615_) {
         return this.value;
      }

      public void fillArray(double[] p_208617_, DensityFunction.ContextProvider p_208618_) {
         Arrays.fill(p_208617_, this.value);
      }

      public double minValue() {
         return this.value;
      }

      public double maxValue() {
         return this.value;
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static final class EndIslandDensityFunction implements DensityFunction.SimpleFunction {
      public static final Codec<DensityFunctions.EndIslandDensityFunction> CODEC = Codec.unit(new DensityFunctions.EndIslandDensityFunction(0L));
      final SimplexNoise islandNoise;

      public EndIslandDensityFunction(long p_208630_) {
         RandomSource randomsource = new LegacyRandomSource(p_208630_);
         randomsource.consumeCount(17292);
         this.islandNoise = new SimplexNoise(randomsource);
      }

      public double compute(DensityFunction.FunctionContext p_208633_) {
         return ((double)TheEndBiomeSource.getHeightValue(this.islandNoise, p_208633_.blockX() / 8, p_208633_.blockZ() / 8) - 8.0D) / 128.0D;
      }

      public double minValue() {
         return -0.84375D;
      }

      public double maxValue() {
         return 0.5625D;
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
      public double compute(DensityFunction.FunctionContext p_208641_) {
         return this.function.value().compute(p_208641_);
      }

      public void fillArray(double[] p_208645_, DensityFunction.ContextProvider p_208646_) {
         this.function.value().fillArray(p_208645_, p_208646_);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208643_) {
         return p_208643_.apply(new DensityFunctions.HolderHolder(new Holder.Direct<>(this.function.value().mapAll(p_208643_))));
      }

      public double minValue() {
         return this.function.value().minValue();
      }

      public double maxValue() {
         return this.function.value().maxValue();
      }

      public Codec<? extends DensityFunction> codec() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }
   }

   protected static record Mapped(DensityFunctions.Mapped.Type type, DensityFunction input, double minValue, double maxValue) implements DensityFunctions.PureTransformer {
      public static DensityFunctions.Mapped create(DensityFunctions.Mapped.Type p_208672_, DensityFunction p_208673_) {
         double d0 = p_208673_.minValue();
         double d1 = transform(p_208672_, d0);
         double d2 = transform(p_208672_, p_208673_.maxValue());
         return p_208672_ != DensityFunctions.Mapped.Type.ABS && p_208672_ != DensityFunctions.Mapped.Type.SQUARE ? new DensityFunctions.Mapped(p_208672_, p_208673_, d1, d2) : new DensityFunctions.Mapped(p_208672_, p_208673_, Math.max(0.0D, d0), Math.max(d1, d2));
      }

      private static double transform(DensityFunctions.Mapped.Type p_208669_, double p_208670_) {
         double d1;
         switch(p_208669_) {
         case ABS:
            d1 = Math.abs(p_208670_);
            break;
         case SQUARE:
            d1 = p_208670_ * p_208670_;
            break;
         case CUBE:
            d1 = p_208670_ * p_208670_ * p_208670_;
            break;
         case HALF_NEGATIVE:
            d1 = p_208670_ > 0.0D ? p_208670_ : p_208670_ * 0.5D;
            break;
         case QUARTER_NEGATIVE:
            d1 = p_208670_ > 0.0D ? p_208670_ : p_208670_ * 0.25D;
            break;
         case SQUEEZE:
            double d0 = Mth.clamp(p_208670_, -1.0D, 1.0D);
            d1 = d0 / 2.0D - d0 * d0 * d0 / 24.0D;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return d1;
      }

      public double transform(double p_208665_) {
         return transform(this.type, p_208665_);
      }

      public DensityFunctions.Mapped mapAll(DensityFunction.Visitor p_208677_) {
         return create(this.type, this.input.mapAll(p_208677_));
      }

      public Codec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         SQUEEZE("squeeze");

         private final String name;
         final Codec<DensityFunctions.Mapped> codec = DensityFunctions.singleFunctionArgumentCodec((p_208700_) -> {
            return DensityFunctions.Mapped.create(this, p_208700_);
         }, DensityFunctions.Mapped::input);

         private Type(String p_208697_) {
            this.name = p_208697_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record Marker(DensityFunctions.Marker.Type type, DensityFunction wrapped) implements DensityFunctions.MarkerOrMarked {
      public double compute(DensityFunction.FunctionContext p_208712_) {
         return this.wrapped.compute(p_208712_);
      }

      public void fillArray(double[] p_208716_, DensityFunction.ContextProvider p_208717_) {
         this.wrapped.fillArray(p_208716_, p_208717_);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208714_) {
         return p_208714_.apply(new DensityFunctions.Marker(this.type, this.wrapped.mapAll(p_208714_)));
      }

      public double minValue() {
         return this.wrapped.minValue();
      }

      public double maxValue() {
         return this.wrapped.maxValue();
      }

      public DensityFunctions.Marker.Type type() {
         return this.type;
      }

      public DensityFunction wrapped() {
         return this.wrapped;
      }

      static enum Type implements StringRepresentable {
         Interpolated("interpolated"),
         FlatCache("flat_cache"),
         Cache2D("cache_2d"),
         CacheOnce("cache_once"),
         CacheAllInCell("cache_all_in_cell");

         private final String name;
         final Codec<DensityFunctions.MarkerOrMarked> codec = DensityFunctions.singleFunctionArgumentCodec((p_208740_) -> {
            return new DensityFunctions.Marker(this, p_208740_);
         }, DensityFunctions.MarkerOrMarked::wrapped);

         private Type(String p_208737_) {
            this.name = p_208737_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   public interface MarkerOrMarked extends DensityFunction {
      DensityFunctions.Marker.Type type();

      DensityFunction wrapped();

      default Codec<? extends DensityFunction> codec() {
         return this.type().codec;
      }
   }

   static record MulOrAdd(DensityFunctions.MulOrAdd.Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements DensityFunctions.TwoArgumentSimpleFunction, DensityFunctions.PureTransformer {
      public DensityFunctions.TwoArgumentSimpleFunction.Type type() {
         return this.specificType == DensityFunctions.MulOrAdd.Type.MUL ? DensityFunctions.TwoArgumentSimpleFunction.Type.MUL : DensityFunctions.TwoArgumentSimpleFunction.Type.ADD;
      }

      public DensityFunction argument1() {
         return DensityFunctions.constant(this.argument);
      }

      public DensityFunction argument2() {
         return this.input;
      }

      public double transform(double p_208759_) {
         double d0;
         switch(this.specificType) {
         case MUL:
            d0 = p_208759_ * this.argument;
            break;
         case ADD:
            d0 = p_208759_ + this.argument;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return d0;
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208761_) {
         DensityFunction densityfunction = this.input.mapAll(p_208761_);
         double d0 = densityfunction.minValue();
         double d1 = densityfunction.maxValue();
         double d2;
         double d3;
         if (this.specificType == DensityFunctions.MulOrAdd.Type.ADD) {
            d2 = d0 + this.argument;
            d3 = d1 + this.argument;
         } else if (this.argument >= 0.0D) {
            d2 = d0 * this.argument;
            d3 = d1 * this.argument;
         } else {
            d2 = d1 * this.argument;
            d3 = d0 * this.argument;
         }

         return new DensityFunctions.MulOrAdd(this.specificType, densityfunction, d2, d3, this.argument);
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      static enum Type {
         MUL,
         ADD;
      }
   }

   protected static record Noise(Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise noise, double xzScale, double yScale) implements DensityFunction.SimpleFunction {
      public static final MapCodec<DensityFunctions.Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208798_) -> {
         return p_208798_.group(NormalNoise.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctions.Noise::noiseData), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.Noise::yScale)).apply(p_208798_, DensityFunctions.Noise::createUnseeded);
      });
      public static final Codec<DensityFunctions.Noise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public static DensityFunctions.Noise createUnseeded(Holder<NormalNoise.NoiseParameters> p_208802_, @Deprecated double p_208803_, double p_208804_) {
         return new DensityFunctions.Noise(p_208802_, (NormalNoise)null, p_208803_, p_208804_);
      }

      public double compute(DensityFunction.FunctionContext p_208800_) {
         return this.noise == null ? 0.0D : this.noise.getValue((double)p_208800_.blockX() * this.xzScale, (double)p_208800_.blockY() * this.yScale, (double)p_208800_.blockZ() * this.xzScale);
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise == null ? 2.0D : this.noise.maxValue();
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   interface PureTransformer extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext p_208817_) {
         return this.transform(this.input().compute(p_208817_));
      }

      default void fillArray(double[] p_208819_, DensityFunction.ContextProvider p_208820_) {
         this.input().fillArray(p_208819_, p_208820_);

         for(int i = 0; i < p_208819_.length; ++i) {
            p_208819_[i] = this.transform(p_208819_[i]);
         }

      }

      double transform(double p_208815_);
   }

   static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec<DensityFunctions.RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208837_) -> {
         return p_208837_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(DensityFunctions.RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(DensityFunctions.RangeChoice::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(DensityFunctions.RangeChoice::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(DensityFunctions.RangeChoice::whenOutOfRange)).apply(p_208837_, DensityFunctions.RangeChoice::new);
      });
      public static final Codec<DensityFunctions.RangeChoice> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208839_) {
         double d0 = this.input.compute(p_208839_);
         return d0 >= this.minInclusive && d0 < this.maxExclusive ? this.whenInRange.compute(p_208839_) : this.whenOutOfRange.compute(p_208839_);
      }

      public void fillArray(double[] p_208843_, DensityFunction.ContextProvider p_208844_) {
         this.input.fillArray(p_208843_, p_208844_);

         for(int i = 0; i < p_208843_.length; ++i) {
            double d0 = p_208843_[i];
            if (d0 >= this.minInclusive && d0 < this.maxExclusive) {
               p_208843_[i] = this.whenInRange.compute(p_208844_.forIndex(i));
            } else {
               p_208843_[i] = this.whenOutOfRange.compute(p_208844_.forIndex(i));
            }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208841_) {
         return p_208841_.apply(new DensityFunctions.RangeChoice(this.input.mapAll(p_208841_), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(p_208841_), this.whenOutOfRange.mapAll(p_208841_)));
      }

      public double minValue() {
         return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
      }

      public double maxValue() {
         return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static record Shift(Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise offsetNoise) implements DensityFunctions.ShiftNoise {
      static final Codec<DensityFunctions.Shift> CODEC = DensityFunctions.singleArgumentCodec(NormalNoise.NoiseParameters.CODEC, (p_208868_) -> {
         return new DensityFunctions.Shift(p_208868_, (NormalNoise)null);
      }, DensityFunctions.Shift::noiseData);

      public double compute(DensityFunction.FunctionContext p_208864_) {
         return this.compute((double)p_208864_.blockX(), (double)p_208864_.blockY(), (double)p_208864_.blockZ());
      }

      public DensityFunctions.ShiftNoise withNewNoise(NormalNoise p_208866_) {
         return new DensityFunctions.Shift(this.noiseData, p_208866_);
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public Holder<NormalNoise.NoiseParameters> noiseData() {
         return this.noiseData;
      }

      @Nullable
      public NormalNoise offsetNoise() {
         return this.offsetNoise;
      }
   }

   protected static record ShiftA(Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise offsetNoise) implements DensityFunctions.ShiftNoise {
      static final Codec<DensityFunctions.ShiftA> CODEC = DensityFunctions.singleArgumentCodec(NormalNoise.NoiseParameters.CODEC, (p_208888_) -> {
         return new DensityFunctions.ShiftA(p_208888_, (NormalNoise)null);
      }, DensityFunctions.ShiftA::noiseData);

      public double compute(DensityFunction.FunctionContext p_208884_) {
         return this.compute((double)p_208884_.blockX(), 0.0D, (double)p_208884_.blockZ());
      }

      public DensityFunctions.ShiftNoise withNewNoise(NormalNoise p_208886_) {
         return new DensityFunctions.ShiftA(this.noiseData, p_208886_);
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public Holder<NormalNoise.NoiseParameters> noiseData() {
         return this.noiseData;
      }

      @Nullable
      public NormalNoise offsetNoise() {
         return this.offsetNoise;
      }
   }

   protected static record ShiftB(Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise offsetNoise) implements DensityFunctions.ShiftNoise {
      static final Codec<DensityFunctions.ShiftB> CODEC = DensityFunctions.singleArgumentCodec(NormalNoise.NoiseParameters.CODEC, (p_208908_) -> {
         return new DensityFunctions.ShiftB(p_208908_, (NormalNoise)null);
      }, DensityFunctions.ShiftB::noiseData);

      public double compute(DensityFunction.FunctionContext p_208904_) {
         return this.compute((double)p_208904_.blockZ(), (double)p_208904_.blockX(), 0.0D);
      }

      public DensityFunctions.ShiftNoise withNewNoise(NormalNoise p_208906_) {
         return new DensityFunctions.ShiftB(this.noiseData, p_208906_);
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public Holder<NormalNoise.NoiseParameters> noiseData() {
         return this.noiseData;
      }

      @Nullable
      public NormalNoise offsetNoise() {
         return this.offsetNoise;
      }
   }

   interface ShiftNoise extends DensityFunction.SimpleFunction {
      Holder<NormalNoise.NoiseParameters> noiseData();

      @Nullable
      NormalNoise offsetNoise();

      default double minValue() {
         return -this.maxValue();
      }

      default double maxValue() {
         NormalNoise normalnoise = this.offsetNoise();
         return (normalnoise == null ? 2.0D : normalnoise.maxValue()) * 4.0D;
      }

      default double compute(double p_208918_, double p_208919_, double p_208920_) {
         NormalNoise normalnoise = this.offsetNoise();
         return normalnoise == null ? 0.0D : normalnoise.getValue(p_208918_ * 0.25D, p_208919_ * 0.25D, p_208920_ * 0.25D) * 4.0D;
      }

      DensityFunctions.ShiftNoise withNewNoise(NormalNoise p_208921_);
   }

   protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise noise) implements DensityFunction {
      private static final MapCodec<DensityFunctions.ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208943_) -> {
         return p_208943_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(DensityFunctions.ShiftedNoise::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(DensityFunctions.ShiftedNoise::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(DensityFunctions.ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.ShiftedNoise::yScale), NormalNoise.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctions.ShiftedNoise::noiseData)).apply(p_208943_, DensityFunctions.ShiftedNoise::createUnseeded);
      });
      public static final Codec<DensityFunctions.ShiftedNoise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public static DensityFunctions.ShiftedNoise createUnseeded(DensityFunction p_208949_, DensityFunction p_208950_, DensityFunction p_208951_, double p_208952_, double p_208953_, Holder<NormalNoise.NoiseParameters> p_208954_) {
         return new DensityFunctions.ShiftedNoise(p_208949_, p_208950_, p_208951_, p_208952_, p_208953_, p_208954_, (NormalNoise)null);
      }

      public double compute(DensityFunction.FunctionContext p_208945_) {
         if (this.noise == null) {
            return 0.0D;
         } else {
            double d0 = (double)p_208945_.blockX() * this.xzScale + this.shiftX.compute(p_208945_);
            double d1 = (double)p_208945_.blockY() * this.yScale + this.shiftY.compute(p_208945_);
            double d2 = (double)p_208945_.blockZ() * this.xzScale + this.shiftZ.compute(p_208945_);
            return this.noise.getValue(d0, d1, d2);
         }
      }

      public void fillArray(double[] p_208956_, DensityFunction.ContextProvider p_208957_) {
         p_208957_.fillAllDirectly(p_208956_, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208947_) {
         return p_208947_.apply(new DensityFunctions.ShiftedNoise(this.shiftX.mapAll(p_208947_), this.shiftY.mapAll(p_208947_), this.shiftZ.mapAll(p_208947_), this.xzScale, this.yScale, this.noiseData, this.noise));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise == null ? 2.0D : this.noise.maxValue();
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Slide(@Nullable NoiseSettings settings, DensityFunction input) implements DensityFunctions.TransformerWithContext {
      public static final Codec<DensityFunctions.Slide> CODEC = DensityFunctions.singleFunctionArgumentCodec((p_208985_) -> {
         return new DensityFunctions.Slide((NoiseSettings)null, p_208985_);
      }, DensityFunctions.Slide::input);

      public double transform(DensityFunction.FunctionContext p_208980_, double p_208981_) {
         return this.settings == null ? p_208981_ : NoiseRouterData.applySlide(this.settings, p_208981_, (double)p_208980_.blockY());
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208983_) {
         return p_208983_.apply(new DensityFunctions.Slide(this.settings, this.input.mapAll(p_208983_)));
      }

      public double minValue() {
         return this.settings == null ? this.input.minValue() : Math.min(this.input.minValue(), Math.min(this.settings.bottomSlideSettings().target(), this.settings.topSlideSettings().target()));
      }

      public double maxValue() {
         return this.settings == null ? this.input.maxValue() : Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlideSettings().target(), this.settings.topSlideSettings().target()));
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }
   }

   public static record Spline(CubicSpline<TerrainShaper.PointCustom> spline, double minValue, double maxValue) implements DensityFunction {
      private static final MapCodec<DensityFunctions.Spline> DATA_CODEC = RecordCodecBuilder.mapCodec((p_211713_) -> {
         return p_211713_.group(TerrainShaper.SPLINE_CUSTOM_CODEC.fieldOf("spline").forGetter(DensityFunctions.Spline::spline), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_value").forGetter(DensityFunctions.Spline::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_value").forGetter(DensityFunctions.Spline::maxValue)).apply(p_211713_, DensityFunctions.Spline::new);
      });
      public static final Codec<DensityFunctions.Spline> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_211715_) {
         return Mth.clamp((double)this.spline.apply(TerrainShaper.makePoint(p_211715_)), this.minValue, this.maxValue);
      }

      public void fillArray(double[] p_211722_, DensityFunction.ContextProvider p_211723_) {
         p_211723_.fillAllDirectly(p_211722_, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_211717_) {
         return p_211717_.apply(new DensityFunctions.Spline(this.spline.mapAll((p_211720_) -> {
            Object object;
            if (p_211720_ instanceof TerrainShaper.CoordinateCustom) {
               TerrainShaper.CoordinateCustom terrainshaper$coordinatecustom = (TerrainShaper.CoordinateCustom)p_211720_;
               object = terrainshaper$coordinatecustom.mapAll(p_211717_);
            } else {
               object = p_211720_;
            }

            return (ToFloatFunction<TerrainShaper.PointCustom>)object;
         }), this.minValue, this.maxValue));
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }
   }

   /** @deprecated */
   @Deprecated
   public static record TerrainShaperSpline(DensityFunction continentalness, DensityFunction erosion, DensityFunction weirdness, @Nullable TerrainShaper shaper, DensityFunctions.TerrainShaperSpline.SplineType spline, double minValue, double maxValue) implements DensityFunction {
      private static final MapCodec<DensityFunctions.TerrainShaperSpline> DATA_CODEC = RecordCodecBuilder.mapCodec((p_209014_) -> {
         return p_209014_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("continentalness").forGetter(DensityFunctions.TerrainShaperSpline::continentalness), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("erosion").forGetter(DensityFunctions.TerrainShaperSpline::erosion), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("weirdness").forGetter(DensityFunctions.TerrainShaperSpline::weirdness), DensityFunctions.TerrainShaperSpline.SplineType.CODEC.fieldOf("spline").forGetter(DensityFunctions.TerrainShaperSpline::spline), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_value").forGetter(DensityFunctions.TerrainShaperSpline::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_value").forGetter(DensityFunctions.TerrainShaperSpline::maxValue)).apply(p_209014_, DensityFunctions.TerrainShaperSpline::createUnseeded);
      });
      public static final Codec<DensityFunctions.TerrainShaperSpline> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public static DensityFunctions.TerrainShaperSpline createUnseeded(DensityFunction p_209020_, DensityFunction p_209021_, DensityFunction p_209022_, DensityFunctions.TerrainShaperSpline.SplineType p_209023_, double p_209024_, double p_209025_) {
         return new DensityFunctions.TerrainShaperSpline(p_209020_, p_209021_, p_209022_, (TerrainShaper)null, p_209023_, p_209024_, p_209025_);
      }

      public double compute(DensityFunction.FunctionContext p_209016_) {
         return this.shaper == null ? 0.0D : Mth.clamp((double)this.spline.spline.apply(this.shaper, TerrainShaper.makePoint((float)this.continentalness.compute(p_209016_), (float)this.erosion.compute(p_209016_), (float)this.weirdness.compute(p_209016_))), this.minValue, this.maxValue);
      }

      public void fillArray(double[] p_209027_, DensityFunction.ContextProvider p_209028_) {
         for(int i = 0; i < p_209027_.length; ++i) {
            p_209027_[i] = this.compute(p_209028_.forIndex(i));
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor p_209018_) {
         return p_209018_.apply(new DensityFunctions.TerrainShaperSpline(this.continentalness.mapAll(p_209018_), this.erosion.mapAll(p_209018_), this.weirdness.mapAll(p_209018_), this.shaper, this.spline, this.minValue, this.maxValue));
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      interface Spline {
         float apply(TerrainShaper p_209041_, TerrainShaper.Point p_209042_);
      }

      public static enum SplineType implements StringRepresentable {
         OFFSET("offset", TerrainShaper::offset),
         FACTOR("factor", TerrainShaper::factor),
         JAGGEDNESS("jaggedness", TerrainShaper::jaggedness);

         private static final Map<String, DensityFunctions.TerrainShaperSpline.SplineType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(DensityFunctions.TerrainShaperSpline.SplineType::getSerializedName, (p_209059_) -> {
            return p_209059_;
         }));
         public static final Codec<DensityFunctions.TerrainShaperSpline.SplineType> CODEC = StringRepresentable.fromEnum(DensityFunctions.TerrainShaperSpline.SplineType::values, BY_NAME::get);
         private final String name;
         final DensityFunctions.TerrainShaperSpline.Spline spline;

         private SplineType(String p_209055_, DensityFunctions.TerrainShaperSpline.Spline p_209056_) {
            this.name = p_209055_;
            this.spline = p_209056_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   interface TransformerWithContext extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext p_209065_) {
         return this.transform(p_209065_, this.input().compute(p_209065_));
      }

      default void fillArray(double[] p_209069_, DensityFunction.ContextProvider p_209070_) {
         this.input().fillArray(p_209069_, p_209070_);

         for(int i = 0; i < p_209069_.length; ++i) {
            p_209069_[i] = this.transform(p_209070_.forIndex(i), p_209069_[i]);
         }

      }

      double transform(DensityFunction.FunctionContext p_209066_, double p_209067_);
   }

   interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static DensityFunctions.TwoArgumentSimpleFunction create(DensityFunctions.TwoArgumentSimpleFunction.Type p_209074_, DensityFunction p_209075_, DensityFunction p_209076_) {
         double d0 = p_209075_.minValue();
         double d1 = p_209076_.minValue();
         double d2 = p_209075_.maxValue();
         double d3 = p_209076_.maxValue();
         if (p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
            boolean flag = d0 >= d3;
            boolean flag1 = d1 >= d2;
            if (flag || flag1) {
               LOGGER.warn("Creating a " + p_209074_ + " function between two non-overlapping inputs: " + p_209075_ + " and " + p_209076_);
            }
         }

         double d6;
         switch(p_209074_) {
         case ADD:
            d6 = d0 + d1;
            break;
         case MAX:
            d6 = Math.max(d0, d1);
            break;
         case MIN:
            d6 = Math.min(d0, d1);
            break;
         case MUL:
            d6 = d0 > 0.0D && d1 > 0.0D ? d0 * d1 : (d2 < 0.0D && d3 < 0.0D ? d2 * d3 : Math.min(d0 * d3, d2 * d1));
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         double d5 = d6;
         switch(p_209074_) {
         case ADD:
            d6 = d2 + d3;
            break;
         case MAX:
            d6 = Math.max(d2, d3);
            break;
         case MIN:
            d6 = Math.min(d2, d3);
            break;
         case MUL:
            d6 = d0 > 0.0D && d1 > 0.0D ? d2 * d3 : (d2 < 0.0D && d3 < 0.0D ? d0 * d1 : Math.max(d0 * d1, d2 * d3));
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         double d4 = d6;
         if (p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (p_209075_ instanceof DensityFunctions.Constant) {
               DensityFunctions.Constant densityfunctions$constant1 = (DensityFunctions.Constant)p_209075_;
               return new DensityFunctions.MulOrAdd(p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, p_209076_, d5, d4, densityfunctions$constant1.value);
            }

            if (p_209076_ instanceof DensityFunctions.Constant) {
               DensityFunctions.Constant densityfunctions$constant = (DensityFunctions.Constant)p_209076_;
               return new DensityFunctions.MulOrAdd(p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, p_209075_, d5, d4, densityfunctions$constant.value);
            }
         }

         return new DensityFunctions.Ap2(p_209074_, p_209075_, p_209076_, d5, d4);
      }

      DensityFunctions.TwoArgumentSimpleFunction.Type type();

      DensityFunction argument1();

      DensityFunction argument2();

      default Codec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      public static enum Type implements StringRepresentable {
         ADD("add"),
         MUL("mul"),
         MIN("min"),
         MAX("max");

         final Codec<DensityFunctions.TwoArgumentSimpleFunction> codec = DensityFunctions.doubleFunctionArgumentCodec((p_209092_, p_209093_) -> {
            return DensityFunctions.TwoArgumentSimpleFunction.create(this, p_209092_, p_209093_);
         }, DensityFunctions.TwoArgumentSimpleFunction::argument1, DensityFunctions.TwoArgumentSimpleFunction::argument2);
         private final String name;

         private Type(String p_209089_) {
            this.name = p_209089_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record WeirdScaledSampler(DensityFunction input, Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise noise, DensityFunctions.WeirdScaledSampler.RarityValueMapper rarityValueMapper) implements DensityFunctions.TransformerWithContext {
      private static final MapCodec<DensityFunctions.WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208438_) -> {
         return p_208438_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.WeirdScaledSampler::input), NormalNoise.NoiseParameters.CODEC.fieldOf("noise").forGetter(DensityFunctions.WeirdScaledSampler::noiseData), DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(DensityFunctions.WeirdScaledSampler::rarityValueMapper)).apply(p_208438_, DensityFunctions.WeirdScaledSampler::createUnseeded);
      });
      public static final Codec<DensityFunctions.WeirdScaledSampler> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public static DensityFunctions.WeirdScaledSampler createUnseeded(DensityFunction p_208445_, Holder<NormalNoise.NoiseParameters> p_208446_, DensityFunctions.WeirdScaledSampler.RarityValueMapper p_208447_) {
         return new DensityFunctions.WeirdScaledSampler(p_208445_, p_208446_, (NormalNoise)null, p_208447_);
      }

      public double transform(DensityFunction.FunctionContext p_208440_, double p_208441_) {
         if (this.noise == null) {
            return 0.0D;
         } else {
            double d0 = this.rarityValueMapper.mapper.get(p_208441_);
            return d0 * Math.abs(this.noise.getValue((double)p_208440_.blockX() / d0, (double)p_208440_.blockY() / d0, (double)p_208440_.blockZ() / d0));
         }
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208443_) {
         this.input.mapAll(p_208443_);
         return p_208443_.apply(new DensityFunctions.WeirdScaledSampler(this.input.mapAll(p_208443_), this.noiseData, this.noise, this.rarityValueMapper));
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return this.rarityValueMapper.maxRarity * (this.noise == null ? 2.0D : this.noise.maxValue());
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }

      public static enum RarityValueMapper implements StringRepresentable {
         TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0D),
         TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0D);

         private static final Map<String, DensityFunctions.WeirdScaledSampler.RarityValueMapper> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(DensityFunctions.WeirdScaledSampler.RarityValueMapper::getSerializedName, (p_208475_) -> {
            return p_208475_;
         }));
         public static final Codec<DensityFunctions.WeirdScaledSampler.RarityValueMapper> CODEC = StringRepresentable.fromEnum(DensityFunctions.WeirdScaledSampler.RarityValueMapper::values, BY_NAME::get);
         private final String name;
         final Double2DoubleFunction mapper;
         final double maxRarity;

         private RarityValueMapper(String p_208470_, Double2DoubleFunction p_208471_, double p_208472_) {
            this.name = p_208470_;
            this.mapper = p_208471_;
            this.maxRarity = p_208472_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
      private static final MapCodec<DensityFunctions.YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208494_) -> {
         return p_208494_.group(Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(DensityFunctions.YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(DensityFunctions.YClampedGradient::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(DensityFunctions.YClampedGradient::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(DensityFunctions.YClampedGradient::toValue)).apply(p_208494_, DensityFunctions.YClampedGradient::new);
      });
      public static final Codec<DensityFunctions.YClampedGradient> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208496_) {
         return Mth.clampedMap((double)p_208496_.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
      }

      public double minValue() {
         return Math.min(this.fromValue, this.toValue);
      }

      public double maxValue() {
         return Math.max(this.fromValue, this.toValue);
      }

      public Codec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }
}