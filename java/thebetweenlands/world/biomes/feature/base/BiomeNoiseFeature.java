package thebetweenlands.world.biomes.feature.base;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

import java.util.Random;

public class BiomeNoiseFeature {
	public Random currentRNG = null;
	public ChunkProviderBetweenlands currentProvider = null;
	public BiomeGenBase[] currentBiomesForGeneration = null;
	
	private int chunkX = 0, chunkZ = 0;
	private World world = null;
	
	public void setChunkAndWorld(int x, int z, World world) {
		this.chunkX = x;
		this.chunkZ = z;
		this.world = world;
	}
	
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome){}
	public void generateNoise(int chunkX, int chunkZ, BiomeGenBaseBetweenlands biome){}
	public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, 
			BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng){}
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks, byte[] chunkMeta, 
			BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng){}
}