package thebetweenlands.client.tab;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;

public class TabHerblore extends CreativeTabBetweenlands {
	public TabHerblore() {
		super("thebetweenlands.herblore");
	}

	@Override
	public ItemStack createIcon() {
		return EnumItemCrushed.GROUND_GENERIC_LEAF.create(1);
	}
}
