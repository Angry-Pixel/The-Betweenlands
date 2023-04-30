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
	 * Tries to read the region from cache or a file and if it doesn't exist and {@code create} is true a new region is created.
	 * If {@code createNonPersistentIfEmpty} is true, then a new region is also created, but it is non-persistent and
	 * can thus only be read from and not be modified. If {@code metaOnly} is true, then only metadata is loaded and
	 * the rest of the data is lazily loaded.
	 * @param region
	 * @param create Whether to create a persistent region if none exists already.
	 * @param createNonPersistentIfEmpty Whether to create a non-persistent region if none exists already.
	 * @param metaOnly Whether only metadata should be loaded.
	 * @return
	 */
	@Nullable
	public LocalRegionData getOrCreateRegion(LocalRegion region, boolean create, boolean createNonPersistentIfEmpty, boolean metaOnly) {
		LocalRegionData cachedRegion = this.regionData.get(region);

		if(cachedRegion == null || (!createNonPersistentIfEmpty && !cachedRegion.isPersistent())) {
			LocalRegionData newRegion = LocalRegionData.getOrCreateRegion(this, this.dir, region, create, createNonPersistentIfEmpty, metaOnly, cachedRegion);

			if(newRegion != null) {
				this.regionData.put(region, newRegion);
			}

			cachedRegion = newRegion;
		}

		return cachedRegion;
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
			if(data.isPersistent() && data.isDirty()) {
				data.saveRegion();
			}

			//Unload dangling regions that for some reason haven't been unloaded properly (should be none)
			if(!data.hasReferences()) {
				unloadRegions.add(data);
			}
		}

		for(LocalRegionData unloadRegion : unloadRegions) {
			TheBetweenlands.logger.warn(String.format("Unloading dangling local storage region %s. This should not happen...", unloadRegion.getID()));
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
