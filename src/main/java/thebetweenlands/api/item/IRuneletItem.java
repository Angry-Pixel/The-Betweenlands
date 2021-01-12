package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.runechain.rune.RuneCategory;

public interface IRuneletItem {
	public ItemStack carve(ItemStack stack, RuneCategory category);
}
