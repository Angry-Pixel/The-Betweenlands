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

public class SiltNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin siltNoiseGen;
	private double[] siltNoise = new double[256];
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.siltNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.siltNoise = this.siltNoiseGen.func_151599_a(this.siltNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.03125D * 2.0D, 0.03125D * 2.0D, 1.0D);
	}

	@Override
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) {
		int sliceSize = chunkBlocks.length / 256;
		if(this.siltNoise[x * 16 + z] / 1.6f + 1.5f <= 0) {
			int y = WorldProviderBetweenlands.LAYER_HEIGHT;
			Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
			Block blockAbove = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y + 1, z, sliceSize)];
			if(currentBlock == biome.topBlock && (blockAbove == null || blockAbove == Blocks.air)) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.silt;
			}
		}
	}

	@Override
	public void preReplaceStackBlocks(int x, int y, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) { }
}
