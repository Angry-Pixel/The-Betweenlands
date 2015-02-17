package thebetweenlands.world.biomes.feature;

import java.util.Random;

import net.minecraft.block.Block;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.BiomeGenBaseBetweenlands;

public interface BiomeNoiseFeature {
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome);
	public void generateNoise(int chunkX, int chunkZ, BiomeGenBaseBetweenlands biome);
	public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider);
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, BiomeGenBaseBetweenlands biome);
}