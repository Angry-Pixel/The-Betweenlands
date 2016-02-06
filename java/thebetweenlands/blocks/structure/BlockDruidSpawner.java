package thebetweenlands.blocks.structure;

import thebetweenlands.blocks.BlockSpawner;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockDruidSpawner extends BlockSpawner {

	public BlockDruidSpawner(String mobName) {
		super("thebetweenlands." + mobName);
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.druidSpawner");
		setBlockTextureName("thebetweenlands:druidSpawner");
		setCreativeTab(BLCreativeTabs.blocks);
	}

}
