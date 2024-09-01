package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BlockPlaceConfiguration(BlockState state, int offset, int attempts) implements FeatureConfiguration {

	public static final Codec<BlockPlaceConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockState.CODEC.fieldOf("state").forGetter(obj -> obj.state),
			ExtraCodecs.POSITIVE_INT.fieldOf("offset").forGetter(obj -> obj.offset),
			ExtraCodecs.POSITIVE_INT.fieldOf("attempts").forGetter(obj -> obj.attempts)
		).apply(instance, BlockPlaceConfiguration::new));
}
