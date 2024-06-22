package thebetweenlands.common.world.noisegenerators.genlayers;


public class GenLayerMask extends GenLayerBetweenlands {
	private final int from, to;

	public GenLayerMask(InstancedIntCache cache, GenLayer parent, int from, int to) {
		super(cache, 0L);
		this.parent = parent;
		this.from = from;
		this.to = to;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int[] currentInts = this.parent.getInts(areaX, areaZ, sizeX, sizeZ);
		int[] maskInts = this.cache.getIntCache(sizeX * sizeZ);

		for (int i = 0; i < sizeX * sizeZ; ++i) {
			int id = currentInts[i];
			if (id == this.from) {
				maskInts[i] = this.to;
			} else {
				maskInts[i] = -1;
			}
		}

		return maskInts;
	}
}
