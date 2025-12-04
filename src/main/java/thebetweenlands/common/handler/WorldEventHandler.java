package thebetweenlands.common.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.WorldStorageGetter;

import java.util.HashMap;
import java.util.Map;

public class WorldEventHandler {

	public static final String CHUNK_NBT_TAG = TheBetweenlands.ID + ":chunk_data";

	private static final Map<LevelChunk, IWorldStorage> UNLOAD_QUEUE = new HashMap<>();

	public static void init() {
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onChunkLoad);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onChunkRead);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onChunkUnload);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onChunkSave);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onWatchChunk);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onUnwatchChunk);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onWorldSave);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onServerTick);
		NeoForge.EVENT_BUS.addListener(WorldEventHandler::onWorldTick);
	}

	private static void onChunkLoad(ChunkEvent.Load event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null && event.getChunk() instanceof LevelChunk chunk) {
			storage.loadChunk(chunk);

			if (!event.getLevel().isClientSide()) {
				IChunkStorage chunkStorage = storage.getChunkStorage(chunk);
				if (chunkStorage != null) {
					storage.getLocalStorageHandler().loadDeferredOperations(chunk.getLevel(), chunkStorage);
				}
			}
		}
	}

	private static void onChunkRead(ChunkDataEvent.Load event) {
		if (event.getData().contains(CHUNK_NBT_TAG, Tag.TAG_COMPOUND) && event.getType() == ChunkType.LEVELCHUNK) {
			IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
			if (storage != null) {
				storage.readAndLoadChunk(event.getChunk(), event.getData().getCompound(CHUNK_NBT_TAG));
			}
		}
	}

	private static void onChunkUnload(ChunkEvent.Unload event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null && event.getChunk() instanceof LevelChunk chunk) {
			if (event.getLevel().isClientSide()) {
				//Unload immediately on client side
				storage.unloadChunk(chunk);
			} else {
				//Queue chunk to be unloaded later because there's no way to know
				//whether chunk will be saved to disk or not
				UNLOAD_QUEUE.put(chunk, storage);
			}
		}
	}

	private static void onChunkSave(ChunkDataEvent.Save event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null && event.getChunk() instanceof LevelChunk chunk) {
			CompoundTag nbt = storage.saveChunk(event.getChunk());
			if (nbt != null) {
				event.getData().put(CHUNK_NBT_TAG, nbt);
			}
			if (!chunk.loaded) {
				//Unload immediately after saving
				storage.unloadChunk(chunk);
				UNLOAD_QUEUE.remove(chunk);
			}
		}
	}

	private static void onWatchChunk(ChunkWatchEvent.Watch event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null) {
			storage.watchChunk(event.getPos(), event.getPlayer());
		}
	}

	private static void onUnwatchChunk(ChunkWatchEvent.UnWatch event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null) {
			storage.unwatchChunk(event.getPos(), event.getPlayer());
		}
	}

	private static void onWorldSave(LevelEvent.Save event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());

		if (storage != null) {
			storage.getLocalStorageHandler().saveAll((Level) event.getLevel());
		}
	}

	private static void onServerTick(ServerTickEvent.Post event) {
		//Unload queued chunks that weren't saved to disk
		for (Map.Entry<LevelChunk, IWorldStorage> entry : UNLOAD_QUEUE.entrySet()) {
			LevelChunk chunk = entry.getKey();
			if (!chunk.loaded) {
				entry.getValue().unloadChunk(chunk);
			}
		}
		UNLOAD_QUEUE.clear();
	}

	private static void onWorldTick(LevelTickEvent.Post event) {
		IWorldStorage storage = WorldStorageGetter.getNullable(event.getLevel());
		if (storage != null) {
			storage.tick(event.getLevel());
		}
	}
}
