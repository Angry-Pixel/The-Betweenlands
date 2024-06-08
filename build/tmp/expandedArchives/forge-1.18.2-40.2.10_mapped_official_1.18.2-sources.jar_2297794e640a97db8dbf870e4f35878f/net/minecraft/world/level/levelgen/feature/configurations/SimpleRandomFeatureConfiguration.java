package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SimpleRandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<SimpleRandomFeatureConfiguration> CODEC = ExtraCodecs.nonEmptyHolderSet(PlacedFeature.LIST_CODEC).fieldOf("features").xmap(SimpleRandomFeatureConfiguration::new, (p_204844_) -> {
      return p_204844_.features;
   }).codec();
   public final HolderSet<PlacedFeature> features;

   public SimpleRandomFeatureConfiguration(HolderSet<PlacedFeature> p_204842_) {
      this.features = p_204842_;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.features.stream().flatMap((p_204846_) -> {
         return p_204846_.value().getFeatures();
      });
   }
}