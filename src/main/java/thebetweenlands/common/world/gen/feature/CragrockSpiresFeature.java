package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.world.gen.feature.config.CragrockSpiresFeatureConfiguration;
import thebetweenlands.common.world.gen.feature.config.SimplexNoiseConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexData;

public class CragrockSpiresFeature extends Feature<CragrockSpiresFeatureConfiguration> {
	
	public CragrockSpiresFeature(Codec<CragrockSpiresFeatureConfiguration> codec) {
		super(codec);
	}

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
		
		double noise = rawNoise * config.noiseValueMultiplier() + config.noiseValueOffset();
		
		// The height of the spire above the water level
		final double spireHeightDouble = -noise * config.spireHeightFactor();
		final int spireHeight = Mth.floor(spireHeightDouble);

		final int minY = seafloorY;
		final int maxY = sealevelY + spireHeight;

		if(maxY <= minY) {
			return false;
		}

		MutableBlockPos mutablePos = pos.mutable();
		for(int y = minY; y < maxY; ++y) {
			mutablePos.setY(y);
			this.setBlock(level, mutablePos, config.baseState());
		}
		
		return true;
	}
}
