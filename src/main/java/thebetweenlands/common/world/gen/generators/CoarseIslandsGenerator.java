package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.ChunkPos;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.CoarseIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.util.BiSimplexData;
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
		CoarseIslandsGeneratorConfiguration config = context.config();
		
		// Get the noise values for this chunk
		CoarseIslandNoise noise = this.computeNoise(context);
		
		return false;
	}

	protected record CoarseIslandNoise(double[] islandNoiseScaled, double[] cragNoiseScaled) {}
	
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
	
}
