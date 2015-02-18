package thebetweenlands.world.biomes.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;

public class SmallIslandNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.islandNoise = this.islandNoiseGen.func_151599_a(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}

	@Override
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) { }

	@Override
	public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) {
		int sliceSize = chunkBlocks.length / 256;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		//Flatten terrain
		int lowestBlock = 0;
		for(int yOff = 0; yOff < layerHeight; yOff++) {
			int y = layerHeight - yOff;
			Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
			if(currentBlock != provider.layerBlock) {
				lowestBlock = y;
				break;
			}
		}
		double noise = this.islandNoise[x * 16 + z] / 8.0f;
		for(int y = lowestBlock; y < layerHeight - (layerHeight - lowestBlock) / 2.5f + noise * (layerHeight - lowestBlock); y++) {
			chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
		}
	}
}
