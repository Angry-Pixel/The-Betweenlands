package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class TrapezoidHeight extends HeightProvider {
   public static final Codec<TrapezoidHeight> CODEC = RecordCodecBuilder.create((p_162005_) -> {
      return p_162005_.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((p_162021_) -> {
         return p_162021_.minInclusive;
      }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((p_162019_) -> {
         return p_162019_.maxInclusive;
      }), Codec.INT.optionalFieldOf("plateau", Integer.valueOf(0)).forGetter((p_162014_) -> {
         return p_162014_.plateau;
      })).apply(p_162005_, TrapezoidHeight::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int plateau;

   private TrapezoidHeight(VerticalAnchor p_162000_, VerticalAnchor p_162001_, int p_162002_) {
      this.minInclusive = p_162000_;
      this.maxInclusive = p_162001_;
      this.plateau = p_162002_;
   }

   public static TrapezoidHeight of(VerticalAnchor p_162010_, VerticalAnchor p_162011_, int p_162012_) {
      return new TrapezoidHeight(p_162010_, p_162011_, p_162012_);
   }

   public static TrapezoidHeight of(VerticalAnchor p_162007_, VerticalAnchor p_162008_) {
      return of(p_162007_, p_162008_, 0);
   }

   public int sample(Random p_162016_, WorldGenerationContext p_162017_) {
      int i = this.minInclusive.resolveY(p_162017_);
      int j = this.maxInclusive.resolveY(p_162017_);
      if (i > j) {
         LOGGER.warn("Empty height range: {}", (Object)this);
         return i;
      } else {
         int k = j - i;
         if (this.plateau >= k) {
            return Mth.randomBetweenInclusive(p_162016_, i, j);
         } else {
            int l = (k - this.plateau) / 2;
            int i1 = k - l;
            return i + Mth.randomBetweenInclusive(p_162016_, 0, i1) + Mth.randomBetweenInclusive(p_162016_, 0, l);
         }
      }
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.TRAPEZOID;
   }

   public String toString() {
      return this.plateau == 0 ? "triangle (" + this.minInclusive + "-" + this.maxInclusive + ")" : "trapezoid(" + this.plateau + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}