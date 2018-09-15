package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerCircleMask extends GenLayerBetweenlands {
	private final int id;
	private final int radius;

	public GenLayerCircleMask(long seed, GenLayer parentGenLayer, int id, int radius) {
		super(seed);
		this.parent = parentGenLayer;
		this.id = id;
		this.radius = radius;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int[] currentInts = this.parent.getInts(areaX - this.radius, areaZ - this.radius, sizeX + this.radius * 2, sizeZ + this.radius * 2);
		int[] maskInts = IntCache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				maskInts[xx + zz * sizeX] = -1;

				check: for(int xo = -this.radius; xo <= this.radius; xo++) {
					for(int zo = -this.radius; zo <= this.radius; zo++) {
						if(xo * xo + zo * zo <= this.radius * this.radius) {
							int index = xx + this.radius + xo + (zz + this.radius + zo) * (sizeX + this.radius * 2);
							int id = currentInts[index];
							if(id == this.id) {
								maskInts[xx + zz * sizeX] = id;
								break check;
							}
						}
					}
				}
			}
		}

		return maskInts;
	}
}
