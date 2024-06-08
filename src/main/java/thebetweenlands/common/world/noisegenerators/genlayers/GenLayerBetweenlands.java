package thebetweenlands.common.world.noisegenerators.genlayers;

import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.gen.layer.IntCache;
import thebetweenlands.util.WeightedList;

public class GenLayerBetweenlands extends GenLayer {
	private final WeightedList biomesToGenerate = new WeightedList();
	private final int totalWeight;

	InstancedIntCache cache;


	public GenLayerBetweenlands(InstancedIntCache cache, long seed) {
		super(seed);
		for(BiomeBetweenlands biome : BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY) {
			if(biome.biomeWeight > 0) {
				this.biomesToGenerate.add(biome.biomeWeight);
			}
		}
		this.totalWeight = this.biomesToGenerate.total;
		this.cache = cache;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		int[] ints = this.cache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				this.initChunkSeed(xx + x, zz + z);
				// just use the index in the list as an id
				ints[xx + zz * sizeX] = this.biomesToGenerate.getRandomItem(this.nextInt(this.totalWeight));
			}
		}

		return ints;
	}
}
