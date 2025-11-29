package thebetweenlands.common.world.gen.warp;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseSettings;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

public record TerrainWarperSettings(int cellWidth, int cellHeight, int cellCountY, BiomeSource biomeSource, NoiseSettings noiseSettings, NoiseSlider topSlide, NoiseSlider bottomSlide, double dimensionDensityFactor, double dimensionDensityOffset, NoiseModifier caveNoiseModifier) {

	public TerrainWarperSettings(int cellWidth, int cellHeight, int cellCountY, BiomeSource biomeSource, NoiseSettings noiseSettings, NoiseSlider topSlide, NoiseSlider bottomSlide, NoiseModifier caveNoiseModifier, BLLegacyBlendedNoise blendedNoise) {
		this(
				cellWidth, cellHeight, cellCountY, 
				biomeSource, 
				noiseSettings, 
				topSlide, bottomSlide,
				biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getGlobalFactor() : 1.0F, 
				biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getSurfaceDepth() : 0.0F, 
				caveNoiseModifier
			);
	}
	
}
