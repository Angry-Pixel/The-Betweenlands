package thebetweenlands.common.inventory.slot;

import javax.annotation.Nullable;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot {
	private Container container;

	public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition, Container container) {
		super(inventoryIn, index, xPosition, yPosition);
		this.container = container;
	}

	@Override
	public boolean isItemValid(@Nullable ItemStack stack) {
		return false;
	}
}
