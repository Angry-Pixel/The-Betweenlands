package thebetweenlands.client.tabs;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class TabPlants extends CreativeTabBetweenlands {
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(BlockRegistry.SUNDEW);
	}
}
