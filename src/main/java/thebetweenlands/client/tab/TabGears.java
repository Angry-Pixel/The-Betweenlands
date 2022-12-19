package thebetweenlands.client.tab;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.ItemRegistry;

public class TabGears extends CreativeTabBetweenlands {
	public TabGears() {
		super("thebetweenlands.gear");
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ItemRegistry.VALONITE_PICKAXE);
	}
}
