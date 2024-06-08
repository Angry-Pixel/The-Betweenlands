package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.VisibleForTesting;

public record TerrainShaper(CubicSpline<TerrainShaper.Point> offsetSampler, CubicSpline<TerrainShaper.Point> factorSampler, CubicSpline<TerrainShaper.Point> jaggednessSampler) {
   private static final Codec<CubicSpline<TerrainShaper.Point>> SPLINE_CODEC = CubicSpline.codec(TerrainShaper.Coordinate.WIDE_CODEC);
   public static final Codec<CubicSpline<TerrainShaper.PointCustom>> SPLINE_CUSTOM_CODEC = CubicSpline.codec(TerrainShaper.CoordinateCustom.WIDE_CODEC);
   public static final Codec<TerrainShaper> CODEC = RecordCodecBuilder.create((p_187316_) -> {
      return p_187316_.group(SPLINE_CODEC.fieldOf("offset").forGetter(TerrainShaper::offsetSampler), SPLINE_CODEC.fieldOf("factor").forGetter(TerrainShaper::factorSampler), SPLINE_CODEC.fieldOf("jaggedness").forGetter((p_187314_) -> {
         return p_187314_.jaggednessSampler;
      })).apply(p_187316_, TerrainShaper::new);
   });
   private static final float GLOBAL_OFFSET = -0.50375F;
   private static final ToFloatFunction<Float> NO_TRANSFORM = (p_187318_) -> {
      return p_187318_;
   };

   private static float getAmplifiedOffset(float p_187325_) {
      return p_187325_ < 0.0F ? p_187325_ : p_187325_ * 2.0F;
   }

   private static float getAmplifiedFactor(float p_187338_) {
      return 1.25F - 6.25F / (p_187338_ + 5.0F);
   }

   private static float getAmplifiedJaggedness(float p_187342_) {
      return p_187342_ * 2.0F;
   }

   public static TerrainShaper overworld(boolean p_187322_) {
      ToFloatFunction<Float> tofloatfunction = p_187322_ ? TerrainShaper::getAmplifiedOffset : NO_TRANSFORM;
      ToFloatFunction<Float> tofloatfunction1 = p_187322_ ? TerrainShaper::getAmplifiedFactor : NO_TRANSFORM;
      ToFloatFunction<Float> tofloatfunction2 = p_187322_ ? TerrainShaper::getAmplifiedJaggedness : NO_TRANSFORM;
      CubicSpline<TerrainShaper.Point> cubicspline = buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, tofloatfunction);
      CubicSpline<TerrainShaper.Point> cubicspline1 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, tofloatfunction);
      CubicSpline<TerrainShaper.Point> cubicspline2 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, tofloatfunction);
      CubicSpline<TerrainShaper.Point> cubicspline3 = buildErosionOffsetSpline(-0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, tofloatfunction);
      float f = -0.51F;
      float f1 = -0.4F;
      float f2 = 0.1F;
      float f3 = -0.15F;
      CubicSpline<TerrainShaper.Point> cubicspline4 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, tofloatfunction).addPoint(-1.1F, 0.044F, 0.0F).addPoint(-1.02F, -0.2222F, 0.0F).addPoint(-0.51F, -0.2222F, 0.0F).addPoint(-0.44F, -0.12F, 0.0F).addPoint(-0.18F, -0.12F, 0.0F).addPoint(-0.16F, cubicspline, 0.0F).addPoint(-0.15F, cubicspline, 0.0F).addPoint(-0.1F, cubicspline1, 0.0F).addPoint(0.25F, cubicspline2, 0.0F).addPoint(1.0F, cubicspline3, 0.0F).build();
      CubicSpline<TerrainShaper.Point> cubicspline5 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, NO_TRANSFORM).addPoint(-0.19F, 3.95F, 0.0F).addPoint(-0.15F, getErosionFactor(6.25F, true, NO_TRANSFORM), 0.0F).addPoint(-0.1F, getErosionFactor(5.47F, true, tofloatfunction1), 0.0F).addPoint(0.03F, getErosionFactor(5.08F, true, tofloatfunction1), 0.0F).addPoint(0.06F, getErosionFactor(4.69F, false, tofloatfunction1), 0.0F).build();
      float f4 = 0.65F;
      CubicSpline<TerrainShaper.Point> cubicspline6 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, tofloatfunction2).addPoint(-0.11F, 0.0F, 0.0F).addPoint(0.03F, buildErosionJaggednessSpline(1.0F, 0.5F, 0.0F, 0.0F, tofloatfunction2), 0.0F).addPoint(0.65F, buildErosionJaggednessSpline(1.0F, 1.0F, 1.0F, 0.0F, tofloatfunction2), 0.0F).build();
      return new TerrainShaper(cubicspline4, cubicspline5, cubicspline6);
   }

   private static CubicSpline<TerrainShaper.Point> buildErosionJaggednessSpline(float p_187295_, float p_187296_, float p_187297_, float p_187298_, ToFloatFunction<Float> p_187299_) {
      float f = -0.5775F;
      CubicSpline<TerrainShaper.Point> cubicspline = buildRidgeJaggednessSpline(p_187295_, p_187297_, p_187299_);
      CubicSpline<TerrainShaper.Point> cubicspline1 = buildRidgeJaggednessSpline(p_187296_, p_187298_, p_187299_);
      return CubicSpline.builder(TerrainShaper.Coordinate.EROSION, p_187299_).addPoint(-1.0F, cubicspline, 0.0F).addPoint(-0.78F, cubicspline1, 0.0F).addPoint(-0.5775F, cubicspline1, 0.0F).addPoint(-0.375F, 0.0F, 0.0F).build();
   }

   private static CubicSpline<TerrainShaper.Point> buildRidgeJaggednessSpline(float p_187301_, float p_187302_, ToFloatFunction<Float> p_187303_) {
      float f = peaksAndValleys(0.4F);
      float f1 = peaksAndValleys(0.56666666F);
      float f2 = (f + f1) / 2.0F;
      CubicSpline.Builder<TerrainShaper.Point> builder = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187303_);
      builder.addPoint(f, 0.0F, 0.0F);
      if (p_187302_ > 0.0F) {
         builder.addPoint(f2, buildWeirdnessJaggednessSpline(p_187302_, p_187303_), 0.0F);
      } else {
         builder.addPoint(f2, 0.0F, 0.0F);
      }

      if (p_187301_ > 0.0F) {
         builder.addPoint(1.0F, buildWeirdnessJaggednessSpline(p_187301_, p_187303_), 0.0F);
      } else {
         builder.addPoint(1.0F, 0.0F, 0.0F);
      }

      return builder.build();
   }

   private static CubicSpline<TerrainShaper.Point> buildWeirdnessJaggednessSpline(float p_187305_, ToFloatFunction<Float> p_187306_) {
      float f = 0.63F * p_187305_;
      float f1 = 0.3F * p_187305_;
      return CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, p_187306_).addPoint(-0.01F, f, 0.0F).addPoint(0.01F, f1, 0.0F).build();
   }

   private static CubicSpline<TerrainShaper.Point> getErosionFactor(float p_187308_, boolean p_187309_, ToFloatFunction<Float> p_187310_) {
      CubicSpline<TerrainShaper.Point> cubicspline = CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, p_187310_).addPoint(-0.2F, 6.3F, 0.0F).addPoint(0.2F, p_187308_, 0.0F).build();
      CubicSpline.Builder<TerrainShaper.Point> builder = CubicSpline.builder(TerrainShaper.Coordinate.EROSION, p_187310_).addPoint(-0.6F, cubicspline, 0.0F).addPoint(-0.5F, CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, p_187310_).addPoint(-0.05F, 6.3F, 0.0F).addPoint(0.05F, 2.67F, 0.0F).build(), 0.0F).addPoint(-0.35F, cubicspline, 0.0F).addPoint(-0.25F, cubicspline, 0.0F).addPoint(-0.1F, CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, p_187310_).addPoint(-0.05F, 2.67F, 0.0F).addPoint(0.05F, 6.3F, 0.0F).build(), 0.0F).addPoint(0.03F, cubicspline, 0.0F);
      if (p_187309_) {
         CubicSpline<TerrainShaper.Point> cubicspline1 = CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, p_187310_).addPoint(0.0F, p_187308_, 0.0F).addPoint(0.1F, 0.625F, 0.0F).build();
         CubicSpline<TerrainShaper.Point> cubicspline2 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187310_).addPoint(-0.9F, p_187308_, 0.0F).addPoint(-0.69F, cubicspline1, 0.0F).build();
         builder.addPoint(0.35F, p_187308_, 0.0F).addPoint(0.45F, cubicspline2, 0.0F).addPoint(0.55F, cubicspline2, 0.0F).addPoint(0.62F, p_187308_, 0.0F);
      } else {
         CubicSpline<TerrainShaper.Point> cubicspline3 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187310_).addPoint(-0.7F, cubicspline, 0.0F).addPoint(-0.15F, 1.37F, 0.0F).build();
         CubicSpline<TerrainShaper.Point> cubicspline4 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187310_).addPoint(0.45F, cubicspline, 0.0F).addPoint(0.7F, 1.56F, 0.0F).build();
         builder.addPoint(0.05F, cubicspline4, 0.0F).addPoint(0.4F, cubicspline4, 0.0F).addPoint(0.45F, cubicspline3, 0.0F).addPoint(0.55F, cubicspline3, 0.0F).addPoint(0.58F, p_187308_, 0.0F);
      }

      return builder.build();
   }

   private static float calculateSlope(float p_187272_, float p_187273_, float p_187274_, float p_187275_) {
      return (p_187273_ - p_187272_) / (p_187275_ - p_187274_);
   }

   private static CubicSpline<TerrainShaper.Point> buildMountainRidgeSplineWithPoints(float p_187331_, boolean p_187332_, ToFloatFunction<Float> p_187333_) {
      CubicSpline.Builder<TerrainShaper.Point> builder = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187333_);
      float f = -0.7F;
      float f1 = -1.0F;
      float f2 = mountainContinentalness(-1.0F, p_187331_, -0.7F);
      float f3 = 1.0F;
      float f4 = mountainContinentalness(1.0F, p_187331_, -0.7F);
      float f5 = calculateMountainRidgeZeroContinentalnessPoint(p_187331_);
      float f6 = -0.65F;
      if (-0.65F < f5 && f5 < 1.0F) {
         float f14 = mountainContinentalness(-0.65F, p_187331_, -0.7F);
         float f8 = -0.75F;
         float f9 = mountainContinentalness(-0.75F, p_187331_, -0.7F);
         float f10 = calculateSlope(f2, f9, -1.0F, -0.75F);
         builder.addPoint(-1.0F, f2, f10);
         builder.addPoint(-0.75F, f9, 0.0F);
         builder.addPoint(-0.65F, f14, 0.0F);
         float f11 = mountainContinentalness(f5, p_187331_, -0.7F);
         float f12 = calculateSlope(f11, f4, f5, 1.0F);
         float f13 = 0.01F;
         builder.addPoint(f5 - 0.01F, f11, 0.0F);
         builder.addPoint(f5, f11, f12);
         builder.addPoint(1.0F, f4, f12);
      } else {
         float f7 = calculateSlope(f2, f4, -1.0F, 1.0F);
         if (p_187332_) {
            builder.addPoint(-1.0F, Math.max(0.2F, f2), 0.0F);
            builder.addPoint(0.0F, Mth.lerp(0.5F, f2, f4), f7);
         } else {
            builder.addPoint(-1.0F, f2, f7);
         }

         builder.addPoint(1.0F, f4, f7);
      }

      return builder.build();
   }

   private static float mountainContinentalness(float p_187327_, float p_187328_, float p_187329_) {
      float f = 1.17F;
      float f1 = 0.46082947F;
      float f2 = 1.0F - (1.0F - p_187328_) * 0.5F;
      float f3 = 0.5F * (1.0F - p_187328_);
      float f4 = (p_187327_ + 1.17F) * 0.46082947F;
      float f5 = f4 * f2 - f3;
      return p_187327_ < p_187329_ ? Math.max(f5, -0.2222F) : Math.max(f5, 0.0F);
   }

   private static float calculateMountainRidgeZeroContinentalnessPoint(float p_187344_) {
      float f = 1.17F;
      float f1 = 0.46082947F;
      float f2 = 1.0F - (1.0F - p_187344_) * 0.5F;
      float f3 = 0.5F * (1.0F - p_187344_);
      return f3 / (0.46082947F * f2) - 1.17F;
   }

   private static CubicSpline<TerrainShaper.Point> buildErosionOffsetSpline(float p_187285_, float p_187286_, float p_187287_, float p_187288_, float p_187289_, float p_187290_, boolean p_187291_, boolean p_187292_, ToFloatFunction<Float> p_187293_) {
      float f = 0.6F;
      float f1 = 0.5F;
      float f2 = 0.5F;
      CubicSpline<TerrainShaper.Point> cubicspline = buildMountainRidgeSplineWithPoints(Mth.lerp(p_187288_, 0.6F, 1.5F), p_187292_, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline1 = buildMountainRidgeSplineWithPoints(Mth.lerp(p_187288_, 0.6F, 1.0F), p_187292_, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline2 = buildMountainRidgeSplineWithPoints(p_187288_, p_187292_, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline3 = ridgeSpline(p_187285_ - 0.15F, 0.5F * p_187288_, Mth.lerp(0.5F, 0.5F, 0.5F) * p_187288_, 0.5F * p_187288_, 0.6F * p_187288_, 0.5F, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline4 = ridgeSpline(p_187285_, p_187289_ * p_187288_, p_187286_ * p_187288_, 0.5F * p_187288_, 0.6F * p_187288_, 0.5F, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline5 = ridgeSpline(p_187285_, p_187289_, p_187289_, p_187286_, p_187287_, 0.5F, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline6 = ridgeSpline(p_187285_, p_187289_, p_187289_, p_187286_, p_187287_, 0.5F, p_187293_);
      CubicSpline<TerrainShaper.Point> cubicspline7 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187293_).addPoint(-1.0F, p_187285_, 0.0F).addPoint(-0.4F, cubicspline5, 0.0F).addPoint(0.0F, p_187287_ + 0.07F, 0.0F).build();
      CubicSpline<TerrainShaper.Point> cubicspline8 = ridgeSpline(-0.02F, p_187290_, p_187290_, p_187286_, p_187287_, 0.0F, p_187293_);
      CubicSpline.Builder<TerrainShaper.Point> builder = CubicSpline.builder(TerrainShaper.Coordinate.EROSION, p_187293_).addPoint(-0.85F, cubicspline, 0.0F).addPoint(-0.7F, cubicspline1, 0.0F).addPoint(-0.4F, cubicspline2, 0.0F).addPoint(-0.35F, cubicspline3, 0.0F).addPoint(-0.1F, cubicspline4, 0.0F).addPoint(0.2F, cubicspline5, 0.0F);
      if (p_187291_) {
         builder.addPoint(0.4F, cubicspline6, 0.0F).addPoint(0.45F, cubicspline7, 0.0F).addPoint(0.55F, cubicspline7, 0.0F).addPoint(0.58F, cubicspline6, 0.0F);
      }

      builder.addPoint(0.7F, cubicspline8, 0.0F);
      return builder.build();
   }

   private static CubicSpline<TerrainShaper.Point> ridgeSpline(float p_187277_, float p_187278_, float p_187279_, float p_187280_, float p_187281_, float p_187282_, ToFloatFunction<Float> p_187283_) {
      float f = Math.max(0.5F * (p_187278_ - p_187277_), p_187282_);
      float f1 = 5.0F * (p_187279_ - p_187278_);
      return CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, p_187283_).addPoint(-1.0F, p_187277_, f).addPoint(-0.4F, p_187278_, Math.min(f, f1)).addPoint(0.0F, p_187279_, f1).addPoint(0.4F, p_187280_, 2.0F * (p_187280_ - p_187279_)).addPoint(1.0F, p_187281_, 0.7F * (p_187281_ - p_187280_)).build();
   }

   public void addDebugBiomesToVisualizeSplinePoints(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> p_187320_) {
      Climate.Parameter climate$parameter = Climate.Parameter.span(-1.0F, 1.0F);
      p_187320_.accept(Pair.of(Climate.parameters(climate$parameter, climate$parameter, climate$parameter, climate$parameter, Climate.Parameter.point(0.0F), climate$parameter, 0.01F), Biomes.PLAINS));
      CubicSpline.Multipoint<TerrainShaper.Point> multipoint = (CubicSpline.Multipoint)buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, NO_TRANSFORM);
      ResourceKey<Biome> resourcekey = Biomes.DESERT;
      float[] afloat = multipoint.locations();
      int i = afloat.length;

      for(int j = 0; j < i; ++j) {
         Float f = afloat[j];
         p_187320_.accept(Pair.of(Climate.parameters(climate$parameter, climate$parameter, climate$parameter, Climate.Parameter.point(f), Climate.Parameter.point(0.0F), climate$parameter, 0.0F), resourcekey));
         resourcekey = resourcekey == Biomes.DESERT ? Biomes.BADLANDS : Biomes.DESERT;
      }

      afloat = ((CubicSpline.Multipoint)this.offsetSampler).locations();
      i = afloat.length;

      for(int k = 0; k < i; ++k) {
         Float f1 = afloat[k];
         p_187320_.accept(Pair.of(Climate.parameters(climate$parameter, climate$parameter, Climate.Parameter.point(f1), climate$parameter, Climate.Parameter.point(0.0F), climate$parameter, 0.0F), Biomes.SNOWY_TAIGA));
      }

   }

   public float offset(TerrainShaper.Point p_187312_) {
      return this.offsetSampler.apply(p_187312_) + -0.50375F;
   }

   public float factor(TerrainShaper.Point p_187335_) {
      return this.factorSampler.apply(p_187335_);
   }

   public float jaggedness(TerrainShaper.Point p_187340_) {
      return this.jaggednessSampler.apply(p_187340_);
   }

   public static TerrainShaper.Point makePoint(float p_187268_, float p_187269_, float p_187270_) {
      return new TerrainShaper.Point(p_187268_, p_187269_, peaksAndValleys(p_187270_), p_187270_);
   }

   public static TerrainShaper.PointCustom makePoint(DensityFunction.FunctionContext p_211600_) {
      return new TerrainShaper.PointCustom(p_211600_);
   }

   public static float peaksAndValleys(float p_187266_) {
      return -(Math.abs(Math.abs(p_187266_) - 0.6666667F) - 0.33333334F) * 3.0F;
   }

   @VisibleForTesting
   protected static enum Coordinate implements StringRepresentable, ToFloatFunction<TerrainShaper.Point> {
      CONTINENTS(TerrainShaper.Point::continents, "continents"),
      EROSION(TerrainShaper.Point::erosion, "erosion"),
      WEIRDNESS(TerrainShaper.Point::weirdness, "weirdness"),
      /** @deprecated */
      @Deprecated
      RIDGES(TerrainShaper.Point::ridges, "ridges");

      private static final Map<String, TerrainShaper.Coordinate> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(TerrainShaper.Coordinate::getSerializedName, (p_187371_) -> {
         return p_187371_;
      }));
      private static final Codec<TerrainShaper.Coordinate> CODEC = StringRepresentable.fromEnum(TerrainShaper.Coordinate::values, BY_NAME::get);
      static final Codec<ToFloatFunction<TerrainShaper.Point>> WIDE_CODEC = CODEC.flatComapMap((p_187365_) -> {
         return p_187365_;
      }, (p_187363_) -> {
         DataResult dataresult;
         if (p_187363_ instanceof TerrainShaper.Coordinate) {
            TerrainShaper.Coordinate terrainshaper$coordinate = (TerrainShaper.Coordinate)p_187363_;
            dataresult = DataResult.success(terrainshaper$coordinate);
         } else {
            dataresult = DataResult.error("Not a coordinate resolver: " + p_187363_);
         }

         return dataresult;
      });
      private final ToFloatFunction<TerrainShaper.Point> reference;
      private final String name;

      private Coordinate(ToFloatFunction<TerrainShaper.Point> p_187359_, String p_187360_) {
         this.reference = p_187359_;
         this.name = p_187360_;
      }

      public String getSerializedName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public float apply(TerrainShaper.Point p_187367_) {
         return this.reference.apply(p_187367_);
      }
   }

   public static record CoordinateCustom(Holder<DensityFunction> function) implements ToFloatFunction<TerrainShaper.PointCustom> {
      static final Codec<ToFloatFunction<TerrainShaper.PointCustom>> WIDE_CODEC = DensityFunction.CODEC.flatComapMap(TerrainShaper.CoordinateCustom::new, (p_211608_) -> {
         DataResult dataresult;
         if (p_211608_ instanceof TerrainShaper.CoordinateCustom) {
            TerrainShaper.CoordinateCustom terrainshaper$coordinatecustom = (TerrainShaper.CoordinateCustom)p_211608_;
            dataresult = DataResult.success(terrainshaper$coordinatecustom.function());
         } else {
            dataresult = DataResult.error("Not a coordinate resolver: " + p_211608_);
         }

         return dataresult;
      });

      public float apply(TerrainShaper.PointCustom p_211610_) {
         return (float)this.function.value().compute(p_211610_.context());
      }

      public TerrainShaper.CoordinateCustom mapAll(DensityFunction.Visitor p_211612_) {
         return new TerrainShaper.CoordinateCustom(new Holder.Direct<>(this.function.value().mapAll(p_211612_)));
      }
   }

   public static record Point(float continents, float erosion, float ridges, float weirdness) {
   }

   public static record PointCustom(DensityFunction.FunctionContext context) {
   }
}