package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BigBulbCappedMushroomFeatureConfiguration(int minHeightInclusive, int maxHeightInclusive, BlockState stalkBottomState, BlockState stalkState, BlockState headState) implements FeatureConfiguration {

	public static final Codec<BigBulbCappedMushroomFeatureConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				Codec.INT.fieldOf("min_height_inclusive").forGetter(BigBulbCappedMushroomFeatureConfiguration::minHeightInclusive),
				Codec.INT.fieldOf("max_height_inclusive").forGetter(BigBulbCappedMushroomFeatureConfiguration::maxHeightInclusive),
				BlockState.CODEC.fieldOf("stalk_bottom_state").forGetter(BigBulbCappedMushroomFeatureConfiguration::stalkBottomState),
				BlockState.CODEC.fieldOf("stalk_state").forGetter(BigBulbCappedMushroomFeatureConfiguration::stalkState),
				BlockState.CODEC.fieldOf("head_state").forGetter(BigBulbCappedMushroomFeatureConfiguration::headState)
		).apply(instance, BigBulbCappedMushroomFeatureConfiguration::new));
	
}
