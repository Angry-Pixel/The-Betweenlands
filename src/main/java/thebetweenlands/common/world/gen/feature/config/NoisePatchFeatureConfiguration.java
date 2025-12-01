package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import thebetweenlands.util.ExtraCodecs;

public record NoisePatchFeatureConfiguration(double noiseScale, float amplifierFactor, float amplifierOffset) implements FeatureConfiguration {

	public static final Codec<NoisePatchFeatureConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
				ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(NoisePatchFeatureConfiguration::noiseScale),
				Codec.FLOAT.fieldOf("amplifier_factor").forGetter(NoisePatchFeatureConfiguration::amplifierFactor),
				Codec.FLOAT.fieldOf("amplifier_offset").forGetter(NoisePatchFeatureConfiguration::amplifierOffset)
			).apply(instance, NoisePatchFeatureConfiguration::new));
	
}
