package thebetweenlands.common.world.gen.progressivegen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public abstract class ProgressiveGenChunkMarker<T extends ProgressiveGenerator> extends SharedStorage implements ITickable {
	private int index;
	private BlockPos pos;
	private long seed;
	private boolean finished;
	private T generator;

	private final Map<Long, IBlockState> map = new HashMap<>();

	public ProgressiveGenChunkMarker(WorldDataBase<?> worldStorage, UUID uuid) {
		super(worldStorage, uuid);
	}

	public ProgressiveGenChunkMarker(WorldDataBase<?> worldStorage, UUID uuid, BlockPos pos, long seed) {
		super(worldStorage, uuid);
		this.pos = pos;
		this.seed = seed;
	}

	/**
	 * Links the chunk
	 * @return
	 */
	public boolean linkChunk() {
		return this.linkChunk(this.getWorldStorage().getWorld().getChunkFromBlockCoords(this.pos));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setLong("pos", this.pos.toLong());
		nbt.setLong("seed", this.seed);
		nbt.setBoolean("finished", this.finished);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.pos = BlockPos.fromLong(nbt.getLong("pos"));
		this.seed = nbt.getLong("seed");
		this.finished = nbt.getBoolean("finished");
	}

	@Override
	public NBTTagCompound writeToPacketNBT(NBTTagCompound nbt) { return nbt; }

	@Override
	public void readFromPacketNBT(NBTTagCompound nbt) { }

	@Override
	public void onLoaded() {
		if(!this.getWorldStorage().getWorld().isRemote && this.pos != null && !this.finished) {
			(this.generator = this.getGenerator()).generateBulk(new ProgressiveGenProxyWorld(this.getWorldStorage().getWorld()) {
				@Override
				protected void onSetBlockState(BlockPos pos, IBlockState state, int flags) {
					ProgressiveGenChunkMarker.this.map.put(pos.toLong(), state);
				}

				@Override
				protected IBlockState onGetBlockState(BlockPos pos) {
					return ProgressiveGenChunkMarker.this.map.get(pos.toLong());
				}
			}, new Random(this.seed), this.pos);
		}
	}

	@Override
	public void update() {
		World world = this.getWorldStorage().getWorld();
		if(!world.isRemote && !this.finished && world.getWorldTime() % 4 == 0) {
			if(!this.map.isEmpty()) {
				Set<Entry<Long, IBlockState>> blocks = this.map.entrySet();
				Iterator<Entry<Long, IBlockState>> it = blocks.iterator();
				int i = 0;
				while(it.hasNext() && i++ < 1024) {
					Entry<Long, IBlockState> block = it.next();
					BlockPos pos = BlockPos.fromLong(block.getKey());
					IBlockState state = block.getValue();
					world.setBlockState(pos, state, 2);
					it.remove();
					this.index++;
				}
				if(!it.hasNext()) {
					this.map.clear();
					if(this.generator != null) {
						this.generator.post(world, new Random(this.seed), this.pos);
					}
					this.finished = true;

					//Remove marker from chunk and delete file
					this.getWorldStorage().removeSharedStorage(this);
				}
			}
		}
	}

	/**
	 * Returns whether the structure has finished generating
	 * @return
	 */
	public boolean isFinished() {
		return this.finished;
	}

	/**
	 * Returns the position
	 * @return
	 */
	public BlockPos getPos() {
		return this.pos;
	}

	/**
	 * Returns the seed
	 * @return
	 */
	public long getSeed() {
		return this.seed;
	}

	/**
	 * Creates a new instance of the generator
	 * @return
	 */
	protected abstract T getGenerator();
}
