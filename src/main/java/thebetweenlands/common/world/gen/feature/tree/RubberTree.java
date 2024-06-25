package thebetweenlands.common.world.gen.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class RubberTree {

	public boolean generate(LevelAccessor level, BlockPos position, RandomSource random) {
		MutableBlockPos checkPos = new MutableBlockPos();

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		int height = random.nextInt(8) + 8;
		int maxRadius = 4;

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!level.isEmptyBlock(checkPos.set(xx, yy, zz)) && !level.getBlockState(checkPos.set(xx, yy, zz)).canBeReplaced())
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			level.setBlock(new BlockPos(x, yy, z), BlockRegistry.RUBBER_LOG.get().defaultBlockState(), 2);

			if (yy == y + height - 1)
				this.createMainCanopy(level, random, x, yy, z, maxRadius, checkPos);

			if (yy == y + height - 2) {
				this.createBranch(level, BlockRegistry.RUBBER_LOG.get().defaultBlockState(), x + 1, yy, z, 1, 1);
				this.createBranch(level, BlockRegistry.RUBBER_LOG.get().defaultBlockState(), x - 1, yy, z, 2, 1);
				this.createBranch(level, BlockRegistry.RUBBER_LOG.get().defaultBlockState(), x, yy, z + 1, 3, 1);
				this.createBranch(level, BlockRegistry.RUBBER_LOG.get().defaultBlockState(), x, yy, z - 1, 4, 1);
			}
		}
		return true;
	}

	private void createBranch(LevelAccessor accessor, BlockState log, int x, int y, int z, int dir, int branchLength) {
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
			}

			if (dir == 1) {
				accessor.setBlock(new BlockPos(x + i, y, z), log, 2);
				accessor.setBlock(new BlockPos(x + i + 1, y, z), log, 2);
			}

			if (dir == 2) {
				accessor.setBlock(new BlockPos(x - i, y, z), log, 2);
				accessor.setBlock(new BlockPos(x - i - 1, y, z), log, 2);
			}

			if (dir == 3) {
				accessor.setBlock(new BlockPos(x, y, z + i), log, 2);
				accessor.setBlock(new BlockPos(x, y, z + i + 1), log, 2);
			}

			if (dir == 4) {
				accessor.setBlock(new BlockPos(x, y, z - i), log, 2);
				accessor.setBlock(new BlockPos(x, y, z - i - 1), log, 2);
			}

		}
	}

	private void createMainCanopy(LevelAccessor accessor, RandomSource random, int x, int y, int z, int maxRadius, MutableBlockPos checkPos) {
		for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
			for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
				for (int y1 = y; y1 < y + maxRadius; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (!accessor.getBlockState(checkPos.set(x1, y1, z1)).is(BlockRegistry.RUBBER_LOG.get()) && random.nextInt(5) != 0)
							accessor.setBlock(new BlockPos(x1, y1, z1), BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState(), 2);
				}
	}
}
