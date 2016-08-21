package thebetweenlands.common.world.gen.feature.terrain;

import java.util.Random;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class BiomeFeature {
	/**
	 * Initializes additional noise generators.
	 * @param rng Seeded Random
	 */
	public void initializeNoiseGenerators(Random rng, Biome biome) {

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
	 * @param chunkProvider
	 * @param biomesForGeneration
	 * @param blockAccess
	 * @param pass
	 */
	public abstract void replaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ, 
			double baseBlockNoise, Random rng, ChunkPrimer chunkPrimer, 
			/*ChunkProviderBetweenlands*/IChunkProvider chunkProvider, Biome[] biomesForGeneration,
			IBlockAccess blockAccess, int pass);
}
