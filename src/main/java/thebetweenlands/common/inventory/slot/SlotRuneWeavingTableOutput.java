package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;

public class SlotRuneWeavingTableOutput extends Slot {
	private final TileEntityRuneWeavingTable altar;
	private final ContainerRuneWeavingTable container;

	public SlotRuneWeavingTableOutput(TileEntityRuneWeavingTable altar, int slotIndex, int x, int y, ContainerRuneWeavingTable container) {
		super(altar, slotIndex, x, y);
		this.altar = altar;
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.altar.isItemValidForSlot(this.slotNumber, stack);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		for(int i = 0; i < this.container.getRuneInventorySize(); i++) {
			this.container.setRuneItemStack(i, ItemStack.EMPTY);
		}
		return super.onTake(thePlayer, stack);
	}
}
