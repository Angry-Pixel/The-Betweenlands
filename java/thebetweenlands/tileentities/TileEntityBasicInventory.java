package thebetweenlands.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBasicInventory
        extends TileEntity
        implements ISidedInventory
{
    protected ItemStack[] inventory;
    private final String name;

    public TileEntityBasicInventory(int invtSize, String name) {
        this.inventory = new ItemStack[invtSize];
        this.name = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList tags = nbt.getTagList("Items", 10);
        this.inventory = new ItemStack[getSizeInventory()];

        for( int i = 0; i < tags.tagCount(); i++ ) {
            NBTTagCompound data = tags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;

            if( j >= 0 && j < this.inventory.length ) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(data);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList tags = new NBTTagList();

        for( int i = 0; i < this.inventory.length; i++ ) {
            if( this.inventory[i] != null ) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(data);
                tags.appendTag(data);
            }
        }

        nbt.setTag("Items", tags);
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
    public ItemStack decrStackSize(int slot, int size) {
        try {
            if( this.inventory[slot] != null ) {
                ItemStack itemstack;
                if( this.inventory[slot].stackSize <= size ) {
                    itemstack = this.inventory[slot];
                    this.inventory[slot] = null;
                    return itemstack;
                } else {
                    itemstack = this.inventory[slot].splitStack(size);
                    if( this.inventory[slot].stackSize == 0 ) {
                        this.inventory[slot] = null;
                    }

                    return itemstack;
                }
            }

            return null;
        } finally {
            markDirty();
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        try {
            if( this.inventory[slot] != null ) {
                ItemStack itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                return itemstack;
            }

            return null;
        } finally {
            markDirty();
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;

        if( stack != null && stack.stackSize > getInventoryStackLimit() ) {
            stack.stackSize = getInventoryStackLimit();
        }

        markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.name;
    }

    @Override
    public final boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] slots = new int[getSizeInventory()];
        for( int i = 0; i < slots.length; i++ ) {
            slots[i] = i;
        }

        return slots;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }
}
