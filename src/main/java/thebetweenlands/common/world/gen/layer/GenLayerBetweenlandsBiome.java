package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.biome.Biome;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.util.WeightedList;

public class GenLayerBetweenlandsBiome extends GenLayerBetweenlands {
	private final WeightedList<BiomeBetweenlands> biomesToGenerate = new WeightedList<BiomeBetweenlands>();
	private final int totalWeight;

	public GenLayerBetweenlandsBiome(InstancedIntCache cache, long seed) {
		super(cache, seed);
		for(BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
			if(biome.getWeight() > 0 && (!BetweenlandsConfig.DEBUG.debug || BetweenlandsConfig.DEBUG.biomeList.isEmpty() || BetweenlandsConfig.DEBUG.biomeList.contains(biome.getRegistryName().toString()))) {
				this.biomesToGenerate.add(biome);
			}
		}
		this.totalWeight = this.biomesToGenerate.getTotalWeight();
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		int[] ints = this.cache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				this.initChunkSeed(xx + x, zz + z);
				ints[xx + zz * sizeX] = Biome.getIdForBiome(this.biomesToGenerate.getRandomItem(this.nextInt(this.totalWeight)));
			}
		}

		return ints;
	}
}
