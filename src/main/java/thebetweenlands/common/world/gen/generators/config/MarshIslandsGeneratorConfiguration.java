package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.util.ExtraCodecs;

public record MarshIslandsGeneratorConfiguration(int islandLevel, double islandNoiseScale, double fuzzNoiseScale) implements EarlyGeneratorConfiguration {

	public static final Codec<MarshIslandsGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("island_level").forGetter(MarshIslandsGeneratorConfiguration::islandLevel),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("island_noise_scale", 0.16D).forGetter(MarshIslandsGeneratorConfiguration::islandNoiseScale),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("fuzz_noise_scale", 10.5D).forGetter(MarshIslandsGeneratorConfiguration::fuzzNoiseScale)
		).apply(instance, MarshIslandsGeneratorConfiguration::new));
	
}
