package thebetweenlands.common.item.misc;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class ItemGem extends Item {
	public final CircleGemType type;

	public ItemGem(CircleGemType type) {
		this.type = type;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}
}
