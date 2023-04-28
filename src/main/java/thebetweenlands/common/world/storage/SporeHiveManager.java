package thebetweenlands.common.world.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.LocalStorageMetadata;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.world.storage.location.LocationSporeHive;

public class SporeHiveManager {
	protected final World world;

	protected final int range;
	protected final int chunkRange;

	protected final LongSet occupiedChunks = new LongOpenHashSet();

	@SuppressWarnings("serial")
	protected final Set<StorageID> recentlyCheckedLocations = Collections.newSetFromMap(new LinkedHashMap<StorageID, Boolean>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<StorageID, Boolean> eldest) {
			return this.size() > 255;
		}
	});

	public SporeHiveManager(World world, int range) {
		this.world = world;
		this.range = range;
		this.chunkRange = (range >> 4) + 1;
	}

	public boolean occupyIfFree(BlockPos pos) {
		ChunkPos chunk = new ChunkPos(pos);
		long chunkId = ChunkPos.asLong(chunk.x, chunk.z);

		if(this.occupiedChunks.contains(chunkId)) {
			return false;
		}

		int rangeSq = this.range * this.range;
		int chunkRangeSq = rangeSq / 256 + 1;

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.world);
		ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();

		int sx = MathHelper.floor(pos.getX() - this.range) >> 4;
		int sz = MathHelper.floor(pos.getZ() - this.range) >> 4;
		int ex = MathHelper.floor(pos.getX() + this.range) >> 4;
		int ez = MathHelper.floor(pos.getZ() + this.range) >> 4;

		int mx = (sx + ex) / 2;
		int mz = (sz + ez) / 2;

		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				if((mx - cx) * (mx - cx) + (mz - cz) * (mz - cz) <= chunkRangeSq) {
					for(LocalStorageMetadata metadata : handler.getMetadata(LocationSporeHive.class, new ChunkPos(cx, cz), false)) {
						this.setOccupiedChunk(chunk);

						if(this.recentlyCheckedLocations.add(metadata.getStorageId())) {
							this.setOrClearOccupiedChunksAround(pos, true);
						}

						BlockPos source = LocationSporeHive.getSourcePosFromMetadata(metadata.getMetadata());

						if(source.distanceSq(pos) <= rangeSq) {
							return false;
						}
					}
				}
			}
		}

		this.setOccupiedChunk(chunk);
		this.setOrClearOccupiedChunksAround(pos, true);

		return true;
	}

	public void setOccupiedChunk(ChunkPos chunk) {
		if(this.world.getChunkProvider().getLoadedChunk(chunk.x, chunk.z) != null) {
			this.occupiedChunks.add(ChunkPos.asLong(chunk.x, chunk.z));
		}
	}

	public void clearOccupiedChunk(BlockPos pos) {
		this.clearOccupiedChunk(new ChunkPos(pos));
	}
	
	public void clearOccupiedChunk(ChunkPos chunk) {
		this.occupiedChunks.remove(ChunkPos.asLong(chunk.x, chunk.z));
	}

	public void setOrClearOccupiedChunksAround(BlockPos pos, boolean set) {
		int rangeSq = this.range * this.range;
		int chunkRangeSq = rangeSq / 256 + 1;

		int sx = MathHelper.floor(pos.getX() - this.range) >> 4;
		int sz = MathHelper.floor(pos.getZ() - this.range) >> 4;
		int ex = MathHelper.floor(pos.getX() + this.range) >> 4;
		int ez = MathHelper.floor(pos.getZ() + this.range) >> 4;

		int mx = (sx + ex) / 2;
		int mz = (sz + ez) / 2;

		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				if(set) {
					int bx = cx * 16;
					int bz = cz * 16;

					if(bx > pos.getX()) {
						bx += 16;
					}
					if(bz > pos.getZ()) {
						bz += 16;
					}
					
					if((bx - pos.getX()) * (bx - pos.getX()) + (bz - pos.getZ()) * (bz - pos.getZ()) <= rangeSq) {
						this.setOccupiedChunk(new ChunkPos(cx, cz));
					}
				} else {
					if((mx - cx) * (mx - cx) + (mz - cz) * (mz - cz) <= chunkRangeSq) {
						this.clearOccupiedChunk(new ChunkPos(cx, cz));
					}
				}
			}
		}
	}

	public void clearLocation(ILocalStorage location) {
		if(location instanceof LocationSporeHive) {
			BlockPos source = ((LocationSporeHive) location).getSource();
			if(source != null) {
				this.setOrClearOccupiedChunksAround(source, false);
			}
		}
		this.recentlyCheckedLocations.remove(location.getID());
	}

	public void clearCache() {
		this.occupiedChunks.clear();
		this.recentlyCheckedLocations.clear();
	}
}
