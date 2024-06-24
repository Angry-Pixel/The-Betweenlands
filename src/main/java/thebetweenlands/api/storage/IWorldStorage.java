package thebetweenlands.api.storage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public interface IWorldStorage {
	/**
	 * Writes the world data to the tag
	 * @param tag
	 */
	void writeToNBT(CompoundTag tag);

	/**
	 * Reads the world data from the tag
	 * @param tag
	 */
	void readFromNBT(CompoundTag tag);

	/**
	 * Returns the world instance
	 * @return
	 */
	Level getLevel();

	/**
	 * Called when a chunk storage needs to be read from the specified NBT and loaded
	 * @param chunk
	 * @param tag
	 */
	void readAndLoadChunk(ChunkAccess chunk, CompoundTag tag);

	/**
	 * Called when a new chunk is loaded without any NBT data
	 * @param chunk
	 */
	void loadChunk(ChunkAccess chunk);

	/**
	 * Saves the chunk storage data to NBT. May return
	 * null if no data needs to be saved
	 * @param chunk
	 * @return
	 */
	@Nullable
	CompoundTag saveChunk(ChunkAccess chunk);

	/**
	 * Called when a chunk is unloaded
	 * @param chunk
	 */
	void unloadChunk(ChunkAccess chunk);

	/**
	 * Called when a player starts watching the specified chunk
	 * @param pos
	 * @param player
	 */
	void watchChunk(ChunkPos pos, ServerPlayer player);

	/**
	 * Called when a player stops watching the specified chunk
	 * @param pos
	 * @param player
	 */
	void unwatchChunk(ChunkPos pos, ServerPlayer player);

	/**
	 * Returns the chunk storage of the specified chunk
	 * @param chunk
	 * @return
	 */
	IChunkStorage getChunkStorage(ChunkAccess chunk);

	/**
	 * Returns the local storage handler responsible for loading and
	 * saving local storage from/to files and keeping track
	 * the local storage instances
	 * @return
	 */
	ILocalStorageHandler getLocalStorageHandler();

	/**
	 * Ticks the world storage
	 */
	void tick();
}
