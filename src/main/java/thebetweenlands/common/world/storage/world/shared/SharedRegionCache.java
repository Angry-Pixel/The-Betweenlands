package thebetweenlands.common.world.storage.world.shared;

import net.minecraft.util.math.BlockPos;

public class SharedRegionCache {
	//TODO Implement storage cache


	/**
	 * Returns a region identifier for the specified block position
	 * @param pos
	 * @return
	 */
	public static String getRegionIdenfitier(BlockPos pos) {
		return getRegionIdentifier(pos.getX(), pos.getY());
	}

	/**
	 * Returns a region identifier for the specified block position
	 * @param x
	 * @param z
	 * @return
	 */
	public static String getRegionIdentifier(int x, int z) {
		return "";
	}
}
