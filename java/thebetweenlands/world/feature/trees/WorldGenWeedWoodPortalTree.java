package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockTreePortal;
import thebetweenlands.blocks.plants.BlockPoisonIvy;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;

public class WorldGenWeedWoodPortalTree extends WorldGenerator {
	private BlockBLLog bark;
	private BlockBLLog wood;
	private BlockBLLeaves leaves;
	private BlockPoisonIvy ivy;
	private BlockTreePortal portal;

	public boolean generate(World world, Random rand, int x, int y, int z) {
		int radius = 4;
		int height = 16;
		int maxRadius = 9;

		this.bark = (BlockBLLog) BLBlockRegistry.portalBark;
		this.wood = (BlockBLLog) BLBlockRegistry.weedwood;
		this.leaves = (BlockBLLeaves) BLBlockRegistry.weedwoodLeaves;
		this.ivy = (BlockPoisonIvy) BLBlockRegistry.poisonIvy;
		this.portal = (BlockTreePortal) BLBlockRegistry.treePortalBlock;

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz) && world.getBlock(xx, yy, zz).isNormalCube()) {
						if(TheBetweenlands.proxy.getClientPlayer() != null)
							TheBetweenlands.proxy.getClientPlayer().addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("talisman.noplace")));
						return false;
					}

		createMainCanopy(world, rand, x, y + height/2 + 4, z, maxRadius);

		for (int yy = y; yy < y + height; ++yy) {
			if ((yy - y) % 5 == 0 && radius > 1 && yy > 7)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= y + 1)
						world.setBlock(x + i, yy, z + j, wood, 0, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == y || Math.round(Math.sqrt(dSq)) == radius && yy <= y + height - 1)
						world.setBlock(x + i, yy, z + j, bark, 0, 2);
				}

			if(yy == y + 4) {
				portal.makePortalX(world, x + radius, yy - 2, z);
				portal.makePortalX(world, x - radius, yy - 2, z);
				portal.makePortalZ(world, x, yy - 2, z + radius);
				portal.makePortalZ(world, x, yy - 2, z - radius);
			}

			if (yy == y + height/2 + 2) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, false, rand.nextInt(2) + 4);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, false, rand.nextInt(2) + 4);
				createBranch(world, rand, x, yy - rand.nextInt(2), z + radius + 1, 3, false, rand.nextInt(2) + 4);
				createBranch(world, rand, x, yy - rand.nextInt(2), z - radius - 1, 4, false, rand.nextInt(2) + 4);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, false, rand.nextInt(2) + 3);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, false, rand.nextInt(2) + 3);
			}

			if (yy == y + height/2 + 4) {
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, 4);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(2), z + radius + 1, 3, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(2), z - radius - 1, 4, 4);

				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, 3);
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, 3);
			}

			if (yy == y + height/2 + 7) {
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
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(2), z + radius + 1, 3, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x, yy - rand.nextInt(2), z - radius - 1, 4, true, rand.nextInt(2) + 3);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, true, rand.nextInt(2) + 3);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, true, rand.nextInt(2) + 3);
			}
		}
		return true;
	}

	private void createSmallBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2)
				y++;


			if (dir == 1)
				world.setBlock(x + i, y, z, bark);

			if (dir == 2)
				world.setBlock(x - i, y, z, bark);

			if (dir == 3)
				world.setBlock(x, y, z + i, bark);

			if (dir == 4)
				world.setBlock(x, y, z - i, bark);

			if (dir == 5)
				world.setBlock(x + i - 1, y, z + i - 1, bark);

			if (dir == 6)
				world.setBlock(x - i + 1, y, z - i + 1, bark);

			if (dir == 7)
				world.setBlock(x - i + 1, y, z + i - 1, bark);

			if (dir == 8)
				world.setBlock(x + i - 1, y, z - i + 1, bark);
		}
	}

	private void createMainCanopy(World world, Random rand, int x, int y, int z, int maxRadius) {
		for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
			for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
				for (int y1 = y; y1 < y + maxRadius; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlock(x1, y1, z1) != bark && rand.nextInt(5) != 0)
							world.setBlock(x1, y1, z1, leaves);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && rand.nextInt(5) == 0 && y1 > y)
						if (world.getBlock(x1, y1, z1) != bark)
							world.setBlock(x1, y1, z1, bark);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == y)
						if (world.getBlock(x1, y1, z1) != bark)
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
					world.setBlock(x + i, y, z, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x + i, y - 1, z, 2);
				} else {
					world.setBlock(x + i, y, z, bark);
					world.setBlock(x + i, y - 1, z, bark);
				}

			if (dir == 2)
				if (!root) {
					world.setBlock(x - i, y, z, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x - i, y - 1, z, 8);
				} else {
					world.setBlock(x - i, y, z, bark);
					world.setBlock(x - i, y - 1, z, bark);
				}

			if (dir == 3)
				if (!root) {
					world.setBlock(x, y, z + i, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x, y - 1, z + i, 4);
				} else {
					world.setBlock(x, y, z + i, bark);
					world.setBlock(x, y - 1, z + i, bark);
				}

			if (dir == 4)
				if (!root) {
					world.setBlock(x, y, z - i, bark);
					if (i < branchLength +1)
						addVines(world, rand, x, y - 1, z - i, 1);
				} else {
					world.setBlock(x, y, z - i, bark);
					world.setBlock(x, y - 1, z - i, bark);
				}

			if (dir == 5)
				if (!root) {
					world.setBlock(x + i - 1, y, z + i - 1, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x + i - 1, y - 1, z + i - 1, 2);
				} else {
					world.setBlock(x + i - 1, y, z + i - 1, bark);
					world.setBlock(x + i - 1, y - 1, z + i - 1, bark);
				}

			if (dir == 6)
				if (!root) {
					world.setBlock(x - i + 1, y, z - i + 1, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x - i + 1, y - 1, z - i + 1, 8);
				} else {
					world.setBlock(x - i + 1, y, z - i + 1, bark);
					world.setBlock(x - i + 1, y - 1, z - i + 1, bark);
				}

			if (dir == 7)
				if (!root) {
					world.setBlock(x - i + 1, y, z + i - 1, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x - i + 1, y - 1, z + i - 1, 4);
				} else {
					world.setBlock(x - i + 1, y, z + i - 1, bark);
					world.setBlock(x - i + 1, y - 1, z + i - 1, bark);
				}

			if (dir == 8)
				if (!root) {
					world.setBlock(x + i - 1, y, z - i + 1, bark);
					if (i < branchLength + 1)
						addVines(world, rand, x + i - 1, y - 1, z - i + 1, 1);
				} else {
					world.setBlock(x + i - 1, y, z - i + 1, bark);
					world.setBlock(x + i - 1, y - 1, z - i + 1, bark);
				}
		}
	}

	public void addVines(World world, Random rand, int x, int y, int z, int meta) {
		/*	if (rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = y; yy > y - length; --yy)
				if (world.getBlock(x, yy, z) == Blocks.air)
					world.setBlock(x, yy, z,ivy, meta, 2);
				else
					break;
		}*/
	}
}
