package thebetweenlands.common.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SingleItemSlot extends Slot {
	public SingleItemSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
