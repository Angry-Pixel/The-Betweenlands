package thebetweenlands.common.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import thebetweenlands.common.block.entity.CenserBlockEntity;

public class CenserWrapper extends ItemStackHandler {

	private final CenserBlockEntity entity;

	public CenserWrapper(CenserBlockEntity entity) {
		super(entity.getItems());
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		// Don't mark dirty while loading chunk!
		if (this.entity.hasLevel()) {
			this.entity.setChanged();

			//Sync internal slot. Required on client side
			//for rendering stuff
			if (slot == CenserBlockEntity.INTERNAL_SLOT) {
				this.entity.internalSlotChanged = true;
			}
		}

		if (slot == CenserBlockEntity.INTERNAL_SLOT) {
			this.entity.checkInternalSlotForRecipes = true;
			this.entity.checkInputSlotForTransfer = true;
		} else if (slot == CenserBlockEntity.INPUT_SLOT) {
			this.entity.checkInputSlotForTransfer = true;
		}
	}

	//Internal slot stores at most 1 item and can't be extracted

	@Override
	public int getSlotLimit(int slot) {
		return slot == CenserBlockEntity.INTERNAL_SLOT ? 1 : super.getSlotLimit(slot);
	}

	@Override
	protected int getStackLimit(int slot, ItemStack stack) {
		return slot == CenserBlockEntity.INTERNAL_SLOT ? 1 : super.getStackLimit(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return slot == CenserBlockEntity.INTERNAL_SLOT ? stack : super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return slot == CenserBlockEntity.INTERNAL_SLOT ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
	}
}
