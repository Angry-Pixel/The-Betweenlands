package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.BLCreativeTabs;

import java.util.Random;

public class BlockPitstone extends Block implements IGrowable {
	public BlockPitstone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.pitstone");
		setBlockTextureName("thebetweenlands:pitstone");
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean p_149851_5_) {
		return false;
	}

	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		return false;
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		int l = 0;

		while (l < 128) {
			int i1 = x;
			int j1 = y + 1;
			int k1 = z;
			int l1 = 0;

			while (true) {
				if (l1 < l / 16) {
					i1 += rand.nextInt(3) - 1;
					j1 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
					k1 += rand.nextInt(3) - 1;

					if (world.getBlock(i1, j1 - 1, k1) == BLBlockRegistry.pitstone && !world.getBlock(i1, j1, k1).isNormalCube()) {
						++l1;
						continue;
					}
				} else if (world.getBlock(i1, j1, k1) == Blocks.air) {
					if (rand.nextInt(4) != 0) {
						if (BLBlockRegistry.caveGrass.canBlockStay(world, i1, j1, k1)) {
							world.setBlock(i1, j1, k1, BLBlockRegistry.caveGrass, 1, 3);
						}
					}
				}

				++l;
				break;
			}
		}
	}
}
