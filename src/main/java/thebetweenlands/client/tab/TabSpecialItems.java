package thebetweenlands.client.tab;

import net.minecraft.item.Item;
import thebetweenlands.common.registries.ItemRegistry;

public class TabSpecialItems extends CreativeTabBetweenlands {
	public TabSpecialItems() {
		super("thebetweenlands.special");
	}

	@Override
	public Item getTabIconItem() {
		return ItemRegistry.ASTATOS;
	}
}
