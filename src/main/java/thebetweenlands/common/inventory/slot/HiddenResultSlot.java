package thebetweenlands.common.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.NonInteractiveResultSlot;

/**
 * Slot that cannot be interacted with and does not render.
 * Used to inform the client about stacks that that the Screen will manually render.
 */
public class HiddenResultSlot extends NonInteractiveResultSlot {

	public HiddenResultSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}
	
	@Override
	public boolean isActive() {
		return false;
	}

}
