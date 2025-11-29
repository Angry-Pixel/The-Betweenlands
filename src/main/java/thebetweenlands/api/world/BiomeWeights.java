package thebetweenlands.api.world;

import java.lang.reflect.Array;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class BiomeWeights {
	private final float[] weights = new float[256];
	private final Holder<?>[] biomes = new Holder[256];

	public BiomeWeights(float[] weights, Holder<Biome>[] biomes) {
		System.arraycopy(weights, 0, this.weights, 0, Math.min(weights.length, this.weights.length));
		System.arraycopy(biomes,  0, this.biomes,  0, Math.min(biomes.length,  this.biomes.length));
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
	 * Returns the biome
	 * @param x
	 * @param z
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Holder<Biome> getBiome(int x, int z) {
		return (Holder<Biome>)this.biomes[x * 16 + z];
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