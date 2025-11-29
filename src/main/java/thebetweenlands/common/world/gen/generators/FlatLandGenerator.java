package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.generators.config.FlatLandGeneratorConfiguration;

// TODO fix this up so we don't need to generate new perlin noise every single time
// TODO biome tapering
// TODO move to earlier in generation somehow - we preferably want this stuff to happen before biome blocks get replaced
public class FlatLandGenerator extends EarlyGenerator<FlatLandGeneratorConfiguration> {

	public FlatLandGenerator(Codec<FlatLandGeneratorConfiguration> codec) {
		super(codec);
	}

	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(FlatLandGeneratorConfiguration config) {
		return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS);
	}
	
	@Override
	public boolean place(EarlyGenerationContext<FlatLandGeneratorConfiguration> context) {
		long seed = context.worldSeed();
		
		// Create noise generators

		LegacyRandomSource random = new LegacyRandomSource(seed);
		
		// TODO Fix this up so we don't need to create new perlin noise every single time
		PerlinNoise landNoiseGen = PerlinNoise.create(random, IntStream.rangeClosed(-3, 0));
		PerlinNoise riverNoiseGen = PerlinNoise.create(random, IntStream.rangeClosed(-1, 0));
		
		// Compute all the noise values for this chunk
		
		ChunkAccess chunkAccess = context.chunkAccess();
		ChunkPos chunkPos = chunkAccess.getPos();

		double[] landNoise = computeLandNoise(landNoiseGen, chunkPos.x, chunkPos.z);
		double[] riverNoise = computeRiverNoise(riverNoiseGen, chunkPos.x, chunkPos.z);

		int[] minBlockY = new int[256];
		int[] maxBlockY = new int[256];
		
		IntIntPair minHeightMaxHeight = computeColumnBlocks(context, chunkPos, landNoise, riverNoise, minBlockY, maxBlockY);
		int totalMinBlockY = minHeightMaxHeight.leftInt();
		int totalMaxBlockY = minHeightMaxHeight.rightInt();

//		TheBetweenlands.LOGGER.info("Generated noise values for chunk {}: Global Min = {}, Global Max = {}, Min Array = {}, Max Array = {}", chunkPos, totalMinBlockY, totalMaxBlockY, minBlockY, maxBlockY);
		
		// If the min block is above the max block, then no blocks can be placed
		if(totalMinBlockY + 1 > totalMaxBlockY - 1) {
			return false;
		}

		// Minimum section index (inclusive) that needs blocks placed
		int minSectionIndex = chunkAccess.getSectionIndex(totalMinBlockY + 1);

		// Maximum section index (inclusive) that needs blocks placed
		int maxSectionIndex = chunkAccess.getSectionIndex(totalMaxBlockY - 1);
		
		// The block we will be setting
		BlockState terrainBlock = context.blockGenerator().defaultTerrainState();
		
		// The heightmaps (for updating)
		ChunkHeightmaps heightmaps = context.chunkHeightmaps();
		
		for(int sectionIndex = minSectionIndex; sectionIndex <= maxSectionIndex; ++sectionIndex) {
			// The section we'll be setting blocks in
			LevelChunkSection section = chunkAccess.getSection(sectionIndex);
			
			// The y value of block 0 in this section
			int sectionMinY = SectionPos.sectionToBlockCoord(chunkAccess.getSectionYFromSectionIndex(sectionIndex));
			
			for(int x = 0; x < 16; ++x) {
				for(int z = 0; z < 16; ++z) {
					final int index = x * 16 + z;
					
					final int yMin = Math.max(
							minBlockY[index] - sectionMinY + 1,
							0
						);
					
					// if lowest block for this x/z is above this section, don't place anything
					if(yMin >= 16) {
						continue;
					}
					
					final int yMax = Math.min(
							maxBlockY[index] - sectionMinY - 1,
							15
						);
					
					// if highest block for this x/z is below this section, don't place anything
					if(yMax < 0) {
						continue;
					}
					
					for(int y = yMin; y <= yMax; ++y) {
						// Important: disable locks since the section is already acquired by the chunk generator
						section.setBlockState(x, y, z, terrainBlock, false);
						heightmaps.update(x, sectionMinY + y, z, terrainBlock);
					}
				}
			}
		}
		
		return true;
	}

	/**
	 * Computes the min (exclusive) and max (exclusive) height that blocks will need to be placed in each column
	 * @param minBlockYOut the output array for the minimum height that blocks need to be placed in each column
	 * @param maxBlockYOut the output array for the maximum height that blocks need to be placed in each column
	 * @return an int pair containing the global minimum height to set (left) and global maximum height to set (right) of the entire chunk
	 */
	public IntIntPair computeColumnBlocks(EarlyGenerationContext<FlatLandGeneratorConfiguration> context, ChunkPos chunkPos, double[] landNoise, double[] riverNoise, int[] minBlockYOut, int[] maxBlockYOut) {
		// Get info on where these blocks should even go
		FlatLandGeneratorConfiguration config = context.config();
		// Use heightmap so we don't have to manually check each block
		Heightmap oceanfloorHeightmap = context.chunkHeightmaps().oceanfloorHeightmap();
		// Get biome weights
		BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().orElseThrow();

		final int waterLevel = config.waterLevel();
		final int terrainHeight = config.terrainHeight();
		
		int minHeightTotal = waterLevel;
		int maxHeightTotal = waterLevel;
		
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;
				
				// Used to "flatten" the terrain to the water level (so we don't leave gaps between the terrain and seafloor)
				final int lowestBlock = Math.min(oceanfloorHeightmap.getHighestTaken(x, z), waterLevel);
				
				// Store lowest block out
				minBlockYOut[index] = lowestBlock;
				minHeightTotal = Math.min(minHeightTotal, lowestBlock);

				// Fetch noise values
				final double landNoiseValue = landNoise[index];
				final double riverNoiseValue = riverNoise[index];

				// Calculate chance of a river spawning, and how much it'll affect the terrain height
				int columnTerrainHeight = (int)Math.ceil(Math.abs(landNoiseValue * (waterLevel - lowestBlock + terrainHeight)));
				float riverThreshold = 6.0f * (columnTerrainHeight + 2);
				double riverPercentage = 1.0D - (riverNoiseValue / riverThreshold);
				
				// Get biome weighting (for blending)
				float biomeWeight = biomeWeights.get(x, z);
				
				// Calculate lerp weight
				float weight = (Math.min(biomeWeight + 0.5F, 1.0F) - 0.5F) * 2.0F;
				
				// If weight is less than or equal to 0, don't bother calculating what the max height would have been
				if(weight <= 0.0F) {
					maxBlockYOut[index] = lowestBlock;
					continue;
				}
				
				// Calculate max height
				final double maxHeight;
				if(riverNoiseValue < riverThreshold) {
					// If a river should be generating, generate a river (w/ biome blending)
					maxHeight = waterLevel + columnTerrainHeight - riverPercentage * (columnTerrainHeight + ((riverThreshold - riverNoiseValue) / 16.0D));
				} else {
					// If not, just blend the terrain
					maxHeight = waterLevel + columnTerrainHeight;
				}
				
				final int maxBlockY = Mth.ceil(Mth.clampedLerp(lowestBlock, maxHeight, weight));
				maxBlockYOut[index] = maxBlockY;
				maxHeightTotal = Math.max(maxHeightTotal, maxBlockY);
			}
		}
		
		return IntIntPair.of(minHeightTotal, maxHeightTotal);
	}
	
	public static double[] computeLandNoise(PerlinNoise landNoise, int chunkX, int chunkZ) {
		double[] noise = new double[256];
		double scale = 1.0; // 1.0 / 18.0 - account for / 18.0 later in 1.12
		
		for (int i = 0; i < 4; i++) {
			ImprovedNoise improvednoise = landNoise.getOctaveNoise(i);
			if (improvednoise != null) {
				for(int x = 0; x < 16; ++x) {
					final int worldX = (chunkX << 4) + x;
					for(int z = 0; z < 16; ++z) {
						final int worldZ = (chunkZ << 4) + z;
						
						final double noiseValue = improvednoise.noise(PerlinNoise.wrap(0.06D * worldX * scale), PerlinNoise.wrap(1.0D * 10.0D * scale), PerlinNoise.wrap(0.06D * worldZ * scale), 1.0D, -1.0D) / scale;
						noise[x * 16 + z] += noiseValue / 18.0D; // account for X / 18.0 later in 1.12
					}
				}
			}
		
			scale /= 2.0;
		}
		
		return noise;
	}

	public static double[] computeRiverNoise(PerlinNoise riverNoise, int chunkX, int chunkZ) {
		double[] noise = new double[256];
		double scale = 1.0;
		
		for (int i = 0; i < 2; i++) {
			ImprovedNoise improvednoise = riverNoise.getOctaveNoise(i);
			if (improvednoise != null) {
				for(int x = 0; x < 16; ++x) {
					final int worldX = (chunkX << 4) + x;
					for(int z = 0; z < 16; ++z) {
						final int worldZ = (chunkZ << 4) + z;
						noise[x * 16 + z] += improvednoise.noise(PerlinNoise.wrap(0.032D * worldX * scale), PerlinNoise.wrap(1.0D * 10.0D * scale), PerlinNoise.wrap(0.032D * worldZ * scale), 1.0D, -1.0D) / scale;
					}
				}
			}
		
			scale /= 2.0;
		}
		
		for(int x = 16; x-- != 0;) {
			for(int z = 16; z-- != 0;) {
				double riverNoiseValue = noise[x * 16 + z];
				riverNoiseValue = Math.abs(riverNoiseValue) * 4.0D;
				riverNoiseValue = riverNoiseValue * riverNoiseValue * riverNoiseValue * riverNoiseValue * riverNoiseValue;
				riverNoiseValue *= 25.0D;
				noise[x * 16 + z] = riverNoiseValue;
			}
		}
		
		return noise;
	}
	
}
