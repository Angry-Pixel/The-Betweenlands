package thebetweenlands.common.generation.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thebetweenlands.common.registries.BlockRegistry;

public class WeedwoodTree {

	private final BlockState log = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();
	private final BlockState bark = BlockRegistry.WEEDWOOD_BARK_LOG.get().defaultBlockState();
	private final BlockState wood = BlockRegistry.WEEDWOOD.get().defaultBlockState();
	private final BlockState leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState();

	public boolean generate(LevelAccessor accessor, BlockPos pos, RandomSource random) {

		int radius = random.nextInt(2) + 3;
		int height = random.nextInt(5) + 15;
		int maxRadius = 9;

		if (!accessor.isAreaLoaded(pos, maxRadius))
			return false;

		boolean hasPoisonIvy = random.nextInt(7) == 0;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!accessor.isEmptyBlock(pos.offset(xx, yy, zz)) && !accessor.getBlockState(pos.offset(xx, yy, zz)).canBeReplaced()) {
						return false;
					}

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 3)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1 + height - 2)
						accessor.setBlock(pos.offset(i, yy, j), wood, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						accessor.setBlock(pos.offset(i, yy, j), bark, 2);
				}

			if (yy == height / 2 + 2) {
				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, false, random.nextInt(2) + 4, hasPoisonIvy);

				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, false, random.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == height / 2 + 4) {
				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, 4);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, 4);
				this.createSmallBranch(accessor, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, 4);
				this.createSmallBranch(accessor, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, 4);

				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, 3);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, 3);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, 3);
				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, 3);
			}

			if (yy == height / 2 + 7) {
				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, 2);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, 2);
				this.createSmallBranch(accessor, pos.offset(0, yy - random.nextInt(3), radius + 1), 3, 2);
				this.createSmallBranch(accessor, pos.offset(0, yy - random.nextInt(3), -radius - 1), 4, 2);

				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, 2);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, 2);
				this.createSmallBranch(accessor, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, 2);
				this.createSmallBranch(accessor, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, 2);
			}

			if (yy == 0) {
				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, true, random.nextInt(2) + 3, hasPoisonIvy);

				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(accessor, random, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, true, random.nextInt(2) + 3, hasPoisonIvy);
			}
		}

		this.createMainCanopy(accessor, random, pos.offset(0, height / 2 + 4, 0), maxRadius);

		return true;
	}

	private void createSmallBranch(LevelAccessor accessor, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:
					accessor.setBlock(pos.east(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 2:
					accessor.setBlock(pos.west(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 3:
					accessor.setBlock(pos.south(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 4:
					accessor.setBlock(pos.north(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 5:
					accessor.setBlock(pos.east(i).above(y).south(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 6:
					accessor.setBlock(pos.west(i).above(y).north(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 7:
					accessor.setBlock(pos.west(i).above(y).south(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 8:
					accessor.setBlock(pos.east(i).above(y).north(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;
			}
		}
	}

	private void createMainCanopy(LevelAccessor accessor, RandomSource random, BlockPos pos, int maxRadius) {
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && y1 > 0)
						if (!isALog(accessor.getBlockState(pos.offset(x1, y1, z1))))
							accessor.setBlock(pos.offset(x1, y1, z1), this.log, 2);
				}

		//Generate leaves after generating logs so that it doesn't trigger breakBlock when replacing blocks, causing BlockLeaves' CHECK_DECAY to become true
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (!isALog(accessor.getBlockState(pos.offset(x1, y1, z1))) && random.nextInt(5) != 0)
							accessor.setBlock(pos.offset(x1, y1, z1), this.leaves, 2);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && random.nextInt(3) == 0 && y1 == 0)
						if (accessor.getBlockState(pos.offset(x1, y1, z1)) == this.leaves)
							for (int i = 1; i < 1 + random.nextInt(3); i++)
								if (!this.isALog(accessor.getBlockState(pos.offset(x1, y1 - i, z1))))
									accessor.setBlock(pos.offset(x1, y1 - i, z1), this.leaves, 2);
				}
	}

	private boolean isALog(BlockState state) {
		return state.is(BlockTags.LOGS);
	}

	private void createBranch(LevelAccessor accessor, RandomSource random, BlockPos pos, int dir, boolean root, int branchLength, boolean ivy) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:

					if (!root) {
						accessor.setBlock(pos.east(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.east(i).above(y - 1), Direction.EAST);
					} else {
						accessor.setBlock(pos.east(i).below(y), bark, 2);
						accessor.setBlock(pos.east(i).below(y - 1), bark, 2);
					}
					break;

				case 2:
					if (!root) {
						accessor.setBlock(pos.west(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.west(i).above(y - 1), Direction.WEST);
					} else {
						accessor.setBlock(pos.west(i).below(y), bark, 2);
						accessor.setBlock(pos.west(i).below(y - 1), bark, 2);
					}
					break;

				case 3:
					if (!root) {
						accessor.setBlock(pos.south(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.south(i).above(y - 1), Direction.SOUTH);
					} else {
						accessor.setBlock(pos.south(i).below(y), bark, 2);
						accessor.setBlock(pos.south(i).below(y - 1), bark, 2);
					}
					break;

				case 4:
					if (!root) {
						accessor.setBlock(pos.north(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.north(i).above(y - 1), Direction.NORTH);
					} else {
						accessor.setBlock(pos.north(i).below(y), bark, 2);
						accessor.setBlock(pos.north(i).below(y - 1), bark, 2);
					}
					break;

				case 5:
					if (!root) {
						accessor.setBlock(pos.east(i - 1).above(y).south(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.east(i - 1).above(y - 1).south(i - 1), Direction.EAST);
					} else {
						accessor.setBlock(pos.east(i - 1).below(y).south(i - 1), bark, 2);
						accessor.setBlock(pos.east(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 6:
					if (!root) {
						accessor.setBlock(pos.west(i - 1).above(y).north(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.west(i - 1).above(y - 1).north(i - 1), Direction.WEST);
					} else {
						accessor.setBlock(pos.west(i - 1).below(y).north(i - 1), bark, 2);
						accessor.setBlock(pos.west(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;

				case 7:
					if (!root) {
						accessor.setBlock(pos.west(i - 1).above(y).south(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.west(i - 1).above(y - 1).south(i - 1), Direction.SOUTH);
					} else {
						accessor.setBlock(pos.west(i - 1).below(y).south(i - 1), bark, 2);
						accessor.setBlock(pos.west(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 8:
					if (!root) {
						accessor.setBlock(pos.east(i - 1).above(y).north(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(accessor, random, pos.east(i - 1).above(y - 1).north(i - 1), Direction.NORTH);
					} else {
						accessor.setBlock(pos.east(i - 1).below(y).north(i - 1), bark, 2);
						accessor.setBlock(pos.east(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;
			}
		}
	}

	private void addVines(LevelAccessor accessor, RandomSource random, BlockPos pos, Direction facing) {
		if (random.nextInt(4) != 0) {
			int length = random.nextInt(4) + 4;
			for (int yy = 0; yy < length; ++yy)
				if (accessor.isEmptyBlock(pos.below(yy)))
					accessor.setBlock(pos.below(yy), BlockRegistry.POISON_IVY.get().defaultBlockState().setValue(BlockVineBL.getPropertyFor(facing.getOpposite()), true), 2);
				else
					break;
		}
	}
}
