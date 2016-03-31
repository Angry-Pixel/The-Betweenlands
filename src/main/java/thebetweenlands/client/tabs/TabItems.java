package thebetweenlands.client.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TabItems extends CreativeTabBetweenlands {
	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public Item getTabIconItem() {
		return /*BLItemRegistry.swampTalisman*/Item.getItemFromBlock(Blocks.stone);
	}
}
