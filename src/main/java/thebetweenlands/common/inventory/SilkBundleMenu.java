package thebetweenlands.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.inventory.slot.SilkBundleSlot;
import thebetweenlands.common.inventory.slot.SingleItemSlot;
import thebetweenlands.common.registries.MenuRegistry;

public class SilkBundleMenu extends AbstractContainerMenu {

	public SilkBundleMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(4));
	}

	public SilkBundleMenu(int containerId, Inventory playerInventory, Container bundle) {
		super(MenuRegistry.SILK_BUNDLE.get(), containerId);
		checkContainerSize(bundle, 4);

		this.addSlot(new SilkBundleSlot(bundle, 0, 78, 15));
		this.addSlot(new SilkBundleSlot(bundle, 1, 42, 33));
		this.addSlot(new SilkBundleSlot(bundle, 2, 114, 33));
		this.addSlot(new SilkBundleSlot(bundle, 3, 78, 51));

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 7 + j1 * 18, 83 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 7 + i1 * 18, 141));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			stack = slotStack.copy();

			if (!slotStack.getItem().canFitInsideContainerItems()) {
				return ItemStack.EMPTY;
			}

			if (index > 3) {
				if (!this.moveItemStackTo(slotStack, 0, 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotStack, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
