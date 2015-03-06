package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLLeaves;
import thebetweenlands.blocks.BlockBLLog;

public class WorldGenWeedWoodTree extends WorldGenerator {
	private BlockBLLog log = (BlockBLLog) BLBlockRegistry.weedWoodLog;
	private BlockBLLeaves leaves = (BlockBLLeaves) BLBlockRegistry.weedWoodLeaves;

	public boolean generate(World world, Random rand, int x, int y, int z) {
		int radius = rand.nextInt(2) + 3;
		int height = rand.nextInt(5) + 15;
		int maxRadius = 9;

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;
		
		createMainCanopy(world, rand, x, y + height/2 + 1, z, maxRadius);
		
		for (int yy = y; yy < y + height; ++yy) {
			if (yy % 5 == 0 && radius > 1)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) <= radius && yy <= y + height - 2)
						world.setBlock(x + i, yy, z + j, log, 0, 2);
					if (Math.round(Math.sqrt(dSq)) <= radius && yy == y || Math.round(Math.sqrt(dSq)) <= radius && yy == y + height - 1)
						world.setBlock(x + i, yy, z + j, log, 15, 2);
				}

			if (yy == y + height/2) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(2), z + radius + 1, 3, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(2), z - radius - 1, 4, false, rand.nextInt(2) + 3);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, false, rand.nextInt(2) + 3);
			}
			
			if (yy == y + height/2 + 3) {
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, 4);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(2), z + radius + 1, 3, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(2), z - radius - 1, 4, 4);

				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, 3);
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, 3);
			}
			
			if (yy == y + height/2 + 6) {
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, 2);
				createSmallBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, 2);
				createSmallBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, 2);

				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, 2);
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, 2);
			}

			if (yy == y + 1) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z, 1, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z, 2, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, true, rand.nextInt(2) + 3);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z + radius + 1, 5, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z - radius - 1, 6, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z + radius + 1, 7, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z - radius - 1, 8, true, rand.nextInt(2) + 3);
			}
		}
		return true;
	}

	private void createSmallBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				meta = 0;
			}

			if (dir == 1)
				world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);

			if (dir == 2)
				world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);

			if (dir == 3)
				world.setBlock(x, y, z + i, log, meta == 0 ? 0 : 8, 2);

			if (dir == 4)
				world.setBlock(x, y, z - i, log, meta == 0 ? 0 : 8, 2);

			if (dir == 5)
				world.setBlock(x + i - 1, y, z + i - 1, log, meta == 0 ? 0 : 4, 2);

			if (dir == 6)
				world.setBlock(x - i + 1, y, z - i + 1, log, meta == 0 ? 0 : 4, 2);

			if (dir == 7)
				world.setBlock(x - i + 1, y, z + i - 1, log, meta == 0 ? 0 : 8, 2);

			if (dir == 8)
				world.setBlock(x + i - 1, y, z - i + 1, log, meta == 0 ? 0 : 8, 2);
		}
	}

	private void createMainCanopy(World world, Random rand, int x, int y, int z, int maxRadius) {
		for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
			for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
				for (int y1 = y; y1 < y + maxRadius; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlock(x1, y1, z1) != log && rand.nextInt(5) != 0)
							world.setBlock(x1, y1, z1, leaves);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && rand.nextInt(5) == 0 && y1 > y)
						if (world.getBlock(x1, y1, z1) != log)
							world.setBlock(x1, y1, z1, log);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == y)
						if (world.getBlock(x1, y1, z1) != log)
							for (int i = 1; i < 1 + rand.nextInt(3); i++)
								world.setBlock(x1, y1 - i, z1, leaves);
				}
	}

	private void createBranch(World world, Random rand, int x, int y, int z, int dir, boolean root, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3) {
				if(!root)
					y++;
				else
					y--;
				meta = 0;
			}

			if (dir == 1)
				if (!root) {
					world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);
					if (i <= branchLength)
						addVines(world, rand, x + i, y - 1, z, 2);
				} else {
					world.setBlock(x + i, y, z, log, 15, 2);
					world.setBlock(x + i, y - 1, z, log, 15, 2);
				}

			if (dir == 2)
				if (!root) {
					world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);
					if (i <= branchLength)
						addVines(world, rand, x - i, y - 1, z, 8);
				} else {
					world.setBlock(x - i, y, z, log, 15, 2);
					world.setBlock(x - i, y - 1, z, log, 15, 2);
				}

			if (dir == 3)
				if (!root) {
					world.setBlock(x, y, z + i, log, meta == 0 ? 0 : 8, 2);
					if (i <= branchLength)
						addVines(world, rand, x, y - 1, z + i, 4);
				} else {
					world.setBlock(x, y, z + i, log, 15, 2);
					world.setBlock(x, y - 1, z + i, log, 15, 2);
				}

			if (dir == 4)
				if (!root) {
					world.setBlock(x, y, z - i, log, meta == 0 ? 0 : 8, 2);
					if (i <= branchLength)
						addVines(world, rand, x, y - 1, z - i, 1);
				} else {
					world.setBlock(x, y, z - i, log, 15, 2);
					world.setBlock(x, y - 1, z - i, log, 15, 2);
				}

			if (dir == 5)
				if (!root) {
					world.setBlock(x + i - 1, y, z + i - 1, log, meta == 0 ? 0 : 4, 2);
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 1, z + i - 1, 2);
				} else {
					world.setBlock(x + i - 1, y, z + i - 1, log, 15, 2);
					world.setBlock(x + i - 1, y - 1, z + i - 1, log, 15, 2);
				}

			if (dir == 6)
				if (!root) {
					world.setBlock(x - i + 1, y, z - i + 1, log, meta == 0 ? 0 : 4, 2);
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 1, z - i + 1, 8);
				} else {
					world.setBlock(x - i + 1, y, z - i + 1, log, 15, 2);
					world.setBlock(x - i + 1, y - 1, z - i + 1, log, 15, 2);
				}

			if (dir == 7)
				if (!root) {
					world.setBlock(x - i + 1, y, z + i - 1, log, meta == 0 ? 0 : 8, 2);
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 1, z + i - 1, 4);
				} else {
					world.setBlock(x - i + 1, y, z + i - 1, log, 15, 2);
					world.setBlock(x - i + 1, y - 1, z + i - 1, log, 15, 2);
				}

			if (dir == 8)
				if (!root) {
					world.setBlock(x + i - 1, y, z - i + 1, log, meta == 0 ? 0 : 8, 2);
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 1, z - i + 1, 1);
				} else {
					world.setBlock(x + i - 1, y, z - i + 1, log, 15, 2);
					world.setBlock(x + i - 1, y - 1, z - i + 1, log, 15, 2);
				}
		}
	}

	public void addVines(World world, Random rand, int x, int y, int z, int meta) {
		if (rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = y; yy > y - length; --yy)
				if (world.getBlock(x, yy, z) == Blocks.air)
					world.setBlock(x, yy, z, Blocks.vine, meta, 2);
				else
					break;
		}
	}
}
