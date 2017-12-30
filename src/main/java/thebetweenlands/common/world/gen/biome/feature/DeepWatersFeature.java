package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

public class DeepWatersFeature extends BiomeFeature {
	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] terrainNoise = new double[256];

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		Random rng = new Random(seed);
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ,
			Biome biome) {
		this.terrainNoise = this.islandNoiseGen.getRegion(this.terrainNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.05D * 1.0D, 0.05D * 1.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z);
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
			for(int y = lowestBlock; y < lerp(layerHeight - (layerHeight - lowestBlock) / 2.5f + noise * (layerHeight - lowestBlock) - 2, lowestBlock, biomeWeight); y++) {
				chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
			}
		}
	}
}
