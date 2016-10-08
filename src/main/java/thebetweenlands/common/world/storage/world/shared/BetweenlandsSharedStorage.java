package thebetweenlands.common.world.storage.world.shared;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.clientbound.MessageSyncSharedStorage;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public abstract class BetweenlandsSharedStorage extends SharedStorage {
	public BetweenlandsSharedStorage(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid);
	}

	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		if(dirty) {
			this.sendDataToAllWatchers();
		}
	}

	@Override
	public void onWatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		super.onWatched(chunkStorage, player);
		this.sendDataToPlayer(player);
	}

	/**
	 * Sends the chunk data to all watching players
	 */
	protected void sendDataToAllWatchers() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			if (!this.getWatchers().isEmpty()) {
				NBTTagCompound nbt = SharedStorage.save(this, new NBTTagCompound(), true);
				for (EntityPlayerMP watcher : this.getWatchers()) {
					TheBetweenlands.networkWrapper.sendTo(new MessageSyncSharedStorage(nbt), watcher);
				}
			}
		}
	}

	/**
	 * Sends the chunk data to a player
	 * @param player
	 */
	protected void sendDataToPlayer(EntityPlayerMP player) {
		TheBetweenlands.networkWrapper.sendTo(new MessageSyncSharedStorage(SharedStorage.save(this, new NBTTagCompound(), true)), player);
	}
}
