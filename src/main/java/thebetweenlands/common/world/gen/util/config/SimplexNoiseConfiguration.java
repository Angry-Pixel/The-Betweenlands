package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.MapCodec;

import thebetweenlands.common.world.gen.util.SimplexCache;
import thebetweenlands.common.world.gen.util.SimplexData;

public final class SimplexNoiseConfiguration {

	public static final MapCodec<SimplexNoiseConfiguration> CODEC = SimplexNoiseSettings.MAP_CODEC.xmap(SimplexNoiseConfiguration::new, SimplexNoiseConfiguration::noiseSettings);
	
	private final SimplexNoiseSettings noiseSettings;
	private final SimplexCache noiseCache;
	
	public SimplexNoiseConfiguration(SimplexNoiseSettings noiseSettings) {
		this.noiseSettings = noiseSettings;
		this.noiseCache = new SimplexCache(noiseSettings.octaves());
	}
	
	public static SimplexNoiseConfiguration of(int octaves, double noiseScale) {
		return new SimplexNoiseConfiguration(SimplexNoiseSettings.of(octaves, noiseScale));
	}

	public static SimplexNoiseConfiguration of(int octaves, double noiseScale, double noiseValueMultiplier, double noiseValueOffset) {
		return new SimplexNoiseConfiguration(SimplexNoiseSettings.of(octaves, noiseScale, noiseValueMultiplier, noiseValueOffset));
	}
	
	public SimplexNoiseSettings noiseSettings() {
		return this.noiseSettings;
	}

	public double noiseScale() {
		return this.noiseSettings().noiseScale();
	}

	public double octaves() {
		return this.noiseSettings().octaves();
	}

	public double noiseValueMultiplier() {
		return this.noiseSettings().noiseValueMultiplier();
	}

	public double noiseValueOffset() {
		return this.noiseSettings().noiseValueOffset();
	}
	
	public SimplexData getNoise(long seed) {
		return this.noiseCache.getNoise(seed);
	}
	
}
