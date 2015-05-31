package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;

public class WorldGenSapTree extends WorldGenerator {
	private BlockBLLog log = (BlockBLLog) BLBlockRegistry.sapTreeLog;
	private BlockBLLeaves leaves = (BlockBLLeaves) BLBlockRegistry.sapTreeLeaves;

	public boolean generate(World world, Random rand, int x, int y, int z) {
		int height = rand.nextInt(3) + 16;
		int maxRadius = 6;

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		
		for (int yy = y; yy < y + height; ++yy) {
			world.setBlock(x, yy, z, log, 0, 2);
			
			if (yy == y)
				createRoots(world, x, yy, z);
			
			if (yy == y + height - 1)
				createLeaves(world, x, yy, z, true);

			if (yy == y + height - 8 || yy == y + height - 12) {
				if (rand.nextBoolean()) {
					createlBranch(world, rand, x + 1, yy - rand.nextInt(2), z, 1, 2);
					createlBranch(world, rand, x- 1, yy - rand.nextInt(2), z, 2, 2);
					createlBranch(world, rand, x, yy - rand.nextInt(2), z + 1, 3, 2);
					createlBranch(world, rand, x, yy - rand.nextInt(2), z - 1, 4, 2);
				} else {
					createlBranch(world, rand, x + 2, yy - rand.nextInt(2), z + 2, 5, 2);
					createlBranch(world, rand, x - 2, yy - rand.nextInt(2), z - 2, 6, 2);
					createlBranch(world, rand, x - 2, yy - rand.nextInt(2), z + 2, 7, 2);
					createlBranch(world, rand, x + 2, yy - rand.nextInt(2), z - 2, 8, 2);
				}
			}
			
			if (yy == y + height - 4) {
				if (rand.nextBoolean()) {
					createlBranch(world, rand, x + 1, yy- rand.nextInt(2), z, 1, 1);
					createlBranch(world, rand, x - 1, yy- rand.nextInt(2), z, 2, 1);
					createlBranch(world, rand, x, yy- rand.nextInt(2), z + 1, 3, 1);
					createlBranch(world, rand, x, yy- rand.nextInt(2), z - 1, 4, 1);
				} else {
					createlBranch(world, rand, x + 2, yy- rand.nextInt(2), z + 2, 5, 1);
					createlBranch(world, rand, x - 2, yy- rand.nextInt(2), z - 2, 6, 1);
					createlBranch(world, rand, x - 2, yy- rand.nextInt(2), z + 2, 7, 1);
					createlBranch(world, rand, x + 2, yy- rand.nextInt(2), z - 2, 8, 1);
				}
			}
		}
		return true;
	}

	private void createlBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
				meta = 0;
			}
			
			if (dir == 1) {
				world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x + i, y, z, false);
				}
			}

			if (dir == 2) {
				world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x - i, y, z, false);
				}
			}

			if (dir == 3) {
				world.setBlock(x, y, z + i, log,  meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x, y, z + i, false);
				}
			}

			if (dir == 4) {
				world.setBlock(x, y, z - i, log,  meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x, y, z - i, false);
					}
			}

			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x + i - 1, y, z + i - 1, false);
				}
			}

			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, log, meta == 0 ? 0 : 4, 2);
				if(i == branchLength) {
					createLeaves(world, x - i + 1, y, z - i + 1, false);
				}
			}

			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, log, meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x - i + 1, y, z + i - 1, false);
				}
			}

			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, log, meta == 0 ? 0 : 8, 2);
				if(i == branchLength) {
					createLeaves(world, x + i - 1, y, z - i + 1, false);
				}
			}
		}
	}

	private void createLeaves(World world, int x, int y, int z, boolean top) {
		world.setBlock(x, y, z + 1, leaves);
		world.setBlock(x, y, z - 1, leaves);
		world.setBlock(x + 1, y, z, leaves);
		world.setBlock(x - 1, y, z, leaves);
		
		if(top)
			world.setBlock(x, y + 1, z, leaves);
		else
			world.setBlock(x, y - 1, z, leaves);
	}
	
	private void createRoots(World world, int x, int y, int z) {
		world.setBlock(x, y, z + 1, log, 15, 2);
		world.setBlock(x, y, z - 1, log, 15, 2);
		world.setBlock(x + 1, y, z, log, 15, 2);
		world.setBlock(x - 1, y, z, log, 15, 2);
	}
	
}
