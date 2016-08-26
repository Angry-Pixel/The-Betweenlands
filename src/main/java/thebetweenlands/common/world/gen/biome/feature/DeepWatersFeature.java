package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class DeepWatersFeature extends BiomeFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] terrainNoise = new double[256];

	@Override
	public void initializeGenerators(Random rng, Biome biome) {
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			Biome biome) {
		this.terrainNoise = this.islandNoiseGen.getRegion(this.terrainNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.05D * 1.0D, 0.05D * 1.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, float terrainWeight,
			int pass) {
		if(pass == 0) {
			int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
			//Flatten terrain
			int lowestBlock = 0;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
				if(currentBlock != chunkGenerator.layerBlock) {
					lowestBlock = y;
					break;
				}
			}
			double noise = this.terrainNoise[x * 16 + z] / 12.0f;
			for(int y = lowestBlock; y < lerp(layerHeight - (layerHeight - lowestBlock) / 2.5f + noise * (layerHeight - lowestBlock) - 2, lowestBlock, terrainWeight); y++) {
				chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
			}
		}
	}
}
