package thebetweenlands.common.world.gen.biome.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;

import java.util.Random;

/**
 * Adds Cragrock spires to large water bodies
 * Modified to work as a feature type
 */
public class CragSpiresFeature extends DecoratorFeature {
	// TODO: set up mossy crag rock
	private BlockState cragrockDefault;
	private BlockState cragrockMossy1;
	private BlockState cragrockMossy2;

	private NoiseGeneratorPerlin spireNoiseGen;
	private double[] spireNoise = new double[256];

	// We'll just wait until we can call the ChunkGenerator before doing this
	public void initializeGenerators(long seed, int biome) {
		Random rng = new Random(seed);
		this.spireNoiseGen = new NoiseGeneratorPerlin(rng, 4);

		this.cragrockDefault = BlockRegistry.CRAGROCK.get().defaultBlockState();
		this.cragrockMossy1 = BlockRegistry.CRAGROCK.get().defaultBlockState();
		this.cragrockMossy2 = BlockRegistry.CRAGROCK.get().defaultBlockState();
	}

	public void generateNoise(int chunkX, int chunkZ, int biome) {
		this.spireNoise = this.spireNoiseGen.getRegion(this.spireNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkAccess chunkPrimer, ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration, int biome, BiomeWeights biomeWeights, EnumGeneratorPass pass) {
		if (pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z);
			double noise = this.spireNoise[x * 16 + z] / 1.5f * biomeWeight + 2.4f;
			int layerHeight = TheBetweenlands.LAYER_HEIGHT;
			if (chunkPrimer.getBlockState(new BlockPos(x, layerHeight, z)).getBlock() != chunkGenerator.fillfluid.getBlock()) {
				return;
			}
			int lowestBlock = 0;
			for (int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkPrimer.getBlockState(new BlockPos(x, y, z)).getBlock();
				// layerBlock replaced with set dim fluid
				if (currentBlock != chunkGenerator.fillfluid.getBlock()) {
					lowestBlock = y;
					break;
				}
			}
			// Using chunkGenerator.getSeaLevel() now
			if (chunkGenerator.getSeaLevel() - lowestBlock < 3) {
				return;
			}
			if (-noise * 12 >= 1) {
				for (int y = lowestBlock; y < layerHeight; y++) {
					chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
				}
				int rockHeight = (int) Math.floor(-noise * 12);
				for (int yOff = 0; yOff < rockHeight; yOff++) {
					int y = layerHeight + yOff;
					if (yOff == rockHeight - 2) {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockMossy2, false);
					} else if (yOff == rockHeight - 1) {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockMossy1, false);
					} else {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
					}
				}
			} else {
				boolean validSpire = false;
				for (int xo = -4; xo < 4; xo++) {
					for (int zo = -4; zo < 4; zo++) {
						int nx = x + xo;
						int nz = z + zo;
						// IDE told me to use standard library min instead, I don't think a func call is any faster so... ¯\_(ツ)_/¯
						nx = nx < 0 ? 0 : (nx > 15 ? 15 : nx);
						nz = nz < 0 ? 0 : (nz > 15 ? 15 : nz);
						double sNoise = this.spireNoise[nx * 16 + nz] * biomeWeight / 1.5f + 2.4f;
						if (-sNoise * 12 >= 1) {
							validSpire = true;
							break;
						}
					}
				}
				if (validSpire) {
					int rockHeight = (int) Math.floor(-noise * 12);
					for (int y = lowestBlock; y < layerHeight + rockHeight; y++) {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
					}
				}
			}
		}
	}
}
