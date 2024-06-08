package thebetweenlands.common.world.noisegenerators.genlayers;

public class GenLayerZoomIncrement extends GenLayerBetweenlands {
	private final boolean increment;

	public GenLayerZoomIncrement(InstancedIntCache cache, long seed, GenLayer parent, boolean increment) {
		super(cache, seed);
		this.parent = parent;
		this.increment = increment;
	}

	@Override
	public int[] getInts(int areaX, int areaZ, int sizeX, int sizeZ) {
		int parentAreaX = areaX >> 1;
		int parentAreaZ = areaZ >> 1;	
		int parentSizeX = (sizeX >> 1) + 2;
		int parentSizeZ = (sizeZ >> 1) + 2;
		int[] parentInts = this.parent.getInts(parentAreaX, parentAreaZ, parentSizeX, parentSizeZ);
		int newSizeX = parentSizeX - 1 << 1;
		int newSizeZ = parentSizeZ - 1 << 1;
		int[] newInts = this.cache.getIntCache(newSizeX * newSizeZ);

		for (int zo = 0; zo < parentSizeZ - 1; ++zo) {
			int index = (zo << 1) * newSizeX;

			int xnzn = parentInts[0 + 0 + (zo + 0) * parentSizeX];
			int xnzp = parentInts[0 + 0 + (zo + 1) * parentSizeX];

			for (int xo = 0; xo < parentSizeX - 1; ++xo) {
				this.initChunkSeed((long)(xo + parentAreaX << 1), (long)(zo + parentAreaZ << 1));

				int xpzn = parentInts[xo + 1 + (zo + 0) * parentSizeX];
				int xpzp = parentInts[xo + 1 + (zo + 1) * parentSizeX];

				newInts[index] = xnzn;

				int rnd1 = this.selectRandom(new int[] {xnzn, xnzp});
				newInts[index++ + newSizeX] = rnd1 + (this.increment && rnd1 != -1 ? 1 : 0);

				int rnd2 = this.selectRandom(new int[] {xnzn, xpzn});
				newInts[index] = rnd2 + (this.increment && rnd2 != -1 ? 1 : 0);

				int rnd3 = this.selectModeOrRandom(xnzn, xpzn, xnzp, xpzp);
				newInts[index++ + newSizeX] = rnd3 + (this.increment && rnd3 != -1 ? 1 : 0);

				xnzn = xpzn;
				xnzp = xpzp;
			}
		}

		int[] ints = this.cache.getIntCache(sizeX * sizeZ);

		for (int i = 0; i < sizeZ; ++i) {
			System.arraycopy(newInts, (i + (areaZ & 1)) * newSizeX + (areaX & 1), ints, i * sizeX, sizeX);
		}

		return ints;
	}

	/**
	 * Magnifies the mask and increments every set int that is spread out by 1
	 * @param layer
	 * @param num
	 * @return
	 */
	public static GenLayer magnify(InstancedIntCache cache, long seed, GenLayer layer, boolean increment, int num) {
		GenLayer genlayer = layer;

		for (int i = 0; i < num; ++i) {
			genlayer = new GenLayerZoomIncrement(cache, seed, genlayer, increment);
		}

		return genlayer;
	}
}
