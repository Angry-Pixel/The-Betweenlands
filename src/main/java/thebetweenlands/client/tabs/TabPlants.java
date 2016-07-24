package thebetweenlands.client.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;

public class TabPlants extends CreativeTabBetweenlands {
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public Item getTabIconItem() {
		return /*Item.getItemFromBlock(BLBlockRegistry.catTail)*/Item.getItemFromBlock(Blocks.STONE);
	}
}
