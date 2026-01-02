package thebetweenlands.api.world.biome.layer.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomFactoryContext;

/**
 * A "blank" random context for layers that don't have any random seed assigned to them
 */
public record UnconfiguredRandomContext(BiomeLayerRandomFactoryContext factory) implements BiomeLayerRandomContext {
	@Override
	public BiomeLayerRandomFactoryContext getRandomFactory() {
		return this.factory;
	}

	@Override
	public RandomSource createRandom(long x, long z) {
		throw new IllegalStateException("attempt to create a random source from an unconfigured random context");
	}

	@Override
	public ImprovedNoise getBiomeNoise() {
		throw new IllegalStateException("attempt to access biome noise from an unconfigured random context");
	}
}
