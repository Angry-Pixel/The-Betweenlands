package thebetweenlands.common.world.storage.world.shared;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class SharedRegionData {
	private String id;
	private NBTTagCompound nbt;
	private int refCounter;
	private boolean dirty;

	public SharedRegionData(String id, NBTTagCompound nbt) {
		this.id = id;
		this.nbt = nbt;
		this.refCounter = 0;
		this.dirty = false;
	}

	/**
	 * Returns the region ID
	 * @return
	 */
	public String getID() {
		return this.id;
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
	public NBTTagCompound getSharedStorageNBT(String id) {
		if(this.nbt.hasKey(id)) {
			return this.nbt.getCompoundTag(id);
		}
		return null;
	}

	/**
	 * Sets the NBT of a shared storage in this region
	 * @param id
	 * @param nbt
	 */
	public void setSharedStorageNBT(String id, NBTTagCompound nbt) {
		this.nbt.setTag(id, nbt);
		this.dirty = true;
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Removes a shared storage from this region
	 * @param dir
	 * @param id
	 */
	public void deleteSharedStorage(File dir, String id) {
		if(this.nbt.hasKey(id)) {
			this.dirty = true;
			this.nbt.removeTag(id);
			if(this.nbt.getSize() == 0) {
				this.deleteRegionFile(dir);
			}
		}
	}

	/**
	 * Tries to read the region from a file and if it doesn't exist a new region is created
	 * @param dir
	 * @param region
	 * @return
	 */
	public static SharedRegionData getOrCreateRegion(File dir, SharedRegion region) {
		NBTTagCompound regionNbt = null;
		try {
			File file = new File(dir, region.getFileName() + ".dat");
			if(file.exists()) {
				regionNbt = CompressedStreamTools.read(file);
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		if(regionNbt == null) {
			regionNbt = new NBTTagCompound();
		}
		return new SharedRegionData(region.getFileName(), regionNbt);
	}

	/**
	 * Saves the region to a file
	 * @param dir
	 */
	public void saveRegion(File dir) {
		if(this.nbt.getSize() > 0) {
			File file = new File(dir, this.getID() + ".dat");
			try {
				dir.mkdirs();
				CompressedStreamTools.safeWrite(this.nbt, file);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
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
		if(file.exists()) {
			file.delete();
		}
	}
}
