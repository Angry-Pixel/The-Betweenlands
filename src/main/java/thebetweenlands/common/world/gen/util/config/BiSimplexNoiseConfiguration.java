package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.common.world.gen.util.BiSimplexCache;
import thebetweenlands.common.world.gen.util.BiSimplexData;

public class BiSimplexNoiseConfiguration {
	
	public static final MapCodec<BiSimplexNoiseConfiguration> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					SimplexNoiseSettings.MAP_CODEC.fieldOf("first").forGetter(BiSimplexNoiseConfiguration::firstNoiseSettings),
					SimplexNoiseSettings.MAP_CODEC.fieldOf("second").forGetter(BiSimplexNoiseConfiguration::secondNoiseSettings)
			).apply(instance, BiSimplexNoiseConfiguration::new));

	private final SimplexNoiseSettings firstNoiseSettings;
	private final SimplexNoiseSettings secondNoiseSettings;
	private final BiSimplexCache noiseCache;
	
	public BiSimplexNoiseConfiguration(SimplexNoiseSettings firstNoiseSettings, SimplexNoiseSettings secondNoiseSettings) {
		this.firstNoiseSettings = firstNoiseSettings;
		this.secondNoiseSettings = secondNoiseSettings;
		this.noiseCache = new BiSimplexCache(firstNoiseSettings.octaves(), secondNoiseSettings.octaves());
	}
	
	public static BiSimplexNoiseConfiguration of(int firstOctaves, double firstNoiseScale, int secondOctaves, double secondNoiseScale) {
		return new BiSimplexNoiseConfiguration(SimplexNoiseSettings.of(firstOctaves, firstNoiseScale), SimplexNoiseSettings.of(secondOctaves, secondNoiseScale));
	}

	public SimplexNoiseSettings firstNoiseSettings() {
		return this.firstNoiseSettings;
	}
	
	public double firstNoiseScale() {
		return this.firstNoiseSettings().noiseScale();
	}

	public int firstOctaves() {
		return this.firstNoiseSettings().octaves();
	}
	
	public SimplexNoiseSettings secondNoiseSettings() {
		return this.secondNoiseSettings;
	}
	
	public double secondNoiseScale() {
		return this.secondNoiseSettings().noiseScale();
	}

	public int secondOctaves() {
		return this.secondNoiseSettings().octaves();
	}
	
	public BiSimplexData getNoise(long seed) {
		return this.noiseCache.getNoise(seed);
	}
	
}
