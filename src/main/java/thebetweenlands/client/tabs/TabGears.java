package thebetweenlands.client.tabs;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class TabGears extends CreativeTabBetweenlands {
	public TabGears() {
		super("thebetweenlands.gear");
	}

	@Override
	public Item getTabIconItem() {
		return ItemRegistry.VALONITE_PICKAXE;
	}
}
