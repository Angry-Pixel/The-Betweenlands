package thebetweenlands.blocks.ores;

import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class BlockMiddleGemOre extends BlockGenericOre {
	public BlockMiddleGemOre(String blockName, EnumMaterialsBL oreDrops) {
		super(blockName, oreDrops != null ? ItemMaterialsBL.createStack(oreDrops) : null);
		this.setLightLevel(0.8f);
	}
}
