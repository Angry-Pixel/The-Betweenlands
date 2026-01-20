package thebetweenlands.common.world.gen.util;

import java.util.concurrent.atomic.AtomicReference;

import thebetweenlands.util.FractalOpenSimplexNoise;

public class TriFractalOpenSimplexCache {

	private final int octaves1;
	private final double octaveScale1;
	private final int octaves2;
	private final double octaveScale2;
	private final int octaves3;
	private final double octaveScale3;

	public TriFractalOpenSimplexCache(int octaves1, double octaveScale1, int octaves2, double octaveScale2, int octaves3, double octaveScale3) {
		this.octaves1 = octaves1;
		this.octaveScale1 = octaveScale1;
		this.octaves2 = octaves2;
		this.octaveScale2 = octaveScale2;
		this.octaves3 = octaves3;
		this.octaveScale3 = octaveScale3;
	}
	
	// Technically, one volatile field should be equally safe (and faster) because the data is immutable and only read once.
	// However, the atomic reference makes it very clear that this data may be read on multiple threads at once, 
	//          and is much harder to accidentally make thread-unsafe in future.
	protected final AtomicReference<TriFractalOpenSimplexData> noiseCacheReference = new AtomicReference<>();

	public TriFractalOpenSimplexData getNoise(long seed) {
		return this.noiseCacheReference.updateAndGet((triSimplexCache) -> {
			if(triSimplexCache != null && triSimplexCache.worldSeed() == seed) {
				return triSimplexCache;
			}
			
			// If it doesn't exist or has a different seed than expected, create noise with the correct seed
			FractalOpenSimplexNoise fractalOpenSimplexNoise1 = new FractalOpenSimplexNoise(seed, this.octaves1, this.octaveScale1);
			FractalOpenSimplexNoise fractalOpenSimplexNoise2 = new FractalOpenSimplexNoise(seed, this.octaves2, this.octaveScale2);
			FractalOpenSimplexNoise fractalOpenSimplexNoise3 = new FractalOpenSimplexNoise(seed, this.octaves3, this.octaveScale3);
			return new TriFractalOpenSimplexData(seed, fractalOpenSimplexNoise1, fractalOpenSimplexNoise2, fractalOpenSimplexNoise3);
		});
	}
	
}
