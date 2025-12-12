package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.config.BiSimplexNoiseConfiguration;

public record CoarseIslandsGeneratorConfiguration(BiSimplexNoiseConfiguration noiseConfig) implements EarlyGeneratorConfiguration {
	
	public static final Codec<CoarseIslandsGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				BiSimplexNoiseConfiguration.CODEC.fieldOf("noise").forGetter(CoarseIslandsGeneratorConfiguration::noiseConfig)
		).apply(instance, CoarseIslandsGeneratorConfiguration::new));
	
}
