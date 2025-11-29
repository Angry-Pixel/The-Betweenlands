package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;

public record FlatLandGeneratorConfiguration(int waterLevel, int terrainHeight) implements EarlyGeneratorConfiguration {

	public static final Codec<FlatLandGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("water_level").forGetter(FlatLandGeneratorConfiguration::waterLevel),
				Codec.INT.fieldOf("terrain_height").forGetter(FlatLandGeneratorConfiguration::terrainHeight)
		).apply(instance, FlatLandGeneratorConfiguration::new));
	
}
