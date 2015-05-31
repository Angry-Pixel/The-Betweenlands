package thebetweenlands.world.biomes.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;

import java.util.Random;

public class AlgaeNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin algaeNoiseGen;
	private double[] algaeNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.algaeNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.algaeNoise = this.algaeNoiseGen.func_151599_a(this.algaeNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}

	@Override
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) {
		int sliceSize = chunkBlocks.length / 256;
		if(this.algaeNoise[x * 16 + z] / 1.6f + 1.8f <= 0) {
			int y = WorldProviderBetweenlands.LAYER_HEIGHT;
			Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
			Block blockAbove = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y + 1, z, sliceSize)];
			if(currentBlock == provider.layerBlock && (blockAbove == null || blockAbove == Blocks.air)) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y + 1, z, sliceSize)] = BLBlockRegistry.algae;
			}
		}
	}

	@Override
	public void preReplaceStackBlocks(int x, int y, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) { }
}
