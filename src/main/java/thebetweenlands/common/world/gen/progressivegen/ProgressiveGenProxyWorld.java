package thebetweenlands.common.world.gen.progressivegen;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public abstract class ProgressiveGenProxyWorld extends World {
	protected final World parent;

	public ProgressiveGenProxyWorld(World world) {
		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		this.parent = world;
		this.chunkProvider = world.getChunkProvider();
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return this.parent.getChunkProvider();
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return this.isRemote ? (allowEmpty || !this.getChunkProvider().provideChunk(x, z).isEmpty()) : ((ChunkProviderServer)this.getChunkProvider()).chunkExists(x, z);
	}

	@Override
	public void markAndNotifyBlock(BlockPos pos, Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags) { }

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
		this.onSetBlockState(pos, newState, flags);
		return true;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		IBlockState state = this.onGetBlockState(pos);
		return state != null ? state : super.getBlockState(pos);
	}

	/**
	 * Called when a blockstate is set
	 * @param pos
	 * @param state
	 * @param flags
	 */
	protected abstract void onSetBlockState(BlockPos pos, IBlockState state, int flags);

	/**
	 * Called when a blockstate is retrieved, return null to redirect to the normal world
	 * @param pos
	 * @return
	 */
	@Nullable
	protected abstract IBlockState onGetBlockState(BlockPos pos);
}
