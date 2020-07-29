package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.rune.RuneTier;

public interface IRune {
	public ItemStack infuse(ItemStack stack, IAspectType type, RuneTier tier);
}
