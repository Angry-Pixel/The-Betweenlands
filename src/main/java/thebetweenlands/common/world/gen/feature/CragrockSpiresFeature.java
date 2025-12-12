package thebetweenlands.common.world.gen.feature;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.common.world.gen.feature.config.CragrockSpiresFeatureConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.util.BiomeWeightsCache;
import thebetweenlands.common.world.gen.util.SimplexData;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseConfiguration;

public class CragrockSpiresFeature extends Feature<CragrockSpiresFeatureConfiguration> {
	
	public CragrockSpiresFeature(Codec<CragrockSpiresFeatureConfiguration> codec) {
		super(codec);
	}
	
	// Temp biome weights cache, because this feature will be placed multiple times within a single chunk
	protected final BiomeWeightsCache biomeWeightsCache = new BiomeWeightsCache();
	
	@Override
	public boolean place(FeaturePlaceContext<CragrockSpiresFeatureConfiguration> context) {
		CragrockSpiresFeatureConfiguration config = context.config();
		
		WorldGenLevel level = context.level();
		long seed = level.getSeed();
		
		BlockPos pos = context.origin();
		
		// TODO feature height providers
		int seafloorY = config.seafloorProvider().getHeight(level, pos.getX(), pos.getZ());
		int sealevelY = config.sealevelProvider().getHeight(level, pos.getX(), pos.getZ());
		
		// TODO option in feature config for size
		if(sealevelY <= seafloorY || sealevelY - seafloorY < 3) {
			return false;
		}

		SimplexNoiseConfiguration spireNoise = config.spireNoise();
		
		// Get or create noise generators for this seed
		SimplexData noiseData = spireNoise.getNoise(seed);
		
		double rawNoise = EarlyGeneratorHelper.computeSingleNoiseRaw(noiseData.noiseGenerator(), pos.getX(), pos.getZ(), spireNoise.noiseScale());

		ChunkPos chunkPos = new ChunkPos(pos);
		
		// TODO get biome weights properly
		Optional<BiomeWeights> biomeWeights = config.useBiomeWeights() ? this.biomeWeightsCache.getWeights(context.chunkGenerator(), chunkPos.x, chunkPos.z) : Optional.empty();
		
		double weight = biomeWeights.isPresent() ? biomeWeights.get().get(pos.getX() & 15, pos.getZ() & 15) : 1.0F;
		double noise = rawNoise * weight * config.noiseValueMultiplier() + config.noiseValueOffset();
		
		// The height of the spire above the water level
		final int spireHeight = Mth.floor(-noise * config.spireHeightFactor());
		
		// How deep the spire goes into the sea floor
		final int spireBaseDepth = Mth.floor(-noise * config.spireBaseDepthFactor());

		// Figure out which blocks need to be set
		final int minY = seafloorY - spireBaseDepth;
		final int maxY = sealevelY + spireHeight;

		if(maxY <= minY) {
			return false;
		}

		// Set blocks to cragrock
		MutableBlockPos mutablePos = pos.mutable();
		for(int y = minY; y < maxY; ++y) {
			mutablePos.setY(y);
			this.setBlock(level, mutablePos, config.baseState());
		}

		// Maybe replace top states
		this.replaceTopStates(context, sealevelY, minY, maxY);
		
		return true;
	}

	// Replaces the states above the sea level
	public void replaceTopStates(FeaturePlaceContext<CragrockSpiresFeatureConfiguration> context, int sealevelY, int minY, int maxY) {
		CragrockSpiresFeatureConfiguration config = context.config();
		
		List<BlockState> topStates = config.topStates();
		if(maxY > sealevelY && topStates.size() != 0) {
			Predicate<BlockState> predicate = (state) -> state == config.baseState();
			
			WorldGenLevel level = context.level();

			MutableBlockPos mutablePos = context.origin().mutable();
			
			int statesToReplace = Math.min(Math.min(maxY - minY, maxY - sealevelY), topStates.size());
			for(int i = 0; i < statesToReplace; ++i) {
				int y = maxY - i - 1;
				BlockState state = topStates.get(i);
				mutablePos.setY(y);
				this.safeSetBlock(level, mutablePos, state, predicate);
			}
		}
	}
}
