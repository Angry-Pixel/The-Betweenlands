package thebetweenlands.world.biomes.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;

import java.util.Random;

public class PatchNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin mudNoiseGen;
	private double[] mudNoise = new double[256];
	private double scaleX, scaleY;
	private double mult = 1.0D / 1.6D;
	private double offset = 1.5D;
	private Block block;
	
	public PatchNoiseFeature(double scaleX, double scaleY, Block block) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.block = block;
	}
	
	public PatchNoiseFeature(double scaleX, double scaleY, Block block, double mult, double offset) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.block = block;
		this.mult = mult;
		this.offset = offset;
	}
	
	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.mudNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.mudNoise = this.mudNoiseGen.func_151599_a(this.mudNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, this.scaleX, this.scaleY, 1.0D);
	}

	@Override
	public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) {
		int sliceSize = chunkBlocks.length / 256;
		if(this.mudNoise[x * 16 + z] * this.mult + this.offset <= 0) {
			int y = WorldProviderBetweenlands.LAYER_HEIGHT + 20;
			for(int yo = 0; yo < WorldProviderBetweenlands.LAYER_HEIGHT + 20; yo++) {
				Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y - yo, z, sliceSize)];
				Block blockAbove = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y - yo + 1, z, sliceSize)];
				if(currentBlock == biome.topBlock && (blockAbove == Blocks.air || blockAbove == null)) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y - yo, z, sliceSize)] = this.block;
					break;
				}
			}
		}
	}

	@Override
	public void preReplaceStackBlocks(int x, int y, Block[] chunkBlocks,
			byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider, 
			BiomeGenBase[] chunksForGeneration, Random rng) { }
}
