package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.BlockGenerator;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.generators.config.CoarseIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.ColumnVolumeResult;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.ColumnVolumeResult.VolumeBlockstateProvider;
import thebetweenlands.common.world.gen.util.BiSimplexData;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.config.BiSimplexNoiseConfiguration;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseSettings;

public class CoarseIslandsGenerator extends EarlyGenerator<CoarseIslandsGeneratorConfiguration> {

	public CoarseIslandsGenerator(Codec<CoarseIslandsGeneratorConfiguration> codec) {
		super(codec);
	}
	
	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(CoarseIslandsGeneratorConfiguration config) {
		return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS);
	}
	
	@Override
	public boolean place(EarlyGenerationContext<CoarseIslandsGeneratorConfiguration> context) {
		// Get the chunk
		ChunkAccess chunkAccess = context.chunkAccess();
		ChunkPos chunkPos = chunkAccess.getPos();
		
		// Get the noise values for this chunk
		CoarseIslandNoise noise = this.computeNoise(context);
		
		// Compute the blocks that need to be placed in each column
		CoarseIslandVolumeResult columnResult = computeColumnBlocks(context, chunkPos, noise);
		
		// Place blocks
		return this.placeIslandVolumeBlocks(columnResult, chunkAccess, context.blockGenerator(), context.chunkHeightmaps());
	}

	public record CoarseIslandNoise(double[] islandNoiseScaled, double[] cragNoiseScaled) {}
	
	public CoarseIslandNoise computeNoise(EarlyGenerationContext<CoarseIslandsGeneratorConfiguration> context) {
		CoarseIslandsGeneratorConfiguration config = context.config();
		long seed = context.worldSeed();
		ChunkPos chunkPos = context.chunkAccess().getPos();

		// Get biome weights
		BiomeWeights weights = context.extraChunkInfo().biomeWeights().orElseThrow();
		
		// Get or create noise generators for this seed
		BiSimplexNoiseConfiguration noiseConfig = config.noiseConfig();
		BiSimplexData noiseData = noiseConfig.getNoise(seed);

		// Compute island noise values
		SimplexNoiseSettings islandNoiseSettings = noiseConfig.firstNoiseSettings();
		double[] islandNoiseScaled = EarlyGeneratorHelper.computeNoiseFromSettings(noiseData.first(), chunkPos, islandNoiseSettings, (x, z) -> weights.get(x, z, 2, 12));

		// Compute crag noise values
		SimplexNoiseSettings cragNoiseSettings = noiseConfig.secondNoiseSettings();
		double[] cragNoiseScaled = EarlyGeneratorHelper.computeNoiseFromSettings(noiseData.first(), chunkPos, cragNoiseSettings);
		
		return new CoarseIslandNoise(islandNoiseScaled, cragNoiseScaled);
	}
	
	public static record CoarseIslandVolumeResult(ColumnVolumeResult columns, boolean[] isCrag) {}
	
	/**
	 * Computes the min (exclusive) and max (exclusive) height that blocks will need to be placed in each column
	 * @return a {@linkplain CoarseIslandVolumeResult} describing the blocks that need to be placed in each column
	 */
	public CoarseIslandVolumeResult computeColumnBlocks(EarlyGenerationContext<CoarseIslandsGeneratorConfiguration> context, ChunkPos chunkPos, CoarseIslandNoise noise) {
		// Get info on where these blocks should even go
		CoarseIslandsGeneratorConfiguration config = context.config();
		final double islandHeightScale = config.islandHeightScale();
		final int islandMinHeight = config.islandMinHeight();
		
		// Get biome weights
		BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().orElseThrow();
		
		// If specified, only set blocks if the biome matches this
		Optional<Holder<Biome>> biomeLock = context.biome();

		// Maybe use heightmaps so we don't have to manually check each block
		ChunkHeightmaps heightmaps = context.chunkHeightmaps();
		
		// Getter for upper & lower bounds
		BlockHeightSelector seafloorGetter = config.seafloorProvider();
		BlockHeightSelector sealevelGetter = config.sealevelProvider();

		final double[] islandNoiseScaled = noise.islandNoiseScaled;
		final double[] cragNoiseScaled = noise.cragNoiseScaled;
		
		// Determines whether a column is cragrock or normal
		final boolean[] isColumnCrag = new boolean[256];
		
		int minHeightTotal = Integer.MAX_VALUE;
		int maxHeightTotal = Integer.MIN_VALUE;
		final int[] minBlockYOut = new int[256];
		final int[] maxBlockYOut = new int[256];
		
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;

				// If the biome lock is present and the biome test fails, do not set blocks in this column
				if(config.useBiomeLock() && biomeLock.isPresent() && biomeWeights.getBiome(x, z) != biomeLock.get()) {
					continue;
				}
				
				// Used to "flatten" the terrain to the water level (so we don't leave gaps between the terrain and seafloor)
				final int lowestBlock = seafloorGetter.getHeightWG(x, z, chunkPos, heightmaps);
				final int highestBlock = sealevelGetter.getHeightWG(x, z, chunkPos, heightmaps);
				
				// Noise for island
				double islandNoise = islandNoiseScaled[index];
				// Is this column part of an island?
				boolean isIsland = islandNoise <= 0 && lowestBlock < highestBlock;

				final int maxBlockY;
				if(isIsland) {
					// Is this column part of a crag patch?
					boolean isCrag = isColumnCrag[index] = cragNoiseScaled[index] <= 0;
					
					// Island height above the water
					double islandHeightDouble = -islandNoise * islandHeightScale;
					
					// Calculate how high this column should be
					int islandHeight = islandMinHeight + Math.max(Mth.ceil(islandHeightDouble), 0);
					
					// Crag patches are elevated one block
					if(isCrag && islandHeightDouble > 1.25) {
						++islandHeight;
					}
					
					maxBlockY = highestBlock + islandHeight;
				} else {
					// Get biome weight (for blending)
					float weight = biomeWeights.get(x, z, 2, 12);
					
					// Reduce difference between surface and sea floor
					maxBlockY = Mth.ceil(Mth.clampedLerp(lowestBlock, highestBlock - (highestBlock - lowestBlock) / 3.5F, weight));
				}

				// Store lowest block out
				minBlockYOut[index] = lowestBlock;
				minHeightTotal = Math.min(minHeightTotal, lowestBlock);

				// Store highest block out
				maxBlockYOut[index] = maxBlockY;
				maxHeightTotal = Math.max(maxHeightTotal, maxBlockY);
			}
		}
		
		return new CoarseIslandVolumeResult(new ColumnVolumeResult(minBlockYOut, maxBlockYOut, minHeightTotal, maxHeightTotal), isColumnCrag);
	}
	
	public boolean placeIslandVolumeBlocks(CoarseIslandVolumeResult volumeResult, ChunkAccess chunkAccess, BlockGenerator blockGenerator, ChunkHeightmaps heightmaps) {

		final boolean[] isColumnCrag = volumeResult.isCrag();
		
		// Provides the blockstate to place
		VolumeBlockstateProvider blockstateProvider = (int x, int y, int z) -> {
			int index = x * 16 + z;
			
			// FIXME don't use a hardcoded reference to cragrock here
			if(isColumnCrag[index]) {
				return BlockRegistry.CRAGROCK.get().defaultBlockState();
			} else {
				return blockGenerator.defaultTerrainState();
			}
		};
		
		return ColumnVolumeResult.placeColumnVolumeResult(volumeResult.columns(), chunkAccess, blockstateProvider, heightmaps);
	}
}
