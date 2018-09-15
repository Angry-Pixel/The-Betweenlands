package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerMask extends GenLayerBetweenlands {
	private final int from, to;

	public GenLayerMask(GenLayer parent, int from, int to) {
		super(0L);
		this.parent = parent;
		this.from = from;
		this.to = to;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int[] currentInts = this.parent.getInts(areaX, areaZ, sizeX, sizeZ);
		int[] maskInts = IntCache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				int index = xx + zz * sizeX;
				int id = currentInts[index];
				if(id == this.from) {
					maskInts[index] = this.to;
				} else {
					maskInts[index] = -1;
				}
			}
		}

		return maskInts;
	}
}
