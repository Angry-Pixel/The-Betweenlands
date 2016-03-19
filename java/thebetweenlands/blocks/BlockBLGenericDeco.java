package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBLGenericDeco extends Block implements IGrowable {

	private String type;

	public BlockBLGenericDeco(String blockName, Material material) {
		super(material);
		setCreativeTab(BLCreativeTabs.blocks);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	public BlockBLGenericDeco(String blockName, Material material, String tool, int harvestLevel) {
		this(blockName, material);
		setHarvestLevel(tool, harvestLevel);
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	public boolean func_149851_a(World world, int p_149851_2_, int p_149851_3_, int p_149851_4_, boolean p_149851_5_) {
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

					if (world.getBlock(i1, j1 - 1, k1) == BLBlockRegistry.limestone && !world.getBlock(i1, j1, k1).isNormalCube()) {
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