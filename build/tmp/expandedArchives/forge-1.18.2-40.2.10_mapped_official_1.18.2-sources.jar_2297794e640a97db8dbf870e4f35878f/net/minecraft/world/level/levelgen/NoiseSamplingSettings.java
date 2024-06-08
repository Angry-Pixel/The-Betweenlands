package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NoiseSamplingSettings(double xzScale, double yScale, double xzFactor, double yFactor) {
   private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
   public static final Codec<NoiseSamplingSettings> CODEC = RecordCodecBuilder.create((p_64503_) -> {
      return p_64503_.group(SCALE_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::xzScale), SCALE_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingSettings::yScale), SCALE_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::xzFactor), SCALE_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingSettings::yFactor)).apply(p_64503_, NoiseSamplingSettings::new);
   });
}