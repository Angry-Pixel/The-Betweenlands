package thebetweenlands.common.world.noisegenerators.genlayers;


public class GenLayerThinMask extends GenLayerBetweenlands {
	private final int id, range;
	private final float removeChance;

	public GenLayerThinMask(InstancedIntCache cache, long seed, GenLayer parentGenLayer, int id, int range, float removeChance) {
		super(cache, seed);
		this.parent = parentGenLayer;
		this.id = id;
		this.range = range;
		this.removeChance = removeChance;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int[] currentInts = this.parent.getInts(areaX - this.range, areaZ - this.range, sizeX + this.range * 2, sizeZ + this.range * 2);
		int[] ints = this.cache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				this.initChunkSeed(areaX + xx, areaZ + zz);

				int id = currentInts[xx + this.range + (zz + this.range) * (sizeX + this.range * 2)];
				ints[xx + zz * sizeX] = id;

				if(id == this.id) {
					check: for(int xo = -this.range; xo <= this.range; xo++) {
						for(int zo = -this.range; zo <= this.range; zo++) {
							if((xo != 0 || zo != 0) && xo * xo + zo * zo <= this.range * this.range + 1) {
								if(currentInts[xx + this.range + xo + (zz + this.range + zo) * (sizeX + this.range * 2)] == this.id && this.nextInt(10000) <= this.removeChance * 10000) {
									ints[xx + zz * sizeX] = -1;
									break check;
								}
							}
						}
					}
				}
			}
		}

		return ints;
	}

	public static GenLayer thin(InstancedIntCache cache, long seed, GenLayer layer, int id, int range, float removeChance, int num) {
		GenLayer genlayer = layer;

		for (int i = 0; i < num; ++i) {
			genlayer = new GenLayerThinMask(cache, seed + i, genlayer, id, range, removeChance);
		}

		return genlayer;
	}
}
