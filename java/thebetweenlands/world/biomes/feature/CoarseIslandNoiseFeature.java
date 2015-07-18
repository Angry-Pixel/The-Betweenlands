package thebetweenlands.world.biomes.feature;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;

import java.util.Random;

public class CoarseIslandNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];

	private NoiseGeneratorPerlin cragNoiseGen;
	private double[] cragNoise = new double[256];

	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.cragNoiseGen = new NoiseGeneratorPerlin(rng, 5);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.islandNoise = this.islandNoiseGen.func_151599_a(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 1.7D, 0.08D * 1.7D, 1.0D);
		this.cragNoise = this.cragNoiseGen.func_151599_a(this.cragNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.1D * 4.5D, 0.1D * 4.5D, 1.0D);
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
		double noise = this.islandNoise[x * 16 + z] / 0.9f + 2.1f;
		double cragNoise = this.cragNoise[x * 16 + z] / 2.1f + 2.0f;
		boolean isCrag = cragNoise <= 0;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		if(noise <= 0 && chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, layerHeight, z, sliceSize)] == provider.layerBlock) {
			int minHeight = 2;
			int heightVariation = 5;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
				if(currentBlock == provider.layerBlock) {
					if(isCrag) {
						chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
					} else {
						chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
					}
				} else {
					break;
				}
			}
			int maxHeight = (int) Math.ceil(-noise / 4.0f * heightVariation);
			for(int yOff = 0; yOff < maxHeight; yOff++) {
				int y = minHeight + layerHeight + yOff;
				if(isCrag) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
					if(yOff == maxHeight - 2) {
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_MOSSYCRAGROCK2;
					} else if(yOff == maxHeight - 1) {
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_MOSSYCRAGROCK1;
					} else {
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
					}
				} else {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
				}
			}
			for(int yOff = 0; yOff < minHeight; yOff++) {
				int y = layerHeight + yOff;
				if(isCrag) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
					if(yOff == minHeight - 1 && maxHeight >= 1) {
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_MOSSYCRAGROCK2;
					} else {
						chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
					}
				} else {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
				}
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
