package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.config.BiSimplexNoiseConfiguration;

public record CoarseIslandsGeneratorConfiguration(
		BiSimplexNoiseConfiguration noiseConfig, 
		BlockHeightSelector sealevelProvider, BlockHeightSelector seafloorProvider,
		double islandHeightScale, 
		int islandMinHeight,
		boolean useBiomeWeights, boolean ignoreBiomeLock
	) implements EarlyGeneratorConfiguration {
	
	public static final Codec<CoarseIslandsGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				BiSimplexNoiseConfiguration.namedCodec("island_noise", "crag_noise").fieldOf("noise").forGetter(CoarseIslandsGeneratorConfiguration::noiseConfig),
				BlockHeightSelectors.codec().fieldOf("sealevel").forGetter(CoarseIslandsGeneratorConfiguration::sealevelProvider),
				BlockHeightSelectors.codec().fieldOf("seafloor").forGetter(CoarseIslandsGeneratorConfiguration::seafloorProvider),
				Codec.DOUBLE.fieldOf("island_height_scale").forGetter(CoarseIslandsGeneratorConfiguration::islandHeightScale),
				Codec.INT.fieldOf("island_min_height").forGetter(CoarseIslandsGeneratorConfiguration::islandMinHeight),
				Codec.BOOL.fieldOf("use_biome_weights").forGetter(CoarseIslandsGeneratorConfiguration::useBiomeWeights),
				Codec.BOOL.optionalFieldOf("ignore_biome_lock", false).forGetter(CoarseIslandsGeneratorConfiguration::ignoreBiomeLock)
		).apply(instance, CoarseIslandsGeneratorConfiguration::new));

	public boolean useBiomeLock() {
		return !this.ignoreBiomeLock();
	}
}
