package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;
import java.util.Optional;

import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.FlatLandGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.BiSimplexCache;
import thebetweenlands.common.world.gen.generators.util.BiSimplexData;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public class FlatLandGenerator extends EarlyGenerator<FlatLandGeneratorConfiguration> {

	// Cache so we don't have to re-create the simplex noise every execution
	protected final BiSimplexCache noiseCache = new BiSimplexCache(4, 2);
	
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

		// Get or create noise generators for this seed
		BiSimplexData noise = this.noiseCache.getNoise(seed);

		BLLegacyPerlinSimplexNoise landNoiseGen = noise.first();
		BLLegacyPerlinSimplexNoise riverNoiseGen = noise.second();
		
		// Compute all the noise values for this chunk
		
		ChunkAccess chunkAccess = context.chunkAccess();
		ChunkPos chunkPos = chunkAccess.getPos();
		
		FlatLandGeneratorConfiguration config = context.config();

		double[] landNoise = computeLandNoise(landNoiseGen, chunkPos, config.landNoiseScale());
		double[] riverNoise = computeRiverNoise(riverNoiseGen, chunkPos, config.riverNoiseScale());

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
					
					// If lowest block for this x/z is above this section, don't place anything
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
						// Important: disable locks since the section was already acquired by the chunk generator
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
		// If specified, only set blocks if the biome matches this
		Optional<Holder<Biome>> biomeLock = context.biome();
		
		final int waterLevel = config.waterLevel();
		final int terrainHeight = config.terrainHeight();
		
		int minHeightTotal = waterLevel;
		int maxHeightTotal = waterLevel;
		
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;
				
				// If the biome lock is present and the biome test fails, do not set blocks in this column
				if(biomeLock.isPresent() && biomeWeights.getBiome(x, z) != biomeLock.get()) {
					minBlockYOut[index] = waterLevel;
					maxBlockYOut[index] = waterLevel;
					continue;
				}
				
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
				float biomeWeight = biomeWeights.get(x, z, 0, 10);
				
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
	
	public static double[] computeLandNoise(BLLegacyPerlinSimplexNoise landNoise, ChunkPos pos, double scale) {
		double[] noise = EarlyGeneratorHelper.computeNoiseRaw(landNoise, pos, scale);

		
		for(int i = 256; i-- != 0;) {
			noise[i] /= 18.0D; // account for X / 18.0 later in 1.12
		}
		
		return noise;
	}
	
	public static double[] computeRiverNoise(BLLegacyPerlinSimplexNoise riverNoise, ChunkPos pos, double scale) {
		double[] noise = EarlyGeneratorHelper.computeNoiseRaw(riverNoise, pos, scale);
		
		for(int i = 256; i-- != 0;) {
			double riverNoiseValue = noise[i];
			riverNoiseValue = Math.abs(riverNoiseValue) * 4.0D;
			riverNoiseValue = riverNoiseValue * riverNoiseValue * riverNoiseValue * riverNoiseValue * riverNoiseValue;
			riverNoiseValue *= 25.0D;
			noise[i] = riverNoiseValue;
		}
		
		return noise;
	}
}
