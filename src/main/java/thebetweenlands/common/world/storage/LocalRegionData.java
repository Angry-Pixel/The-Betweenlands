package thebetweenlands.common.world.storage;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;

public class LocalRegionData {
	private LocalRegion region;
	private NBTTagCompound nbt;
	private int refCounter;
	private boolean dirty;

	private final LocalRegionCache cache;

	private LocalRegionData(LocalRegionCache cache, LocalRegion region, NBTTagCompound nbt) {
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
		NBTTagCompound regionNbt = null;
		File file = new File(dir, region.getFileName() + ".dat");
		try {
			regionNbt = cache.getLocalStorageHandler().getSaveHandler().loadFileNbt(file);
		} catch(Exception ex) {
			TheBetweenlands.logger.error("Failed loading local region cache", ex);
			File backup = new File(file.getAbsolutePath() + ".backup");
			try {
				FileUtils.copyFile(file, backup);
				TheBetweenlands.logger.info(String.format("Created a backup of local region cache at %s", backup.getAbsolutePath()));
			} catch (IOException e) {
				TheBetweenlands.logger.error("Failed creating backup of local region cache", e);
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
			
			regionNbt = new NBTTagCompound();
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
	public NBTTagCompound getLocalStorageNBT(StorageID id) {
		if(this.nbt.hasKey(id.getStringID(), Constants.NBT.TAG_COMPOUND)) {
			return this.nbt.getCompoundTag(id.getStringID());
		}
		return null;
	}

	/**
	 * Sets the NBT of a local storage in this region
	 * @param id
	 * @param nbt
	 */
	public void setLocalStorageNBT(StorageID id, NBTTagCompound nbt) {
		this.nbt.setTag(id.getStringID(), nbt);
		this.dirty = true;
	}

	/**
	 * Removes a shared storage from this region
	 * @param dir
	 * @param id
	 * @return true if something was removed
	 */
	public boolean deleteLocalStorage(File dir, StorageID id) {
		if(this.nbt.hasKey(id.getStringID(), Constants.NBT.TAG_COMPOUND)) {
			this.dirty = true;
			this.nbt.removeTag(id.getStringID());
			if(this.nbt.getSize() == 0) {
				this.deleteRegionFile(dir);
			}
			return true;
		}
		return false;
	}

	public void setChunkNBT(ChunkPos chunk, NBTTagCompound nbt) {
		this.nbt.setTag("ChunkData." + chunk.x + "." + chunk.z, nbt);
		this.dirty = true;
	}

	@Nullable
	public NBTTagCompound getChunkNBT(ChunkPos chunk) {
		if(this.nbt.hasKey("ChunkData." + chunk.x + "." + chunk.z, Constants.NBT.TAG_COMPOUND)) {
			return this.nbt.getCompoundTag("ChunkData." + chunk.x + "." + chunk.z);
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
		if(this.nbt.getSize() > 0) {
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
