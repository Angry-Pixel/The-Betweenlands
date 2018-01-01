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
 * Adds coarse, corroded islands to larger water bodies
 */
public class CoarseIslandsFeature extends BiomeFeature {
	private final IBlockState cragrockDefault = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.DEFAULT);
	private final IBlockState cragrockMossy1 = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_1);
	private final IBlockState cragrockMossy2 = BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_2);

	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];

	private NoiseGeneratorPerlin cragNoiseGen;
	private double[] cragNoise = new double[256];

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		Random rng = new Random(seed);
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.cragNoiseGen = new NoiseGeneratorPerlin(rng, 5);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.islandNoise = this.islandNoiseGen.getRegion(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 0.6D, 0.08D * 0.6D, 1.0D);
		this.cragNoise = this.cragNoiseGen.getRegion(this.cragNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.1D * 4.5D, 0.1D * 4.5D, 1.0D);
	}

	public double getIslandNoiseAt(int inChunkX, int inChunkZ, float biomeWeight) {
		double islandNoise = this.islandNoise[inChunkX * 16 + inChunkZ] / 0.9f * biomeWeight + 2.1f;
		return islandNoise;
	}
	
	public double getCragrockNoiseAt(int inChunkX, int inChunkZ) {
		double cragNoise = this.cragNoise[inChunkX * 16 + inChunkZ] / 2.1f + 2.0f;
		return cragNoise;
	}
	
	public boolean isIslandAt(int inChunkX, int inChunkZ, float biomeWeight) {
		return this.getIslandNoiseAt(inChunkX, inChunkZ, biomeWeight) <= 0;
	}
	
	public boolean isIslandCragrockAt(int inChunkX, int inChunkZ) {
		return this.getCragrockNoiseAt(inChunkX, inChunkZ) <= 0;
	}
	
	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z, 2, 12);
			double islandNoise = this.getIslandNoiseAt(x, z, biomeWeight);
			double cragNoise = this.getCragrockNoiseAt(x, z);
			boolean isCrag = cragNoise <= 0;
			int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
			if(islandNoise <= 0 && chunkPrimer.getBlockState(x, layerHeight, z).getBlock() == chunkGenerator.layerBlock) {
				int minHeight = 1;
				int heightVariation = 4;
				for(int yOff = 0; yOff < layerHeight; yOff++) {
					int y = layerHeight - yOff;
					Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
					if(currentBlock == chunkGenerator.layerBlock) {
						if(isCrag) {
							chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
						} else {
							chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
						}
					} else {
						break;
					}
				}
				int islandHeight = (int) Math.ceil(-islandNoise / 4.0f * heightVariation);
				int maxHeight = islandHeight + (isCrag && -islandNoise / 4.0F * heightVariation >= 1.25F ? 1 : 0);
				for(int yOff = 0; yOff < maxHeight; yOff++) {
					int y = minHeight + layerHeight + yOff;
					if(isCrag) {
						if(yOff == maxHeight - 2) {
							chunkPrimer.setBlockState(x, y, z, this.cragrockMossy2);
						} else if(yOff == maxHeight - 1) {
							chunkPrimer.setBlockState(x, y, z, this.cragrockMossy1);
						} else {
							chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
						}
					} else {
						chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
					}
				}
				for(int yOff = 0; yOff < minHeight; yOff++) {
					int y = layerHeight + yOff;
					if(isCrag) {
						if(yOff == minHeight - 1 && maxHeight >= 1) {
							chunkPrimer.setBlockState(x, y, z, this.cragrockMossy2);
						} else {
							chunkPrimer.setBlockState(x, y, z, this.cragrockDefault);
						}
					} else {
						chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
					}
				}
			} else {
				int lowestBlock = 0;
				for(int yOff = 0; yOff < layerHeight; yOff++) {
					int y = layerHeight - yOff;
					Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
					if(currentBlock != chunkGenerator.layerBlock) {
						lowestBlock = y;
						break;
					}
				}
				for(int y = lowestBlock; y < lerp(layerHeight - (layerHeight - lowestBlock) / 3.5f, lowestBlock, biomeWeight); y++) {
					chunkPrimer.setBlockState(x, y, z, chunkGenerator.baseBlockState);
				}
			}
		}
	}
}
