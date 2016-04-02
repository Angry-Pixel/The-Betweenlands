package thebetweenlands.client.tabs;

import thebetweenlands.client.tab.CreativeTabBetweenlands;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TabSpecialItems extends CreativeTabBetweenlands {
	public TabSpecialItems() {
		super("thebetweenlands.special");
	}

	@Override
	public Item getTabIconItem() {
		return /*BLItemRegistry.shimmerStone*/Item.getItemFromBlock(Blocks.stone);
	}
}
