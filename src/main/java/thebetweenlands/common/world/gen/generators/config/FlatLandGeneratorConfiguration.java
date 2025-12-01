package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.util.ExtraCodecs;

public record FlatLandGeneratorConfiguration(int waterLevel, int terrainHeight, double landNoiseScale, double riverNoiseScale) implements EarlyGeneratorConfiguration {

	public static final Codec<FlatLandGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("water_level").forGetter(FlatLandGeneratorConfiguration::waterLevel),
				Codec.INT.fieldOf("terrain_height").forGetter(FlatLandGeneratorConfiguration::terrainHeight),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("land_noise_scale", 0.06D).forGetter(FlatLandGeneratorConfiguration::landNoiseScale),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("river_noise_scale", 0.032D).forGetter(FlatLandGeneratorConfiguration::riverNoiseScale)
		).apply(instance, FlatLandGeneratorConfiguration::new));
	
}
