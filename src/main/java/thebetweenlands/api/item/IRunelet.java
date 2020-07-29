package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.RuneCategory;

public interface IRunelet {
	public ItemStack carve(ItemStack stack, RuneCategory category);
}
