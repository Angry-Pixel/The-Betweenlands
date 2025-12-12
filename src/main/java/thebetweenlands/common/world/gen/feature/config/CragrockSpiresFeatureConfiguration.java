package thebetweenlands.common.world.gen.feature.config;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseConfiguration;

public record CragrockSpiresFeatureConfiguration(
		SimplexNoiseConfiguration spireNoise,
		double spireHeightFactor,
		double spireBaseDepthFactor,
		BlockHeightSelector sealevelProvider, BlockHeightSelector seafloorProvider,
		boolean useBiomeWeights,
		BlockState baseState,
		List<BlockState> topStates
	) implements FeatureConfiguration {

	public static final Codec<CragrockSpiresFeatureConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragrockSpiresFeatureConfiguration::spireNoise),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragrockSpiresFeatureConfiguration::spireHeightFactor),
					Codec.DOUBLE.fieldOf("spire_base_depth_factor").forGetter(CragrockSpiresFeatureConfiguration::spireBaseDepthFactor),
					BlockHeightSelectors.codec().fieldOf("sealevel").forGetter(CragrockSpiresFeatureConfiguration::sealevelProvider),
					BlockHeightSelectors.codec().fieldOf("seafloor").forGetter(CragrockSpiresFeatureConfiguration::seafloorProvider),
					Codec.BOOL.fieldOf("use_biome_weights").forGetter(CragrockSpiresFeatureConfiguration::useBiomeWeights),
					BlockState.CODEC.fieldOf("base_state").forGetter(CragrockSpiresFeatureConfiguration::baseState),
					BlockState.CODEC.listOf().fieldOf("top_states").forGetter(CragrockSpiresFeatureConfiguration::topStates)
			).apply(instance, CragrockSpiresFeatureConfiguration::new));
	
}
