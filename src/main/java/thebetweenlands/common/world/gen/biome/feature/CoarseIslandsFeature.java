package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;

/**
 * Adds coarse, corroded islands to larger water bodies
 */
public class CoarseIslandsFeature extends DecoratorFeature {
	private BlockState cragrockDefault;
	private BlockState cragrockMossy1;
	private BlockState cragrockMossy2;

	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];

	private NoiseGeneratorPerlin cragNoiseGen;
	private double[] cragNoise = new double[256];

	@Override
	public void initializeGenerators(long seed, int biome) {
		Random rng = new Random(seed);
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		this.cragNoiseGen = new NoiseGeneratorPerlin(rng, 5);

		this.cragrockDefault = BlockRegistry.CRAGROCK.get().defaultBlockState();
		this.cragrockMossy1 = BlockRegistry.CRAGROCK.get().defaultBlockState();
		this.cragrockMossy2 = BlockRegistry.CRAGROCK.get().defaultBlockState();
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, int biome) {
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
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkAccess chunkPrimer,
								   ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration, int biome, BiomeWeights biomeWeights,
								   EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS) {
			float biomeWeight = biomeWeights.get(x, z, 2, 12);
			double islandNoise = this.getIslandNoiseAt(x, z, biomeWeight);
			double cragNoise = this.getCragrockNoiseAt(x, z);
			boolean isCrag = cragNoise <= 0;
			int layerHeight = TheBetweenlands.LAYER_HEIGHT;
			if(islandNoise <= 0 && chunkPrimer.getBlockState(new BlockPos(x, layerHeight, z)).getBlock() == chunkGenerator.fillfluid.getBlock()) {
				int minHeight = 1;
				int heightVariation = 4;
				for(int yOff = 0; yOff < layerHeight; yOff++) {
					int y = layerHeight - yOff;
					Block currentBlock = chunkPrimer.getBlockState(new BlockPos(x, y, z)).getBlock();
					if(currentBlock == chunkGenerator.fillfluid.getBlock()) {
						if(isCrag) {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
						} else {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), chunkGenerator.baseBlockState, false);
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
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockMossy2, false);
						} else if(yOff == maxHeight - 1) {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockMossy1, false);
						} else {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
						}
					} else {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), chunkGenerator.baseBlockState, false);
					}
				}
				for(int yOff = 0; yOff < minHeight; yOff++) {
					int y = layerHeight + yOff;
					if(isCrag) {
						if(yOff == minHeight - 1 && maxHeight >= 1) {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockMossy2, false);
						} else {
							chunkPrimer.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
						}
					} else {
						chunkPrimer.setBlockState(new BlockPos(x, y, z), chunkGenerator.baseBlockState, false);
					}
				}
			} else {
				int lowestBlock = 0;
				for(int yOff = 0; yOff < layerHeight; yOff++) {
					int y = layerHeight - yOff;
					Block currentBlock = chunkPrimer.getBlockState(new BlockPos(x, y, z)).getBlock();
					if(currentBlock != chunkGenerator.fillfluid.getBlock()) {
						lowestBlock = y;
						break;
					}
				}
				for(int y = lowestBlock; y < lerp(layerHeight - (layerHeight - lowestBlock) / 3.5f, lowestBlock, biomeWeight); y++) {
					chunkPrimer.setBlockState(new BlockPos(x, y, z), chunkGenerator.baseBlockState, false);
				}
			}
		}
	}
}
