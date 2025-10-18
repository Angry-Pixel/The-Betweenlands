package thebetweenlands.common.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import thebetweenlands.common.block.entity.GrubHubBlockEntity;
import thebetweenlands.common.block.entity.MothHouseBlockEntity;

public class GrubHubWrapper extends ItemStackHandler {

	public GrubHubWrapper(GrubHubBlockEntity entity) {
		super(entity.getItems());
	}

	//dont allow inserting items
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
