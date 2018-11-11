package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;

public class GenLayerResetCache extends GenLayerBetweenlands {
	public GenLayerResetCache(InstancedIntCache cache, GenLayer parent) {
		super(cache, 0L);
		this.parent = parent;
	}

	@Override
	public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
		this.cache.resetIntCache();
		return this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
	}
}
