package thebetweenlands.common.world.storage.chunk;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.clientbound.MessageSyncChunkData;
import thebetweenlands.common.world.storage.chunk.storage.ChunkStorage;


public class BetweenlandsChunkData extends ChunkDataBase {
	private final List<EntityPlayerMP> watchers = new ArrayList<EntityPlayerMP>();
	private final List<ChunkStorage> storage = new ArrayList<ChunkStorage>();

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
			if (!this.watchers.isEmpty()) {
				NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
				for (EntityPlayerMP watcher : this.watchers) {
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
		TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this.getChunk(), this.writeToNBT(new NBTTagCompound())), player);
	}

	@Override
	protected void onWatched(EntityPlayerMP player) {
		this.watchers.add(player);
		this.sendDataToPlayer(player);
	}

	@Override
	protected void onUnwatched(EntityPlayerMP player) {
		this.watchers.remove(player);
	}


	////////// Data //////////

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("storage")) {
			//System.out.println("Reading data");
			this.storage.clear();
			NBTTagList storageList = nbt.getTagList("storage", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < storageList.tagCount(); i++) {
				NBTTagCompound storageCompound = storageList.getCompoundTagAt(i);
				String type = storageCompound.getString("type");
				Class<? extends ChunkStorage> storageClass = ChunkStorage.getStorageClass(type);
				try {
					if (storageClass == null)
						throw new Exception("Chunk storage type not mapped!");
					Constructor<? extends ChunkStorage> ctor = storageClass.getConstructor(Chunk.class, BetweenlandsChunkData.class);
					ChunkStorage storage = ctor.newInstance(this.getChunk(), this);
					storage.readFromNBT(storageCompound.getCompoundTag("storage"));
					this.storage.add(storage);
				} catch (Exception ex) {
					this.markDirty();
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (!this.storage.isEmpty()) {
			NBTTagList storageList = new NBTTagList();
			for (ChunkStorage storage : this.storage) {
				NBTTagCompound storageCompound = new NBTTagCompound();
				storage.writeToNBT(storageCompound);
				String type = ChunkStorage.getStorageType(storage.getClass());
				try {
					if (type == null)
						throw new Exception("Chunk storage type not mapped!");
					NBTTagCompound fullNBT = new NBTTagCompound();
					fullNBT.setString("type", type);
					fullNBT.setTag("storage", storageCompound);
					storageList.appendTag(fullNBT);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			nbt.setTag("storage", storageList);
		}
		return nbt;
	}

	/**
	 * Returns the storage list
	 * @return
	 */
	public List<ChunkStorage> getStorage() {
		return this.storage;
	}

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
