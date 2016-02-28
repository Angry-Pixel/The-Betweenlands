package thebetweenlands.world.storage.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.message.base.AbstractMessage;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData.ChunkSyncHandler.MessageSyncChunkData;

public class BetweenlandsChunkData extends ChunkDataBase {
	public boolean hasData = false;

	public BetweenlandsChunkData() {
		super(ModInfo.ID + ":chunkData");
	}

	@Override
	protected void load() {
		//System.out.println("LOAD CHUNK DATA");
		this.hasData = this.getData().getBoolean("hasData");
	}

	@Override
	protected void save() {
		//System.out.println("SAVE CHUNK DATA");
		this.getData().setBoolean("hasData", this.hasData);
	}

	@Override
	protected void init() {
		//System.out.println("INIT");
	}

	@Override
	protected void setDefaults() {
		//System.out.println("DEFAULTS");
	}

	public static BetweenlandsChunkData forChunk(Chunk chunk) {
		return ChunkDataBase.forChunk(chunk, BetweenlandsChunkData.class);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.syncData();
	}

	@Override
	protected void postInit() {
		this.syncData();
	}

	public void syncData() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			this.save();
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
				this.nbt = data.getData() != null ? data.getData() : new NBTTagCompound();
				//System.out.println("SEND: " + this.nbt);
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
				//System.out.println("RECEIVE: " + message.nbt);
				synchronized(CHUNK_DATA_HANDLER) {
					ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(message.chunkX, message.chunkZ);
					NBTTagCompound currentNBT = CHUNK_NBT_CACHE.get(chunkPos);
					if(currentNBT == null)
						currentNBT = new NBTTagCompound();
					currentNBT.setTag(message.name, message.nbt);
					CHUNK_NBT_CACHE.put(chunkPos, currentNBT);
					for(Entry<ChunkDataTypePair, ChunkDataBase> dataPair : CACHE.entrySet()) {
						if(message.name.equals(dataPair.getValue().name) && dataPair.getKey().pos.equals(chunkPos)) {
							dataPair.getValue().readFromNBT(currentNBT);
							dataPair.getValue().load();
						}
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
						BetweenlandsChunkData data = BetweenlandsChunkData.forChunk(chunk);
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
}
