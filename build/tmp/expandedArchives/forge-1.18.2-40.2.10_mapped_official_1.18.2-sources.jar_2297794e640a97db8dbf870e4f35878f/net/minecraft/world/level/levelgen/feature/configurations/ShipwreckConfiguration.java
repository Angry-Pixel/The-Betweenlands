package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class ShipwreckConfiguration implements FeatureConfiguration {
   public static final Codec<ShipwreckConfiguration> CODEC = Codec.BOOL.fieldOf("is_beached").orElse(false).xmap(ShipwreckConfiguration::new, (p_68067_) -> {
      return p_68067_.isBeached;
   }).codec();
   public final boolean isBeached;

   public ShipwreckConfiguration(boolean p_68065_) {
      this.isBeached = p_68065_;
   }
}