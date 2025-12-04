package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import thebetweenlands.api.storage.TickableStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

import javax.annotation.Nullable;

public abstract class WorldStorageImpl implements IWorldStorage {
	private final Map<ChunkPos, ChunkStorageImpl> storageMap = new HashMap<>();
	private final List<TickableStorage> tickableStorages = new ArrayList<>();

	private ILocalStorageHandler localStorageHandler;

	private ResourceKey<Level> dimension;

	/**
	 * Called after the world is set
	 */
	protected void init(Level level) {
		this.dimension = level.dimension();
		this.localStorageHandler = new LocalStorageHandlerImpl(level, this);
	}

	@Override
	public ResourceKey<Level> getDimension() {
		return this.dimension;
	}

	@Override
	public void loadChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.setDefaults();
				this.storageMap.put(chunk.getPos(), storage);
				this.tickableStorages.add(storage);
				//Makes sure that the default values are saved
				chunk.setUnsaved(true);
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error("Failed creating chunk storage at {}", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]", ex);
			}
		}
	}

	@Override
	public void readAndLoadChunk(ChunkAccess chunk, CompoundTag nbt) {
		if (this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn("Reading chunk storage at {}, but chunk storage is already loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]");
		} else {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.readFromNBT(chunk.getLevel(), nbt, false);
				this.storageMap.put(chunk.getPos(), storage);

				this.tickableStorages.add(storage);
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error("Failed reading chunk storage at {}", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]", ex);
			}
		}
	}

	@Override
	public void unloadChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn("Unloading chunk storage at {}, but chunk storage is not loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]");
		} else {
			ChunkStorageImpl storage = this.storageMap.remove(chunk.getPos());
			if (storage != null) {
				this.tickableStorages.remove(storage);
			}
			storage.onUnload(chunk.getLevel());
		}
	}

	@Nullable
	@Override
	public CompoundTag saveChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn("Saving chunk storage at {}, but chunk storage is not loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]");
		} else {
			try {
				ChunkStorageImpl storage = this.storageMap.get(chunk.getPos());
				CompoundTag nbt = storage.writeToNBT(new CompoundTag(), false);
				storage.setDirty(false);
				return nbt;
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error("Failed saving chunk storage at {}", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]", ex);
			}
		}
		return null;
	}

	@Override
	public void watchChunk(ChunkPos pos, ServerPlayer player) {
		ChunkStorageImpl storage = this.storageMap.get(pos);
		if (storage != null) {
			storage.addWatcher(player);
		}
	}

	@Override
	public void unwatchChunk(ChunkPos pos, ServerPlayer player) {
		ChunkStorageImpl storage = this.storageMap.get(pos);
		if (storage != null) {
			storage.removeWatcher(player);
		}
	}

	@Nullable
	@Override
	public ChunkStorageImpl getChunkStorage(ChunkAccess chunk) {
		return this.storageMap.get(chunk.getPos());
	}

	@Override
	public ILocalStorageHandler getLocalStorageHandler() {
		return this.localStorageHandler;
	}

	@Override
	public void tick(Level level) {
		if (!level.isClientSide()) {
			this.localStorageHandler.tick(level);
		}

		for (TickableStorage tickable : this.tickableStorages) {
			tickable.tick(level);
		}
	}
}
