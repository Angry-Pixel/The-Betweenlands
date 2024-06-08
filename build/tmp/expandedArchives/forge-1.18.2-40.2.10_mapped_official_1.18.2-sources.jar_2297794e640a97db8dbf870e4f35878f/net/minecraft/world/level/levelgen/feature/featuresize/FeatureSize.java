package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.Registry;

public abstract class FeatureSize {
   public static final Codec<FeatureSize> CODEC = Registry.FEATURE_SIZE_TYPES.byNameCodec().dispatch(FeatureSize::type, FeatureSizeType::codec);
   protected static final int MAX_WIDTH = 16;
   protected final OptionalInt minClippedHeight;

   protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> minClippedHeightCodec() {
      return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap((p_68292_) -> {
         return p_68292_.map(OptionalInt::of).orElse(OptionalInt.empty());
      }, (p_68294_) -> {
         return p_68294_.isPresent() ? Optional.of(p_68294_.getAsInt()) : Optional.empty();
      }).forGetter((p_68290_) -> {
         return p_68290_.minClippedHeight;
      });
   }

   public FeatureSize(OptionalInt p_68285_) {
      this.minClippedHeight = p_68285_;
   }

   protected abstract FeatureSizeType<?> type();

   public abstract int getSizeAtHeight(int p_68287_, int p_68288_);

   public OptionalInt minClippedHeight() {
      return this.minClippedHeight;
   }
}