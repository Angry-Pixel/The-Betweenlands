package thebetweenlands.common.world.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import thebetweenlands.common.TheBetweenlands;

public class LocalStorageSaveHandler implements IThreadedFileIO {
	private static final CompoundTag DELETE_NBT = new CompoundTag();

	private final ConcurrentHashMap<File, CompoundTag> filesToSave = new ConcurrentHashMap<>();

	private final Set<File> fileLocks = new HashSet<>();

	/**
	 * Tries to queue the region to be saved by the file IO thread
	 * @param regionFile The region file
	 * @param regionNbtCopy A copy of the region NBT that is not changed anywhere else. Null if the file should be deleted
	 * @return True if the task was queued
	 */
	public boolean queueRegion(File regionFile, @Nullable CompoundTag regionNbtCopy) {
		this.filesToSave.put(regionFile, regionNbtCopy == null ? DELETE_NBT : regionNbtCopy);
		ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
		return true;
	}

	/**
	 * Triers to queue the local storage to be saved by the file IO thread
	 * @param storageFile The local storage file
	 * @param storageNbtCopy A copy of the local storage NBT that is not changed anywhere else. Null if the file should ne deleted
	 * @return True if the task was queued
	 */
	public boolean queueLocalStorage(File storageFile, @Nullable CompoundTag storageNbtCopy) {
		this.filesToSave.put(storageFile, storageNbtCopy == null ? DELETE_NBT : storageNbtCopy);
		ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
		return true;
	}

	private void lockFile(File file) throws InterruptedException {
		synchronized(this.fileLocks) {
			while(this.fileLocks.contains(file)) {
				this.fileLocks.wait();
			}
			this.fileLocks.add(file);
		}
	}

	private void unlockFile(File file) {
		synchronized(this.fileLocks) {
			this.fileLocks.remove(file);
			this.fileLocks.notifyAll();
		}
	}

	/**
	 * Loads the specified file as NBT
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Nullable
	public CompoundTag loadFileNbt(File file) throws IOException {
		final CompoundTag queuedNbt = this.filesToSave.get(file);
		if(queuedNbt != null) {
			return queuedNbt.copy();
		} else {
			try {
				this.lockFile(file);
				try {
					return NbtIo.read(file.toPath());
				} finally {
					this.unlockFile(file);
				}
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			return null;
		}
	}

	@Override
	public boolean writeNextIO() {
		if(!this.filesToSave.isEmpty()) {
			final File file = this.filesToSave.keySet().iterator().next();

			final CompoundTag nbt = this.filesToSave.remove(file);

			if(nbt != null) {
				try {
					this.lockFile(file);
					try {
						if(nbt == DELETE_NBT) {
							if(file.exists()) {
								file.delete();
							}
						} else {
							try {
								file.getParentFile().mkdirs();
								NbtIo.write(nbt, file.toPath());
							} catch(Exception ex) {
								TheBetweenlands.LOGGER.error("Failed to save region or local storage: " + file.getAbsolutePath(), ex);
							}
						}
					} finally {
						this.unlockFile(file);
					}
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}

			return true;
		}
		return false;
	}

	public void flush() {
		while(this.writeNextIO());
	}
}
