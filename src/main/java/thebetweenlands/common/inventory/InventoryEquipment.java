package thebetweenlands.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

public class InventoryEquipment implements IInventory, ITickable {
	protected final ItemStack[] inventory;
	protected final EquipmentEntityCapability capability;

	public InventoryEquipment(EquipmentEntityCapability capability, ItemStack[] inventory) {
		this.capability = capability;
		this.inventory = inventory;
	}

	@Override
	public String getName() {
		return "container.betweenlands.equipment";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	@Nullable
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = null;
		if(index < this.getSizeInventory()) {
			stack = ItemStackHelper.getAndRemove(this.inventory, index);
			this.markDirty();
		}
		return stack;
	}

	@Override
	@Nullable
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = null;
		if(index < this.getSizeInventory()) {
			stack = ItemStackHelper.getAndSplit(this.inventory, index, count);
			this.markDirty();
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		if(index < this.getSizeInventory()) {
			this.inventory[index] = stack;
			this.markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		this.capability.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index < this.getSizeInventory()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value)  {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.inventory.length; ++i) {
			this.inventory[i] = null;
		}
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	@Override
	@Nullable
	public ItemStack getStackInSlot(int index) {
		return index >= this.getSizeInventory() ? null : this.inventory[index];
	}

	@Override
	public void update() {
		for(int i = 0; i < this.inventory.length; i++) {
			ItemStack stack = this.inventory[i];
			if(stack != null)
				stack.getItem().onUpdate(stack, this.capability.getEntity().getEntityWorld(), this.capability.getEntity(), i, false);
		}
	}
}
