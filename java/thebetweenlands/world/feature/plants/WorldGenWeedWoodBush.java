package thebetweenlands.world.feature.plants;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

import java.util.Random;

public class WorldGenWeedWoodBush extends WorldGenerator {
	public WorldGenWeedWoodBush() {
	}

	public boolean generate(World world, Random rand, int x, int y, int z) {
		Block block;

		do {
			block = world.getBlock(x, y, z);
			if (!(block.isLeaves(world, x, y, z) || block.isAir(world, x, y, z))) {
				break;
			}
			--y;
		} while (y > 0);

		Block block1 = world.getBlock(x, y, z);
		++y;
		this.setBlockAndNotifyAdequately(world, x, y, z, BLBlockRegistry.weedwoodBush, 0);

		for (int l = y; l <= y + 2; ++l) {
			int i1 = l - y;
			int j1 = 2 - i1;

			for (int k1 = x - j1; k1 <= x + j1; ++k1) {
				int l1 = k1 - x;

				for (int i2 = z - j1; i2 <= z + j1; ++i2) {
					int j2 = i2 - z;

					if ((Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0) && world.getBlock(k1, l, i2).canBeReplacedByLeaves(world, k1, l, i2)) {
						this.setBlockAndNotifyAdequately(world, k1, l, i2, BLBlockRegistry.weedwoodBush, 0);
					}
				}
			}
		}

		return true;
	}
}