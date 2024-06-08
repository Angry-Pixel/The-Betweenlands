package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class BiasedToBottomHeight extends HeightProvider {
   public static final Codec<BiasedToBottomHeight> CODEC = RecordCodecBuilder.create((p_161930_) -> {
      return p_161930_.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((p_161943_) -> {
         return p_161943_.minInclusive;
      }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((p_161941_) -> {
         return p_161941_.maxInclusive;
      }), Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", 1).forGetter((p_161936_) -> {
         return p_161936_.inner;
      })).apply(p_161930_, BiasedToBottomHeight::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int inner;

   private BiasedToBottomHeight(VerticalAnchor p_161925_, VerticalAnchor p_161926_, int p_161927_) {
      this.minInclusive = p_161925_;
      this.maxInclusive = p_161926_;
      this.inner = p_161927_;
   }

   public static BiasedToBottomHeight of(VerticalAnchor p_161932_, VerticalAnchor p_161933_, int p_161934_) {
      return new BiasedToBottomHeight(p_161932_, p_161933_, p_161934_);
   }

   public int sample(Random p_161938_, WorldGenerationContext p_161939_) {
      int i = this.minInclusive.resolveY(p_161939_);
      int j = this.maxInclusive.resolveY(p_161939_);
      if (j - i - this.inner + 1 <= 0) {
         LOGGER.warn("Empty height range: {}", (Object)this);
         return i;
      } else {
         int k = p_161938_.nextInt(j - i - this.inner + 1);
         return p_161938_.nextInt(k + this.inner) + i;
      }
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.BIASED_TO_BOTTOM;
   }

   public String toString() {
      return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
   }
}