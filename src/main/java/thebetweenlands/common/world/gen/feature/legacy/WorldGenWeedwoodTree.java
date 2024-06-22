package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.blocks.BetweenlandsLogBlock;
import thebetweenlands.common.blocks.BlockPoisonIvy;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenWeedwoodTree extends WorldGenHelper {
	private BlockState logX, logY, logZ, bark;
	private BlockState wood;
	private BlockState leaves;
	private BlockState ivy;

	@Override
	public boolean generate(Level world, Random rand, BlockPos pos) {
		int radius = rand.nextInt(2) + 3;
		int height = rand.nextInt(5) + 15;
		int maxRadius = 9;

		this.logX = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
		this.logY = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
		this.logZ = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
		this.bark = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		this.wood = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.getDefaultState();
		this.leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState();//.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
		this.ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

		if (!world.isAreaLoaded(pos, maxRadius))
			return false;

		boolean hasPoisonIvy = rand.nextInt(7) == 0;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++) {
					BlockPos tpos = pos.offset(xx, yy, zz);
					BlockState state = world.getBlockState(tpos);
					if (!world.isEmptyBlock(tpos) && state.isFaceSturdy(world, tpos.above(), Direction.DOWN)) {
						return false;
					}
				}

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 3)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1 + height - 2)
						world.setBlock(pos.offset(i, yy, j), wood, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						world.setBlock(pos.offset(i, yy, j), bark, 2);
				}

			if (yy == height / 2 + 2) {
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, false, rand.nextInt(2) + 4, hasPoisonIvy);

				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, false, rand.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == height / 2 + 4) {
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 4);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 4);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, 4);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, 4);

				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 3);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 3);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 3);
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 3);
			}

			if (yy == height / 2 + 7) {
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 2);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(3), radius + 1), 3, 2);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(3), -radius - 1), 4, 2);

				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 2);
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 2);
			}

			if (yy == 0) {
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, true, rand.nextInt(2) + 3, hasPoisonIvy);

				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, true, rand.nextInt(2) + 3, hasPoisonIvy);
			}
		}

		createMainCanopy(world, rand, pos.offset(0, height / 2 + 4, 0), maxRadius);

		return true;
	}

	@Override
	public boolean generate(WorldGenLevel world, Random rand, BlockPos pos) {
		int radius = rand.nextInt(2) + 3;
		int height = rand.nextInt(5) + 15;
		int maxRadius = 9;

		this.logX = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
		this.logY = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
		this.logZ = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
		this.bark = BlockRegistry.WEEDWOOD_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		this.wood = BlockRegistry.WEEDWOOD.get().defaultBlockState();
		this.leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
		this.ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

		if (!world.isAreaLoaded(pos, maxRadius))
			return false;

		boolean hasPoisonIvy = rand.nextInt(7) == 0;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!world.isEmptyBlock(pos.offset(xx, yy, zz)) && world.getBlockState(pos.offset(xx, yy, zz)).canOcclude()) {
						return false;
					}

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 3)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1 + height - 2)
						world.setBlock(pos.offset(i, yy, j), wood, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						world.setBlock(pos.offset(i, yy, j), bark, 2);
				}

			if (yy == height / 2 + 2) {
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, false, rand.nextInt(2) + 4, hasPoisonIvy);

				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, false, rand.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == height / 2 + 4) {
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 4);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 4);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, 4);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, 4);

				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 3);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 3);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 3);
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 3);
			}

			if (yy == height / 2 + 7) {
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 2);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(3), radius + 1), 3, 2);
				createSmallBranch(world, rand, pos.offset(0, yy - rand.nextInt(3), -radius - 1), 4, 2);

				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 2);
				createSmallBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 2);
				createSmallBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 2);
			}

			if (yy == 0) {
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, true, rand.nextInt(2) + 3, hasPoisonIvy);

				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, true, rand.nextInt(2) + 3, hasPoisonIvy);
			}
		}

		createMainCanopy(world, rand, pos.offset(0, height / 2 + 4, 0), maxRadius);

		return true;
	}

	private void createSmallBranch(Level world, Random rand, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:
					world.setBlock(pos.east(i).above(y), branchBend ? logY : logX, 2);
					break;

				case 2:
					world.setBlock(pos.west(i).above(y), branchBend ? logY : logX, 2);
					break;

				case 3:
					world.setBlock(pos.south(i).above(y), branchBend ? logY : logZ, 2);
					break;

				case 4:
					world.setBlock(pos.north(i).above(y), branchBend ? logY : logZ, 2);
					break;

				case 5:
					world.setBlock(pos.east(i).above(y).south(i), branchBend ? logY : logX, 2);
					break;

				case 6:
					world.setBlock(pos.west(i).above(y).north(i), branchBend ? logY : logX, 2);
					break;

				case 7:
					world.setBlock(pos.west(i).above(y).south(i), branchBend ? logY : logZ, 2);
					break;

				case 8:
					world.setBlock(pos.east(i).above(y).north(i), branchBend ? logY : logZ, 2);
					break;
			}
		}
	}

	private void createSmallBranch(WorldGenLevel world, Random rand, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:
					world.setBlock(pos.east(i).above(y), branchBend ? logY : logX, 2);
					break;

				case 2:
					world.setBlock(pos.west(i).above(y), branchBend ? logY : logX, 2);
					break;

				case 3:
					world.setBlock(pos.south(i).above(y), branchBend ? logY : logZ, 2);
					break;

				case 4:
					world.setBlock(pos.north(i).above(y), branchBend ? logY : logZ, 2);
					break;

				case 5:
					world.setBlock(pos.east(i).above(y).south(i), branchBend ? logY : logX, 2);
					break;

				case 6:
					world.setBlock(pos.west(i).above(y).north(i), branchBend ? logY : logX, 2);
					break;

				case 7:
					world.setBlock(pos.west(i).above(y).south(i), branchBend ? logY : logZ, 2);
					break;

				case 8:
					world.setBlock(pos.east(i).above(y).north(i), branchBend ? logY : logZ, 2);
					break;
			}
		}
	}

	private void createMainCanopy(Level world, Random rand, BlockPos pos, int maxRadius) {
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && y1 > 0)
						if (!isALog(world.getBlockState(pos.offset(x1, y1, z1))))
							world.setBlock(pos.offset(x1, y1, z1), logY, 1);
				}

		//Generate leaves after generating logs so that it doesn't trigger breakBlock when replacing blocks, causing BlockLeaves' CHECK_DECAY to become true
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (!isALog(world.getBlockState(pos.offset(x1, y1, z1))) && rand.nextInt(5) != 0)
							world.setBlock(pos.offset(x1, y1, z1), leaves, 2);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == 0)
						if (world.getBlockState(pos.offset(x1, y1, z1)) == leaves)
							for (int i = 1; i < 1 + rand.nextInt(3); i++)
								if (!isALog(world.getBlockState(pos.offset(x1, y1 - i, z1))))
									world.setBlock(pos.offset(x1, y1 - i, z1), leaves, 2);
				}
	}

	private void createMainCanopy(WorldGenLevel world, Random rand, BlockPos pos, int maxRadius) {
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && y1 > 0)
						if (!isALog(world.getBlockState(pos.offset(x1, y1, z1))))
							world.setBlock(pos.offset(x1, y1, z1), logY, 1);
				}

		//Generate leaves after generating logs so that it doesn't trigger breakBlock when replacing blocks, causing BlockLeaves' CHECK_DECAY to become true
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (!isALog(world.getBlockState(pos.offset(x1, y1, z1))) && rand.nextInt(5) != 0)
							world.setBlock(pos.offset(x1, y1, z1), leaves, 2);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == 0)
						if (world.getBlockState(pos.offset(x1, y1, z1)) == leaves)
							for (int i = 1; i < 1 + rand.nextInt(3); i++)
								if (!isALog(world.getBlockState(pos.offset(x1, y1 - i, z1))))
									world.setBlock(pos.offset(x1, y1 - i, z1), leaves, 2);
				}
	}

	private boolean isALog(BlockState state) {
		return state.getBlock() instanceof BetweenlandsLogBlock;
	}

	private void createBranch(Level world, Random rand, BlockPos pos, int dir, boolean root, int branchLength, boolean ivy) {
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
						world.setBlock(pos.east(i).above(y), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i).above(y - 1), Direction.EAST);
					} else {
						world.setBlock(pos.east(i).below(y), bark, 2);
						world.setBlock(pos.east(i).below(y - 1), bark, 2);
					}
					break;

				case 2:
					if (!root) {
						world.setBlock(pos.west(i).above(y), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i).above(y - 1), Direction.WEST);
					} else {
						world.setBlock(pos.west(i).below(y), bark, 2);
						world.setBlock(pos.west(i).below(y - 1), bark, 2);
					}
					break;

				case 3:
					if (!root) {
						world.setBlock(pos.south(i).above(y), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.south(i).above(y - 1), Direction.SOUTH);
					} else {
						world.setBlock(pos.south(i).below(y), bark, 2);
						world.setBlock(pos.south(i).below(y - 1), bark, 2);
					}
					break;

				case 4:
					if (!root) {
						world.setBlock(pos.north(i).above(y), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.north(i).above(y - 1), Direction.NORTH);
					} else {
						world.setBlock(pos.north(i).below(y), bark, 2);
						world.setBlock(pos.north(i).below(y - 1), bark, 2);
					}
					break;

				case 5:
					if (!root) {
						world.setBlock(pos.east(i - 1).above(y).south(i - 1), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i - 1).above(y - 1).south(i - 1), Direction.EAST);
					} else {
						world.setBlock(pos.east(i - 1).below(y).south(i - 1), bark, 2);
						world.setBlock(pos.east(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 6:
					if (!root) {
						world.setBlock(pos.west(i - 1).above(y).north(i - 1), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i - 1).above(y - 1).north(i - 1), Direction.WEST);
					} else {
						world.setBlock(pos.west(i - 1).below(y).north(i - 1), bark, 2);
						world.setBlock(pos.west(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;

				case 7:
					if (!root) {
						world.setBlock(pos.west(i - 1).above(y).south(i - 1), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i - 1).above(y - 1).south(i - 1), Direction.SOUTH);
					} else {
						world.setBlock(pos.west(i - 1).below(y).south(i - 1), bark, 2);
						world.setBlock(pos.west(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 8:
					if (!root) {
						world.setBlock(pos.east(i - 1).above(y).north(i - 1), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i - 1).above(y - 1).north(i - 1), Direction.NORTH);
					} else {
						world.setBlock(pos.east(i - 1).below(y).north(i - 1), bark, 2);
						world.setBlock(pos.east(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;
			}
		}
	}

	private void createBranch(WorldGenLevel world, Random rand, BlockPos pos, int dir, boolean root, int branchLength, boolean ivy) {
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
						world.setBlock(pos.east(i).above(y), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i).above(y - 1), Direction.EAST);
					} else {
						world.setBlock(pos.east(i).below(y), bark, 2);
						world.setBlock(pos.east(i).below(y - 1), bark, 2);
					}
					break;

				case 2:
					if (!root) {
						world.setBlock(pos.west(i).above(y), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i).above(y - 1), Direction.WEST);
					} else {
						world.setBlock(pos.west(i).below(y), bark, 2);
						world.setBlock(pos.west(i).below(y - 1), bark, 2);
					}
					break;

				case 3:
					if (!root) {
						world.setBlock(pos.south(i).above(y), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.south(i).above(y - 1), Direction.SOUTH);
					} else {
						world.setBlock(pos.south(i).below(y), bark, 2);
						world.setBlock(pos.south(i).below(y - 1), bark, 2);
					}
					break;

				case 4:
					if (!root) {
						world.setBlock(pos.north(i).above(y), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.north(i).above(y - 1), Direction.NORTH);
					} else {
						world.setBlock(pos.north(i).below(y), bark, 2);
						world.setBlock(pos.north(i).below(y - 1), bark, 2);
					}
					break;

				case 5:
					if (!root) {
						world.setBlock(pos.east(i - 1).above(y).south(i - 1), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i - 1).above(y - 1).south(i - 1), Direction.EAST);
					} else {
						world.setBlock(pos.east(i - 1).below(y).south(i - 1), bark, 2);
						world.setBlock(pos.east(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 6:
					if (!root) {
						world.setBlock(pos.west(i - 1).above(y).north(i - 1), branchBend ? logY : logX, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i - 1).above(y - 1).north(i - 1), Direction.WEST);
					} else {
						world.setBlock(pos.west(i - 1).below(y).north(i - 1), bark, 2);
						world.setBlock(pos.west(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;

				case 7:
					if (!root) {
						world.setBlock(pos.west(i - 1).above(y).south(i - 1), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.west(i - 1).above(y - 1).south(i - 1), Direction.SOUTH);
					} else {
						world.setBlock(pos.west(i - 1).below(y).south(i - 1), bark, 2);
						world.setBlock(pos.west(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 8:
					if (!root) {
						world.setBlock(pos.east(i - 1).above(y).north(i - 1), branchBend ? logY : logZ, 2);
						if (i <= branchLength && ivy)
							addVines(world, rand, pos.east(i - 1).above(y - 1).north(i - 1), Direction.NORTH);
					} else {
						world.setBlock(pos.east(i - 1).below(y).north(i - 1), bark, 2);
						world.setBlock(pos.east(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;
			}
		}
	}

	private void addVines(Level world, Random rand, BlockPos pos, Direction facing) {
		if (rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = 0; yy < length; ++yy)
				if (world.isEmptyBlock(pos.below(yy)))
					world.setBlock(pos.below(yy), ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(facing.getOpposite()), Boolean.TRUE), 2);
				else
					break;
		}
	}

	private void addVines(WorldGenLevel world, Random rand, BlockPos pos, Direction facing) {
		if (rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = 0; yy < length; ++yy)
				if (world.isEmptyBlock(pos.below(yy)))
					world.setBlock(pos.below(yy), ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(facing.getOpposite()), Boolean.TRUE), 2);
				else
					break;
		}
	}
}
