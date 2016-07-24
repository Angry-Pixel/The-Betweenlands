package thebetweenlands.client.tabs;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class TabItems extends CreativeTabBetweenlands {
	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public Item getTabIconItem() {
		return ItemRegistry.SWAMP_TALISMAN;
	}
}
