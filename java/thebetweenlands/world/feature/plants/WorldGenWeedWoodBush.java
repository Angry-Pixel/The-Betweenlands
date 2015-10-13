package thebetweenlands.world.feature.plants;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
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
		++y;

		this.setBlockAndNotifyAdequately(world, x, y, z, BLBlockRegistry.weedwoodBush, 0);

		for (int by = y; by <= y + (world.rand.nextInt(2) == 0 ? world.rand.nextInt(4) + 1 : 1); ++by) {
			int yo = by - y;
			int radius = (int)((world.rand.nextInt(3) - yo) / (world.rand.nextFloat() * (yo * 2 + 1) * 0.1 + 1.0));

			for (int bx = x - radius; bx <= x + radius; ++bx) {
				int xo = bx - x;

				for (int bz = z - radius; bz <= z + radius; ++bz) {
					int zo = bz - z;

					if ((Math.abs(xo) != radius || Math.abs(zo) != radius || rand.nextInt(4) == 0) && world.getBlock(bx, by, bz).canBeReplacedByLeaves(world, bx, by, bz) && rand.nextInt((int)((xo*xo+zo*zo)*1.5+1)) < 2) {
						Block blockBelow = world.getBlock(bx, by - 1, bz);
						if(blockBelow == BLBlockRegistry.weedwoodBush || blockBelow.isSideSolid(world, bx, by - 1, bz, ForgeDirection.UP)) {
							this.setBlockAndNotifyAdequately(world, bx, by, bz, BLBlockRegistry.weedwoodBush, 0);
						}
					}
				}
			}
		}

		return true;
	}
}