package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

/**
 * Takes all base terrain below the water height, moves it above the water and creates small rivers
 */
public class FlatLandFeature extends BiomeFeature {
	private final int waterHeight;

	private NoiseGeneratorPerlin landNoiseGen;
	private double[] landNoise = new double[256];

	private NoiseGeneratorPerlin riverNoiseGen;
	private double[] riverNoise = new double[256];

	public FlatLandFeature(int waterHeight) {
		this.waterHeight = waterHeight;
	}

	@Override
	public void initializeGenerators(Random rng, Biome biome) {
		this.landNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.riverNoiseGen = new NoiseGeneratorPerlin(rng, 2);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.landNoise = this.landNoiseGen.getRegion(this.landNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.06D, 0.06D, 1.0D);
		this.riverNoise = this.riverNoiseGen.getRegion(this.riverNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.032D, 0.032D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, float terrainWeight,
			int pass) {
		if(pass == 0) {
			//Flatten terrain
			int lowestBlock = 0;
			for(int yOff = 0; yOff < this.waterHeight; yOff++) {
				int y = this.waterHeight - yOff;
				Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
				if(currentBlock != chunkGenerator.layerBlock && currentBlock != Blocks.AIR) {
					lowestBlock = y;
					break;
				}
			}
			double noise = this.landNoise[x * 16 + z] / 18.0f;
			double riverNoise = Math.abs(this.riverNoise[x * 16 + z]) * 4.0D;
			riverNoise *= riverNoise * riverNoise * riverNoise * riverNoise;
			riverNoise *= 25.0D;
			int terrainHeight = (int)Math.ceil(Math.abs(noise * (this.waterHeight - lowestBlock)));
			float riverThreshold = 6.0f * (terrainHeight + 2);
			double riverPercentage = 1.0D - (riverNoise / riverThreshold);
			if(riverNoise < riverThreshold) {
				for(int y = lowestBlock; y < lerp((this.waterHeight + terrainHeight - riverPercentage * (terrainHeight + ((riverThreshold - riverNoise) / 16.0D))), lowestBlock, Math.sqrt(terrainWeight)); y++) {
					chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
				}
			} else {
				for(int y = lowestBlock; y < lerp((this.waterHeight + terrainHeight), lowestBlock, Math.sqrt(terrainWeight)); y++) {
					chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
				}
			}
		}
	}
}
