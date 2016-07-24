package thebetweenlands.client.tabs;

import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.CreativeTabBetweenlands;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;

public class TabItems extends CreativeTabBetweenlands {
	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public ItemStack getIconItemStack() {
		return EnumTalisman.SWAMP_TALISMAN_0.create(1);
	}
}
