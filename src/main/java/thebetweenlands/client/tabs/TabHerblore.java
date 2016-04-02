package thebetweenlands.client.tabs;

import thebetweenlands.client.tab.CreativeTabBetweenlands;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TabHerblore extends CreativeTabBetweenlands {
	public TabHerblore() {
		super("thebetweenlands.herbLore");
	}

	@Override
	public Item getTabIconItem() {
		return /*BLItemRegistry.pestle*/Item.getItemFromBlock(Blocks.stone);
	}
}
