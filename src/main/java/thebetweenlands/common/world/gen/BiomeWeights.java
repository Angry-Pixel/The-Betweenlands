package thebetweenlands.common.world.gen;

// ported from original betweenlands
public class BiomeWeights {
	private final float[] weights = new float[256];

	public BiomeWeights(float[] weights) {
		for(int i = 0; i < weights.length; i++) {
			this.weights[i] = weights[i];
		}
	}

	/**
	 * Returns the weight
	 * @param x
	 * @param z
	 * @return
	 */
	public float get(int x, int z) {
		return this.weights[x * 16 + z];
	}

	/**
	 * Returns the weight linearly interpolated between the minimum block distance and the maximum block distance
	 * from the biome edge. Node that the ranges are only approximations and may not always be correct. If the biome
	 * edge distance is smaller than the minimum distance 0.0F is returned, if the edge distance is greater than the maximum distance
	 * 1.0F is returned. The maximum distance goes up to ~16 blocks.
	 * @param x
	 * @param z
	 * @param min
	 * @param max
	 * @return
	 */
	public float get(int x, int z, float min, float max) {
		float blockDst = this.get(x, z) * 34.0F;
		if(blockDst >= min && blockDst <= max) {
			return (blockDst - min) / (max - min);
		} else if(blockDst < min) {
			return 0.0F;
		}
		return 1.0F;
	}
}
