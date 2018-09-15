package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.util.WeightedList;

public class GenLayerBetweenlandsBiome extends GenLayerBetweenlands {
	private final WeightedList<BiomeBetweenlands> biomesToGenerate = new WeightedList<BiomeBetweenlands>();
	private final int totalWeight;

	public GenLayerBetweenlandsBiome(long seed, GenLayer parentGenLayer) {
		super(seed);
		for(BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
			if(biome.getWeight() > 0) {
				this.biomesToGenerate.add(biome);
			}
		}
		this.totalWeight = this.biomesToGenerate.getTotalWeight();
		this.parent = parentGenLayer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		this.parent.getInts(x, z, sizeX, sizeZ);
		int[] ints = IntCache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				this.initChunkSeed(xx + x, zz + z);
				ints[xx + zz * sizeX] = Biome.getIdForBiome(this.biomesToGenerate.getRandomItem(this.nextInt(this.totalWeight)));
			}
		}

		return ints;
	}
}
