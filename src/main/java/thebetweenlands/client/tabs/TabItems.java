package thebetweenlands.client.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;

public class TabItems extends CreativeTabBetweenlands {
	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public Item getTabIconItem() {
		return /*BLItemRegistry.SWAMP_TALISMAN*/Item.getItemFromBlock(Blocks.STONE);
	}
}
