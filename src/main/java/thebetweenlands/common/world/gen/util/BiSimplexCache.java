package thebetweenlands.common.world.gen.util;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

/**
 * A cache for simple simplex noise generators, for use by {@link EarlyGenerator EarlyGenerators} so they don't have to re-create the noise for every chunk generated
 */
public final class BiSimplexCache {

	private final int firstOctaves;
	private final int secondOctaves;

	public BiSimplexCache(int firstOctaves, int secondOctaves) {
		this.firstOctaves = firstOctaves;
		this.secondOctaves = secondOctaves;
	}
	
	// Technically, one volatile field should be equally safe (and faster) because the data is immutable and only read once.
	// However, the atomic reference makes it very clear that this data may be read on multiple threads at once, 
	//          and is much harder to accidentally make thread-unsafe in future.
	protected final AtomicReference<BiSimplexData> noiseCacheReference = new AtomicReference<>();

	// Profile results from atomic cache vs creating a new instance each time:
	//     Without atomic cache (new instance each time):
	//         Approx 11800000 nanoseconds (~11.8 millis) on the first run of each thread (first 8 chunks generated)
	//         Approx 300000 nanoseconds (~0.30 millis) on each subsequent run
	//     With atomic cache:
	//         Approx 17300000 nanoseconds (~17.3 millis) on the first run of each thread (first 8 chunks generated)
	//         Approx 2700 nanoseconds (~0.0027 millis) on each subsequent run
	// Bonus non-atomic cache results:
	//    Two separate volatile SimplexCache fields:
	//        Approx 16200000 nanoseconds (~16.2 millis) on the first run of each thread (first 8 chunks generated)
	//        Approx 1500 nanoseconds (~0.0015 millis) on each subsequent run
	//    One volatile BiSimplexCache field:
	//        Approx 16000000 nanoseconds (~16.0 millis) on the first run of each thread (first 8 chunks generated)
	//        Approx 1000 nanoseconds (~0.0010 millis) on each subsequent run
	//
	// Note: Performance data collected with firstOctave = 4, secondOctave = 2.
	public BiSimplexData getNoise(long seed) {
		return this.noiseCacheReference.updateAndGet((biSimplexCache) -> {
			if(biSimplexCache != null && biSimplexCache.worldSeed() == seed) {
				return biSimplexCache;
			}
			
			// If it doesn't exist or has a different seed than expected, create noise with the correct seed
			LegacyRandomSource random = new LegacyRandomSource(seed);

			BLLegacyPerlinSimplexNoise landNoiseGen = new BLLegacyPerlinSimplexNoise(random, this.firstOctaves);
			BLLegacyPerlinSimplexNoise riverNoiseGen = new BLLegacyPerlinSimplexNoise(random, this.secondOctaves);
			return new BiSimplexData(seed, landNoiseGen, riverNoiseGen);
		});
	}
}
