package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.world.gen.feature.config.NoisePatchFeatureConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexCache;
import thebetweenlands.common.world.gen.generators.util.SimplexData;

public class CragrockSpiresFeature extends Feature<NoisePatchFeatureConfiguration> {

	public CragrockSpiresFeature(Codec<NoisePatchFeatureConfiguration> codec) {
		super(codec);
	}

	protected final SimplexCache noiseCache = new SimplexCache(4);
	
	@Override
	public boolean place(FeaturePlaceContext<NoisePatchFeatureConfiguration> context) {
		NoisePatchFeatureConfiguration config = context.config();
		
		WorldGenLevel level = context.level();
		long seed = level.getSeed();

		// Get or create noise generators for this seed
		SimplexData noise = this.noiseCache.getNoise(seed);
		
		double[] terrainNoise = EarlyGeneratorHelper.computeNoiseRaw(noise.noiseGenerator(), new ChunkPos(context.origin()), config.noiseScale());
		
		return false;
	}

}
