package thebetweenlands.world.storage.chunk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.message.base.AbstractMessage;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData.ChunkSyncHandler.MessageSyncChunkData;
import thebetweenlands.world.storage.chunk.storage.ChunkStorage;

public class BetweenlandsChunkData extends ChunkDataBase {
	public BetweenlandsChunkData() {
		super(ModInfo.ID + ":chunkData");
	}

	@Override
	protected void init() {
		//System.out.println("INIT");
	}

	@Override
	protected void setDefaults() {
		//System.out.println("DEFAULTS");
	}

	public static BetweenlandsChunkData forChunk(World world, Chunk chunk) {
		return ChunkDataBase.forChunk(world, chunk, BetweenlandsChunkData.class);
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	protected void postInit() {
		this.save();
		this.syncData();
	}

	public void syncData() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			ChunkCoordIntPair pos = new ChunkCoordIntPair(this.getChunk().xPosition, this.getChunk().zPosition);
			List<EntityPlayerMP> watchers = CHUNK_SYNC_HANDLER.chunkWatchers.get(pos);
			if(watchers != null && !watchers.isEmpty()) {
				for(EntityPlayerMP watcher : watchers) {
					TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(this), watcher);
				}
			}
		}
	}

	public static final ChunkSyncHandler CHUNK_SYNC_HANDLER = new ChunkSyncHandler();

	public static class ChunkSyncHandler {
		/**
		 * Registers the packet that keeps the properties in sync
		 * @param networkWrapper
		 * @param packetID
		 */
		public void registerPacket(SimpleNetworkWrapper networkWrapper, int packetID) {
			networkWrapper.registerMessage(MessageSyncChunkData.class, MessageSyncChunkData.class, packetID, Side.CLIENT);
		}

		public static class MessageSyncChunkData extends AbstractMessage<MessageSyncChunkData> {
			private int chunkX, chunkZ;
			private String name;
			private NBTTagCompound nbt;

			public MessageSyncChunkData() {}

			public MessageSyncChunkData(BetweenlandsChunkData data) {
				this.chunkX = data.getChunk().xPosition;
				this.chunkZ = data.getChunk().zPosition;
				this.name = data.name;
				NBTTagCompound nbtData = data.readData();
				this.nbt = nbtData != null ? nbtData : new NBTTagCompound();
			}

			@Override
			public void fromBytes(ByteBuf buf) {
				this.chunkX = buf.readInt();
				this.chunkZ = buf.readInt();
				PacketBuffer packetBuffer = new PacketBuffer(buf);
				try {
					this.name = packetBuffer.readStringFromBuffer(128);
					this.nbt = packetBuffer.readNBTTagCompoundFromBuffer();
				} catch (IOException e) {
				}
			}

			@Override
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.chunkX);
				buf.writeInt(this.chunkZ);
				PacketBuffer packetBuffer = new PacketBuffer(buf);
				try {
					packetBuffer.writeStringToBuffer(this.name);
					packetBuffer.writeNBTTagCompoundToBuffer(this.nbt);
				} catch (IOException e) {
				}
			}

			@Override
			public void onMessageClientSide(MessageSyncChunkData message, EntityPlayer player) {
				synchronized(CHUNK_DATA_HANDLER) {
					ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(message.chunkX, message.chunkZ);
					NBTTagCompound currentNBT = ChunkDataBase.getNBTCache(chunkPos, player.worldObj);
					if(currentNBT == null)
						currentNBT = new NBTTagCompound();
					currentNBT.setTag(message.name, message.nbt);
					ChunkDataBase.addNBTCache(chunkPos, player.worldObj, currentNBT);
					ChunkDataBase data = ChunkDataBase.getDataCache(chunkPos, player.worldObj, BetweenlandsChunkData.class);
					if(data != null) {
						data.writeData(message.nbt);
						data.load();
					}
				}
			}

			@Override
			public void onMessageServerSide(MessageSyncChunkData message, EntityPlayer player) { }
		}


		private final Map<ChunkCoordIntPair, List<EntityPlayerMP>> chunkWatchers = new HashMap<ChunkCoordIntPair, List<EntityPlayerMP>>();

		private boolean addWatcher(ChunkCoordIntPair chunk, EntityPlayerMP player) {
			List<EntityPlayerMP> watchers = this.chunkWatchers.get(chunk);
			if(watchers == null)
				this.chunkWatchers.put(chunk, watchers = new ArrayList<EntityPlayerMP>());
			if(!watchers.contains(player)) {
				watchers.add(player);
				return true;
			}
			return false;
		}

		private void removeWatcher(ChunkCoordIntPair chunk, EntityPlayerMP player) {
			List<EntityPlayerMP> watchers = this.chunkWatchers.get(chunk);
			if(watchers != null)
				watchers.remove(player);
		}

		@SubscribeEvent
		public void onWatch(ChunkWatchEvent event) {
			if(event instanceof ChunkWatchEvent.Watch) {
				if(this.addWatcher(event.chunk, event.player)) {
					synchronized(CHUNK_DATA_HANDLER) {
						Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
						BetweenlandsChunkData data = BetweenlandsChunkData.forChunk(event.player.worldObj, chunk);
						TheBetweenlands.networkWrapper.sendTo(new MessageSyncChunkData(data), event.player);
					}
				}
			} else if(event instanceof ChunkWatchEvent.UnWatch) {
				this.removeWatcher(event.chunk, event.player);
			}
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			this.chunkWatchers.remove(event.getChunk());
		}
	}




	////////// Data //////////

	private List<ChunkStorage> storage = new ArrayList<ChunkStorage>();

	@Override
	protected void load() {
		try {
			NBTTagCompound nbt = this.readData();
			if(nbt.hasKey("storage")) {
				this.storage.clear();
				NBTTagList storageList = nbt.getTagList("storage", Constants.NBT.TAG_COMPOUND);
				for(int i = 0; i < storageList.tagCount(); i++) {
					NBTTagCompound storageCompound = storageList.getCompoundTagAt(i);
					String type = storageCompound.getString("type");
					Class<? extends ChunkStorage> storageClass = ChunkStorage.getStorageClass(type);
					if(storageClass == null)
						throw new Exception("Chunk storage type not mapped!");
					Constructor<? extends ChunkStorage> ctor = storageClass.getConstructor(Chunk.class, BetweenlandsChunkData.class);
					ChunkStorage storage = ctor.newInstance(this.getChunk(), this);
					storage.readFromNBT(storageCompound.getCompoundTag("storage"));
					this.storage.add(storage);
				}
			}
		} catch(Exception ex) {
			this.markDirty();
			ex.printStackTrace();
		}
	}

	@Override
	protected void save() {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			if(!this.storage.isEmpty()) {
				NBTTagList storageList = new NBTTagList();
				for(ChunkStorage storage : this.storage) {
					NBTTagCompound storageCompound = new NBTTagCompound();
					storage.writeToNBT(storageCompound);
					String type = ChunkStorage.getStorageType(storage.getClass());
					if(type == null)
						throw new Exception("Chunk storage type not mapped!");
					NBTTagCompound fullNBT = new NBTTagCompound();
					fullNBT.setString("type", type);
					fullNBT.setTag("storage", storageCompound);
					storageList.appendTag(fullNBT);
				}
				nbt.setTag("storage", storageList);
			}
			this.writeData(nbt);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		this.syncData();
	}

	public List<ChunkStorage> getStorage() {
		return this.storage;
	}
}
