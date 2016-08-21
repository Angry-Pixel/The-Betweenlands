package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBetweenlandsBiome extends GenLayerBetweenlands {
	//TODO: Requires biomes
	//private final WeightedList<BiomeGenBaseBetweenlands> biomesToGenerate;
	//private final int totalWeight;

	public GenLayerBetweenlandsBiome(long seed, GenLayer parentGenLayer) {
		super(seed);
		//biomesToGenerate = BLBiomeRegistry.biomeList;
		//totalWeight = biomesToGenerate.getTotalWeight();
		parent = parentGenLayer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		parent.getInts(x, z, sizeX, sizeZ);
		int[] ints = IntCache.getIntCache(sizeX * sizeZ);

		/*for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				initChunkSeed(xx + x, zz + z);
				ints[xx + zz * sizeX] = biomesToGenerate.getRandomItem(nextInt(totalWeight)).biomeID;
			}
		}*/

		return ints;
	}
}
