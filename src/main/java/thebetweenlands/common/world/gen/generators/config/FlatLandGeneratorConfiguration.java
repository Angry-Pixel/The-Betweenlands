package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.util.ExtraCodecs;

public record FlatLandGeneratorConfiguration(int waterLevel, int terrainHeight, double landNoiseScale, double riverNoiseScale) implements EarlyGeneratorConfiguration {

	public static final double DEFAULT_LAND_NOISE_SCALE = 0.06D;
	public static final double DEFAULT_RIVER_NOISE_SCALE = 0.032D;
	
	public static final Codec<FlatLandGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("water_level").forGetter(FlatLandGeneratorConfiguration::waterLevel),
				Codec.INT.fieldOf("terrain_height").forGetter(FlatLandGeneratorConfiguration::terrainHeight),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("land_noise_scale", DEFAULT_LAND_NOISE_SCALE).forGetter(FlatLandGeneratorConfiguration::landNoiseScale),
				ExtraCodecs.POSITIVE_DOUBLE.lenientOptionalFieldOf("river_noise_scale", DEFAULT_RIVER_NOISE_SCALE).forGetter(FlatLandGeneratorConfiguration::riverNoiseScale)
		).apply(instance, FlatLandGeneratorConfiguration::new));
	
	public FlatLandGeneratorConfiguration(int waterLevel, int terrainHeight) {
		this(waterLevel, terrainHeight, DEFAULT_LAND_NOISE_SCALE, DEFAULT_RIVER_NOISE_SCALE);
	}
	
}
