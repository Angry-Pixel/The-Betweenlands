package thebetweenlands.common.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public interface IBlockStateAccessOnly {
	public boolean isAirBlock(BlockPos pos);

	public IBlockState getBlockState(BlockPos pos);

	public void setBlockState(BlockPos pos, IBlockState state);

	public void setBlockState(BlockPos pos, IBlockState state, int  flags);

	public static IBlockStateAccessOnly from(final World world) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				return world.isAirBlock(pos);
			}

			@Override
			public IBlockState getBlockState(BlockPos pos) {
				return world.getBlockState(pos);
			}

			@Override
			public void setBlockState(BlockPos pos, IBlockState state) {
				world.setBlockState(pos, state);
			}

			@Override
			public void setBlockState(BlockPos pos, IBlockState state, int flags) {
				world.setBlockState(pos, state, flags);
			}
		};
	}

	public static IBlockStateAccessOnly from(final int chunkX, final int chunkZ, final ChunkPrimer primer) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				return primer.getBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16) == Blocks.AIR.getDefaultState();
			}

			@Override
			public IBlockState getBlockState(BlockPos pos) {
				return primer.getBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16);
			}

			@Override
			public void setBlockState(BlockPos pos, IBlockState state) {
				primer.setBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16, state);
			}

			@Override
			public void setBlockState(BlockPos pos, IBlockState state, int flags) {
				primer.setBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16, state);
			}
		};
	}
}
