package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.GemSingerItem;


public class BetweenlandsChunkStorage extends ChunkStorageImpl {
	private IntSet savedGemTargets = new IntOpenHashSet();
	private Int2ObjectMap<IntSet> gemToPositions = new Int2ObjectOpenHashMap<>();

	private boolean rescanGemSingerTargets = false;

	public BetweenlandsChunkStorage(IWorldStorage worldStorage, ChunkAccess chunk) {
		super(worldStorage, chunk);
	}

	@Nullable
	public static BetweenlandsChunkStorage forChunk(Level level, ChunkAccess chunk) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(level);
		if(worldStorage != null) {
			ChunkStorageImpl chunkStorage = worldStorage.getChunkStorage(chunk);
			if(chunkStorage instanceof BetweenlandsChunkStorage) {
				return (BetweenlandsChunkStorage) chunkStorage;
			}
		}
		return null;
	}

	@Override
	public void setDefaults() {
		//Chunk is new and all gem targets will already be marked during world gen
		for(GemSingerItem.GemSingerTarget target : GemSingerItem.GemSingerTarget.values()) {
			this.savedGemTargets.add(target.getId());
		}
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt, boolean packet) {
		super.writeToNBT(nbt, packet);

		if(!packet) {
			nbt.put("gemTargetTypes", new IntArrayTag(this.savedGemTargets.toArray(new int[0])));

			ListTag gemToPositionsNbt = new ListTag();
			for(Int2ObjectMap.Entry<IntSet> entry : this.gemToPositions.int2ObjectEntrySet()) {
				CompoundTag targetNbt = new CompoundTag();
				targetNbt.putInt("id", entry.getIntKey());
				targetNbt.put("positions", new IntArrayTag(entry.getValue().toIntArray()));
				gemToPositionsNbt.add(targetNbt);
			}
			nbt.put("gemToPositions", gemToPositionsNbt);
		}

		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt, boolean packet) {
		super.readFromNBT(nbt, packet);

		if(!packet) {
			this.savedGemTargets.clear();
			int[] gemTargetTypes = nbt.getIntArray("gemTargetTypes");
			for(int target : gemTargetTypes) {
				this.savedGemTargets.add(target);
			}

			if(this.level.dimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				for(GemSingerItem.GemSingerTarget target : GemSingerItem.GemSingerTarget.values()) {
					if(!this.savedGemTargets.contains(target.getId())) {
						//A new gem singer target was added -> chunk needs to be rescanned
						this.rescanGemSingerTargets = true;
						break;
					}
				}
			}

			this.gemToPositions.clear();
			ListTag gemToPositionsNbt = nbt.getList("gemToPositions", Tag.TAG_COMPOUND);
			for(int i = 0; i < gemToPositionsNbt.size(); i++) {
				CompoundTag targetNbt = gemToPositionsNbt.getCompound(i);
				int id = targetNbt.getInt("id");
				int[] positions = targetNbt.getIntArray("positions");
				this.gemToPositions.put(id, new IntArraySet(positions));
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide() && this.rescanGemSingerTargets) {
			this.rescanGemSingerTargets = false;
			this.gemToPositions.clear();

			ChunkAccess chunk = this.getChunk();

			int maxCheckY = Math.min(TheBetweenlands.LAYER_HEIGHT + 16, 255);

			for(int y = 0; y < maxCheckY; y++) {
				for(int x = 0; x < 16; x++) {
					for(int z = 0; z < 16; z++) {
						BlockState state = chunk.getBlockState(BlockPos.containing(x, y, z));

						for(GemSingerItem.GemSingerTarget target : GemSingerItem.GemSingerTarget.values()) {
							if(target.test(state)) {
								IntSet indices = this.gemToPositions.get(target.getId());
								if(indices == null) {
									this.gemToPositions.put(target.getId(), indices = new IntArraySet());
								}
								indices.add(getGemSingerTargetIndex(x, y, z));
								break;
							}
						}
					}
				}
			}

			for(GemSingerItem.GemSingerTarget target : GemSingerItem.GemSingerTarget.values()) {
				this.savedGemTargets.add(target.getId());
			}

			this.markDirty();
		}
	}

	/**
	 * Encodes a position into an index that might be contained in {@link #findGems(thebetweenlands.common.items.GemSingerItem.GemSingerTarget)}
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @return index for the specified position
	 */
	public static int getGemSingerTargetIndex(int x, int y, int z) {
		return y << 8 | (z & 15) << 4 | (x & 15);
	}

	/**
	 * Decodes a position index retrieved from {@link #findGems(thebetweenlands.common.items.GemSingerItem.GemSingerTarget)}
	 * into a block position <b>relative to the chunk it was retrieved from</b>
	 * @param index index to decode
	 * @return block position <b>relative to the chunk it was retrieved from</b>
	 */
	public static BlockPos getGemSingerTargetPosition(int index) {
		return new BlockPos(index & 0b1111, (index >> 8) & 0b11111111, (index >> 4) & 0b1111);
	}

	/**
	 * Marks a gem at the specified position
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param target Gem target type
	 * @return true if successfully marked
	 */
	public boolean markGem(int x, int y, int z, GemSingerItem.GemSingerTarget target) {
		IntSet indices = this.gemToPositions.get(target.getId());
		if(indices == null) {
			this.gemToPositions.put(target.getId(), indices = new IntArraySet());
		}
		int index = getGemSingerTargetIndex(x & 15, y, z & 15);
		return indices.add(index);
	}

	/**
	 * Unmarks a gem at the specified position
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param target Gem target type
	 * @return true if successfully unmarked
	 */
	public boolean unmarkGem(int x, int y, int z, GemSingerItem.GemSingerTarget target) {
		IntSet indices = this.gemToPositions.get(target.getId());
		if(indices != null) {
			if(indices.remove(getGemSingerTargetIndex(x & 15, y, z & 15))) {
				if(indices.isEmpty()) {
					this.gemToPositions.remove(target.getId());
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to find all gems of the specified target type
	 * @param target Gem target type
	 * @return an unmodifiable set of all gem positions which can be decoded by {@link #getGemSingerTargetPosition(int)}
	 */
	public IntSet findGems(GemSingerItem.GemSingerTarget target) {
		IntSet indices = this.gemToPositions.get(target.getId());
		if(indices != null) {
			return IntSets.unmodifiable(indices);
		}
		return IntSets.EMPTY_SET;
	}

	/**
	 * Tries to find a random gem of the specified target type
	 * @param target Gem target type
	 * @param rand RNG to pick the random gem
	 * @param pos Center position for the range check <b>in world coordinates</b>
	 * @param range Maximum range from pos
	 * @return position of a gem <b>in world coordinates</b>, or null if none was found
	 */
	@Nullable
	public BlockPos findRandomGem(GemSingerItem.GemSingerTarget target, RandomSource rand, BlockPos pos, float range) {
		BlockPos relPos = pos.offset(-this.getChunk().getPos().x * 16, 0, -this.getChunk().getPos().z * 16);
		IntSet indices = this.gemToPositions.get(target.getId());
		if(indices != null) {
			List<BlockPos> found = new ArrayList<>();
			for(int index : indices) {
				BlockPos gem = getGemSingerTargetPosition(index);
				if(gem.distSqr(relPos) <= range * range) {
					found.add(gem);
				}
			}
			if(!found.isEmpty()) {
				return found.get(rand.nextInt(found.size())).offset(this.getChunk().getPos().x * 16, 0, this.getChunk().getPos().z * 16);
			}
		}
		return null;
	}

	/**
	 * @see #markGem(int, int, int, thebetweenlands.common.items.GemSingerItem.GemSingerTarget)
	 *
	 * @param level World
	 * @param pos World coordinate
	 * @param target Gem target type
	 * @return
	 */
	public static boolean markGem(Level level, BlockPos pos, GemSingerItem.GemSingerTarget target) {
		BetweenlandsChunkStorage storage = forChunk(level, level.getChunk(pos));
		if(storage != null) {
			return storage.markGem(pos.getX(), pos.getY(), pos.getZ(), target);
		}
		return false;
	}

	/**
	 * @see #unmarkGem(int, int, int, thebetweenlands.common.items.GemSingerItem.GemSingerTarget)
	 *
	 * @param level World
	 * @param pos World coordinate
	 * @param target Gem target type
	 * @return
	 */
	public static boolean unmarkGem(Level level, BlockPos pos, GemSingerItem.GemSingerTarget target) {
		BetweenlandsChunkStorage storage = forChunk(level, level.getChunk(pos));
		if(storage != null) {
			return storage.unmarkGem(pos.getX(), pos.getY(), pos.getZ(), target);
		}
		return false;
	}

	/**
	 * Tries to find a random gem of the specified target type
	 * @param level World
	 * @param target Gem target type
	 * @param rand RNG to pick the random gem
	 * @param pos Center position for the range check <b>in world coordinates</b>
	 * @param range Maximum range from pos
	 * @return position of a gem <b>in world coordinates</b>, or null if none was found
	 */
	@Nullable
	public static BlockPos findRandomGem(Level level, GemSingerItem.GemSingerTarget target, RandomSource rand, BlockPos pos, float range) {
		BetweenlandsChunkStorage storage = forChunk(level, level.getChunk(pos));
		if(storage != null) {
			return storage.findRandomGem(target, rand, pos, range);
		}
		return null;
	}
}
