package thebetweenlands.common.tile;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCompostBin extends TileEntity implements ITickable, ISidedInventory {
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
    private NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private int[] processes = new int[MAX_ITEMS];
    private int[] compostAmounts = new int[MAX_ITEMS];
    private int[] compostTimes = new int[MAX_ITEMS];


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
                    if (!this.inventory.get(i).isEmpty()) {
                        if (this.processes[i] >= this.compostTimes[i]) {
                            this.compostedAmount += this.compostAmounts[i];
                            this.inventory.set(i, ItemStack.EMPTY);
                            this.processes[i] = 0;
                            this.compostTimes[i] = 0;
                            this.compostAmounts[i] = 0;

                            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
                            this.markDirty();
                        } else {
                            this.processes[i]++;
                        }
                    }
                }
            }

            // Fall down
            for (int i = 1; i < this.inventory.size(); i++) {
                if (this.inventory.get(i - 1).isEmpty() && !this.inventory.get(i).isEmpty()) {
                    this.inventory.set(i - 1, this.inventory.get(i));
                    this.inventory.set(i, ItemStack.EMPTY);
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
                if (this.inventory.get(i).isEmpty()) {
                    if (!doSimulate) {
                        this.inventory.set(i, stack.copy());
                        this.inventory.get(i).setCount(1);
                        this.compostAmounts[i] = clampedAmount;
                        this.compostTimes[i] = compostTime;
                        this.processes[i] = 0;
                        this.totalCompostAmount += clampedAmount;

                        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
                        this.markDirty();
                    }
                    return 1;
                }
            }
            return 0;
        }
        return -1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.readNbt(nbt);
    }

    protected void readNbt(NBTTagCompound nbt) {
		NBTTagList inventoryTags = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		this.inventory = NonNullList.withSize(this.inventory.size(), ItemStack.EMPTY);
		for (int i = 0; i < inventoryTags.tagCount(); i++) {
			NBTTagCompound data = inventoryTags.getCompoundTagAt(i);
			int j = data.getByte("Slot") & 255;
			this.inventory.set(j, new ItemStack(data));
		}
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
        this.writeNbt(nbt);
        return nbt;
    }

    protected void writeNbt(NBTTagCompound nbt) {
		NBTTagList inventoryTags = new NBTTagList();
		for (int i = 0; i < this.inventory.size(); i++) {
			if (!this.inventory.get(i).isEmpty()) {
				NBTTagCompound data = new NBTTagCompound();
				data.setByte("Slot", (byte) i);
				this.inventory.get(i).writeToNBT(data);
				inventoryTags.appendTag(data);
			}
		}
        nbt.setIntArray("Processes", this.processes);
        nbt.setIntArray("CompostAmounts", this.compostAmounts);
        nbt.setIntArray("CompostTimes", this.compostTimes);
        nbt.setInteger("TotalCompostAmount", this.totalCompostAmount);
        nbt.setInteger("CompostedAmount", this.compostedAmount);
		nbt.setTag("Items", inventoryTags);
        nbt.setBoolean("Open", this.open);
        nbt.setFloat("LidAngle", this.lidAngle);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        this.writeNbt(nbt);
        return nbt;
    }

    /**
     * Returns whether there are any items in the compost bin
     *
     * @return
     */
    public boolean hasCompostableItems() {
        for (ItemStack stack : inventory) {
            if (stack != null) {
                return true;
            }
        }
        return false;
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
        return recipe != null && this.open && !itemStackIn.isEmpty() && direction == EnumFacing.UP && addItemToBin(itemStackIn, recipe.getCompostAmount(itemStackIn), recipe.getCompostingTime(itemStackIn), true) == 1;
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
            ItemStack itemStack = this.inventory.get(index).copy();
            itemStack.setCount(64); //hacky way to make hoppers not ignore stack limit set by the bin it self...
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < inventory.size() && !this.inventory.get(index).isEmpty()) {
            ItemStack itemstack;
            if (this.inventory.get(index).getCount() <= count) {
                itemstack = this.inventory.get(index);
                this.inventory.set(index, ItemStack.EMPTY);
                this.processes[index] = 0;
                this.compostTimes[index] = 0;
                this.totalCompostAmount -= this.compostAmounts[index];
                this.compostAmounts[index] = 0;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.inventory.get(index).splitStack(count);
                if (this.inventory.get(index).getCount() == 0) {
                    this.inventory.set(index, ItemStack.EMPTY);
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
            ItemStack itemstack = this.inventory.get(index);
            this.processes[index] = 0;
            this.compostTimes[index] = 0;
            this.totalCompostAmount -= this.compostAmounts[index];
            this.compostAmounts[index] = 0;
            this.inventory.set(index, ItemStack.EMPTY);
            this.markDirty();
            return itemstack;
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
                this.markDirty();
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
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        ICompostBinRecipe recipe = CompostRecipe.getCompostRecipe(stack);
        return recipe != null &&  addItemToBin(stack, recipe.getCompostAmount(stack), recipe.getCompostingTime(stack), true) == 1;
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
    public void clear() {
        for (int i = 0; i < this.inventory.size(); i++) {
            this.processes[i] = 0;
            this.compostTimes[i] = 0;
            this.compostAmounts[i] = 0;
            this.totalCompostAmount = 0;
            this.inventory.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public String getName() {
        return "compost_bin";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
