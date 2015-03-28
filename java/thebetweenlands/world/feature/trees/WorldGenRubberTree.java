package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;

public class WorldGenRubberTree extends WorldGenerator {

	private BlockBLLog log = (BlockBLLog) BLBlockRegistry.rubberTreeLog;
	private BlockBLLeaves leaves = (BlockBLLeaves) BLBlockRegistry.rubberTreeLeaves;

	public boolean generate(World world, Random rand, int x, int y, int z) {

		int height = rand.nextInt(8) + 8;
		int maxRadius = 4;

		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			world.setBlock(x, yy, z, log, 0, 2);

			if (yy == y + height - 1)
				createMainCanopy(world, rand, x, yy, z, maxRadius);

			if (yy == y + height - 2) {
				createBranch(world, rand, x + 1, yy, z, 1, 1);
				createBranch(world, rand, x- 1, yy, z, 2, 1);
				createBranch(world, rand, x, yy, z + 1, 3, 1);
				createBranch(world, rand, x, yy, z - 1, 4, 1);
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

			if (dir == 1)
				world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);

			if (dir == 2)
				world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);

			if (dir == 3)
				world.setBlock(x, y, z + i, log,  meta == 0 ? 0 : 8, 2);

			if (dir == 4)
				world.setBlock(x, y, z - i, log,  meta == 0 ? 0 : 8, 2);

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
				}
		}

}
