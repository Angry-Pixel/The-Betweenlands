package thebetweenlands.client.tab;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.ItemRegistry;

public class TabSpecialItems extends CreativeTabBetweenlands {
	public TabSpecialItems() {
		super("thebetweenlands.special");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ItemRegistry.ASTATOS);
	}
}
