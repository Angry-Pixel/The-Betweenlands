package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record TwistingVinesConfig(int spreadWidth, int spreadHeight, int maxHeight) implements FeatureConfiguration {
   public static final Codec<TwistingVinesConfig> CODEC = RecordCodecBuilder.create((p_191375_) -> {
      return p_191375_.group(ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesConfig::spreadWidth), ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesConfig::spreadHeight), ExtraCodecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesConfig::maxHeight)).apply(p_191375_, TwistingVinesConfig::new);
   });
}