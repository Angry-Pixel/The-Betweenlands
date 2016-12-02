package thebetweenlands.common.world.storage.chunk;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.clientbound.MessageSyncChunkData;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;


public class BetweenlandsChunkData extends ChunkDataBase {
	public BetweenlandsChunkData() {
		//Constructor must be accessible
	}

	public static BetweenlandsChunkData forChunk(World world, Chunk chunk) {
		return ChunkDataBase.forChunk(BetweenlandsWorldData.forWorld(world), chunk);
	}

	@Override
	public void init() {
		//System.out.println("INIT");
	}

	@Override
	protected void setDefaults() {
		//System.out.println("DEFAULTS");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.sendDataToAllWatchers(true);
	}

	/**
	 * Sends the chunk data to all watching players
	 * @param sendEmpty
	 */
	protected void sendDataToAllWatchers(boolean sendEmpty) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			if (!this.getWatchers().isEmpty()) {
				NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
				if(sendEmpty || nbt.getSize() > 0) {
					for (EntityPlayerMP watcher : this.getWatchers()) {
						TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this.getChunk(), nbt), watcher);
					}
				}
			}
		}
	}

	/**
	 * Sends the chunk data to a player
	 * @param player
	 * @param sendEmpty
	 */
	protected void sendDataToPlayer(EntityPlayerMP player, boolean sendEmpty) {
		NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
		if(sendEmpty || nbt.getSize() > 0) {
			TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this.getChunk(), nbt), player);
		}
	}

	@Override
	protected void onWatched(EntityPlayerMP player) {
		super.onWatched(player);
		this.sendDataToPlayer(player, false /*Empty chunks don't have to be sent*/);
	}


	////////// Data //////////

	@Override
	public String getName() {
		return "ChunkData";
	}

	//Always load and cache with the chunk to keep the client synced
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		forChunk(event.getWorld(), event.getChunk());
	}
}
