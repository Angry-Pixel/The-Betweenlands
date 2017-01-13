package thebetweenlands.common.world.storage.world.shared;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SharedRegionCache {
	private final Map<SharedRegion, SharedRegionData> regionData = new HashMap<SharedRegion, SharedRegionData>();

	private final File dir;

	public SharedRegionCache(File dir) {
		this.dir = dir;
	}

	/**
	 * Returns the region save directory
	 * @return
	 */
	public File getDir() {
		return this.dir;
	}

	/**
	 * Returns a cached region if available, otherwise tries to read the region from a file and if it doesn't exist a new region is created
	 * @param dir
	 * @param region
	 * @return
	 */
	public SharedRegionData getOrCreateRegion(SharedRegion region) {
		SharedRegionData data = this.regionData.get(region);
		if(data == null) {
			this.regionData.put(region, data = SharedRegionData.getOrCreateRegion(this.dir, region));
		}
		return data;
	}

	/**
	 * Removes a region without saving anything
	 * @param region
	 */
	public void removeRegion(SharedRegion region) {
		this.regionData.remove(region);
	}

	/**
	 * Saves all regions
	 */
	public void saveAllRegions() {
		for(SharedRegionData data : this.regionData.values()) {
			if(data.isDirty()) {
				data.saveRegion(this.dir);
			}
		}
	}

	/**
	 * Clears the cache without saving anything
	 */
	public void clearCache() {
		this.regionData.clear();
	}

	/**
	 * Returns a map of all currently cached regions
	 * @return
	 */
	public Map<SharedRegion, SharedRegionData> getRegions() {
		return Collections.unmodifiableMap(this.regionData);
	}
}
