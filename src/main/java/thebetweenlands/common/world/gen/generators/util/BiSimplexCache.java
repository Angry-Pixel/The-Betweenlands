package thebetweenlands.common.world.gen.generators.util;

import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public record BiSimplexCache(long worldSeed, BLLegacyPerlinSimplexNoise first, BLLegacyPerlinSimplexNoise second) {

}
