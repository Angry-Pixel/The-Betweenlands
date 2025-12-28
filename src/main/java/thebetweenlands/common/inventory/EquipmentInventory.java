package thebetweenlands.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.api.event.EquipmentChangedEvent;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.Collections;

public class EquipmentInventory implements Container, Nameable {

	protected final NonNullList<ItemStack> inventory;
	protected final NonNullList<ItemStack> prevTickStacks;
	protected final Entity entity;
	private int lastChangeCheck = 0;

	public EquipmentInventory(Entity entity, NonNullList<ItemStack> inventory) {
		this.entity = entity;
		this.inventory = inventory;
		this.prevTickStacks = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack stack = this.inventory.get(i);
			if (!stack.isEmpty())
				this.prevTickStacks.set(i, stack.copy());
		}
	}

	@Override
	public Component getName() {
		return Component.translatable("container.thebetweenlands.equipment");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = ItemStack.EMPTY;
		if (index < this.getContainerSize()) {
			stack = ContainerHelper.takeItem(inventory, index);
			this.setChanged();
		}
		return stack;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = ItemStack.EMPTY;
		if (index < this.getContainerSize()) {
			stack = ContainerHelper.removeItem(this.inventory, index, count);
			this.setChanged();
		}
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index < this.getContainerSize()) {
			this.inventory.set(index, stack);
			this.setChanged();
		}
	}

	@Override
	public void setChanged() {
		this.entity.syncData(AttachmentRegistry.EQUIPMENT);
		NeoForge.EVENT_BUS.post(new EquipmentChangedEvent(this.entity, this.entity.getData(AttachmentRegistry.EQUIPMENT)));
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}


	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return index < this.getContainerSize();
	}

	@Override
	public void clearContent() {
		Collections.fill(this.inventory, ItemStack.EMPTY);
		this.setChanged();
	}

	@Override
	public int getContainerSize() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return index >= this.getContainerSize() ? ItemStack.EMPTY : this.inventory.get(index);
	}

	public void tick() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable equippable) {
				equippable.onEquipmentTick(stack, this.entity, this);
			}
		}

		if(this.lastChangeCheck++ > 10) {
			this.detectChangesAndMarkDirty();
			this.lastChangeCheck = 0;
		}
	}

	protected void detectChangesAndMarkDirty() {
		for (int i = 0; i < this.inventory.size(); ++i) {
			ItemStack stack = this.inventory.get(i);
			ItemStack prevStack = this.prevTickStacks.get(i);

			if (!ItemStack.isSameItemSameComponents(prevStack, stack)) {
				this.prevTickStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
				this.setChanged();
			}
		}
	}
}
