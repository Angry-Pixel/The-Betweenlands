package thebetweenlands.client.tabs;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class TabSpecialItems extends CreativeTabBetweenlands {
	public TabSpecialItems() {
		super("thebetweenlands.special");
	}

	@Override
	public Item getTabIconItem() {
		return ItemRegistry.CANDY_BLUE;
	}
}
