package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import thebetweenlands.common.world.gen.generators.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.generators.util.BlockHeightSelectors.BlockHeightSelector;

public record CragrockSpiresFeatureConfiguration(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, BlockHeightSelector sealevelProvider, BlockHeightSelector seafloorProvider, BlockState baseState) implements FeatureConfiguration {

	public static final Codec<CragrockSpiresFeatureConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragrockSpiresFeatureConfiguration::spireNoise),
					Codec.DOUBLE.fieldOf("noise_value_multiplier").forGetter(CragrockSpiresFeatureConfiguration::noiseValueMultiplier),
					Codec.DOUBLE.fieldOf("noise_value_offset").forGetter(CragrockSpiresFeatureConfiguration::noiseValueOffset),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragrockSpiresFeatureConfiguration::spireHeightFactor),
					BlockHeightSelectors.codec().fieldOf("sealevel").forGetter(CragrockSpiresFeatureConfiguration::sealevelProvider),
					BlockHeightSelectors.codec().fieldOf("seafloor").forGetter(CragrockSpiresFeatureConfiguration::seafloorProvider),
					BlockState.CODEC.fieldOf("base_state").forGetter(CragrockSpiresFeatureConfiguration::baseState)
			).apply(instance, CragrockSpiresFeatureConfiguration::new));
	
}
