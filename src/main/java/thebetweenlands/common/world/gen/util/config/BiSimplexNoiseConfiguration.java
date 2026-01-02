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
	
	public static final MapCodec<BiSimplexNoiseConfiguration> namedCodec(String firstName, String secondName) {
		return RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						SimplexNoiseSettings.MAP_CODEC.fieldOf(firstName).forGetter(BiSimplexNoiseConfiguration::firstNoiseSettings),
						SimplexNoiseSettings.MAP_CODEC.fieldOf(secondName).forGetter(BiSimplexNoiseConfiguration::secondNoiseSettings)
				).apply(instance, BiSimplexNoiseConfiguration::new));
	}

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

	public static BiSimplexNoiseConfiguration of(
			int firstOctaves, double firstNoiseScale, double firstNoiseValueMultiplier, double firstNoiseValueOffset, 
			int secondOctaves, double secondNoiseScale, double secondNoiseValueMultiplier, double secondNoiseValueOffset
	) {
		return new BiSimplexNoiseConfiguration(
				SimplexNoiseSettings.of(firstOctaves, firstNoiseScale, firstNoiseValueMultiplier, firstNoiseValueOffset),
				SimplexNoiseSettings.of(secondOctaves, secondNoiseScale, secondNoiseValueMultiplier, secondNoiseValueOffset)
			);
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

	public double firstNoiseValueMultiplier() {
		return this.firstNoiseSettings().noiseValueMultiplier();
	}

	public double firstNoiseValueOffset() {
		return this.firstNoiseSettings().noiseValueOffset();
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

	public double secondNoiseValueMultiplier() {
		return this.secondNoiseSettings().noiseValueMultiplier();
	}

	public double secondNoiseValueOffset() {
		return this.secondNoiseSettings().noiseValueOffset();
	}
	
	
	public BiSimplexData getNoise(long seed) {
		return this.noiseCache.getNoise(seed);
	}
	
}
