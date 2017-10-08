package thebetweenlands.common.tile;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileEntityBasicInventory extends TileEntity implements ISidedInventory {
	private final String name;
	protected NonNullList<ItemStack> inventory;
	protected final ItemStackHandler inventoryHandler;

	public TileEntityBasicInventory(int invtSize, String name) {
		this.inventoryHandler = new ItemStackHandler(this.inventory = NonNullList.<ItemStack>withSize(invtSize, ItemStack.EMPTY)) {
			@Override
			public void setSize(int size) {
				this.stacks = TileEntityBasicInventory.this.inventory = NonNullList.<ItemStack>withSize(invtSize, ItemStack.EMPTY);
			}

			@Override
			protected void onContentsChanged(int slot) {
				TileEntityBasicInventory.this.markDirty();
			}

			@Override
			public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
				TileEntityBasicInventory.this.accessSlot(slot);
				super.setStackInSlot(slot, stack);
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				TileEntityBasicInventory.this.accessSlot(slot);
				return super.getStackInSlot(slot);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				TileEntityBasicInventory.this.accessSlot(slot);
				return super.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				TileEntityBasicInventory.this.accessSlot(slot);
				return super.extractItem(slot, amount, simulate);
			}
		};
		this.name = name;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readInventoryNBT(nbt);
	}

	/**
	 * Reads the inventory from NBT
	 * @param nbt
	 */
	protected void readInventoryNBT(NBTTagCompound nbt) {
		this.clear();
		if(nbt.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
			this.inventoryHandler.deserializeNBT(nbt.getCompoundTag("Inventory"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	/**
	 * Writes the inventory to NBT
	 * @param nbt
	 */
	protected void writeInventoryNBT(NBTTagCompound nbt) {
		NBTTagCompound inventoryNbt = this.inventoryHandler.serializeNBT();
		nbt.setTag("Inventory", inventoryNbt);
	}

	@Override
	public int getSizeInventory() {
		return this.inventoryHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	@MethodsReturnNonnullByDefault
	public ItemStack getStackInSlot(int slot) {
		this.accessSlot(slot);
		return this.inventoryHandler.getStackInSlot(slot);
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[getSizeInventory()];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}

		return slots;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		this.accessSlot(slot);
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		this.accessSlot(slot);
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		this.accessSlot(index);
		return this.inventoryHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		this.accessSlot(index);
		return this.inventoryHandler.extractItem(index, !this.inventoryHandler.getStackInSlot(index).isEmpty() ? this.inventoryHandler.getStackInSlot(index).getCount() : 0, false);
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		this.accessSlot(index);
		this.inventoryHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.inventoryHandler.getSlots(); i++) {
			this.inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	/**
	 * Called before a slot is accessed
	 * @param slot
	 */
	protected void accessSlot(int slot) {

	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) this.inventoryHandler;
		}
		return super.getCapability(capability, facing);
	}
}
