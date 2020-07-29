package thebetweenlands.api.item;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.rune.RuneCategory;
import thebetweenlands.api.rune.RuneTier;

public interface IRuneItem {
	public RuneCategory getRuneCategory(ItemStack stack);
	
	public default int getInfusionCost(ItemStack stack, IAspectType type, RuneTier tier) {
		return 100;
	}

	public ItemStack infuse(ItemStack stack, IAspectType type, RuneTier tier);
}
