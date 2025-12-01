package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.SimplexTerrainGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.generators.util.ColumnVolumeResult;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexCache;
import thebetweenlands.common.world.gen.generators.util.SimplexData;

public class SimplexTerrainGenerator extends EarlyGenerator<SimplexTerrainGeneratorConfiguration> {

	// Cache so we don't have to re-create the simplex noise every execution
	protected final SimplexCache noiseCache = new SimplexCache(4);

	public SimplexTerrainGenerator(Codec<SimplexTerrainGeneratorConfiguration> codec) {
		super(codec);
	}

	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(SimplexTerrainGeneratorConfiguration config) {
		if(config.useBiomeWeights() || config.useBiomeLock()) {
			return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS);
		} else {
			return EnumSet.noneOf(ExtraChunkInfoTypes.class);
		}
	}
	
	@Override
	public boolean place(EarlyGenerationContext<SimplexTerrainGeneratorConfiguration> context) {
		SimplexTerrainGeneratorConfiguration config = context.config();
		
		long seed = context.worldSeed();

		// Get or create noise generator for this seed
		SimplexData noise = noiseCache.getNoise(seed);

		// Compute all the noise values for this chunk
		ChunkAccess chunkAccess = context.chunkAccess();
		ChunkPos chunkPos = chunkAccess.getPos();

		double[] terrainNoise = EarlyGeneratorHelper.computeNoiseRaw(noise.noiseGenerator(), chunkPos, config.noiseScale());

		ColumnVolumeResult columnBlocks = computeColumnBlocks(context, chunkPos, terrainNoise);

		// The block we will be setting
		BlockState terrainBlock = context.blockGenerator().defaultTerrainState();
		
		// The heightmaps (for updating)
		ChunkHeightmaps heightmaps = context.chunkHeightmaps();
		
		return ColumnVolumeResult.placeColumnVolumeResult(columnBlocks, chunkAccess, terrainBlock, heightmaps);
	}
	
	/**
	 * Computes the min (exclusive) and max (exclusive) height that blocks will need to be placed in each column
	 * @return a {@linkplain ColumnVolumeResult} describing the blocks that need to be placed in each column
	 */
	public ColumnVolumeResult computeColumnBlocks(EarlyGenerationContext<SimplexTerrainGeneratorConfiguration> context, ChunkPos chunkPos, double[] terrainNoise) {
		// Get info on where these blocks should even go
		SimplexTerrainGeneratorConfiguration config = context.config();
		
		// Get biome weights
		BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().orElse(null);
		// If specified, only set blocks if the biome matches this
		Optional<Holder<Biome>> biomeLock = context.biome();

		// Maybe use heightmaps so we don't have to manually check each block
		ChunkHeightmaps heightmaps = context.chunkHeightmaps();
		
		// Getter for upper & lower bounds
		BlockHeightSelector minLevelGetter = config.minLevel();
		BlockHeightSelector maxLevelGetter = config.maxLevel();
		
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
				
				// Get min and max height to blend between
				final int lowestBlock = minLevelGetter.getHeight(x, z, chunkPos, heightmaps);
				final int highestBlock = maxLevelGetter.getHeight(x, z, chunkPos, heightmaps);

				// Fetch noise values
				double noise = terrainNoise[index] / 12.0f;
				
				// Get variance between upper bound and lower bound
				final double variance = highestBlock - lowestBlock;
				
				double maxHeight = highestBlock - variance / 2.5D + noise * variance - 2;
				
				if(maxHeight < lowestBlock + 1) {
					continue;
				}
				
				float weight = config.useBiomeWeights() ? biomeWeights.get(x, z) : 1.0F;
				
				// Note: Mth.lerp is (delta, min, max) whereas Mth.clampedLerp is (min, max, delta)
				final int maxBlockY = Mth.ceil(Mth.lerp(weight, lowestBlock, maxHeight));

				// Store lowest block out
				minBlockYOut[index] = lowestBlock;
				minHeightTotal = Math.min(minHeightTotal, lowestBlock);
				
				// Store highest block out
				maxBlockYOut[index] = maxBlockY;
				maxHeightTotal = Math.max(maxHeightTotal, maxBlockY);
			}
		}
		
		return new ColumnVolumeResult(minBlockYOut, maxBlockYOut, minHeightTotal, maxHeightTotal);
	}
}
