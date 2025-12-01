package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.MarshIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.BiSimplexCache;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public class MarshIslandsGenerator extends EarlyGenerator<MarshIslandsGeneratorConfiguration> {

	public MarshIslandsGenerator(Codec<MarshIslandsGeneratorConfiguration> codec) {
		super(codec);
	}

	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(MarshIslandsGeneratorConfiguration config) {
		return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS);
	}

	// Cache so we don't have to re-create the simplex noise every execution
	// Technically, one volatile field should be equally safe (and faster) because the data is immutable and only read once.
	// However, the atomic reference makes it very clear that this data may be read on multiple threads at once, 
	//          and is much harder to accidentally make thread-unsafe in future.
	protected final AtomicReference<BiSimplexCache> noiseCacheReference = new AtomicReference<>();

	protected BiSimplexCache getNoise(long seed) {
		return this.noiseCacheReference.updateAndGet((biSimplexCache) -> {
			if(biSimplexCache != null && biSimplexCache.worldSeed() == seed) {
				return biSimplexCache;
			}
			
			// If it doesn't exist or has a different seed than expected, create noise with the correct seed
			LegacyRandomSource random = new LegacyRandomSource(seed);

			BLLegacyPerlinSimplexNoise islandNoiseGen = new BLLegacyPerlinSimplexNoise(random, 4);
			BLLegacyPerlinSimplexNoise fuzzNoiseGen = new BLLegacyPerlinSimplexNoise(random, 8);
			return new BiSimplexCache(seed, islandNoiseGen, fuzzNoiseGen);
		});
	}

	protected static record MarshIslandColumns(int globalMinBlockY, int[] minBlockY) {}
	
	@Override
	public boolean place(EarlyGenerationContext<MarshIslandsGeneratorConfiguration> context) {
		MarshIslandsGeneratorConfiguration config = context.config();
		
		long seed = context.worldSeed();

		// Get or create noise generators for this seed
		BiSimplexCache noise = getNoise(seed);

		BLLegacyPerlinSimplexNoise islandNoiseGen = noise.first();
		BLLegacyPerlinSimplexNoise fuzzNoiseGen = noise.second();
		
		// Compute all the noise values for this chunk
		
		ChunkAccess chunkAccess = context.chunkAccess();
		ChunkPos chunkPos = chunkAccess.getPos();

		double[] islandNoise = EarlyGeneratorHelper.computeNoiseRaw(islandNoiseGen, chunkPos, config.islandNoiseScale());
		double[] fuzzNoise = EarlyGeneratorHelper.computeNoiseRaw(fuzzNoiseGen, chunkPos, config.fuzzNoiseScale());
		
		MarshIslandColumns columnMask = computeColumnMask(context, chunkPos, islandNoise, fuzzNoise);

		final int islandLevel = config.islandLevel();
		
		// No blocks need to be placed
		if(columnMask.globalMinBlockY() + 1 > islandLevel) {
			return false;
		}
		
		final int[] minBlockY = columnMask.minBlockY();
		
		// Minimum section index (inclusive) that needs blocks placed
		int minSectionIndex = chunkAccess.getSectionIndex(columnMask.globalMinBlockY() + 1);

		// Maximum section index (inclusive) that needs blocks placed
		int maxSectionIndex = chunkAccess.getSectionIndex(islandLevel);
		
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
					
					final int yMax = Math.min(islandLevel - sectionMinY, 15);
					
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
	 * Computes the min (exclusive) height that blocks will need to be placed in each column, and the global minimum height
	 */
	public static MarshIslandColumns computeColumnMask(EarlyGenerationContext<MarshIslandsGeneratorConfiguration> context, ChunkPos chunkPos, double[] islandNoise, double[] fuzzNoise) {
		// Get info on where these blocks should even go
		MarshIslandsGeneratorConfiguration config = context.config();
		// Use heightmap so we don't have to manually check each block
		Heightmap oceanfloorHeightmap = context.chunkHeightmaps().oceanfloorHeightmap();
		// Get biome weights
		BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().orElseThrow();
		// If specified, only set blocks if the biome matches this
		Optional<Holder<Biome>> biomeLock = context.biome();
		
		final int islandHeight = config.islandLevel();
		
		int[] minBlockY = new int[256];
		int globalMinBlockY = islandHeight;

		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;
				
				// If the biome lock is present and the biome test fails, do not set blocks in this column
				if(biomeLock.isPresent() && biomeWeights.getBiome(x, z) != biomeLock.get()) {
					minBlockY[index] = islandHeight;
					continue;
				}
				
				final int lowestBlock = oceanfloorHeightmap.getHighestTaken(x, z);
				
				// If the island would be underground, do not set blocks in this column
				if(lowestBlock >= islandHeight) {
					minBlockY[index] = islandHeight;
					continue;
				}
				
				float biomeWeight = biomeWeights.get(x, z, 0, 5);
				
				double noise = (islandNoise[index] + fuzzNoise[index]) / 1.4f * Math.pow(biomeWeight, 4) + 1.8f;
				
				if(noise <= 0) {
					minBlockY[index] = lowestBlock;
					globalMinBlockY = Math.min(globalMinBlockY, lowestBlock);
				} else {
					minBlockY[index] = islandHeight;
				}
			}
		}
		
		return new MarshIslandColumns(globalMinBlockY, minBlockY);
	}
}
