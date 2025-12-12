package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SimplexNoiseSettings(int octaves, double noiseScale) {

	public static final MapCodec<SimplexNoiseSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("noise_octaves").forGetter(SimplexNoiseSettings::octaves),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(SimplexNoiseSettings::noiseScale)
			).apply(instance, SimplexNoiseSettings::new));

	public static final Codec<SimplexNoiseSettings> CODEC = MAP_CODEC.codec();
	
	public static SimplexNoiseSettings of(int octaves, double noiseScale) {
		return new SimplexNoiseSettings(octaves, noiseScale);
	}
	
}
