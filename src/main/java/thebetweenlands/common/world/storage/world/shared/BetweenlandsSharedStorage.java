package thebetweenlands.common.world.storage.world.shared;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.clientbound.MessageRemoveSharedStorage;
import thebetweenlands.common.message.clientbound.MessageSyncSharedStorage;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public abstract class BetweenlandsSharedStorage extends SharedStorage implements ITickable {
	protected boolean requiresSync = false;

	public BetweenlandsSharedStorage(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid);
	}

	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		if(dirty) {
			this.requiresSync = true;
		}
	}

	@Override
	public void onWatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		super.onWatched(chunkStorage, player);
		this.sendDataToPlayer(player);
	}

	@Override
	public void update() {
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
				this.sendDataToAllWatchers(new MessageRemoveSharedStorage(this.getUUID()));
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
