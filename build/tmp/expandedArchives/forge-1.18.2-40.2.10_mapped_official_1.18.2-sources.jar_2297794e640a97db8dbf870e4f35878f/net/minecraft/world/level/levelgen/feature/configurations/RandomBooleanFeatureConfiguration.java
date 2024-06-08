package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomBooleanFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomBooleanFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_67877_) -> {
      return p_67877_.group(PlacedFeature.CODEC.fieldOf("feature_true").forGetter((p_204809_) -> {
         return p_204809_.featureTrue;
      }), PlacedFeature.CODEC.fieldOf("feature_false").forGetter((p_204807_) -> {
         return p_204807_.featureFalse;
      })).apply(p_67877_, RandomBooleanFeatureConfiguration::new);
   });
   public final Holder<PlacedFeature> featureTrue;
   public final Holder<PlacedFeature> featureFalse;

   public RandomBooleanFeatureConfiguration(Holder<PlacedFeature> p_204804_, Holder<PlacedFeature> p_204805_) {
      this.featureTrue = p_204804_;
      this.featureFalse = p_204805_;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.featureTrue.value().getFeatures(), this.featureFalse.value().getFeatures());
   }
}