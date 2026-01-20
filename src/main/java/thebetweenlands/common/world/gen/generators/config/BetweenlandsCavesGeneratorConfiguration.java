package thebetweenlands.common.world.gen.generators.config;

import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;

public record BetweenlandsCavesGeneratorConfiguration(
//		double caveScaleX, double caveScaleY, double caveScaleZ,
		FractalOpenSimplexNoiseSettings3D caveNoiseSettings,
		FractalOpenSimplexNoiseSettings3D formNoiseSettings,
		FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings
//		double formScaleX, double formScaleY, double formScaleZ,
//		double surfaceOpeningScaleX, double surfacneOpeningScaleZ,
		
	) implements EarlyGeneratorConfiguration {

}
