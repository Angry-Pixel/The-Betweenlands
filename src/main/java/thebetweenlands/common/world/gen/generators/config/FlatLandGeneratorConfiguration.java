package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;

public record FlatLandGeneratorConfiguration(int waterLevel, int terrainLevel) implements EarlyGeneratorConfiguration {

	public static final Codec<FlatLandGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("water_level").forGetter(FlatLandGeneratorConfiguration::waterLevel),
				Codec.INT.fieldOf("terrain_level").forGetter(FlatLandGeneratorConfiguration::terrainLevel)
		).apply(instance, FlatLandGeneratorConfiguration::new));
	
}
