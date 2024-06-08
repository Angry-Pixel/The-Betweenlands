package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class ThreeLayersFeatureSize extends FeatureSize {
   public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create((p_68326_) -> {
      return p_68326_.group(Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter((p_161335_) -> {
         return p_161335_.limit;
      }), Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter((p_161333_) -> {
         return p_161333_.upperLimit;
      }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((p_161331_) -> {
         return p_161331_.lowerSize;
      }), Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter((p_161329_) -> {
         return p_161329_.middleSize;
      }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((p_161327_) -> {
         return p_161327_.upperSize;
      }), minClippedHeightCodec()).apply(p_68326_, ThreeLayersFeatureSize::new);
   });
   private final int limit;
   private final int upperLimit;
   private final int lowerSize;
   private final int middleSize;
   private final int upperSize;

   public ThreeLayersFeatureSize(int p_68314_, int p_68315_, int p_68316_, int p_68317_, int p_68318_, OptionalInt p_68319_) {
      super(p_68319_);
      this.limit = p_68314_;
      this.upperLimit = p_68315_;
      this.lowerSize = p_68316_;
      this.middleSize = p_68317_;
      this.upperSize = p_68318_;
   }

   protected FeatureSizeType<?> type() {
      return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
   }

   public int getSizeAtHeight(int p_68321_, int p_68322_) {
      if (p_68322_ < this.limit) {
         return this.lowerSize;
      } else {
         return p_68322_ >= p_68321_ - this.upperLimit ? this.upperSize : this.middleSize;
      }
   }
}