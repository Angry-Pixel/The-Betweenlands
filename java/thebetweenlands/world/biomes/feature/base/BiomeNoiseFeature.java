package thebetweenlands.world.biomes.feature.base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public interface BiomeNoiseFeature {
	public Random currentRNG = null;
	public ChunkProviderBetweenlands currentProvider = null;
	public BiomeGenBase[] currentBiomesForGeneration = null;
	
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome);
	public void generateNoise(int chunkX, int chunkZ, BiomeGenBaseBetweenlands biome);
	public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, 
			BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng);
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, 
			BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng);
}