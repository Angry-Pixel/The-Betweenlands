package thebetweenlands.common.world.gen.feature;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;
import thebetweenlands.common.world.gen.feature.config.NoisePatchWithLevelFeatureConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.util.SimplexCache;
import thebetweenlands.common.world.gen.util.SimplexData;

public class AlgaePatchFeature extends Feature<NoisePatchWithLevelFeatureConfiguration> {

	public AlgaePatchFeature(Codec<NoisePatchWithLevelFeatureConfiguration> codec) {
		super(codec);
	}

	protected final SimplexCache noiseCache = new SimplexCache(4);
	
	@Override
	public boolean place(FeaturePlaceContext<NoisePatchWithLevelFeatureConfiguration> context) {
		NoisePatchWithLevelFeatureConfiguration config = context.config();
		
		WorldGenLevel level = context.level();
		long seed = level.getSeed();

		// Get or create noise generators for this seed
		SimplexData noise = this.noiseCache.getNoise(seed);

		final ChunkPos chunkPos = new ChunkPos(context.origin());
		
		double[] algaeNoise = EarlyGeneratorHelper.computeNoiseRaw(noise.noiseGenerator(), chunkPos, config.noiseScale());
		
		final int seaLevel = config.level().orElse(context.chunkGenerator().getSeaLevel());
		
		MutableBlockPos pos = new MutableBlockPos();

		// TODO get biome weights properly
		Optional<BiomeWeights> biomeWeights = context.chunkGenerator() instanceof BetweenlandsChunkGenerator generator ? Optional.of(generator.calculateBiomeWeights(chunkPos)) : Optional.empty();
		
		for(int x = 0; x < 16; ++x) {
			pos.setX(chunkPos.getBlockX(x));
			for(int z = 0; z < 16; ++z) {
				
				float biomeWeight = biomeWeights.isPresent() ? biomeWeights.get().get(x, z) : 1.0F;
				if(algaeNoise[x * 16 + z] / config.amplifierFactor() * biomeWeight + config.amplifierOffset() > 0) {
					continue;
				}

				pos.setZ(chunkPos.getBlockZ(z));
				if(!level.getFluidState(pos.setY(seaLevel)).is(BLFluidTagGenerator.UNDERWATER_PLANT_PLACEABLE)) {
					continue;
				}
				
				BlockState blockAbove = level.getBlockState(pos.setY(seaLevel + 1));
				
				if(blockAbove.is(BlockTags.AIR) && !blockAbove.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
					this.setBlock(level, pos, BlockRegistry.ALGAE.get().defaultBlockState());
				}
			}
		}
		
		return true;
	}

}
