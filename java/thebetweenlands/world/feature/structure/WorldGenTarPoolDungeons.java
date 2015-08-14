package thebetweenlands.world.feature.structure;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

public class WorldGenTarPoolDungeons extends WorldGenerator { // TODO

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		byte height = 4;
		int halfSize = 7;
		int j1 = 0;
		int i2;

		if (y + height + 1 >= 70)
			return false;

		for (int xx = x - halfSize - 1; xx <= x + halfSize + 1; ++xx)
			for (int yy = y - 1; yy <= y + height + 1; ++yy)
				for (int zz = z - halfSize - 1; zz <= z + halfSize + 1; ++zz) {
					Material mat = world.getBlock(xx, yy, zz).getMaterial();

					if ((yy == y - 1 || yy == y + height + 1) && !mat.isSolid())
						return false;
					if ((xx == x - halfSize - 1 || xx == x + halfSize + 1 || zz == z - halfSize - 1 || zz == z + halfSize + 1) && yy == y && world.isAirBlock(xx, yy, zz) && world.isAirBlock(xx, yy + 1, zz))
						++j1;
				}

		if (j1 >= 1 && j1 <= 5) {
			for (int xx = x - halfSize - 1; xx <= x + halfSize + 1; ++xx)
				for (int yy = y + height + 1; yy >= y - 1; --yy)
					for (int zz = z - halfSize - 1; zz <= z + halfSize + 1; ++zz) {
						if (xx != x - halfSize - 1 && yy != y - 1 && zz != z - halfSize - 1 && xx != x + halfSize + 1 && yy != y + height + 1 && zz != z + halfSize + 1)
							world.setBlockToAir(xx, yy, zz);
						else if (yy >= 0 && !world.getBlock(xx, yy - 1, zz).getMaterial().isSolid())
							world.setBlockToAir(xx, yy, zz);
						else if (world.getBlock(xx, yy, zz).getMaterial().isSolid())
							world.setBlock(xx, yy, zz, BLBlockRegistry.betweenstone, 0, 2);
						if (xx == x - halfSize || zz == z - halfSize || xx == x + halfSize || yy == y + height || zz == z + halfSize) {
							if (rand.nextBoolean() && rand.nextBoolean())
								world.setBlock(xx, yy, zz, BLBlockRegistry.betweenstone, 0, 2);
							}
					}
			 System.out.println("Tar Pool Dungeon Here: " + x + " " + y + " " + z);

		}
		for (int yy = y - 1; yy >= y - 3; --yy) {
			for (int xx = halfSize * -1; xx <= halfSize; ++xx) {
				for (int zz = halfSize * -1; zz <= halfSize; ++zz) {
					double dSq = xx * xx + zz * zz;
					if (Math.round(Math.sqrt(dSq)) == halfSize - 1 && yy >= y - 2)
						world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.betweenstoneTiles);
					if (Math.round(Math.sqrt(dSq)) < halfSize - 1 && yy >= y - 2)
						world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.tarFluid);
					if (Math.round(Math.sqrt(dSq)) <= halfSize - 1 && yy == y - 3) {
						if (rand.nextBoolean() && rand.nextBoolean())
							world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.solidTar);
						else
							world.setBlock(x + xx, yy, z + zz, BLBlockRegistry.betweenstone);
						world.setBlock(x, y - 3, z, BLBlockRegistry.tarBeastSpawner, 0, 3);
					}
				}
			}
		}
		return true;
	}
}