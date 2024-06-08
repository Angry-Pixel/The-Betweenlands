package thebetweenlands.common.world.noisegenerators.genlayers;


import thebetweenlands.common.world.biome.BiomeBetweenlands;

public class GenLayerSurrounded extends GenLayerBetweenlands {
	private final int checkRange;
	private final float spawnChance;
	private final int surrounding, biome;

	public GenLayerSurrounded(InstancedIntCache cache, long seed, GenLayer parentGenLayer, BiomeBetweenlands surrounding, BiomeBetweenlands biome, int checkRange, float spawnChance) {
		super(cache, seed);
		this.parent = parentGenLayer;
		this.checkRange = checkRange;
		this.spawnChance = spawnChance;
		this.surrounding = surrounding.id;
		this.biome = biome.id;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int[] currentBiomeInts = this.parent.getInts(areaX - this.checkRange, areaZ - this.checkRange, sizeX + this.checkRange * 2, sizeZ + this.checkRange * 2);
		int[] biomeInts = this.cache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				this.initChunkSeed(areaX + xx, areaZ + zz);

				biomeInts[xx + zz * sizeX] = currentBiomeInts[xx + this.checkRange + (zz + this.checkRange) * (sizeX + this.checkRange * 2)];

				boolean surrounded = true;

				check: for(int xo = -this.checkRange; xo <= this.checkRange; xo++) {
					for(int zo = -this.checkRange; zo <= this.checkRange; zo++) {
						if(xo * xo + zo * zo <= this.checkRange * this.checkRange + 1) {
							int biomeID = currentBiomeInts[xx + this.checkRange + xo + (zz + this.checkRange + zo) * (sizeX + this.checkRange * 2)];
							if(biomeID != this.surrounding) {
								surrounded = false;
								break check;
							}
						}
					}
				}

				if(surrounded && this.nextInt(10000) <= this.spawnChance * 10000) {
					biomeInts[xx + zz * sizeX] = this.biome;
				}
			}
		}

		return biomeInts;
	}
}
