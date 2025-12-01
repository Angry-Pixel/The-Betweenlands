package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.generators.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.util.ExtraCodecs;

public record SimplexTerrainGeneratorConfiguration(BlockHeightSelector minLevel, BlockHeightSelector maxLevel, double noiseScale, boolean useBiomeWeights, boolean ignoreBiomeLock) implements EarlyGeneratorConfiguration {

	public static final Codec<SimplexTerrainGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				BlockHeightSelectors.codec().fieldOf("min_level").forGetter(SimplexTerrainGeneratorConfiguration::minLevel),
				BlockHeightSelectors.codec().fieldOf("max_level").forGetter(SimplexTerrainGeneratorConfiguration::maxLevel),
				ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(SimplexTerrainGeneratorConfiguration::noiseScale),
				Codec.BOOL.fieldOf("use_biome_weights").forGetter(SimplexTerrainGeneratorConfiguration::useBiomeWeights),
				Codec.BOOL.optionalFieldOf("ignore_biome_lock", false).forGetter(SimplexTerrainGeneratorConfiguration::ignoreBiomeLock)
		).apply(instance, SimplexTerrainGeneratorConfiguration::new));
	
	public boolean useBiomeLock() {
		return !this.ignoreBiomeLock();
	}
}
