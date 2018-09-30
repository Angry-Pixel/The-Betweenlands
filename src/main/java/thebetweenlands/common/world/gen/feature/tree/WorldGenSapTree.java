package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockLeavesBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenSapTree extends WorldGenerator {
	private IBlockState logX, logY, logZ, roots;
	private IBlockState  leaves;

    @Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		int height = rand.nextInt(3) + 16;
		int maxRadius = 6;

		this.logX = BlockRegistry.LOG_SAP.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
		this.logY = BlockRegistry.LOG_SAP.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
		this.logZ = BlockRegistry.LOG_SAP.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
		this.roots = BlockRegistry.LOG_SAP.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		this.leaves = BlockRegistry.LEAVES_SAP_TREE.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);

		for (int xx = - maxRadius; xx <= maxRadius; xx++)
			for (int zz = - maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!world.isAirBlock(pos.add(xx, yy, zz)) && !world.getBlockState(pos.add(xx, yy, zz)).getBlock().isReplaceable(world, pos.add(xx, yy, zz)))
						return false;

		for (int yy = 0; yy < height; ++yy) {
			world.setBlockState(pos.add(0, yy, 0), logY, 2);

			if (yy == 0)
				createRoots(world, pos);

			if (yy == height - 1)
				createLeaves(world, pos.add(0, yy, 0), true);

			if (yy == height - 8 || yy == height - 12) {
				if (rand.nextBoolean()) {
					createBranch(world, rand, pos.add(1, yy - rand.nextInt(2), 0), 1, 2);
					createBranch(world, rand, pos.add(- 1, yy - rand.nextInt(2), 0), 2, 2);
					createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), 1), 3, 2);
					createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), - 1), 4, 2);
				} else {
					createBranch(world, rand, pos.add(2, yy - rand.nextInt(2), 2), 5, 2);
					createBranch(world, rand, pos.add(- 2, yy - rand.nextInt(2), - 2), 6, 2);
					createBranch(world, rand, pos.add(- 2, yy - rand.nextInt(2), 2), 7, 2);
					createBranch(world, rand, pos.add(2, yy - rand.nextInt(2), - 2), 8, 2);
				}
			}

			if (yy == height - 4) {
				if (rand.nextBoolean()) {
					createBranch(world, rand, pos.add(1, yy - rand.nextInt(2), 0), 1, 1);
					createBranch(world, rand, pos.add(- 1, yy - rand.nextInt(2), 0), 2, 1);
					createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), 1), 3, 1);
					createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), - 1), 4, 1);
				} else {
					createBranch(world, rand, pos.add(2, yy - rand.nextInt(2), 2), 5, 1);
					createBranch(world, rand, pos.add(- 2, yy - rand.nextInt(2), - 2), 6, 1);
					createBranch(world, rand, pos.add(- 2, yy - rand.nextInt(2), 2), 7, 1);
					createBranch(world, rand, pos.add(2, yy - rand.nextInt(2), - 2), 8, 1);
				}
			}
		}
		return true;
	}

	private void createBranch(World world, Random rand, BlockPos pos, int dir, int branchLength) {
		boolean branchBend = false;
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
				branchBend = true;
			}

			if (dir == 1) {
				world.setBlockState(pos.add(i, y, 0), branchBend ? logY : logX, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(i, y, 0), false);
				}
			}

			if (dir == 2) {
				world.setBlockState(pos.add(- i, y, 0), branchBend ? logY : logX, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(- i, y, 0), false);
				}
			}

			if (dir == 3) {
				world.setBlockState(pos.add(0, y, i), branchBend ? logY : logZ, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(0, y, i), false);
				}
			}

			if (dir == 4) {
				world.setBlockState(pos.add(0, y, - i), branchBend ? logY : logZ, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(0, y, - i), false);
				}
			}

			if (dir == 5) {
				world.setBlockState(pos.add(i - 1, y, i - 1),branchBend ? logY : logX, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(i - 1, y, i - 1), false);
				}
			}

			if (dir == 6) {
				world.setBlockState(pos.add(- i + 1, y, - i + 1), branchBend ? logY : logX, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(- i + 1, y, - i + 1), false);
				}
			}

			if (dir == 7) {
				world.setBlockState(pos.add(- i + 1, y, i - 1), branchBend ? logY : logZ, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(- i + 1, y, i - 1), false);
				}
			}

			if (dir == 8) {
				world.setBlockState(pos.add(i - 1, y, - i + 1), branchBend ? logY : logZ, 2);
				if(i == branchLength) {
					createLeaves(world, pos.add(i - 1, y, - i + 1), false);
				}
			}
		}
	}

	private void createLeaves(World world, BlockPos pos, boolean top) {
		world.setBlockState(pos.add(0, 0, 1), leaves, 2);
		world.setBlockState(pos.add(0, 0, - 1), leaves, 2);
		world.setBlockState(pos.add(1, 0, 0), leaves, 2);
		world.setBlockState(pos.add(- 1, 0, 0), leaves, 2);

		if(top)
			world.setBlockState(pos.add(0, 1, 0), leaves, 2);
		else
			world.setBlockState(pos.add(0, - 1, 0), leaves, 2);
	}

	private void createRoots(World world, BlockPos pos) {
		world.setBlockState(pos.add(0, 0, 1), roots, 2);
		world.setBlockState(pos.add(0, 0, - 1), roots, 2);
		world.setBlockState(pos.add(1, 0, 0), roots, 2);
		world.setBlockState(pos.add(- 1, 0, 0), roots, 2);
	}

}
