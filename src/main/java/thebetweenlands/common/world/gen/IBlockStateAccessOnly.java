package thebetweenlands.common.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface IBlockStateAccessOnly {
	public boolean isAirBlock(BlockPos pos);

	public BlockState getBlockState(BlockPos pos);

	public void setBlockState(BlockPos pos, BlockState state);

	public void setBlockState(BlockPos pos, BlockState state, int flags);

	public static IBlockStateAccessOnly from(final Level world) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				return world.getBlockState(pos).isAir();
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return world.getBlockState(pos);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state) {
				world.setBlock(pos, state, 0);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state, int flags) {
				world.setBlock(pos, state, flags);
			}
		};
	}

	public static IBlockStateAccessOnly from(final WorldGenLevel world) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				return world.getBlockState(pos).isAir();
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return world.getBlockState(pos);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state) {
				world.setBlock(pos, state, 0);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state, int flags) {
				world.setBlock(pos, state, flags);
			}
		};
	}

	public static IBlockStateAccessOnly from(final int chunkX, final int chunkZ, final ChunkAccess primer) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isAirBlock(BlockPos pos) {
				return primer.getBlockState(pos.offset(-chunkX * 16, 0, -chunkZ * 16)).isAir();
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return primer.getBlockState(pos.offset(-chunkX * 16, 0, -chunkZ * 16));
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state) {
				primer.setBlockState(pos.offset(-chunkX * 16, 0, -chunkZ * 16), state, false);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state, int flags) {
				primer.setBlockState(pos.offset(-chunkX * 16, 0, -chunkZ * 16), state, false);
			}
		};
	}
}
