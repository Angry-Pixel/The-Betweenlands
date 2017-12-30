package thebetweenlands.common.world.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorage;

public abstract class BetweenlandsLocalStorage extends LocalStorageImpl {
	protected boolean requiresSync = false;

	public BetweenlandsLocalStorage(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region);
	}


	/**
	 * Sets whether the data is dirty and needs to be saved to the file and sent to the client
	 * @param dirty
	 * @return
	 */
	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		if(dirty) {
			this.requiresSync = true;
		}
	}

	/**
	 * Sets whether the data is dirty and needs to be saved to the file and/or sent to the client
	 * @param dirty
	 * @param sendUpdate
	 * @return
	 */
	public void setDirty(boolean dirty, boolean sendUpdate) {
		if(sendUpdate) {
			this.setDirty(dirty);
		} else {
			super.setDirty(dirty);
		}
	}

	@Override
	public void update() {
		super.update();
		this.updateTracker();
	}

	/**
	 * Updates the tracker and sends packets to watchers if necessary
	 */
	protected void updateTracker() {
		if(this.requiresSync) {
			if(!this.getWorldStorage().getWorld().isRemote) {
				if (!this.getWatchers().isEmpty()) {
					this.sendDataToAllWatchers(new MessageSyncLocalStorage(this.getWorldStorage().getLocalStorageHandler().saveLocalStorageToNBT(new NBTTagCompound(), this, true)));
				}
			}
			this.requiresSync = false;
		}
	}
}
