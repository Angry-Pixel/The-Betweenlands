package thebetweenlands.common.capability;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.DataComponentRegistry;

public class MortarWrapper extends SidedInvWrapper {

	public MortarWrapper(WorldlyContainer inv, @Nullable Direction side) {
		super(inv, side);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack extractedStack = super.extractItem(slot, amount, simulate);
		// Remove pestle active component
		if (getSlot(this.inv, slot, this.side) == 1 && !extractedStack.isEmpty()) {
			extractedStack.remove(DataComponentRegistry.PESTLE_ACTIVE);
		}
		return extractedStack;
	}
	
	@Override
	public int getSlotLimit(int slot) {
		// If we're the output slot and we contain an aspect vial, the max stack size is 1
		if (getSlot(this.inv, slot, this.side) == 2 && this.getStackInSlot(slot).is(BLItemTagProvider.FILLABLE_ASPECT_VIALS)) {
			return 1;
		}
		return super.getSlotLimit(slot);
	}
}
