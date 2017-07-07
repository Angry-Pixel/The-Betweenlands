package thebetweenlands.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;

public class InventoryItem implements IInventory {
	private String name = "";
	private final ItemStack invItem;
	private ItemStack[] inventory;

	public InventoryItem(ItemStack stack, int inventorySize, String inventoryName) {
		this.invItem = stack;
		this.inventory = new ItemStack[inventorySize];
		this.name = inventoryName;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		this.readFromNBT(stack.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null) {
			if(stack.getCount() > amount) {
				stack = stack.splitStack(amount);
				this.markDirty();
			} else {
				this.setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (this.getStackInSlot(i) != null && this.getStackInSlot(i).getCount() == 0) {
				this.inventory[i] = null;		
			}
		}
		this.writeToNBT(this.invItem.getTagCompound());
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !(itemstack.getItem() instanceof ItemLurkerSkinPouch);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < this.getSizeInventory()) {
				this.inventory[slot] = new ItemStack(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound) {
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (this.getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}

		tagcompound.setTag("ItemInventory", items);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return this.name.length() > 0;
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = this.getStackInSlot(slot);
		this.setInventorySlotContents(slot, null);
		return stack;
	}

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.getSizeInventory(); i++) {
			this.setInventorySlotContents(i, null);
		}
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventory) {
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}
}
