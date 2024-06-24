package thebetweenlands.common.world.storage;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.io.FileUtils;

import net.minecraft.nbt.CompoundTag;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;

public class LocalRegionData {
	private final LocalRegion region;
	private final CompoundTag nbt;
	private int refCounter;
	private boolean dirty;

	private final LocalRegionCache cache;

	private LocalRegionData(LocalRegionCache cache, LocalRegion region, CompoundTag nbt) {
		this.region = region;
		this.nbt = nbt;
		this.refCounter = 0;
		this.dirty = false;
		this.cache = cache;
	}

	/**
	 * Tries to read the region from a file and if it doesn't exist and {@code create} is true a new region is created
	 * @param cache
	 * @param dir
	 * @param region
	 * @param
	 * @return
	 */
	@Nullable
	public static LocalRegionData getOrCreateRegion(LocalRegionCache cache, File dir, LocalRegion region, boolean create) {
		CompoundTag regionNbt = null;
		File file = new File(dir, region.getFileName() + ".dat");
		try {
			regionNbt = cache.getLocalStorageHandler().getSaveHandler().loadFileNbt(file);
		} catch(Exception ex) {
			TheBetweenlands.LOGGER.error("Failed loading local region cache", ex);
			File backup = new File(file.getAbsolutePath() + ".backup");
			try {
				FileUtils.copyFile(file, backup);
				TheBetweenlands.LOGGER.info(String.format("Created a backup of local region cache at %s", backup.getAbsolutePath()));
			} catch (IOException e) {
				TheBetweenlands.LOGGER.error("Failed creating backup of local region cache", e);
			}
			try {
				file.delete();
			} catch(Exception e) {}
			regionNbt = null;
		}
		if(regionNbt == null) {
			if(!create) {
				return null;
			}

			regionNbt = new CompoundTag();
		}
		return new LocalRegionData(cache, region, regionNbt);
	}

	/**
	 * Returns the region ID
	 * @return
	 */
	public String getID() {
		return this.region.getFileName();
	}

	/**
	 * Returns the region this data belongs to
	 * @return
	 */
	public LocalRegion getRegion() {
		return this.region;
	}

	/**
	 * Increases the reference counter
	 */
	public void incrRefCounter() {
		this.refCounter++;
	}

	/**
	 * Decreases the reference counter
	 */
	public void decrRefCounter() {
		this.refCounter--;
	}

	/**
	 * Returns whether there are any references left
	 * @return
	 */
	public boolean hasReferences() {
		return this.refCounter > 0;
	}

	@Nullable
	public CompoundTag getLocalStorageNBT(StorageID id) {
		if(this.nbt.contains(id.getStringID(), Tag.TAG_COMPOUND)) {
			return this.nbt.getCompound(id.getStringID());
		}
		return null;
	}

	/**
	 * Sets the NBT of a local storage in this region
	 * @param id
	 * @param nbt
	 */
	public void setLocalStorageNBT(StorageID id, CompoundTag nbt) {
		this.nbt.put(id.getStringID(), nbt);
		this.dirty = true;
	}

	/**
	 * Removes a shared storage from this region
	 * @param dir
	 * @param id
	 * @return true if something was removed
	 */
	public boolean deleteLocalStorage(File dir, StorageID id) {
		if(this.nbt.contains(id.getStringID(), Tag.TAG_COMPOUND)) {
			this.dirty = true;
			this.nbt.remove(id.getStringID());
			if(this.nbt.isEmpty()) {
				this.deleteRegionFile(dir);
			}
			return true;
		}
		return false;
	}

	public void setChunkNBT(ChunkPos chunk, CompoundTag nbt) {
		this.nbt.put("ChunkData." + chunk.x + "." + chunk.z, nbt);
		this.dirty = true;
	}

	@Nullable
	public CompoundTag getChunkNBT(ChunkPos chunk) {
		if(this.nbt.contains("ChunkData." + chunk.x + "." + chunk.z, Tag.TAG_COMPOUND)) {
			return this.nbt.getCompound("ChunkData." + chunk.x + "." + chunk.z);
		}
		return null;
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Saves the region to a file
	 * @param dir
	 */
	public void saveRegion(File dir) {
		if(!this.nbt.isEmpty()) {
			File file = new File(dir, this.getID() + ".dat");
			this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(file, this.nbt.copy());
		} else {
			this.deleteRegionFile(dir);
		}
		this.dirty = false;
	}

	/**
	 * Deletes the region file
	 * @param dir
	 */
	public void deleteRegionFile(File dir) {
		File file = new File(dir, this.getID() + ".dat");
		this.cache.getLocalStorageHandler().getSaveHandler().queueRegion(file, null);
	}
}
