package thebetweenlands.items.equipment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ItemBasicInventory implements IInventory {
	private String name = "";
	private final ItemStack invItem;
	private ItemStack[] inventory;

	public ItemBasicInventory(ItemStack stack, int inventorySize, String inventoryName) {
		invItem = stack;
		inventory = new ItemStack[inventorySize];
		name = inventoryName;
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		readFromNBT(stack.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null) {
			if(stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			}
			else
				setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return name.length() > 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); ++i)
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0)
				inventory[i] = null;		
		writeToNBT(invItem.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !(itemstack.getItem() instanceof ItemLurkerSkinPouchSmall) || !(itemstack.getItem() instanceof ItemLurkerSkinPouchLarge);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound) {
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		tagcompound.setTag("ItemInventory", items);
	}

}
