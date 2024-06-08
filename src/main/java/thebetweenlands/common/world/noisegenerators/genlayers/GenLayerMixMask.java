package thebetweenlands.common.world.noisegenerators.genlayers;


public class GenLayerMixMask extends GenLayerBetweenlands {
	private final GenLayer mask;

	public GenLayerMixMask(InstancedIntCache cache, GenLayer parent, GenLayer maskLayer) {
		super(cache, 0L);
		this.parent = parent;
		this.mask = maskLayer;
	}

	@Override
	public void initWorldGenSeed(long seed) {
		this.mask.initWorldGenSeed(seed);
		super.initWorldGenSeed(seed);
	}

	@Override
	public int[] getInts(int areaX, int areaY, int sizeX, int sizeZ) {
		int[] parentInts = this.parent.getInts(areaX, areaY, sizeX, sizeZ);
		int[] maskInts = this.mask.getInts(areaX, areaY, sizeX, sizeZ);
		int[] mixedInts = this.cache.getIntCache(sizeX * sizeZ);

		for (int i = 0; i < sizeX * sizeZ; ++i) {
			int mask = maskInts[i];
			if(mask != -1) {
				mixedInts[i] = mask;
			} else {
				mixedInts[i] = parentInts[i];
			}
		}

		return mixedInts;
	}
}
