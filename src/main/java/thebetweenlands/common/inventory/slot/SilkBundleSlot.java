package thebetweenlands.common.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SilkBundleSlot extends Slot {
	public SilkBundleSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}
}
