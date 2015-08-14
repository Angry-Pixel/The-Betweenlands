package thebetweenlands.blocks.structure;

import thebetweenlands.blocks.BlockSpawner;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockTarBeastSpawner extends BlockSpawner {

	public BlockTarBeastSpawner(String mobName) {
		super("thebetweenlands." + mobName);
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.tarBeastSpawner");
		//setBlockTextureName("thebetweenlands:tarBeastSpawner");
		setCreativeTab(ModCreativeTabs.blocks);
	}

}
