package thebetweenlands.world.biomes.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.BiomeGenBaseBetweenlands;

public class CoarseIslandNoiseFeature implements BiomeNoiseFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.islandNoise = this.islandNoiseGen.func_151599_a(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.1D * 2.0D, 0.1D * 2.0D, 1.0D);
	}

	@Override
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome) { }

	@Override
	public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome , ChunkProviderBetweenlands provider) {
		int sliceSize = chunkBlocks.length / 256;
		double noise = this.islandNoise[x * 16 + z] / 1.4f + 1.8f;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		if(noise <= 0 && chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, layerHeight, z, sliceSize)] == BLBlockRegistry.swampWater) {
			int minHeight = 2;
			int heightVariation = 5;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
				if(currentBlock == BLBlockRegistry.swampWater) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
				} else {
					break;
				}
			}
			for(int yOff = 0; yOff < minHeight; yOff++) {
				int y = layerHeight + yOff;
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
			for(int yOff = 0; yOff < -noise / 3.0f * heightVariation; yOff++) {
				int y = minHeight + layerHeight + yOff;
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		} else {
			int lowestBlock = 0;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
				if(currentBlock != BLBlockRegistry.swampWater) {
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
