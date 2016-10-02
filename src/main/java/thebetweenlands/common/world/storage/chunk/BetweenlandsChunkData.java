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


public class BetweenlandsChunkData extends ChunkDataBase {
	public BetweenlandsChunkData() {
		//Constructor must be accessible
	}

	public static BetweenlandsChunkData forChunk(World world, Chunk chunk) {
		return ChunkDataBase.forChunk(world, chunk, BetweenlandsChunkData.class);
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
		this.sendDataToAllWatchers();
	}

	/**
	 * Sends the chunk data to all watching players
	 */
	protected void sendDataToAllWatchers() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			if (!this.getWatchers().isEmpty()) {
				NBTTagCompound nbt = this.writeToPacketNBT(new NBTTagCompound());
				for (EntityPlayerMP watcher : this.getWatchers()) {
					TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this.getChunk(), nbt), watcher);
				}
			}
		}
	}

	/**
	 * Sends the chunk data to a player
	 * @param player
	 */
	protected void sendDataToPlayer(EntityPlayerMP player) {
		TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this.getChunk(), this.writeToPacketNBT(new NBTTagCompound())), player);
	}

	@Override
	protected void onWatched(EntityPlayerMP player) {
		super.onWatched(player);
		this.sendDataToPlayer(player);
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
