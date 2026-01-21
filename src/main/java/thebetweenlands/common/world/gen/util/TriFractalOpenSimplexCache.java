package thebetweenlands.common.world.gen.util;

import java.util.concurrent.atomic.AtomicReference;

import thebetweenlands.util.FractalOpenSimplexNoise;

public final class TriFractalOpenSimplexCache {

	private final int octaves1;
	private final boolean additiveSeed1;
	private final int octaves2;
	private final boolean additiveSeed2;
	private final int octaves3;
	private final boolean additiveSeed3;

	public TriFractalOpenSimplexCache(int octaves1, boolean additiveSeed1, int octaves2, boolean additiveSeed2, int octaves3, boolean additiveSeed3) {
		this.octaves1 = octaves1;
		this.additiveSeed1 = additiveSeed1;
		this.octaves2 = octaves2;
		this.additiveSeed2 = additiveSeed2;
		this.octaves3 = octaves3;
		this.additiveSeed3 = additiveSeed3;
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
			FractalOpenSimplexNoise fractalOpenSimplexNoise1 = FractalOpenSimplexNoise.create(seed + 0, this.octaves1, this.additiveSeed1);
			FractalOpenSimplexNoise fractalOpenSimplexNoise2 = FractalOpenSimplexNoise.create(seed + 1, this.octaves2, this.additiveSeed2);
			FractalOpenSimplexNoise fractalOpenSimplexNoise3 = FractalOpenSimplexNoise.create(seed + 2, this.octaves3, this.additiveSeed3);
			return new TriFractalOpenSimplexData(seed, fractalOpenSimplexNoise1, fractalOpenSimplexNoise2, fractalOpenSimplexNoise3);
		});
	}
	
}
