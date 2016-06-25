package thebetweenlands.common.tile;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class TileEntityCompostBin extends TileEntity implements ITickable, ISidedInventory {
    public static final int MAX_COMPOST_AMOUNT = 200;
    public static final int COMPOST_PER_ITEM = 25;
    public static final int MAX_ITEMS = 20;

    public static final float MAX_OPEN = 90f;
    public static final float MIN_OPEN = 0f;
    public static final float OPEN_SPEED = 10f;
    public static final float CLOSE_SPEED = 10f;

    public int compostedAmount;
    public int totalCompostAmount;
    public boolean open = false;
    public float lidAngle = 0.0f;
    private ItemStack[] inventory = new ItemStack[MAX_ITEMS];
    private int[] processes = new int[MAX_ITEMS];
    private int[] compostAmounts = new int[MAX_ITEMS];
    private int[] compostTimes = new int[MAX_ITEMS];

    private ItemStack output = null;

    public static int[] readIntArrayFixedSize(String id, int length, NBTTagCompound compound) {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    @Override
    public void update() {
        lidAngle = open ? Math.min(lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(lidAngle - CLOSE_SPEED, MIN_OPEN);

        if (!worldObj.isRemote) {
            if (!this.open) {
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] != null) {
                        if (processes[i] >= compostTimes[i]) {
                            compostedAmount += compostAmounts[i];
                            inventory[i] = null;
                            processes[i] = 0;
                            compostTimes[i] = 0;
                            compostAmounts[i] = 0;

                            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
                            markDirty();
                        } else {
                            processes[i]++;
                        }
                    }
                }
            }

            // Fall down
            for (int i = 1; i < inventory.length; i++) {
                if (inventory[i - 1] == null && inventory[i] != null) {
                    inventory[i - 1] = inventory[i];
                    inventory[i] = null;
                    processes[i - 1] = processes[i];
                    processes[i] = 0;
                    compostAmounts[i - 1] = compostAmounts[i];
                    compostAmounts[i] = 0;
                    compostTimes[i - 1] = compostTimes[i];
                    compostTimes[i] = 0;
                }
            }
        }

        if (compostedAmount >= COMPOST_PER_ITEM) {
            if (output == null)
                output = ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.COMPOST);
            else
                output.stackSize++;
        }
        totalCompostAmount = 0;
        for (int i : compostAmounts)
            totalCompostAmount += i;
        totalCompostAmount += compostedAmount + (output != null ? output.stackSize * COMPOST_PER_ITEM : 0);
    }

    public boolean removeCompost(int amount) {
        if (compostedAmount != 0) {
            if (compostedAmount >= amount) {
                compostedAmount -= amount;
                totalCompostAmount -= amount;
            } else {
                compostedAmount = 0;
                totalCompostAmount = 0;
            }
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
            markDirty();
            return true;
        }
        return false;
    }

    public int addItemToBin(ItemStack stack, int compostAmount, int compostTime, boolean doSimulate) {
        if (getTotalCompostAmount() + compostAmount <= MAX_COMPOST_AMOUNT) {
            for (int i = 0; i < this.inventory.length; i++) {
                if (inventory[i] == null) {
                    if (!doSimulate) {
                        this.inventory[i] = stack.copy();
                        this.inventory[i].stackSize = 1;
                        this.compostAmounts[i] = compostAmount;
                        this.compostTimes[i] = compostTime;
                        processes[i] = 0;


                        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
                        markDirty();
                    }
                    return 1;
                }
            }
            return 0;
        } else if (getTotalCompostAmount() < MAX_COMPOST_AMOUNT) {
            int newCompostAmount = MAX_COMPOST_AMOUNT - totalCompostAmount;
            for (int i = 0; i < this.inventory.length; i++) {
                if (inventory[i] == null) {
                    if (!doSimulate) {
                        this.inventory[i] = stack.copy();
                        this.inventory[i].stackSize = 1;
                        this.compostAmounts[i] = newCompostAmount;
                        this.compostTimes[i] = compostTime;
                        processes[i] = 0;


                        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
                        markDirty();
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
        NBTTagList inventoryTags = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        this.inventory = new ItemStack[inventory.length];

        for (int i = 0; i < inventoryTags.tagCount(); i++) {
            NBTTagCompound data = inventoryTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.inventory[j] = ItemStack.loadItemStackFromNBT(data);
        }

        if (nbt.hasKey("output"))
            output = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt.getTag("output"));
        processes = readIntArrayFixedSize("Processes", inventory.length, nbt);
        compostAmounts = readIntArrayFixedSize("CompostAmounts", inventory.length, nbt);
        compostTimes = readIntArrayFixedSize("CompostTimes", inventory.length, nbt);

        totalCompostAmount = nbt.getInteger("totalCompostAmount");

        compostedAmount = nbt.getInteger("compostedAmount");
        open = nbt.getBoolean("open");
        lidAngle = nbt.getFloat("lidAngle");

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList inventoryTags = new NBTTagList();

        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(data);
                inventoryTags.appendTag(data);
            }
        }
        if (output != null) {
            NBTTagCompound data = new NBTTagCompound();
            output.writeToNBT(data);
            nbt.setTag("output", data);
        }

        nbt.setIntArray("Processes", processes);
        nbt.setIntArray("CompostAmounts", compostAmounts);
        nbt.setIntArray("CompostTimes", compostTimes);

        nbt.setInteger("totalCompostAmount", totalCompostAmount);

        nbt.setInteger("compostedAmount", compostedAmount);
        nbt.setTag("Items", inventoryTags);
        nbt.setBoolean("open", open);
        nbt.setFloat("lidAngle", lidAngle);
        return nbt;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public boolean hasCompostableItems() {
        for (ItemStack stack : inventory) {
            if (stack != null) return true;
        }
        return false;
    }

    public float getLidAngle(float lerp) {
        return open ? Math.min(lidAngle + OPEN_SPEED * lerp, MAX_OPEN) : Math.max(lidAngle - CLOSE_SPEED * lerp, MIN_OPEN);
    }

    public int getTotalCompostAmount() {
        return totalCompostAmount;
    }

    public int getTotalCompostedAmount() {
        return compostedAmount;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP) {
            int[] slots = new int[20];
            for (int i = 0; i < inventory.length; i++)
                slots[i] = i;
            return slots;
        } else if (side == EnumFacing.DOWN) {
            return new int[]{inventory.length + 1};
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        CompostRecipe recipe = CompostRecipe.getCompostRecipe(itemStackIn);
        return recipe != null && open && inventory[index] != null && itemStackIn != null && direction == EnumFacing.UP && addItemToBin(itemStackIn, recipe.compostAmount, recipe.compostTime, true) == 1;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return open && stack.getItem() == ItemRegistry.ITEMS_GENERIC && stack.getItemDamage() == ItemGeneric.EnumItemGeneric.COMPOST.ordinal() && direction == EnumFacing.DOWN;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < getSizeInventory())
            return inventory[index];
        return null;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        try {
            if (index < inventory.length && this.inventory[index] != null) {
                ItemStack itemstack;
                if (this.inventory[index].stackSize <= count) {
                    itemstack = this.inventory[index];
                    this.inventory[index] = null;
                    processes[index] = 0;
                    compostTimes[index] = 0;
                    compostAmounts[index] = 0;
                    return itemstack;
                } else {
                    itemstack = this.inventory[index].splitStack(count);
                    if (this.inventory[index].stackSize == 0) {
                        this.inventory[index] = null;
                    }

                    return itemstack;
                }
            } else if (output != null) {
                ItemStack itemstack;
                if (output.stackSize <= count) {
                    itemstack = this.output;
                    this.output = null;
                    return itemstack;
                } else {
                    itemstack = this.output.splitStack(count);
                    if (this.output.stackSize == 0) {
                        this.output = null;
                    }

                    return itemstack;
                }
            }

            return null;
        } finally {
            markDirty();
        }
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index > inventory.length)
            if (output != null) {
                ItemStack itemstack = this.output;
                this.output = null;
                return itemstack;
            } else
                return null;
        if (this.inventory[index] != null) {
            ItemStack itemstack = this.inventory[index];
            processes[index] = 0;
            compostTimes[index] = 0;
            compostAmounts[index] = 0;
            this.inventory[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index < MAX_ITEMS) {
            CompostRecipe recipe = CompostRecipe.getCompostRecipe(stack);
            if (recipe != null)
                addItemToBin(stack, recipe.compostAmount, recipe.compostTime, false);
        }

        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
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
        CompostRecipe recipe = CompostRecipe.getCompostRecipe(stack);
        return recipe != null && addItemToBin(stack, recipe.compostAmount, recipe.compostAmount, true) == 1;
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
        for (int i = 0; i < inventory.length; i++) {
            processes[i] = 0;
            compostTimes[i] = 0;
            compostAmounts[i] = 0;
            this.inventory[i] = null;
        }
        output = null;
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
