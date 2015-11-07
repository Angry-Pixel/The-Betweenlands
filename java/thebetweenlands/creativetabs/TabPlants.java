package thebetweenlands.creativetabs;

import net.minecraft.item.Item;
import thebetweenlands.blocks.BLBlockRegistry;

public class TabPlants
extends CreativeTabBetweenlands
{
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(BLBlockRegistry.catTail);
	}
}
