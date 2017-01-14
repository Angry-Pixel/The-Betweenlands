package thebetweenlands.common.world.storage.world.shared;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.message.clientbound.MessageRemoveSharedStorage;
import thebetweenlands.common.network.message.clientbound.MessageSyncSharedStorage;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public abstract class BetweenlandsSharedStorage extends SharedStorage implements ITickable {
	protected boolean requiresSync = false;

	public BetweenlandsSharedStorage(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
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
	public void onWatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		super.onWatched(chunkStorage, player);
		this.sendDataToPlayer(player);
	}

	@Override
	public void update() {
		this.updateTracker();
	}

	/**
	 * Updates the tracker and sends packets to watchers if necessary
	 */
	protected void updateTracker() {
		if(this.requiresSync) {
			if(!this.getWorldStorage().getWorld().isRemote) {
				if (!this.getWatchers().isEmpty()) {
					NBTTagCompound nbt = SharedStorage.save(this, new NBTTagCompound(), true);
					this.sendDataToAllWatchers(new MessageSyncSharedStorage(nbt));
				}
			}
			this.requiresSync = false;
		}
	}

	@Override
	public void onRemoved() {
		//Notify clients if shared storage is removed
		if(!this.getWorldStorage().getWorld().isRemote) {
			if (!this.getWatchers().isEmpty()) {
				this.sendDataToAllWatchers(new MessageRemoveSharedStorage(this.getID()));
			}
		}
	}

	/**
	 * Sends the message to all watching players
	 */
	protected void sendDataToAllWatchers(IMessage message) {
		for (EntityPlayerMP watcher : this.getWatchers()) {
			TheBetweenlands.networkWrapper.sendTo(message, watcher);
		}
	}

	/**
	 * Sends the shared storage data to a player
	 * @param player
	 */
	protected void sendDataToPlayer(EntityPlayerMP player) {
		TheBetweenlands.networkWrapper.sendTo(new MessageSyncSharedStorage(SharedStorage.save(this, new NBTTagCompound(), true)), player);
	}
}
