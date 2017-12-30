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

public class Marsh1Feature extends BiomeFeature {
	protected NoiseGeneratorPerlin islandNoiseGen;
	protected double[] islandNoise = new double[256];
	protected NoiseGeneratorPerlin fuzzNoiseGen;
	protected double[] fuzzNoise = new double[256];

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		Random rng = new Random(seed);
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.fuzzNoiseGen = new NoiseGeneratorPerlin(rng, 8);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.islandNoise = this.islandNoiseGen.getRegion(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
		this.fuzzNoise = this.fuzzNoiseGen.getRegion(this.fuzzNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 10.5D, 10.5D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z, 0, 5);
			double noise = (this.islandNoise[x * 16 + z] / 1.4f +
					this.fuzzNoise[x * 16 + z] / 1.4f) * Math.pow(biomeWeight, 4) + 1.8f;
			int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
			if(noise <= 0 && chunkPrimer.getBlockState(x, layerHeight, z).getBlock() == chunkGenerator.layerBlock) {
				int waterHeight = 2;
				for(int yOff = 0; yOff < layerHeight; yOff++) {
					int y = layerHeight - yOff;
					waterHeight = yOff;
					Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
					if(currentBlock == chunkGenerator.layerBlock) {
						chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
					} else {
						break;
					}
				}
				for(int yOff = waterHeight; yOff > 0; yOff--) {
					int y = layerHeight - yOff;
					chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
				}
			}
		}
	}
}
