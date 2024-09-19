package thebetweenlands.common.inventory.container;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ItemContainer implements Container {

	private final ItemStack stack;
	private final NonNullList<ItemStack> contents;

	public ItemContainer(ItemStack stack, int slots) {
		this.stack = stack;
		this.contents = NonNullList.withSize(slots, ItemStack.EMPTY);
		ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		for (int i = 0; i < slots; i++) {
			if (container.getSlots() > i) {
				this.contents.set(i, container.getStackInSlot(i));
			} else {
				this.contents.set(i, ItemStack.EMPTY);
			}
		}
	}

	public ItemStack getContainerStack() {
		return this.stack;
	}

	@Override
	public int getContainerSize() {
		return this.contents.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.contents) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.contents.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = ContainerHelper.removeItem(this.contents, slot, amount);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(this.contents, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.contents.set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		this.setChanged();
	}

	@Override
	public void setChanged() {
		this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.contents));
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.contents.clear();
	}

	@Override
	public void stopOpen(Player player) {
		this.setChanged();
	}
}
