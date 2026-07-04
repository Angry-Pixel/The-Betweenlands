package thebetweenlands.common.world.gen.util;

import thebetweenlands.util.FractalOpenSimplexNoise;

public record TriFractalOpenSimplexData(long worldSeed, FractalOpenSimplexNoise noise1, FractalOpenSimplexNoise noise2, FractalOpenSimplexNoise noise3) {

}
