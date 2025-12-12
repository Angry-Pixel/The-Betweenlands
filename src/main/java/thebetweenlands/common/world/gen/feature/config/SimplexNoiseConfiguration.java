package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.common.world.gen.generators.util.SimplexCache;
import thebetweenlands.common.world.gen.generators.util.SimplexData;
import thebetweenlands.util.ExtraCodecs;

public class SimplexNoiseConfiguration {

	public static final MapCodec<SimplexNoiseConfiguration> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(SimplexNoiseConfiguration::noiseScale),
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("noise_octaves").forGetter(SimplexNoiseConfiguration::octaves)
			).apply(instance, SimplexNoiseConfiguration::new));
	
	private final double noiseScale;
	private final int octaves;
	private final SimplexCache noiseCache;
	
	public SimplexNoiseConfiguration(double noiseScale, int octaves) {
		this.noiseScale = noiseScale;
		this.octaves = octaves;
		this.noiseCache = new SimplexCache(octaves);
	}
	
	public static SimplexNoiseConfiguration of(double noiseScale, int octaves) {
		return new SimplexNoiseConfiguration(noiseScale, octaves);
	}

	public static SimplexNoiseConfiguration of(int octaves, double noiseScale) {
		return new SimplexNoiseConfiguration(noiseScale, octaves);
	}

	public double noiseScale() {
		return this.noiseScale;
	}

	public int octaves() {
		return this.octaves;
	}
	
	public SimplexData getNoise(long seed) {
		return this.noiseCache.getNoise(seed);
	}
	
}
