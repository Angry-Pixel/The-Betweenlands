package thebetweenlands.common.world.gen.util;

import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public record BiSimplexData(long worldSeed, BLLegacyPerlinSimplexNoise first, BLLegacyPerlinSimplexNoise second) {

}
