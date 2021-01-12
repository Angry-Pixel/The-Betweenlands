package thebetweenlands.common.item.herblore.rune;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IRuneletItem;
import thebetweenlands.api.runechain.rune.RuneCategory;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemRunelet extends Item implements IRuneletItem {
	private final Supplier<ItemRune> rune;

	public ItemRunelet(Supplier<ItemRune> rune) {
		this.setCreativeTab(BLCreativeTabs.HERBLORE);
		this.rune = rune;
	}
	
	@Override
	public ItemStack carve(ItemStack stack, RuneCategory category) {
		return this.rune.get().carve(category);
	}
}
