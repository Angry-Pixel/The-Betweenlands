package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NoOpFeature extends Feature<NoneFeatureConfiguration> {
   public NoOpFeature(Codec<NoneFeatureConfiguration> p_66431_) {
      super(p_66431_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160112_) {
      return true;
   }
}