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

public class CragSpiresNoiseFeature extends BiomeNoiseFeature {
	private NoiseGeneratorPerlin spireNoiseGen;
	private double[] spireNoise = new double[256];

	@Override
	public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
		this.spireNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			BiomeGenBaseBetweenlands biome) {
		this.spireNoise = this.spireNoiseGen.func_151599_a(this.spireNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
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
		double noise = this.spireNoise[x * 16 + z] / 1.5f + 2.4f;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		if(chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, layerHeight, z, sliceSize)] != provider.layerBlock) {
			return;
		}
		int lowestBlock = 0;
		for(int yOff = 0; yOff < layerHeight; yOff++) {
			int y = layerHeight - yOff;
			Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
			if(currentBlock != provider.layerBlock) {
				lowestBlock = y;
				break;
			}
		}
		if(WorldProviderBetweenlands.LAYER_HEIGHT - lowestBlock < 3) {
			return;
		}
		if(-noise * 12 >= 1) {
			for(int y = lowestBlock; y < layerHeight; y++) {
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
				chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
			}
			int rockHeight = (int)Math.floor(-noise * 12);
			for(int yOff = 0; yOff < rockHeight; yOff++) {
				int y = layerHeight + yOff;
				chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
				if(yOff == rockHeight - 2) {
					chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_MOSSYCRAGROCK2;
				} else if(yOff == rockHeight - 1) {
					chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_MOSSYCRAGROCK1;
				} else {
					chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
				}
			}
		} else {
			boolean validSpire = false;
			for(int xo = -4; xo < 4; xo++) {
				for(int zo = -4; zo < 4; zo++) {
					int nx = x + xo;
					int nz = z + zo;
					nx = nx < 0 ? 0 : (nx > 15 ? 15 : nx);
					nz = nz < 0 ? 0 : (nz > 15 ? 15 : nz);
					double sNoise = this.spireNoise[nx * 16 + nz] / 1.5f + 2.4f;
					if(-sNoise * 12 >= 1) {
						validSpire = true;
						break;
					}
				}
			}
			if(validSpire) {
				int rockHeight = (int)Math.floor(-noise * 12);
				for(int y = lowestBlock; y < layerHeight + rockHeight; y++) {
					chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BLBlockRegistry.genericStone;
					chunkMeta[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = BlockGenericStone.META_CRAGROCK;
				}
			}
		}
	}
}
