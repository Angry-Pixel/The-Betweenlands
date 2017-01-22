package thebetweenlands.common.world.storage.world.shared.location.guard;

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
	protected final Long2ObjectMap<GuardChunk> chunkMap = new Long2ObjectOpenHashMap<>(8192);

	@Override
	public void setGuarded(World world, BlockPos pos, boolean guarded) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		long id = ChunkPos.chunkXZ2Int(x / 16, z / 16);
		GuardChunk chunk = this.chunkMap.get(id);
		if(guarded) {
			if(chunk == null) {
				this.chunkMap.put(id, chunk = new GuardChunk(x / 16, z / 16));
			}
			chunk.setGuarded(x & 15, y, z & 15, true);
		} else if(chunk != null) {
			chunk.setGuarded(x & 15, y, z & 15, false);
		}
	}

	@Override
	public boolean isGuarded(World world, @Nullable Entity entity, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		long id = ChunkPos.chunkXZ2Int(x / 16, z / 16);
		GuardChunk chunk = this.chunkMap.get(id);
		if(chunk != null && chunk.isGuarded(x & 15, y, z & 15)) {
			return true;
		}
		return false;
	}

	@Override
	public void clear(World world) {
		this.chunkMap.clear();
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
				this.chunkMap.put(ChunkPos.chunkXZ2Int(x, z), chunk);
			}
		}
	}

	protected static class GuardChunkSection {
		private final byte[] data; //8 blocks per byte, 2 bytes per row, 2*16*16 bytes in total
		private int blockRefCount = 0;

		protected GuardChunkSection() {
			this.data = new byte[512];
		}

		protected GuardChunkSection(byte[] data) {
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

		public void setGuarded(int x, int y, int z, boolean guarded) {
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
			}
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
		}

		public byte[] getData() {
			return this.data;
		}
	}

	protected static class GuardChunk {
		private final GuardChunkSection[] sections = new GuardChunkSection[16];
		public final int x, z;

		public GuardChunk(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public void setGuarded(int x, int y, int z, boolean guarded) {
			if(y >= 0 && y < 256) {
				int sectionId = y >> 4;
				GuardChunkSection section = this.sections[sectionId];
				if(guarded) {
					if(section == null) {
						this.sections[sectionId] = section = new GuardChunkSection();
					}
					section.setGuarded(x, y & 15, z, true);
				} else if(section != null) {
					section.setGuarded(x, y & 15, z, false);
					if(section.isEmpty()) {
						this.sections[sectionId] = null;
					}
				}
			}
		}

		public boolean isGuarded(int x, int y, int z) {
			int sectionId = y >> 4;
			GuardChunkSection section = this.sections[sectionId];
			if(section != null && section.isGuarded(x, y & 15, z)) {
				return true;
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
					byte[] data = section.getData();
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
