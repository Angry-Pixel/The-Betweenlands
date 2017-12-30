package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

/**
 * Adds Cragrock spires to large water bodies
 */
public class CragSpiresFeature extends BiomeFeature {
	private final IBlockState cragrockDefault = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.DEFAULT);
	private final IBlockState cragrockMossy1 = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_1);
	private final IBlockState cragrockMossy2 = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_2);

	private NoiseGeneratorPerlin spireNoiseGen;
	private double[] spireNoise = new double[256];

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		Random rng = new Random(seed);
		this.spireNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.spireNoise = this.spireNoiseGen.getRegion(this.spireNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z);
			double noise = this.spireNoise[x * 16 + z] / 1.5f * biomeWeight + 2.4f;
			int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
			if(chunkPrimer.getBlockState(x, layerHeight, z).getBlock() != chunkGenerator.layerBlock) {
				return;
			}
			int lowestBlock = 0;
			for(int yOff = 0; yOff < layerHeight; yOff++) {
				int y = layerHeight - yOff;
				Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
				if(currentBlock != chunkGenerator.layerBlock) {
					lowestBlock = y;
					break;
				}
			}
			if(WorldProviderBetweenlands.LAYER_HEIGHT - lowestBlock < 3) {
				return;
			}
			if(-noise * 12 >= 1) {
				for(int y = lowestBlock; y < layerHeight; y++) {
					chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
				}
				int rockHeight = (int)Math.floor(-noise * 12);
				for(int yOff = 0; yOff < rockHeight; yOff++) {
					int y = layerHeight + yOff;
					if(yOff == rockHeight - 2) {
						chunkPrimer.setBlockState(x, y, z, this.cragrockMossy2);
					} else if(yOff == rockHeight - 1) {
						chunkPrimer.setBlockState(x, y, z, this.cragrockMossy1);
					} else {
						chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
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
						double sNoise = this.spireNoise[nx * 16 + nz] * biomeWeight / 1.5f + 2.4f;
						if(-sNoise * 12 >= 1) {
							validSpire = true;
							break;
						}
					}
				}
				if(validSpire) {
					int rockHeight = (int)Math.floor(-noise * 12);
					for(int y = lowestBlock; y < layerHeight + rockHeight; y++) {
						chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
					}
				}
			}
		}
	}
}
