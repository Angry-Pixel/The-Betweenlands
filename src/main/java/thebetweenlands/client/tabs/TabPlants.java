package thebetweenlands.client.tabs;

import thebetweenlands.client.tab.CreativeTabBetweenlands;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TabPlants extends CreativeTabBetweenlands {
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public Item getTabIconItem() {
		return /*Item.getItemFromBlock(BLBlockRegistry.catTail)*/Item.getItemFromBlock(Blocks.stone);
	}
}
