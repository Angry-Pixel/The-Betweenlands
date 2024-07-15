package thebetweenlands.common.world.gen.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thebetweenlands.common.registries.BlockRegistry;

public class SapTree {

	private final BlockState logX = BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.X);
	private final BlockState logY = BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
	private final BlockState logZ = BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z);
	private final BlockState roots = BlockRegistry.SAP_BARK.get().defaultBlockState();
	private final BlockState leaves = BlockRegistry.SAP_LEAVES.get().defaultBlockState();

	public boolean generate(LevelAccessor accessor, BlockPos pos, RandomSource random) {

		int height = random.nextInt(3) + 16;
		int maxRadius = 6;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!accessor.isEmptyBlock(pos.offset(xx, yy, zz)) && !accessor.getBlockState(pos.offset(xx, yy, zz)).canBeReplaced())
						return false;

		for (int yy = 0; yy < height; ++yy) {
			accessor.setBlock(pos.offset(0, yy, 0), logY, 2);

			if (yy == 0)
				createRoots(accessor, pos);

			if (yy == height - 1)
				createLeaves(accessor, pos.offset(0, yy, 0), true);

			if (yy == height - 8 || yy == height - 12) {
				if (random.nextBoolean()) {
					createBranch(accessor, random, pos.offset(1, yy - random.nextInt(2), 0), 1, 2);
					createBranch(accessor, random, pos.offset(-1, yy - random.nextInt(2), 0), 2, 2);
					createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), 1), 3, 2);
					createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), -1), 4, 2);
				} else {
					createBranch(accessor, random, pos.offset(2, yy - random.nextInt(2), 2), 5, 2);
					createBranch(accessor, random, pos.offset(-2, yy - random.nextInt(2), -2), 6, 2);
					createBranch(accessor, random, pos.offset(-2, yy - random.nextInt(2), 2), 7, 2);
					createBranch(accessor, random, pos.offset(2, yy - random.nextInt(2), -2), 8, 2);
				}
			}

			if (yy == height - 4) {
				if (random.nextBoolean()) {
					createBranch(accessor, random, pos.offset(1, yy - random.nextInt(2), 0), 1, 1);
					createBranch(accessor, random, pos.offset(-1, yy - random.nextInt(2), 0), 2, 1);
					createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), 1), 3, 1);
					createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), -1), 4, 1);
				} else {
					createBranch(accessor, random, pos.offset(2, yy - random.nextInt(2), 2), 5, 1);
					createBranch(accessor, random, pos.offset(-2, yy - random.nextInt(2), -2), 6, 1);
					createBranch(accessor, random, pos.offset(-2, yy - random.nextInt(2), 2), 7, 1);
					createBranch(accessor, random, pos.offset(2, yy - random.nextInt(2), -2), 8, 1);
				}
			}
		}
		return true;
	}

	private void createBranch(LevelAccessor accessor, RandomSource random, BlockPos pos, int dir, int branchLength) {
		boolean branchBend = false;
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
				branchBend = true;
			}

			if (dir == 1) {
				accessor.setBlock(pos.offset(i, y, 0), branchBend ? logY : logX, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(i, y, 0), false);
				}
			}

			if (dir == 2) {
				accessor.setBlock(pos.offset(-i, y, 0), branchBend ? logY : logX, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(-i, y, 0), false);
				}
			}

			if (dir == 3) {
				accessor.setBlock(pos.offset(0, y, i), branchBend ? logY : logZ, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(0, y, i), false);
				}
			}

			if (dir == 4) {
				accessor.setBlock(pos.offset(0, y, -i), branchBend ? logY : logZ, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(0, y, -i), false);
				}
			}

			if (dir == 5) {
				accessor.setBlock(pos.offset(i - 1, y, i - 1), branchBend ? logY : logX, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(i - 1, y, i - 1), false);
				}
			}

			if (dir == 6) {
				accessor.setBlock(pos.offset(-i + 1, y, -i + 1), branchBend ? logY : logX, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(-i + 1, y, -i + 1), false);
				}
			}

			if (dir == 7) {
				accessor.setBlock(pos.offset(-i + 1, y, i - 1), branchBend ? logY : logZ, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(-i + 1, y, i - 1), false);
				}
			}

			if (dir == 8) {
				accessor.setBlock(pos.offset(i - 1, y, -i + 1), branchBend ? logY : logZ, 2);
				if (i == branchLength) {
					createLeaves(accessor, pos.offset(i - 1, y, -i + 1), false);
				}
			}
		}
	}

	private void createLeaves(LevelAccessor accessor, BlockPos pos, boolean top) {
		accessor.setBlock(pos.offset(0, 0, 1), leaves, 2);
		accessor.setBlock(pos.offset(0, 0, -1), leaves, 2);
		accessor.setBlock(pos.offset(1, 0, 0), leaves, 2);
		accessor.setBlock(pos.offset(-1, 0, 0), leaves, 2);

		if (top)
			accessor.setBlock(pos.offset(0, 1, 0), leaves, 2);
		else
			accessor.setBlock(pos.offset(0, -1, 0), leaves, 2);
	}

	private void createRoots(LevelAccessor accessor, BlockPos pos) {
		accessor.setBlock(pos.offset(0, 0, 1), roots, 2);
		accessor.setBlock(pos.offset(0, 0, -1), roots, 2);
		accessor.setBlock(pos.offset(1, 0, 0), roots, 2);
		accessor.setBlock(pos.offset(-1, 0, 0), roots, 2);
	}
}
