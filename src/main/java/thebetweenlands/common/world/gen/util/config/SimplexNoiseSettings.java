package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SimplexNoiseSettings(int octaves, double noiseScale, double noiseValueMultiplier, double noiseValueOffset) {

	public static final double DEFAULT_NOISE_VALUE_MULTIPLIER = 1.0D;
	public static final double DEFAULT_NOISE_VALUE_OFFSET = 0.0D;
	
	public static final MapCodec<SimplexNoiseSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("noise_octaves").forGetter(SimplexNoiseSettings::octaves),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(SimplexNoiseSettings::noiseScale),
					Codec.DOUBLE.optionalFieldOf("noise_value_multiplier", DEFAULT_NOISE_VALUE_MULTIPLIER).forGetter(SimplexNoiseSettings::noiseValueMultiplier),
					Codec.DOUBLE.optionalFieldOf("noise_value_offset", DEFAULT_NOISE_VALUE_OFFSET).forGetter(SimplexNoiseSettings::noiseValueOffset)
			).apply(instance, SimplexNoiseSettings::new));

	public static final Codec<SimplexNoiseSettings> CODEC = MAP_CODEC.codec();

	public static SimplexNoiseSettings of(int octaves, double noiseScale, double noiseValueMultiplier, double noiseValueOffset) {
		return new SimplexNoiseSettings(octaves, noiseScale, noiseValueMultiplier, noiseValueOffset);
	}
	
	public static SimplexNoiseSettings of(int octaves, double noiseScale) {
		return new SimplexNoiseSettings(octaves, noiseScale, DEFAULT_NOISE_VALUE_MULTIPLIER, DEFAULT_NOISE_VALUE_OFFSET);
	}
	
}
