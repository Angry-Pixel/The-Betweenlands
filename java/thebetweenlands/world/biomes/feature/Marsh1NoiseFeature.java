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

public class Marsh1NoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];
	private NoiseGeneratorPerlin fuzzNoiseGen;
	private double[] fuzzNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.fuzzNoiseGen = new NoiseGeneratorPerlin(rng, 8);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.islandNoise = this.islandNoiseGen.func_151599_a(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
		this.fuzzNoise = this.fuzzNoiseGen.func_151599_a(this.fuzzNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 10.5D, 10.5D, 1.0D);
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
		double noise = this.islandNoise[x * 16 + z] / 1.4f+
				this.fuzzNoise[x * 16 + z] / 1.4f + 1.8f;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		if(noise <= 0 && chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, layerHeight, z, sliceSize)] == provider.layerBlock) {
			int waterHeight = 2;
			int heightVariation = 5;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				waterHeight = yOff;
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
				if(currentBlock == provider.layerBlock) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
				} else {
					break;
				}
			}
			for(int yOff = waterHeight; yOff > 0; yOff--) {
				int y = layerHeight - yOff;
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		} else {
			int lowestBlock = 0;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
				if(currentBlock != provider.layerBlock) {
					lowestBlock = y;
					break;
				}
			}
			for(int y = lowestBlock; y < layerHeight - (layerHeight - lowestBlock) / 7.5f; y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		}
	}
}
