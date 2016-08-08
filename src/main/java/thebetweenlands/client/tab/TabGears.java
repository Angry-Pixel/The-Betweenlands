package thebetweenlands.client.tab;

import net.minecraft.item.Item;
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
