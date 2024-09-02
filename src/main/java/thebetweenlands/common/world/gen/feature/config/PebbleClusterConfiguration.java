package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record PebbleClusterConfiguration(BlockState state, int offset, int attempts, boolean underwater) implements FeatureConfiguration {
	public static final Codec<PebbleClusterConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockState.CODEC.fieldOf("state").forGetter(obj -> obj.state),
			Codec.INT.fieldOf("offset").forGetter(obj -> obj.offset),
			Codec.INT.fieldOf("attempts").forGetter(obj -> obj.attempts),
			Codec.BOOL.fieldOf("underwater").forGetter(obj -> obj.underwater)
		).apply(instance, PebbleClusterConfiguration::new));
}
