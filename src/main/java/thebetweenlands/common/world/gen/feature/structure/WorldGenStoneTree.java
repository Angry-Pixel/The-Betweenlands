package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenStoneTree extends WorldGenerator {
	private IBlockState BETWEENSTONE = BlockRegistry.BETWEENSTONE.getDefaultState();
	private IBlockState BETWEENSTONE_BRICKS = BlockRegistry.BETWEENSTONE_BRICKS.getDefaultState();
	public WorldGenStoneTree() {
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		generateTree(world, rand, pos);
		return true;

	}

	public void generateTree(World world, Random rand, BlockPos pos) {
		
		int radius = 4 + rand.nextInt(2);
		int height = 30 + rand.nextInt(5);
		int maxRadius = 9;

		for (int yy = 0; yy < height; ++yy) {
			if ((yy) % 5 == 0 && radius > 2)
				--radius;

			for (int i = radius * -1; i <= radius; ++i) {
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius)
						world.setBlockState(pos.add(i, yy, j), BETWEENSTONE, 2);
					if (Math.round(Math.sqrt(dSq)) == radius || Math.round(Math.sqrt(dSq)) <= radius && yy == height - 1)
						world.setBlockState(pos.add(i, yy, j), BETWEENSTONE, 2);
				}
				
			}
			if (yy == height/2) {
				createBranch(world, rand, pos.add(radius + 1, yy, 0), 1, false, height/3 + rand.nextInt(4));
				createBranch(world, rand, pos.add(- radius - 1, yy, 0), 2, false, height/3 + rand.nextInt(4));
				createBranch(world, rand, pos.add(0, yy, radius + 1), 3, false, height/3 + rand.nextInt(4));
				createBranch(world, rand, pos.add(0, yy, - radius - 1), 4, false, height/3 + rand.nextInt(4));
			}
			if (yy == height/2 + 3) {
				createBranch(world, rand, pos.add(radius, yy, radius), 5, false, height/4 + rand.nextInt(4));
				createBranch(world, rand, pos.add(- radius - 1, yy, - radius - 1), 6, false, height/4 + rand.nextInt(4));
				createBranch(world, rand, pos.add(- radius - 1, yy, radius + 1), 7, false, height/4 + rand.nextInt(4));
				createBranch(world, rand, pos.add(radius + 1, yy, - radius - 1), 8, false, height/4 + rand.nextInt(4));
			}
			if (yy == height/2 + 6) {
				createBranch(world, rand, pos.add(radius + 1, yy, 0), 1, false, height/5 + rand.nextInt(3));
				createBranch(world, rand, pos.add(- radius - 1, yy, 0), 2, false, height/5 + rand.nextInt(3));
				createBranch(world, rand, pos.add(0, yy, radius + 1), 3, false, height/5 + rand.nextInt(3));
				createBranch(world, rand, pos.add(0, yy, - radius - 1), 4, false, height/5 + rand.nextInt(3));
			}
			if (yy == height/2 + 9) {
				createBranch(world, rand, pos.add(radius, yy, radius), 5, false, height/6 + rand.nextInt(3));
				createBranch(world, rand, pos.add(- radius - 1, yy, - radius - 1), 6, false, height/6 + rand.nextInt(3));
				createBranch(world, rand, pos.add(- radius - 1, yy, radius + 1), 7, false, height/6 + rand.nextInt(3));
				createBranch(world, rand, pos.add(radius + 1, yy, - radius - 1), 8, false, height/6 + rand.nextInt(3));
			}
			if (yy == 3) {
				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(3), 0), 1, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(3), 0), 2, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(3), radius + 1), 3, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(3), - radius - 1), 4, true, rand.nextInt(6) + 6);

				createBranch(world, rand, pos.add(radius - 1, yy - rand.nextInt(3), radius), 5, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(- radius + 1, yy - rand.nextInt(3), - radius), 6, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(- radius, yy - rand.nextInt(3), radius - 1), 7, true, rand.nextInt(6) + 6);
				createBranch(world, rand, pos.add(radius, yy - rand.nextInt(3), - radius + 1), 8, true, rand.nextInt(6) + 6);
			}
		}
		createMainCanopy(world, rand, pos.add(0, height - 2, 0), height/4 + rand.nextInt(3));
	}

	private void createSmallBranch(World world, Random rand, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2)
				y++;

			if (dir == 1) {
				world.setBlockState(pos.add(i, y, 0), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(i, y, 0), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 2) {
				world.setBlockState(pos.add(- i, y, 0), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(- i, y, 0), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 3) {
				world.setBlockState(pos.add(0, y, i), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(0, y, i), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 4) {
				world.setBlockState(pos.add(0, y, - i), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(0, y, - i), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 5) {
				world.setBlockState(pos.add(i - 1, y, i - 1), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(i - 1, y, i - 1), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 6) {
				world.setBlockState(pos.add(- i + 1, y, - i + 1), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(- i + 1, y, - i + 1), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 7) {
				world.setBlockState(pos.add(- i + 1, y, i - 1), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(- i + 1, y, i - 1), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 8) {
				world.setBlockState(pos.add(i - 1, y, - i + 1), BETWEENSTONE, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, pos.add(i - 1, y, - i + 1), branchLength / 2 + rand.nextInt(branchLength / 4));
			}
		}
	}

	private void createMainCanopy(World world, Random rand, BlockPos pos, int maxRadius) {
		for (int x1 = - maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = - maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1 ++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlockState(pos.add(x1, y1, z1)) != BETWEENSTONE)
							world.setBlockState(pos.add(x1, y1, z1), BETWEENSTONE_BRICKS, 2);
				}
	}

	private void createBranch(World world, Random rand, BlockPos pos, int dir, boolean root, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (!root && i >= 3)
				y++;
			else if (root && i >= 4)
				y--;

			if (dir == 1) {
				world.setBlockState(pos.add(i, y, 0), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i, y - 1, 0), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i, y - 1, 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i, y - 1, - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i, y - 2, 0), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength/2) {
						createSmallBranch(world, rand, pos.add(i, y - 1, 1), 3, 4);
						createSmallBranch(world, rand, pos.add(i, y - 1, - 1), 4, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(i, y + 1, 0), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 2) {
				world.setBlockState(pos.add(- i, y, 0), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i, y - 1, 0), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i, y - 1, 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i, y - 1, - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i, y - 2, 0), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength/2) {
						createSmallBranch(world, rand, pos.add(- i, y - 1, 1), 3, 4);
						createSmallBranch(world, rand, pos.add(- i, y - 1, - 1), 4, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(- i, y + 1, 0), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 3) {
				world.setBlockState(pos.add(0, y, i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(0, y - 1, i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(1, y - 1, i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- 1, y - 1, i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(0, y - 2, i), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength/2) {
						createSmallBranch(world, rand, pos.add(1, y - 1, i), 1, 4);
						createSmallBranch(world, rand, pos.add(- 1, y - 1, i), 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(0, y + 1, i), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 4) {
				world.setBlockState(pos.add(0, y, - i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(0, y - 1, - i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(1, y - 1, - i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- 1, y - 1, - i), BETWEENSTONE, 2);
				world.setBlockState(pos.add(0, y - 2, - i), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength/2) {
						createSmallBranch(world, rand, pos.add(1, y - 1, - i), 1, 4);
						createSmallBranch(world, rand, pos.add(- 1, y - 1, - i), 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(0, y + 1, - i), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 5) {
				world.setBlockState(pos.add(i - 1, y, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 1, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 2, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 2, y - 1, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 1, i - 2), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(i - 1, y + 1, i - 1), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 6) {
				world.setBlockState(pos.add(- i + 1, y, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 1, y - 1, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add( - i + 1, y - 2, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 2, y - 1, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 1, y - 1, - i + 2), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(- i + 1, y + 1, - i + 1), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 7) {
				world.setBlockState(pos.add(- i + 1, y, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 1, y - 1, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add( - i + 1, y - 2, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 2, y - 1, i - 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(- i + 1, y - 1, i - 2), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(- i + 1, y + 1, i - 1), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 8) {
				world.setBlockState(pos.add(i - 1, y, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 1, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 2, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 2, y - 1, - i + 1), BETWEENSTONE, 2);
				world.setBlockState(pos.add(i - 1, y - 1, - i + 2), BETWEENSTONE, 2);
				if (!root) {
					if (i == branchLength)
						createMainCanopy(world, rand, pos.add(i - 1, y + 1, - i + 1), branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
		}
	}
}
