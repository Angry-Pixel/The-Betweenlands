package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.world.gen.feature.config.CragrockSpiresFeatureConfiguration;
import thebetweenlands.common.world.gen.feature.config.SimplexNoiseConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexData;

public class CragrockSpiresFeature extends Feature<CragrockSpiresFeatureConfiguration> {

	// TODO make this a configured thing
	// Note: also used in CragSpiresPlacement
	public static final int SPIRE_CELL_SIZE = 4;
	public static final int SPIRE_CELLS_PER_CHUNK = 16 / SPIRE_CELL_SIZE;
	
	static {
		if(16 % SPIRE_CELL_SIZE != 0) {
			throw new IllegalStateException("\"SPIRE_CELL_SIZE\" must divide 16");
		}
	}
	
	public CragrockSpiresFeature(Codec<CragrockSpiresFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<CragrockSpiresFeatureConfiguration> context) {
		CragrockSpiresFeatureConfiguration config = context.config();
		
		WorldGenLevel level = context.level();
		long seed = level.getSeed();
		
		ChunkPos chunkPos = new ChunkPos(context.origin());
		
		// Get or create noise generators for this seed
		SimplexData noise = config.spireNoise().getNoise(seed);
		
//		double[] terrainNoise = EarlyGeneratorHelper.computeNoiseRaw(noise.noiseGenerator(), chunkPos, config.spireNoise().noiseScale());

		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;
				
//				TheBetweenlands.LOGGER.info("Pos: [{}, {}], Region: {}, Single: {}", x, z, terrainNoise[index], noise.noiseGenerator().getValue(chunkPos.getBlockZ(z) * config.noiseScale(), chunkPos.getBlockX(x) * config.noiseScale(), true));
			}
		}
		
		
		return false;
	}
	
	protected boolean placeStack(FeaturePlaceContext<CragrockSpiresFeatureConfiguration> context) {
		CragrockSpiresFeatureConfiguration config = context.config();
		
		WorldGenLevel level = context.level();
		long seed = level.getSeed();
		
		BlockPos pos = context.origin();
		
		ChunkPos chunkPos = new ChunkPos(pos);
		
		// TODO feature height providers
		int lowestBlock = context.level().getHeight(Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
		int highestBlock = config.level();
		
		// TODO option in feature config for size
		if(highestBlock - lowestBlock < 3) {
			return false;
		}

		SimplexNoiseConfiguration spireNoise = config.spireNoise();
		
		// Get or create noise generators for this seed
		SimplexData noiseData = spireNoise.getNoise(seed);
		
		double rawNoise = EarlyGeneratorHelper.computeSingleNoiseRaw(noiseData.noiseGenerator(), pos.getX(), pos.getZ(), spireNoise.noiseScale());
		
		double noise = rawNoise * config.noiseValueMultiplier() + config.noiseValueOffset();
		
		// The height of the spire above the water level
		final double spireHeight = -noise * config.spireHeightFactor();
		
		if(spireHeight >= 1) {
			
		}
		
		return false;
	}
	
//	protected boolean[] getValidGenerationColumns(FeaturePlaceContext<CragrockSpiresFeatureConfiguration> context) {
//		boolean[] validColumns = new boolean[256];
//		
//		Holder<Biome> prevBiome = null;
//		boolean prevBiomeWasValid = false;
//		
//		final WorldGenLevel level = context.level();
//		final ChunkGenerator chunkGenerator = context.chunkGenerator();
//		
//		for(int x = 0; x < 16; ++x) {
//			for(int z = 0; z < 16; ++z) {
//				
//			}
//		}
//		
//		return validColumns;
//	}

}
