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

public class FlatLandNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin landNoiseGen;
	private double[] landNoise = new double[256];
	
	private NoiseGeneratorPerlin riverNoiseGen;
	private double[] riverNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.landNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.riverNoiseGen = new NoiseGeneratorPerlin(rng, 2);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.landNoise = this.landNoiseGen.func_151599_a(this.landNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.06D, 0.06D, 1.0D);
		this.riverNoise = this.riverNoiseGen.func_151599_a(this.riverNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.032D, 0.032D, 1.0D);
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
		double noise = this.landNoise[x * 16 + z] / 18.0f;
		double riverNoise = Math.abs(this.riverNoise[x * 16 + z]) * 4.0D;
		riverNoise *= riverNoise * riverNoise * riverNoise * riverNoise;
		riverNoise *= 25.0D;
		int terrainHeight = (int)Math.ceil(Math.abs(noise * (layerHeight - lowestBlock)));
		/*if(riverNoise < 6.0f) {
			for(int y = lowestBlock; y < layerHeight - ((6.0D-riverNoise) / 3.0 / terrainHeight); y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		} else {
			for(int y = lowestBlock; y < layerHeight + terrainHeight; y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		}*/
		float riverThreshold = 6.0f * (terrainHeight + 2);
		double riverPercentage = 1.0D - (riverNoise / riverThreshold);
		if(riverNoise < riverThreshold) {
			for(int y = lowestBlock; y < layerHeight + terrainHeight - riverPercentage * (terrainHeight + ((riverThreshold - riverNoise) / 16.0D)); y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		} else {
			for(int y = lowestBlock; y < layerHeight + terrainHeight; y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
			}
		}
	}
}
