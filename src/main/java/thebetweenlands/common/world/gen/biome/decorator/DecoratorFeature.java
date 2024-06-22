package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

// Replacement for BiomeFeature
public abstract class DecoratorFeature {
	public DecoratorFeature() {
	}

	/**
	 * Initializes additional noise generators.
	 *
	 * @param seed World seed
	 */
	public void initializeGenerators(long seed, int biome) {

	}

	/**
	 * Generates the noise fields.
	 *
	 * @param chunkX
	 * @param chunkZ
	 */
	public void generateNoise(int chunkX, int chunkZ, int biome) {

	}

	/**
	 * Modifies the terrain at the specified block stack.
	 * <p><b>Note:</b> Do not generate outside of the specified block stack!
	 *
	 * @param blockX
	 * @param blockZ
	 * @param inChunkX
	 * @param inChunkZ
	 * @param baseBlockNoise
	 * @param rng
	 * @param chunkPrimer
	 * @param chunkGenerator
	 * @param biomesForGeneration
	 * @param pass
	 */
	public abstract void replaceStackBlocks(int x, int z,
											double baseBlockNoise, ChunkAccess chunkPrimer,
											ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration, int biome, BiomeWeights biomeWeights, BiomeGenerator.EnumGeneratorPass pass);

	public static double lerp(double val1, double val2, double lerp) {
		return val2 + (val1 - val2) * lerp;
	}

	public static int findGround(ChunkAccess chunkPrimer, int x, int z, int startY) {
		for (int y = startY; y >= 0; --y) {
			BlockState state = chunkPrimer.getBlockState(new BlockPos(x, y, z));
			if (state != null && state.getBlock() != Blocks.AIR) {
				return y;
			}
		}
		return 0;
	}
}
