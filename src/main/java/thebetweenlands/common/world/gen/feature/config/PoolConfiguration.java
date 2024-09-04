package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record PoolConfiguration(BlockState fluid, double size, int minY) implements FeatureConfiguration {

	public static final Codec<PoolConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockState.CODEC.fieldOf("fluid").forGetter(obj -> obj.fluid),
			Codec.DOUBLE.fieldOf("side").forGetter(obj -> obj.size),
			ExtraCodecs.POSITIVE_INT.fieldOf("min_y").forGetter(obj -> obj.minY)
		).apply(instance, PoolConfiguration::new));
}
