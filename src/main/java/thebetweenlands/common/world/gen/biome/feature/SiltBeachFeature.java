package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;

/**
 * Adds silt beaches to low areas close to water
 */
public class SiltBeachFeature extends DecoratorFeature {
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
	public void initializeGenerators(long seed, int biome) {
		Random rng = new Random(seed);
		this.siltNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, int biome) {
		this.siltNoise = this.siltNoiseGen.getRegion(this.siltNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.03125D * 2.0D, 0.03125D * 2.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkAccess chunkPrimer,
								   ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration, int biome, BiomeWeights biomeWeights,
								   EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
			float biomeWeight = biomeWeights.get(x, z);
			if(this.siltNoise[x * 16 + z] / 1.6f + 1.5f <= 0 && biomeWeight <= this.terrainWeightThreshold) {
				int y = TheBetweenlands.LAYER_HEIGHT;
				Block currentBlock = chunkPrimer.getBlockState(new BlockPos(x, y, z)).getBlock();
				Block blockAbove = chunkPrimer.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
				if(currentBlock == chunkGenerator.betweenlandsBiomeProvider.BiomeFromID(biome).topBlock() && blockAbove == Blocks.AIR) {
					chunkPrimer.setBlockState(new BlockPos(x, y, z), BlockRegistry.SILT.get().defaultBlockState(),false);
				}
			}
		}
	}
}
