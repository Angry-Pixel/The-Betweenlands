package thebetweenlands.common.tile;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCompostBin extends TileEntityBasicInventory implements ITickable {
    public static final int COMPOST_PER_ITEM = 25;
    public static final int MAX_COMPOST_AMOUNT = COMPOST_PER_ITEM * 16;
    public static final int MAX_ITEMS = 20;

    public static final float MAX_OPEN = 90f;
    public static final float MIN_OPEN = 0f;
    public static final float OPEN_SPEED = 10f;
    public static final float CLOSE_SPEED = 10f;

    private int compostedAmount;
    private int totalCompostAmount;
    private boolean open = false;
    private float lidAngle = 0.0f;
    private int[] processes = new int[MAX_ITEMS];
    private int[] compostAmounts = new int[MAX_ITEMS];
    private int[] compostTimes = new int[MAX_ITEMS];

    public TileEntityCompostBin() {
        super("container.bl.compost_bin", NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY), (te, inv) -> new ItemStackHandler(inv) {
            @Override
            public void setSize(int size) {
                this.stacks = te.inventory = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
            }

            @Override
            protected void onContentsChanged(int slot) {
                // Don't mark dirty while loading chunk!
                if(te.hasWorld()) {
                    te.markDirty();
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        });
    }


    public static int[] readIntArrayFixedSize(String id, int length, NBTTagCompound compound) {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    /**
     * Sets whether the lid is closed
     *
     * @return
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Sets whether the lid is open
     *
     * @param open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void update() {
        this.lidAngle = this.open ? Math.min(this.lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(this.lidAngle - CLOSE_SPEED, MIN_OPEN);

        if (!this.world.isRemote) {
            if (!this.open) {
                for (int i = 0; i < this.inventory.size(); i++) {
                    if (!this.getStackInSlot(i).isEmpty()) {
                        if (this.processes[i] >= this.compostTimes[i]) {
                            this.compostedAmount += this.compostAmounts[i];
                            super.setInventorySlotContents(i, ItemStack.EMPTY);
                            this.processes[i] = 0;
                            this.compostTimes[i] = 0;
                            this.compostAmounts[i] = 0;

                            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
                        } else {
                            this.processes[i]++;
                        }
                    }
                }
            }

            // Fall down
            for (int i = 1; i < this.inventory.size(); i++) {
                if (this.getStackInSlot(i - 1).isEmpty() && !this.getStackInSlot(i).isEmpty()) {
                    this.setInventorySlotContents(i - 1, getStackInSlot(i));
                    this.setInventorySlotContents(i, ItemStack.EMPTY);
                    this.processes[i - 1] = this.processes[i];
                    this.processes[i] = 0;
                    this.compostAmounts[i - 1] = this.compostAmounts[i];
                    this.compostAmounts[i] = 0;
                    this.compostTimes[i - 1] = this.compostTimes[i];
                    this.compostTimes[i] = 0;
                }
            }
        }
    }

    /**
     * Removes the specified amount of compost and returns true if successful
     *
     * @param amount
     * @return
     */
    public boolean removeCompost(int amount) {
        if (this.compostedAmount != 0) {
            if (this.compostedAmount >= amount) {
                this.compostedAmount -= amount;
                this.totalCompostAmount -= amount;
            } else {
                this.compostedAmount = 0;
                this.totalCompostAmount = 0;
            }
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
            this.markDirty();
            return true;
        }
        return false;
    }

    /**
     * Adds an item to the compost bin
     *
     * @param stack
     * @param compostAmount
     * @param compostTime
     * @param doSimulate
     * @return
     */
    public int addItemToBin(ItemStack stack, int compostAmount, int compostTime, boolean doSimulate) {
        int clampedAmount = this.getTotalCompostAmount() + compostAmount <= MAX_COMPOST_AMOUNT ? compostAmount : MAX_COMPOST_AMOUNT - this.getTotalCompostAmount();
        if (clampedAmount > 0) {
            for (int i = 0; i < this.inventory.size(); i++) {
                if (this.getStackInSlot(i).isEmpty()) {
                    if (!doSimulate) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        super.setInventorySlotContents(i, copy);
                        this.compostAmounts[i] = clampedAmount;
                        this.compostTimes[i] = compostTime;
                        this.processes[i] = 0;
                        this.totalCompostAmount += clampedAmount;

                        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
                    }
                    return 1;
                }
            }
            return 0;
        }
        return -1;
    }

    private boolean canAddItemToBin(int compostAmount, int index) {
        return this.inventory.get(index).isEmpty() && (this.getTotalCompostAmount() + compostAmount <= MAX_COMPOST_AMOUNT ? compostAmount : MAX_COMPOST_AMOUNT - this.getTotalCompostAmount()) > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("Items", Constants.NBT.TAG_INT_ARRAY)) { // To update from the old keys
            nbt.setIntArray("Inventory", nbt.getIntArray("Items"));
        }
        super.readFromNBT(nbt);
        this.processes = readIntArrayFixedSize("Processes", inventory.size(), nbt);
        this.compostAmounts = readIntArrayFixedSize("CompostAmounts", inventory.size(), nbt);
        this.compostTimes = readIntArrayFixedSize("CompostTimes", inventory.size(), nbt);
        this.totalCompostAmount = nbt.getInteger("TotalCompostAmount");
        this.compostedAmount = nbt.getInteger("CompostedAmount");
        this.open = nbt.getBoolean("Open");
        this.lidAngle = nbt.getFloat("LidAngle");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        nbt.setIntArray("Processes", this.processes);
        nbt.setIntArray("CompostAmounts", this.compostAmounts);
        nbt.setIntArray("CompostTimes", this.compostTimes);
        nbt.setInteger("TotalCompostAmount", this.totalCompostAmount);
        nbt.setInteger("CompostedAmount", this.compostedAmount);
        nbt.setBoolean("Open", this.open);
        nbt.setFloat("LidAngle", this.lidAngle);
        return nbt;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    /**
     * Returns the lid angle
     *
     * @param partialTicks
     * @return
     */
    public float getLidAngle(float partialTicks) {
        return this.open ? Math.min(this.lidAngle + OPEN_SPEED * partialTicks, MAX_OPEN) : Math.max(this.lidAngle - CLOSE_SPEED * partialTicks, MIN_OPEN);
    }

    /**
     * Returns the total compost at the end of the process
     *
     * @return
     */
    public int getTotalCompostAmount() {
        return this.totalCompostAmount;
    }

    /**
     * Returns the current total amount of compost
     *
     * @return
     */
    public int getCompostedAmount() {
        return this.compostedAmount;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP) {
            int[] slots = new int[MAX_ITEMS];
            for (int i = 0; i < MAX_ITEMS; i++)
                slots[i] = i;
            return slots;
        } else if (side == EnumFacing.DOWN) {
            return new int[]{this.inventory.size() + 1};
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, EnumFacing direction) {
        ICompostBinRecipe recipe = CompostRecipe.getCompostRecipe(itemStackIn);
        return recipe != null && this.open && !itemStackIn.isEmpty() && direction == EnumFacing.UP && canAddItemToBin(recipe.getCompostAmount(itemStackIn), index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return this.open && EnumItemMisc.COMPOST.isItemOf(stack) && direction == EnumFacing.DOWN;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : this.inventory) {
        	if(!stack.isEmpty()) {
        		return false;
        	}
        }
        return true;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < getSizeInventory()) {
            return super.getStackInSlot(index);
        }
        return ItemStack.EMPTY;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < inventory.size() && !this.getStackInSlot(index).isEmpty()) {
            ItemStack itemstack;
            if (this.getStackInSlot(index).getCount() <= count) {
                this.processes[index] = 0;
                this.compostTimes[index] = 0;
                this.totalCompostAmount -= this.compostAmounts[index];
                this.compostAmounts[index] = 0;
                return super.decrStackSize(index, count);
            } else {
                itemstack = this.getStackInSlot(index).splitStack(count);
                if (this.getStackInSlot(index).getCount() == 0) {
                    this.setInventorySlotContents(index, ItemStack.EMPTY);
                }
                this.markDirty();
                return itemstack;
            }
        }

        return ItemStack.EMPTY;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.inventory.get(index).isEmpty()) {
            this.processes[index] = 0;
            this.compostTimes[index] = 0;
            this.totalCompostAmount -= this.compostAmounts[index];
            this.compostAmounts[index] = 0;
            return super.removeStackFromSlot(index);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index < MAX_ITEMS) {
            ICompostBinRecipe recipe = CompostRecipe.getCompostRecipe(stack);
            if (recipe != null) {
                this.addItemToBin(stack, recipe.getCompostAmount(stack), recipe.getCompostingTime(stack), false);
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        ICompostBinRecipe recipe = CompostRecipe.getCompostRecipe(stack);
        return recipe != null && canAddItemToBin(recipe.getCompostAmount(stack), index);
    }

}
