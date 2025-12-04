package thebetweenlands.common.world.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.common.TheBetweenlands;

public class LocalRegionCache {
	private final Map<LocalRegion, LocalRegionData> regionData = new HashMap<LocalRegion, LocalRegionData>();

	private final File dir;

	private final LocalStorageHandlerImpl handler;

	public LocalRegionCache(LocalStorageHandlerImpl handler, File dir) {
		this.dir = dir;
		this.handler = handler;
	}

	public LocalStorageHandlerImpl getLocalStorageHandler() {
		return this.handler;
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
	 * @param region
	 * @return
	 */
	public LocalRegionData getOrCreateRegion(LocalRegion region) {
		return this.getOrCreateRegion(region, true);
	}

	/**
	 * Returns a cached region if available, otherwise tries to read the region from a file. If such a file doesn't exist, and {@code create} is true, a new region is created, otherwise it returns null
	 * @param region
	 * @param create
	 * @return
	 */
	@Nullable
	public LocalRegionData getOrCreateRegion(LocalRegion region, boolean create) {
		LocalRegionData data = this.regionData.get(region);
		if(data == null) {
			data = LocalRegionData.getOrCreateRegion(this, this.dir, region, create);
			if(data != null) {
				this.regionData.put(region, data);
			}
		}
		return data;
	}

	/**
	 * Returns the cached region data of the specified region, or null if that region is not cached
	 * @param region
	 * @return
	 */
	@Nullable
	public LocalRegionData getCachedRegion(LocalRegion region) {
		return this.regionData.get(region);
	}

	/**
	 * Removes a region without saving anything
	 * @param region
	 */
	public void removeRegion(LocalRegion region) {
		this.regionData.remove(region);
	}

	/**
	 * Saves all regions
	 */
	public void saveAllRegions() {
		List<LocalRegionData> unloadRegions = new ArrayList<>();

		for(LocalRegionData data : this.regionData.values()) {
			if(data.isDirty()) {
				data.saveRegion(this.dir);
			}

			//Unload dangling regions that for some reason haven't been unloaded properly (should be none)
			if(!data.hasReferences()) {
				unloadRegions.add(data);
			}
		}

		for(LocalRegionData unloadRegion : unloadRegions) {
			TheBetweenlands.LOGGER.warn(String.format("Unloading dangling local storage region %s. This should not happen...", unloadRegion.getID()));
			this.removeRegion(unloadRegion.getRegion());
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
	public Map<LocalRegion, LocalRegionData> getRegions() {
		return Collections.unmodifiableMap(this.regionData);
	}
}
