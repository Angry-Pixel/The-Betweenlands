package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLLeaves;
import thebetweenlands.blocks.BlockBLLog;

public class WorldGenRubberTree extends WorldGenerator {

	private BlockBLLog log = (BlockBLLog) BLBlockRegistry.rubberTreeLog;
	private BlockBLLeaves leaves = (BlockBLLeaves) BLBlockRegistry.rubberTreeLeaves;
	private boolean alternate;

	public boolean generate(World world, Random rand, int x, int y, int z) {

		int height = rand.nextInt(8) + 16;
		int maxRadius = 5;
		alternate = rand.nextBoolean();

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			world.setBlock(x, yy, z, log, 0, 2);

			if (yy == y + height - 1)
				createLeaves(world, x, yy, z);

			if (yy == y + height - 8 || yy == y + height - 12) {
				if (alternate) {
					createBranch(world, rand, x + 1, yy - rand.nextInt(2), z, 1, 1);
					createBranch(world, rand, x- 1, yy - rand.nextInt(2), z, 2, 1);
					alternate = false;

				} else {
					createBranch(world, rand, x, yy - rand.nextInt(2), z + 1, 3, 1);
					createBranch(world, rand, x, yy - rand.nextInt(2), z - 1, 4, 1);
					alternate = true;
				}
			}

			if (yy == y + height - 4) {
				if (alternate) {
					createBranch(world, rand, x + 1, yy- rand.nextInt(2), z, 1, 1);
					createBranch(world, rand, x - 1, yy- rand.nextInt(2), z, 2, 1);
					alternate = false;

				} else {
					createBranch(world, rand, x, yy- rand.nextInt(2), z + 1, 3, 1);
					createBranch(world, rand, x, yy- rand.nextInt(2), z - 1, 4, 1);
					alternate = true;
				}
			}
		}
		return true;
	}

	private void createBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
				meta = 0;
			}

			if (dir == 1) {
				world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x + i, y, z);
					placeLeaves(world, x + i + 2, y, z);
				}
			}

			if (dir == 2) {
				world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x - i, y, z);
					placeLeaves(world, x - i - 2, y, z);
				}
			}

			if (dir == 3) {
				world.setBlock(x, y, z + i, log,  meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x, y, z + i);
					placeLeaves(world, x, y, z + i + 2);
				}
			}

			if (dir == 4) {
				world.setBlock(x, y, z - i, log,  meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x, y, z - i);
					placeLeaves(world, x, y, z - i - 2);
				}
			}
		}
	}

	private void createLeaves(World world, int x, int y, int z) {
		for(int d = - 1; d < 2; d++)
			for(int d2 = - 1; d2 < 2; d2++)
				placeLeaves(world, x + d, y, z + d2);
	}

	private void placeLeaves(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) == Blocks.air)
			world.setBlock(x, y, z, leaves);
	}

}
