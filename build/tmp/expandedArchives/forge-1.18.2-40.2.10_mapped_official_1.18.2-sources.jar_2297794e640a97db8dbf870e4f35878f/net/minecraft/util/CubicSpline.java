package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C> extends ToFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   float min();

   float max();

   CubicSpline<C> mapAll(CubicSpline.CoordinateVisitor<C> p_211579_);

   static <C> Codec<CubicSpline<C>> codec(Codec<ToFloatFunction<C>> p_184263_) {
      MutableObject<Codec<CubicSpline<C>>> mutableobject = new MutableObject<>();
            record Point<C>(float location, CubicSpline<C> value, float derivative) {
            }
      Codec<Point<C>> codec = RecordCodecBuilder.create((p_184270_) -> {
         return p_184270_.group(Codec.FLOAT.fieldOf("location").forGetter(Point::location), ExtraCodecs.lazyInitializedCodec(mutableobject::getValue).fieldOf("value").forGetter(Point::value), Codec.FLOAT.fieldOf("derivative").forGetter(Point::derivative)).apply(p_184270_, (p_184242_, p_184243_, p_184244_) -> {

            return new Point<>((float)p_184242_, p_184243_, (float)p_184244_);
         });
      });
      Codec<CubicSpline.Multipoint<C>> codec1 = RecordCodecBuilder.create((p_184267_) -> {
         return p_184267_.group(p_184263_.fieldOf("coordinate").forGetter(CubicSpline.Multipoint::coordinate), ExtraCodecs.nonEmptyList(codec.listOf()).fieldOf("points").forGetter((p_184272_) -> {
            return IntStream.range(0, p_184272_.locations.length).mapToObj((p_184249_) -> {
               return new Point<C>(p_184272_.locations()[p_184249_], (CubicSpline)p_184272_.values().get(p_184249_), p_184272_.derivatives()[p_184249_]);
            }).toList();
         })).apply(p_184267_, (p_184258_, p_184259_) -> {
            float[] afloat = new float[p_184259_.size()];
            ImmutableList.Builder<CubicSpline<C>> builder = ImmutableList.builder();
            float[] afloat1 = new float[p_184259_.size()];

            for(int i = 0; i < p_184259_.size(); ++i) {
               Point<C> point = p_184259_.get(i);
               afloat[i] = point.location();
               builder.add(point.value());
               afloat1[i] = point.derivative();
            }

            return new CubicSpline.Multipoint<>(p_184258_, afloat, builder.build(), afloat1);
         });
      });
      mutableobject.setValue(Codec.either(Codec.FLOAT, codec1).xmap((p_184261_) -> {
         return p_184261_.map(CubicSpline.Constant::new, (p_184246_) -> {
            return p_184246_;
         });
      }, (p_184251_) -> {
         Either either;
         if (p_184251_ instanceof CubicSpline.Constant) {
            CubicSpline.Constant<C> constant = (Constant<C>)p_184251_;
            either = Either.left(constant.value());
         } else {
            either = Either.right(p_184251_);
         }

         return either;
      }));
      return mutableobject.getValue();
   }

   static <C> CubicSpline<C> constant(float p_184240_) {
      return new CubicSpline.Constant<>(p_184240_);
   }

   static <C> CubicSpline.Builder<C> builder(ToFloatFunction<C> p_184253_) {
      return new CubicSpline.Builder<>(p_184253_);
   }

   static <C> CubicSpline.Builder<C> builder(ToFloatFunction<C> p_184255_, ToFloatFunction<Float> p_184256_) {
      return new CubicSpline.Builder<>(p_184255_, p_184256_);
   }

   public static final class Builder<C> {
      private final ToFloatFunction<C> coordinate;
      private final ToFloatFunction<Float> valueTransformer;
      private final FloatList locations = new FloatArrayList();
      private final List<CubicSpline<C>> values = Lists.newArrayList();
      private final FloatList derivatives = new FloatArrayList();

      protected Builder(ToFloatFunction<C> p_184293_) {
         this(p_184293_, (p_184307_) -> {
            return p_184307_;
         });
      }

      protected Builder(ToFloatFunction<C> p_184295_, ToFloatFunction<Float> p_184296_) {
         this.coordinate = p_184295_;
         this.valueTransformer = p_184296_;
      }

      public CubicSpline.Builder<C> addPoint(float p_184299_, float p_184300_, float p_184301_) {
         return this.addPoint(p_184299_, new CubicSpline.Constant<>(this.valueTransformer.apply(p_184300_)), p_184301_);
      }

      public CubicSpline.Builder<C> addPoint(float p_184303_, CubicSpline<C> p_184304_, float p_184305_) {
         if (!this.locations.isEmpty() && p_184303_ <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(p_184303_);
            this.values.add(p_184304_);
            this.derivatives.add(p_184305_);
            return this;
         }
      }

      public CubicSpline<C> build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return new CubicSpline.Multipoint<>(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   @VisibleForDebug
   public static record Constant<C>(float value) implements CubicSpline<C> {
      public float apply(C p_184313_) {
         return this.value;
      }

      public String parityString() {
         return String.format("k=%.3f", this.value);
      }

      public float min() {
         return this.value;
      }

      public float max() {
         return this.value;
      }

      public CubicSpline<C> mapAll(CubicSpline.CoordinateVisitor<C> p_211581_) {
         return this;
      }
   }

   public interface CoordinateVisitor<C> {
      ToFloatFunction<C> visit(ToFloatFunction<C> p_211583_);
   }

   @VisibleForDebug
   public static record Multipoint<C>(ToFloatFunction<C> coordinate, float[] locations, List<CubicSpline<C>> values, float[] derivatives) implements CubicSpline<C> {
      public Multipoint(ToFloatFunction<C> coordinate, float[] locations, List<CubicSpline<C>> values, float[] derivatives) {
         if (locations.length == values.size() && locations.length == derivatives.length) {
            this.coordinate = coordinate;
            this.locations = locations;
            this.values = values;
            this.derivatives = derivatives;
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
         }
      }

      public float apply(C p_184340_) {
         float f = this.coordinate.apply(p_184340_);
         int i = Mth.binarySearch(0, this.locations.length, (p_184333_) -> {
            return f < this.locations[p_184333_];
         }) - 1;
         int j = this.locations.length - 1;
         if (i < 0) {
            return this.values.get(0).apply(p_184340_) + this.derivatives[0] * (f - this.locations[0]);
         } else if (i == j) {
            return this.values.get(j).apply(p_184340_) + this.derivatives[j] * (f - this.locations[j]);
         } else {
            float f1 = this.locations[i];
            float f2 = this.locations[i + 1];
            float f3 = (f - f1) / (f2 - f1);
            ToFloatFunction<C> tofloatfunction = this.values.get(i);
            ToFloatFunction<C> tofloatfunction1 = this.values.get(i + 1);
            float f4 = this.derivatives[i];
            float f5 = this.derivatives[i + 1];
            float f6 = tofloatfunction.apply(p_184340_);
            float f7 = tofloatfunction1.apply(p_184340_);
            float f8 = f4 * (f2 - f1) - (f7 - f6);
            float f9 = -f5 * (f2 - f1) + (f7 - f6);
            return Mth.lerp(f3, f6, f7) + f3 * (1.0F - f3) * Mth.lerp(f3, f8, f9);
         }
      }

      @VisibleForTesting
      public String parityString() {
         return "Spline{coordinate=" + this.coordinate + ", locations=" + this.toString(this.locations) + ", derivatives=" + this.toString(this.derivatives) + ", values=" + (String)this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
      }

      private String toString(float[] p_184335_) {
         return "[" + (String)IntStream.range(0, p_184335_.length).mapToDouble((p_184338_) -> {
            return (double)p_184335_[p_184338_];
         }).mapToObj((p_184330_) -> {
            return String.format(Locale.ROOT, "%.3f", p_184330_);
         }).collect(Collectors.joining(", ")) + "]";
      }

      public float min() {
         return (float)this.values().stream().mapToDouble(CubicSpline::min).min().orElseThrow();
      }

      public float max() {
         return (float)this.values().stream().mapToDouble(CubicSpline::max).max().orElseThrow();
      }

      public CubicSpline<C> mapAll(CubicSpline.CoordinateVisitor<C> p_211585_) {
         return new CubicSpline.Multipoint<>(p_211585_.visit(this.coordinate), this.locations, this.values().stream().map((p_211588_) -> {
            return p_211588_.mapAll(p_211585_);
         }).toList(), this.derivatives);
      }
   }
}