package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.ITickable;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

public abstract class WorldStorageImpl implements IWorldStorage {
	private final Map<ChunkPos, ChunkStorageImpl> storageMap = new HashMap<>();
	private final List<ITickable> tickableStorages = new ArrayList<>();

	private Level level;

	private ILocalStorageHandler localStorageHandler;

	//TODO
	@Nullable
	public static IWorldStorage getAttachment(Level level) {
		return null;
	}

	/**
	 * Called after the world is set
	 */
	protected void init() {

	}

	/**
	 * Sets the capability's world
	 *
	 * @param level
	 */
	private void setLevel(Level level) {
		this.level = level;
		this.localStorageHandler = new LocalStorageHandlerImpl(this);
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {

	}

	@Override
	public void readFromNBT(CompoundTag nbt) {

	}

	@Override
	public void loadChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.setDefaults();
				this.storageMap.put(chunk.getPos(), storage);

				if (storage instanceof ITickable) {
					this.tickableStorages.add((ITickable) storage);
				}

				//Makes sure that the default values are saved
				chunk.setUnsaved(true);
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error(String.format("Failed creating chunk storage at %s", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"), ex);
			}
		}
	}

	@Override
	public void readAndLoadChunk(ChunkAccess chunk, CompoundTag nbt) {
		if (this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn(String.format("Reading chunk storage at %s, but chunk storage is already loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"));
		} else {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.readFromNBT(nbt, false);
				this.storageMap.put(chunk.getPos(), storage);

				if (storage instanceof ITickable) {
					this.tickableStorages.add((ITickable) storage);
				}
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error(String.format("Failed reading chunk storage at %s", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"), ex);
			}
		}
	}

	@Override
	public void unloadChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn(String.format("Unloading chunk storage at %s, but chunk storage is not loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"));
		} else {
			ChunkStorageImpl storage = this.storageMap.remove(chunk.getPos());
			if (storage != null) {
				this.tickableStorages.remove(storage);
			}
			storage.onUnload();
		}
	}

	@Override
	public CompoundTag saveChunk(ChunkAccess chunk) {
		if (!this.storageMap.containsKey(chunk.getPos())) {
			if (BetweenlandsConfig.debug) TheBetweenlands.LOGGER.warn(String.format("Saving chunk storage at %s, but chunk storage is not loaded!", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"));
		} else {
			try {
				ChunkStorageImpl storage = this.storageMap.get(chunk.getPos());
				CompoundTag nbt = storage.writeToNBT(new CompoundTag(), false);
				storage.setDirty(false);
				return nbt;
			} catch (Exception ex) {
				TheBetweenlands.LOGGER.error(String.format("Failed saving chunk storage at %s", "[x=" + chunk.getPos().x + ", z=" + chunk.getPos().z + "]"), ex);
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

	@Override
	public ChunkStorageImpl getChunkStorage(ChunkAccess chunk) {
		return this.storageMap.get(chunk.getPos());
	}

	@Override
	public ILocalStorageHandler getLocalStorageHandler() {
		return this.localStorageHandler;
	}

	@Override
	public void tick() {
		this.localStorageHandler.tick();

		for (ITickable tickable : this.tickableStorages) {
			tickable.tick();
		}
	}
}
