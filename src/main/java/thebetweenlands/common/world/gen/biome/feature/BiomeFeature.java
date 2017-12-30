package thebetweenlands.common.world.gen.biome.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

public abstract class BiomeFeature {
	/**
	 * Initializes additional noise generators.
	 * @param seed World seed
	 */
	public void initializeGenerators(long seed, Biome biome) {

	}

	/**
	 * Generates the noise fields.
	 * @param chunkX
	 * @param chunkZ
	 */
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {

	}

	/**
	 * Modifies the terrain at the specified block stack.
	 * <p><b>Note:</b> Do not generate outside of the specified block stack!
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
			double baseBlockNoise, ChunkPrimer chunkPrimer, 
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights, EnumGeneratorPass pass);

	public static double lerp(double val1, double val2, double lerp) {
		return val2 + (val1 - val2) * lerp;
	}

	public static int findGround(ChunkPrimer chunkPrimer, int x, int z, int startY) {
		for (int y = startY; y >= 0; --y) {
			IBlockState state = chunkPrimer.getBlockState(x, y, z);
			if (state != null && state.getBlock() != Blocks.AIR) {
				return y;
			}
		}
		return 0;
	}
}
