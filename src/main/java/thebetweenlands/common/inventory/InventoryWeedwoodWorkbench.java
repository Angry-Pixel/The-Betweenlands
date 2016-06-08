package thebetweenlands.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class InventoryWeedwoodWorkbench extends InventoryCrafting {
    private ItemStack[] stackList;
    private Container container;
    private static final int INV_WIDTH = 3;

    public InventoryWeedwoodWorkbench(Container container, TileEntityWeedwoodWorkbench workbench) {
        super(container, 3, 3);
        this.stackList = workbench.craftingSlots;
        this.container = container;
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= this.getSizeInventory() ? null : this.stackList[slot];
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int col) {
        if (row >= 0 && row < INV_WIDTH) {
            int k = row + col * INV_WIDTH;
            return this.getStackInSlot(k);
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return "container.crafting";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.stackList[slot] != null) {
            ItemStack stack;

            if (this.stackList[slot].stackSize <= amount) {
                stack = this.stackList[slot];
                this.stackList[slot] = null;
                this.container.onCraftMatrixChanged(this);
                return stack;
            } else {
                stack = this.stackList[slot].splitStack(amount);

                if (this.stackList[slot].stackSize == 0) {
                    this.stackList[slot] = null;
                }

                this.container.onCraftMatrixChanged(this);
                return stack;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.stackList[slot] = stack;
        this.container.onCraftMatrixChanged(this);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
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
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}
