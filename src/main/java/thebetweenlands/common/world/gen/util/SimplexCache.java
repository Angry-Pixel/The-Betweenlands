package thebetweenlands.common.world.gen.util;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

/**
 * A cache for simple simplex noise generators, for use by {@link EarlyGenerator EarlyGenerators} so they don't have to re-create the noise for every chunk generated
 */
public final class SimplexCache {

	private final int octaves;

	public SimplexCache(int octaves) {
		this.octaves = octaves;
	}
	
	// Technically, one volatile field should be equally safe (and faster) because the data is immutable and only read once.
	// However, the atomic reference makes it very clear that this data may be read on multiple threads at once, 
	//          and is much harder to accidentally make thread-unsafe in future.
	protected final AtomicReference<SimplexData> noiseCacheReference = new AtomicReference<>();

	public SimplexData getNoise(long seed) {
		return this.noiseCacheReference.updateAndGet((simplexCache) -> {
			if(simplexCache != null && simplexCache.worldSeed() == seed) {
				return simplexCache;
			}
			
			// If it doesn't exist or has a different seed than expected, create noise with the correct seed
			LegacyRandomSource random = new LegacyRandomSource(seed);

			BLLegacyPerlinSimplexNoise noiseGenerator = new BLLegacyPerlinSimplexNoise(random, this.octaves);
			return new SimplexData(seed, noiseGenerator);
		});
	}
}
