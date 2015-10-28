package thebetweenlands.blocks.ores;

import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;

public class BlockMiddleGemOre extends BlockGenericOre {
	public BlockMiddleGemOre(String blockName, EnumItemGeneric oreDrops) {
		super(blockName, oreDrops != null ? ItemGeneric.createStack(oreDrops) : null);
		this.setLightLevel(0.8f);
	}
}
