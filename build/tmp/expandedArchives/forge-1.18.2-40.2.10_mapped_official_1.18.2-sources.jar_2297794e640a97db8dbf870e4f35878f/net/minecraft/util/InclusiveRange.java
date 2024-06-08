package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public record InclusiveRange<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
   public static final Codec<InclusiveRange<Integer>> INT = codec(Codec.INT);

   public InclusiveRange {
      if (minInclusive.compareTo(maxInclusive) > 0) {
         throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
      }
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> p_184573_) {
      return ExtraCodecs.intervalCodec(p_184573_, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> p_184575_, T p_184576_, T p_184577_) {
      Function<InclusiveRange<T>, DataResult<InclusiveRange<T>>> function = (p_184586_) -> {
         if (p_184586_.minInclusive().compareTo(p_184576_) < 0) {
            return DataResult.error("Range limit too low, expected at least " + p_184576_ + " [" + p_184586_.minInclusive() + "-" + p_184586_.maxInclusive() + "]");
         } else {
            return p_184586_.maxInclusive().compareTo(p_184577_) > 0 ? DataResult.error("Range limit too high, expected at most " + p_184577_ + " [" + p_184586_.minInclusive() + "-" + p_184586_.maxInclusive() + "]") : DataResult.success(p_184586_);
         }
      };
      return codec(p_184575_).flatXmap(function, function);
   }

   public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(T p_184581_, T p_184582_) {
      return p_184581_.compareTo(p_184582_) <= 0 ? DataResult.success(new InclusiveRange<T>(p_184581_, p_184582_)) : DataResult.error("min_inclusive must be less than or equal to max_inclusive");
   }

   public boolean isValueInRange(T p_184579_) {
      return p_184579_.compareTo(this.minInclusive) >= 0 && p_184579_.compareTo(this.maxInclusive) <= 0;
   }

   public boolean contains(InclusiveRange<T> p_184571_) {
      return p_184571_.minInclusive().compareTo(this.minInclusive) >= 0 && p_184571_.maxInclusive.compareTo(this.maxInclusive) <= 0;
   }

   public String toString() {
      return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
   }
}