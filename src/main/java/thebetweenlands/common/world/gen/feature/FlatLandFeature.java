package thebetweenlands.common.world.gen.feature;

import java.util.stream.IntStream;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.config.FlatLandConfiguration;

// TODO fix this up so we don't need to generate new perlin noise every single time
// TODO biome tapering
// TODO move to earlier in generation somehow - we preferably want this stuff to happen before biome blocks get replaced
public class FlatLandFeature extends Feature<FlatLandConfiguration> {

	public FlatLandFeature(Codec<FlatLandConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<FlatLandConfiguration> context) {
		WorldGenLevel level = context.level();
		long seed = level.getSeed();
		
		// Create noise generators

		LegacyRandomSource random = new LegacyRandomSource(seed);
		
		// TODO Fix this up so we don't need to create new perlin noise every single time
		PerlinNoise landNoiseGen = PerlinNoise.create(random, IntStream.rangeClosed(-3, 0));
		PerlinNoise riverNoiseGen = PerlinNoise.create(random, IntStream.rangeClosed(-1, 0));
		
		// Compute all the noise values for this chunk
		
		ChunkPos chunkPos = new ChunkPos(context.origin());

		double[] landNoise = computeLandNoise(landNoiseGen, chunkPos.x, chunkPos.z);
		double[] riverNoise = computeRiverNoise(riverNoiseGen, chunkPos.x, chunkPos.z);
		
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				this.placeColumnBlocks(context, chunkPos, x, z, landNoise, riverNoise);
			}
		}
		
		return true;
	}

	public void placeColumnBlocks(FeaturePlaceContext<FlatLandConfiguration> context, ChunkPos chunkPos, int xOffset, int zOffset, double[] landNoise, double[] riverNoise) {
		WorldGenLevel level = context.level();
		FlatLandConfiguration config = context.config();

		final int x = chunkPos.getBlockX(xOffset);
		final int z = chunkPos.getBlockZ(zOffset);
		
		// Used to "flatten" the terrain to the water level (so we don't leave gaps between the terrain and seafloor)
		final int lowestBlock = findHighestBlockBelowWaterLevel(level, x, z, config.waterLevel());

		// Fetch noise values
		final double landNoiseValue = landNoise[xOffset * 16 + zOffset];
		final double riverNoiseValue = riverNoise[xOffset * 16 + zOffset];

		// Calculate chance of a river spawning, and how much it'll affect the terrain height
		int terrainHeight = (int)Math.ceil(Math.abs(landNoiseValue * (config.waterLevel() - lowestBlock + config.terrainLevel())));
		float riverThreshold = 6.0f * (terrainHeight + 2);
		double riverPercentage = 1.0D - (riverNoiseValue / riverThreshold);
		
//		float weight = (Math.min(biomeWeight + 0.5F, 1.0F) - 0.5F) * 2.0F;
		float weight = 0.0F; // Weight: biome weight based number for the blending at the edge of biomes
		
		final double maxHeight;
		if(riverNoiseValue < riverThreshold) {
			// If a river should be generating, generate a river (w/ biome blending)
			maxHeight = config.waterLevel() + terrainHeight - riverPercentage * (terrainHeight + ((riverThreshold - riverNoiseValue) / 16.0D));
		} else {
			// If not, just blend the terrain
			maxHeight = config.waterLevel() + terrainHeight;
		}
		TheBetweenlands.LOGGER.info("Min Height: {}, Max Height: {}", lowestBlock, maxHeight);
		
		MutableBlockPos pos = new MutableBlockPos(x, 0, z);
		for(int y = lowestBlock; y < Mth.clampedLerp(maxHeight, lowestBlock, weight); y++) {
			this.setBlock(level, pos.setY(y), config.terrainState());
		}
	}
	
	public static int findHighestBlockBelowWaterLevel(WorldGenLevel level, int x, int z, int waterLevel) {
		MutableBlockPos pos = new MutableBlockPos(x, 0, z);
		for(int y = waterLevel; y > level.getMinBuildHeight(); y--) {
			BlockState currentBlock = level.getBlockState(pos.setY(y));
			if(!currentBlock.isAir() && !currentBlock.canBeReplaced()) {
				return y;
			}
		}
		
		return level.getMinBuildHeight();
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
