package thebetweenlands.blocks.ores;

import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class BlockMiddleGemOre extends BlockGenericOre {
	public BlockMiddleGemOre(String blockName, EnumMaterialsBL blockDrops) {
		super(blockName, blockDrops);
		this.setLightLevel(0.8f);
	}
}
