package thebetweenlands.common.world.storage.location.guard;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class BlockLocationGuard implements ILocationGuard {
	protected final Long2ObjectMap<GuardChunk> chunkMap = new Long2ObjectOpenHashMap<>(16);

	/**
	 * Returns the chunk at the specified position
	 * @param pos
	 * @return
	 */
	@Nullable
	public GuardChunk getChunk(BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		long id = ChunkPos.asLong(x / 16, z / 16);
		return this.chunkMap.get(id);
	}

	/**
	 * Returns the chunk section at the specified position
	 * @param pos
	 * @return
	 */
	@Nullable
	public GuardChunkSection getSection(BlockPos pos) {
		GuardChunk chunk = this.getChunk(pos);
		if(chunk != null) {
			return chunk.getSection(pos.getY());
		}
		return null;
	}

	@Override
	public boolean setGuarded(Level level, BlockPos pos, boolean guarded) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		long id = ChunkPos.asLong(x / 16, z / 16);
		GuardChunk chunk = this.chunkMap.get(id);
		if(guarded) {
			if(chunk == null) {
				this.chunkMap.put(id, chunk = new GuardChunk(x / 16, z / 16));
			}
			return chunk.setGuarded(x & 15, y, z & 15, true);
		} else if(chunk != null) {
			return chunk.setGuarded(x & 15, y, z & 15, false);
		}
		return false;
	}

	@Override
	public boolean isGuarded(Level level, @Nullable Entity entity, BlockPos pos) {
		if(pos.getY() >= 0) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			long id = ChunkPos.asLong(x / 16, z / 16);
			GuardChunk chunk = this.chunkMap.get(id);
			return chunk != null && chunk.isGuarded(x & 15, y, z & 15);
		}
		return false;
	}

	@Override
	public void clear(Level level) {
		this.chunkMap.clear();
	}

	@Override
	public boolean isClear(Level level) {
		return this.chunkMap.isEmpty();
	}

	@Override
	public void handleExplosion(Level level, Explosion explosion) {
		/*Iterator<BlockPos> posIT = explosion.getAffectedBlockPositions().iterator();
		while(posIT.hasNext()) {
			BlockPos pos = posIT.next();
			if(this.isGuarded(null, explosion.getExplosivePlacedBy(), pos)) {
				posIT.remove();
			}
		}*/
		explosion.clearToBlow();
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		ListTag chunksList = new ListTag();
		for(GuardChunk chunk : this.chunkMap.values()) {
			CompoundTag chunkNbt = new CompoundTag();
			chunkNbt.putInt("X", chunk.x);
			chunkNbt.putInt("Z", chunk.z);
			chunk.writeToNBT(chunkNbt);
			chunksList.add(chunkNbt);
		}
		nbt.put("Chunks", chunksList);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.chunkMap.clear();
		if(nbt.contains("Chunks", Tag.TAG_LIST)) {
			ListTag chunksList = nbt.getList("Chunks", Tag.TAG_COMPOUND);
			for(int i = 0; i < chunksList.size(); i++) {
				CompoundTag chunkNbt = chunksList.getCompound(i);
				int x = chunkNbt.getInt("X");
				int z = chunkNbt.getInt("Z");
				GuardChunk chunk = new GuardChunk(x, z);
				chunk.readFromNBT(chunkNbt);
				this.chunkMap.put(ChunkPos.asLong(x, z), chunk);
			}
		}
	}

	public static class GuardChunkSection {
		private final byte[] data; //8 blocks per byte, 2 bytes per row, 2*16*16 bytes in total
		private int blockRefCount = 0;

		private GuardChunkSection() {
			this.data = new byte[512];
		}

		private GuardChunkSection(byte[] data) {
			this.data = data;
			this.updateBlockRefCount();
		}

		protected int getByteIndex(int x, int y, int z) {
			return (x >> 3) + (z << 1) + (y << 5);
		}

		public void updateBlockRefCount() {
			this.blockRefCount = 0;
			for (byte datum : this.data) {
				this.blockRefCount += Integer.bitCount(datum & 0xFF);
			}
		}

		public int getBlockRefCount() {
			return this.blockRefCount;
		}

		public boolean setGuarded(int x, int y, int z, boolean guarded) {
			int byteIndex = this.getByteIndex(x, y, z);
			byte mask = (byte)(1 << (x & 7));
			byte data = this.data[byteIndex];

			if(((data & mask) == 0) == guarded) {
				this.data[byteIndex] = (byte) (guarded ? (data | mask) : (data & (~mask)));

				if(guarded) {
					this.blockRefCount++;
				} else {
					this.blockRefCount--;
				}

				return true;
			}

			return false;
		}

		public boolean isGuarded(int x, int y, int z) {
			int byteIndex = this.getByteIndex(x, y, z);
			byte mask = (byte)(1 << (x & 7));
			return (this.data[byteIndex] & mask) != 0;
		}

		public boolean isEmpty() {
			return this.blockRefCount == 0;
		}

		public void clear() {
			Arrays.fill(this.data, (byte) 0);
			this.blockRefCount = 0;
		}

		public void loadData(byte[] arr) {
			System.arraycopy(arr, 0, this.data, 0, 512);
			this.updateBlockRefCount();
		}

		public void writeData(byte[] arr) {
			System.arraycopy(this.data, 0, arr, 0, 512);
		}
	}

	public static class GuardChunk {
		private final GuardChunkSection[] sections = new GuardChunkSection[16];
		public final int x, z;

		private GuardChunk(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public GuardChunkSection getSection(int y) {
			return this.sections[y >> 4];
		}

		public boolean setGuarded(int x, int y, int z, boolean guarded) {
			if(y >= 0 && y < 256) {
				boolean changed = false;
				GuardChunkSection section = this.sections[y >> 4];
				if(guarded) {
					if(section == null) {
						this.sections[y >> 4] = section = new GuardChunkSection();
					}
					changed = section.setGuarded(x, y & 15, z, true);
				} else if(section != null) {
					changed = section.setGuarded(x, y & 15, z, false);
					if(section.isEmpty()) {
						this.sections[y >> 4] = null;
					}
				}
				return changed;
			}

			return false;
		}

		public boolean isGuarded(int x, int y, int z) {
			int sectionId = y >> 4;
			if(sectionId >= 0) {
				GuardChunkSection section = this.sections[sectionId];
				return section != null && section.isGuarded(x, y & 15, z);
			}
			return false;
		}

		public void clear() {
			Arrays.fill(this.sections, null);
		}

		public CompoundTag writeToNBT(CompoundTag nbt) {
			ListTag sectionsNbt = new ListTag();
			for(int i = 0; i < this.sections.length; i++) {
				GuardChunkSection section = this.sections[i];
				if(section != null) {
					byte[] data = new byte[512];
					section.writeData(data);
					CompoundTag sectionNbt = new CompoundTag();
					sectionNbt.putByte("Y", (byte)i);
					sectionNbt.putByteArray("Data", data);
					sectionsNbt.add(sectionNbt);
				}
			}
			nbt.put("Sections", sectionsNbt);
			return nbt;
		}

		public void readFromNBT(CompoundTag nbt) {
			this.clear();
			if(nbt.contains("Sections", Tag.TAG_LIST)) {
				ListTag sectionsNbt = nbt.getList("Sections", Tag.TAG_COMPOUND);
				for(int i = 0; i < sectionsNbt.size(); i++) {
					CompoundTag sectionNbt = sectionsNbt.getCompound(i);
					int y = sectionNbt.getByte("Y");
					byte[] data = sectionNbt.getByteArray("Data");
					this.sections[y] = new GuardChunkSection(data);
				}
			}
		}
	}
}
