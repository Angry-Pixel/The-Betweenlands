package thebetweenlands.common.world.gen.feature.config;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import thebetweenlands.util.ExtraCodecs;

public record NoisePatchWithLevelFeatureConfiguration(Optional<Integer> level, double noiseScale, float amplifierFactor, float amplifierOffset) implements FeatureConfiguration {

	public static final Codec<NoisePatchWithLevelFeatureConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.INT.optionalFieldOf("level").forGetter(NoisePatchWithLevelFeatureConfiguration::level),
					ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(NoisePatchWithLevelFeatureConfiguration::noiseScale),
					Codec.FLOAT.fieldOf("amplifier_factor").forGetter(NoisePatchWithLevelFeatureConfiguration::amplifierFactor),
					Codec.FLOAT.fieldOf("amplifier_offset").forGetter(NoisePatchWithLevelFeatureConfiguration::amplifierOffset)
			).apply(instance, NoisePatchWithLevelFeatureConfiguration::new));
	
}
