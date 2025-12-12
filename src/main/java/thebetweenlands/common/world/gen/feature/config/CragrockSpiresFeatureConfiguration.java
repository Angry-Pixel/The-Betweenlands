package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record CragrockSpiresFeatureConfiguration(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int level) implements FeatureConfiguration {

	public static final Codec<CragrockSpiresFeatureConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragrockSpiresFeatureConfiguration::spireNoise),
					Codec.DOUBLE.fieldOf("noise_value_multiplier").forGetter(CragrockSpiresFeatureConfiguration::noiseValueMultiplier),
					Codec.DOUBLE.fieldOf("noise_value_offset").forGetter(CragrockSpiresFeatureConfiguration::noiseValueOffset),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragrockSpiresFeatureConfiguration::spireHeightFactor),
					Codec.INT.fieldOf("level").forGetter(CragrockSpiresFeatureConfiguration::level)
			).apply(instance, CragrockSpiresFeatureConfiguration::new));
	
}
