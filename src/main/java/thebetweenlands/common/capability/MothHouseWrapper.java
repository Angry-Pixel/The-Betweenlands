package thebetweenlands.common.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import thebetweenlands.common.block.entity.MothHouseBlockEntity;

public class MothHouseWrapper extends ItemStackHandler {

	private final MothHouseBlockEntity entity;

	public MothHouseWrapper(MothHouseBlockEntity entity) {
		super(entity.getItems());
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		// Don't mark dirty while loading chunk!
		if (this.entity.hasLevel()) {
			this.entity.setChanged();
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot == MothHouseBlockEntity.SLOT_SILK) {
			ItemStack prevStack = this.getStackInSlot(slot).copy();

			super.setStackInSlot(slot, stack);

			ItemStack newStack = this.getStackInSlot(slot);

			if (newStack.getCount() < prevStack.getCount()) {
				this.entity.onSilkRemoved(prevStack.getCount() - newStack.getCount());
			}
		} else {
			super.setStackInSlot(slot, stack);
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot == MothHouseBlockEntity.SLOT_SILK && !simulate) {
			ItemStack extracted = super.extractItem(slot, amount, false);

			if (extracted.getCount() > 0) {
				this.entity.onSilkRemoved(extracted.getCount());
			}

			return extracted;
		} else {
			return super.extractItem(slot, amount, simulate);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		if (slot == MothHouseBlockEntity.SLOT_GRUBS) {
			return MothHouseBlockEntity.MAX_GRUBS;
		}
		return super.getSlotLimit(slot);
	}
}
