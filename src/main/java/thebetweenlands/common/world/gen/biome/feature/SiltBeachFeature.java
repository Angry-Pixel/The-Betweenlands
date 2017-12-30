package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

/**
 * Adds silt beaches to low areas close to water
 */
public class SiltBeachFeature extends BiomeFeature {
	private NoiseGeneratorPerlin siltNoiseGen;
	private double[] siltNoise = new double[256];

	private final float terrainWeightThreshold;

	public SiltBeachFeature() {
		this(1.0F);
	}

	public SiltBeachFeature(float terrainWeightThreshold) {
		this.terrainWeightThreshold = terrainWeightThreshold;
	}

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		Random rng = new Random(seed);
		this.siltNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.siltNoise = this.siltNoiseGen.getRegion(this.siltNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.03125D * 2.0D, 0.03125D * 2.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
			float biomeWeight = biomeWeights.get(x, z);
			if(this.siltNoise[x * 16 + z] / 1.6f + 1.5f <= 0 && biomeWeight <= this.terrainWeightThreshold) {
				int y = WorldProviderBetweenlands.LAYER_HEIGHT;
				Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
				Block blockAbove = chunkPrimer.getBlockState(x, y + 1, z).getBlock();
				if(currentBlock == biome.topBlock.getBlock() && (blockAbove == null || blockAbove == Blocks.AIR)) {
					chunkPrimer.setBlockState(x, y, z, BlockRegistry.SILT.getDefaultState());
				}
			}
		}
	}
}
