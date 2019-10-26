package thebetweenlands.common.world.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.api.storage.IOfflinePlayerDataHandler;
import thebetweenlands.common.TheBetweenlands;

public class OfflinePlayerHandlerImpl implements IOfflinePlayerDataHandler {
	private static class OfflinePlayerData {
		private NBTTagCompound nbt;
		private boolean dirty;
		private long timestamp;

		protected void setData(NBTTagCompound nbt) {
			this.nbt = nbt;
			this.dirty = true;
			this.refreshUseTimestamp();
		}

		protected NBTTagCompound getData() {
			return this.nbt;
		}

		protected void refreshUseTimestamp() {
			this.timestamp = System.currentTimeMillis();
		}

		protected boolean isExpired() {
			return System.currentTimeMillis() - this.timestamp > 30000;
		}

		protected boolean isDirty() {
			return this.dirty;
		}

		protected void resetDirty() {
			this.dirty = false;
		}
	}

	private Map<UUID, OfflinePlayerData> offlinePlayerDataCache = new HashMap<>();

	private WorldServer world;

	public OfflinePlayerHandlerImpl(WorldServer world) {
		this.world = world;
	}

	@Override
	public void updateCache() {
		Iterator<Entry<UUID, OfflinePlayerData>> playerDataIT = this.offlinePlayerDataCache.entrySet().iterator();
		while(playerDataIT.hasNext()) {
			Entry<UUID, OfflinePlayerData> entry = playerDataIT.next();

			OfflinePlayerData data = entry.getValue();

			if(data.isExpired()) {
				if(data.isDirty()) {
					this.saveOfflinePlayerDataSafely(entry.getKey(), data.getData());
				}

				playerDataIT.remove();
			}
		}
	}

	@Nullable
	@Override
	public NBTTagCompound getOfflinePlayerData(UUID playerUuid) {
		OfflinePlayerData data = this.offlinePlayerDataCache.get(playerUuid);
		if(data != null) {
			data.refreshUseTimestamp();
			return data.nbt;
		}

		NBTTagCompound nbt = this.loadOfflinePlayerDataSafely(playerUuid);
		if(nbt != null) {
			data = new OfflinePlayerData();
			data.setData(nbt);
			this.offlinePlayerDataCache.put(playerUuid, data);
			return nbt;
		}

		return null;
	}

	private File getOfflinePlayerDataFolder(WorldServer world) {
		File file = new File(new File(world.getSaveHandler().getWorldDirectory(), "playerdata"), "offline_player_data");
		file.mkdirs();
		return file;
	}

	@Nullable
	private NBTTagCompound loadOfflinePlayerDataSafely(UUID playerUuid) {
		try {
			return this.loadOfflinePlayerData(playerUuid);
		} catch(IOException ex) {
			TheBetweenlands.logger.error(String.format("Failed loading offline player data for UUID %s", playerUuid.toString()), ex);
		}
		return null;
	}

	@Nullable
	private NBTTagCompound loadOfflinePlayerData(UUID playerUuid) throws IOException {
		String fileName = playerUuid.toString();

		File file = new File(this.getOfflinePlayerDataFolder((WorldServer) this.world), fileName + ".dat");

		if(file.exists()) {
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		}

		return null;
	}

	@Override
	public void setOfflinePlayerData(UUID playerUuid, NBTTagCompound nbt) {
		OfflinePlayerData data = this.offlinePlayerDataCache.get(playerUuid);
		if(data != null) {
			data.setData(nbt);
		} else {
			data = new OfflinePlayerData();
			data.setData(nbt);
			this.offlinePlayerDataCache.put(playerUuid, data);
		}
	}

	private boolean saveOfflinePlayerDataSafely(UUID playerUuid, NBTTagCompound nbt) {
		try {
			this.saveOfflinePlayerData(playerUuid, nbt);
			return true;
		} catch(IOException ex) {
			TheBetweenlands.logger.error(String.format("Failed saving offline player data for UUID %s", playerUuid.toString()), ex);
		}
		return false;
	}

	private void saveOfflinePlayerData(UUID playerUuid, NBTTagCompound nbt) throws IOException {
		String fileName = playerUuid.toString();

		File folder = this.getOfflinePlayerDataFolder((WorldServer) this.world);

		File tempFile = new File(folder, fileName + ".dat.tmp");
		File currentFile = new File(folder, fileName + ".dat");

		CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(tempFile));

		if (currentFile.exists()) {
			currentFile.delete();
		}

		if(!tempFile.renameTo(currentFile)) {
			throw new IOException(String.format("Failed renaming %s to %s", tempFile, currentFile));
		}
	}

	@Override
	public void saveAllOfflinePlayerData() {
		for(Entry<UUID, OfflinePlayerData> entry : this.offlinePlayerDataCache.entrySet()) {
			if(entry.getValue().isDirty()) {
				this.saveOfflinePlayerDataSafely(entry.getKey(), entry.getValue().getData());
			}
		}
	}

	private static OfflinePlayerHandlerImpl handler = null;

	@Nullable
	public static IOfflinePlayerDataHandler getHandler() {
		return handler;
	}

	private static WorldServer getMainWorld(World world) {
		if(world instanceof WorldServer && world == ((WorldServer) world).getMinecraftServer().getEntityWorld()) {
			return (WorldServer) world;
		}
		return null;
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		WorldServer world = getMainWorld(event.getWorld());
		if(world != null) {
			if(handler != null) {
				handler.saveAllOfflinePlayerData();
			}
			handler = new OfflinePlayerHandlerImpl((WorldServer) world);
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		WorldServer world = getMainWorld(event.getWorld());
		if(world != null) {
			if(handler != null) {
				handler.saveAllOfflinePlayerData();
			}
			handler = null;
		}
	}

	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event) {
		WorldServer world = getMainWorld(event.getWorld());
		if(world != null && handler != null) {
			handler.saveAllOfflinePlayerData();
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if(handler != null) {
			handler.updateCache();
		}
	}
}
