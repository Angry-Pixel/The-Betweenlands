package thebetweenlands.client.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import thebetweenlands.client.tab.CreativeTabBetweenlands;

public class TabGears extends CreativeTabBetweenlands {
	public TabGears() {
		super("thebetweenlands.gear");
	}

	@Override
	public Item getTabIconItem() {
		return /*BLItemRegistry.valonitePickaxe*/Item.getItemFromBlock(Blocks.STONE);
	}
}
