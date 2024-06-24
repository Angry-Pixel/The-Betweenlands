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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thebetweenlands.api.storage.IOfflinePlayerDataHandler;
import thebetweenlands.common.TheBetweenlands;

public class OfflinePlayerHandlerImpl implements IOfflinePlayerDataHandler {
	private static class OfflinePlayerData {
		private CompoundTag nbt;
		private boolean dirty;
		private long timestamp;

		protected void setData(CompoundTag nbt) {
			this.nbt = nbt;
			this.dirty = true;
			this.refreshUseTimestamp();
		}

		protected CompoundTag getData() {
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

	private ServerLevel level;

	public OfflinePlayerHandlerImpl(ServerLevel level) {
		this.level = level;
	}

	@Override
	public void updateCache() {
		Iterator<Entry<UUID, OfflinePlayerData>> playerDataIT = this.offlinePlayerDataCache.entrySet().iterator();
		while (playerDataIT.hasNext()) {
			Entry<UUID, OfflinePlayerData> entry = playerDataIT.next();

			OfflinePlayerData data = entry.getValue();

			if (data.isExpired()) {
				if (data.isDirty()) {
					this.saveOfflinePlayerDataSafely(entry.getKey(), data.getData());
				}

				playerDataIT.remove();
			}
		}
	}

	@Nullable
	@Override
	public CompoundTag getOfflinePlayerData(UUID playerUuid) {
		OfflinePlayerData data = this.offlinePlayerDataCache.get(playerUuid);
		if (data != null) {
			data.refreshUseTimestamp();
			return data.nbt;
		}

		CompoundTag nbt = this.loadOfflinePlayerDataSafely(playerUuid);
		if (nbt != null) {
			data = new OfflinePlayerData();
			data.setData(nbt);
			this.offlinePlayerDataCache.put(playerUuid, data);
			return nbt;
		}

		return null;
	}

	private File getOfflinePlayerDataFolder(ServerLevel level) {
		File file = new File(new File(level.getSaveHandler().getWorldDirectory(), "playerdata"), "offline_player_data");
		file.mkdirs();
		return file;
	}

	@Nullable
	private CompoundTag loadOfflinePlayerDataSafely(UUID playerUuid) {
		try {
			return this.loadOfflinePlayerData(playerUuid);
		} catch (IOException ex) {
			TheBetweenlands.LOGGER.error(String.format("Failed loading offline player data for UUID %s", playerUuid.toString()), ex);
		}
		return null;
	}

	@Nullable
	private CompoundTag loadOfflinePlayerData(UUID playerUuid) throws IOException {
		String fileName = playerUuid.toString();

		File file = new File(this.getOfflinePlayerDataFolder(this.level), fileName + ".dat");

		if (file.exists()) {
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		}

		return null;
	}

	@Override
	public void setOfflinePlayerData(UUID playerUuid, CompoundTag nbt) {
		OfflinePlayerData data = this.offlinePlayerDataCache.get(playerUuid);
		if (data != null) {
			data.setData(nbt);
		} else {
			data = new OfflinePlayerData();
			data.setData(nbt);
			this.offlinePlayerDataCache.put(playerUuid, data);
		}
	}

	private boolean saveOfflinePlayerDataSafely(UUID playerUuid, CompoundTag nbt) {
		try {
			this.saveOfflinePlayerData(playerUuid, nbt);
			return true;
		} catch (IOException ex) {
			TheBetweenlands.LOGGER.error(String.format("Failed saving offline player data for UUID %s", playerUuid.toString()), ex);
		}
		return false;
	}

	private void saveOfflinePlayerData(UUID playerUuid, CompoundTag nbt) throws IOException {
		String fileName = playerUuid.toString();

		File folder = this.getOfflinePlayerDataFolder(this.level);

		File tempFile = new File(folder, fileName + ".dat.tmp");
		File currentFile = new File(folder, fileName + ".dat");

		CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(tempFile));

		if (currentFile.exists()) {
			currentFile.delete();
		}

		if (!tempFile.renameTo(currentFile)) {
			throw new IOException(String.format("Failed renaming %s to %s", tempFile, currentFile));
		}
	}

	@Override
	public void saveAllOfflinePlayerData() {
		for (Entry<UUID, OfflinePlayerData> entry : this.offlinePlayerDataCache.entrySet()) {
			if (entry.getValue().isDirty()) {
				this.saveOfflinePlayerDataSafely(entry.getKey(), entry.getValue().getData());
			}
		}
	}

	private static OfflinePlayerHandlerImpl handler = null;

	@Nullable
	public static IOfflinePlayerDataHandler getHandler() {
		return handler;
	}

	private static ServerLevel getMainWorld(LevelAccessor level) {
		if (level instanceof ServerLevel server && level == server.getLevel()) {
			return server;
		}
		return null;
	}

	//TODO hook up events to game bus
	public static void onWorldLoad(LevelEvent.Load event) {
		ServerLevel level = getMainWorld(event.getLevel());
		if (level != null) {
			if (handler != null) {
				handler.saveAllOfflinePlayerData();
			}
			handler = new OfflinePlayerHandlerImpl(level);
		}
	}

	public static void onWorldUnload(LevelEvent.Unload event) {
		ServerLevel world = getMainWorld(event.getLevel());
		if (world != null) {
			if (handler != null) {
				handler.saveAllOfflinePlayerData();
			}
			handler = null;
		}
	}

	public static void onWorldSave(LevelEvent.Save event) {
		ServerLevel world = getMainWorld(event.getLevel());
		if (world != null && handler != null) {
			handler.saveAllOfflinePlayerData();
		}
	}

	public static void onServerTick(ServerTickEvent.Post event) {
		if (handler != null) {
			handler.updateCache();
		}
	}
}
