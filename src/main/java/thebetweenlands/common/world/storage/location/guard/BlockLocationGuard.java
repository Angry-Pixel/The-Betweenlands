package thebetweenlands.common.world.storage.location.guard;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
	public boolean setGuarded(World world, BlockPos pos, boolean guarded) {
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
	public boolean isGuarded(World world, @Nullable Entity entity, BlockPos pos) {
		if(pos.getY() >= 0) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			long id = ChunkPos.asLong(x / 16, z / 16);
			GuardChunk chunk = this.chunkMap.get(id);
			if(chunk != null && chunk.isGuarded(x & 15, y, z & 15)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void clear(World world) {
		this.chunkMap.clear();
	}

	@Override
	public boolean isClear(World world) {
		return this.chunkMap.isEmpty();
	}

	@Override
	public void handleExplosion(World world, Explosion explosion) {
		/*Iterator<BlockPos> posIT = explosion.getAffectedBlockPositions().iterator();
		while(posIT.hasNext()) {
			BlockPos pos = posIT.next();
			if(this.isGuarded(null, explosion.getExplosivePlacedBy(), pos)) {
				posIT.remove();
			}
		}*/
		explosion.clearAffectedBlockPositions();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList chunksList = new NBTTagList();
		for(GuardChunk chunk : this.chunkMap.values()) {
			NBTTagCompound chunkNbt = new NBTTagCompound();
			chunkNbt.setInteger("X", chunk.x);
			chunkNbt.setInteger("Z", chunk.z);
			chunk.writeToNBT(chunkNbt);
			chunksList.appendTag(chunkNbt);
		}
		nbt.setTag("Chunks", chunksList);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.chunkMap.clear();
		if(nbt.hasKey("Chunks", Constants.NBT.TAG_LIST)) {
			NBTTagList chunksList = nbt.getTagList("Chunks", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < chunksList.tagCount(); i++) {
				NBTTagCompound chunkNbt = chunksList.getCompoundTagAt(i);
				int x = chunkNbt.getInteger("X");
				int z = chunkNbt.getInteger("Z");
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
			for(int i = 0; i < this.data.length; i++) {
				this.blockRefCount += Integer.bitCount(this.data[i] & 0xFF);
			}
		}

		public int getBlockRefCount() {
			return this.blockRefCount;
		}

		public boolean setGuarded(int x, int y, int z, boolean guarded) {
			int byteIndex = this.getByteIndex(x, y, z);
			byte mask = (byte)(1 << (x & 7));
			byte data = this.data[byteIndex];

			if(((data & mask) != 0) != guarded) {
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
			for(int i = 0; i < this.data.length; i++) {
				this.data[i] = 0;
			}
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
				if(section != null && section.isGuarded(x, y & 15, z)) {
					return true;
				}
			}
			return false;
		}

		public void clear() {
			for(int i = 0; i < this.sections.length; i++) {
				this.sections[i] = null;
			}
		}

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			NBTTagList sectionsNbt = new NBTTagList();
			for(int i = 0; i < this.sections.length; i++) {
				GuardChunkSection section = this.sections[i];
				if(section != null) {
					byte[] data = new byte[512];
					section.writeData(data);
					NBTTagCompound sectionNbt = new NBTTagCompound();
					sectionNbt.setByte("Y", (byte)i);
					sectionNbt.setByteArray("Data", data);
					sectionsNbt.appendTag(sectionNbt);
				}
			}
			nbt.setTag("Sections", sectionsNbt);
			return nbt;
		}

		public void readFromNBT(NBTTagCompound nbt) {
			this.clear();
			if(nbt.hasKey("Sections", Constants.NBT.TAG_LIST)) {
				NBTTagList sectionsNbt = nbt.getTagList("Sections", Constants.NBT.TAG_COMPOUND);
				for(int i = 0; i < sectionsNbt.tagCount(); i++) {
					NBTTagCompound sectionNbt = sectionsNbt.getCompoundTagAt(i);
					int y = sectionNbt.getByte("Y");
					byte[] data = sectionNbt.getByteArray("Data");
					this.sections[y] = new GuardChunkSection(data);
				}
			}
		}
	}
}
