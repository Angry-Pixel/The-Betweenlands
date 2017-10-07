package thebetweenlands.client.tab;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;

public class TabItems extends CreativeTabBetweenlands {
	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public ItemStack getTabIconItem() {
		return EnumTalisman.SWAMP_TALISMAN_0.create(1);
	}
}
