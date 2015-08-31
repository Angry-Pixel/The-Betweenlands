package thebetweenlands.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityCompostBin extends TileEntity
{
    public static final int COMPOST_PER_ITEM = 25;

    public static final float MAX_OPEN = 90f;
    public static final float MIN_OPEN = 0f;
    public static final float OPEN_SPEED = 10f;
    public static final float CLOSE_SPEED = 10f;

    private int maxItems = 10, maxCompostAmount = 100;

    public int compostedAmount;
    public boolean open = false;
    public float litAngle = 0.0f;
    private ItemStack[] inventory = new ItemStack[maxItems];
    private int[] processes = new int[maxItems];
    private int[] compostAmount = new int[maxItems];
    private int compostTime[] = new int[maxItems];

    @Override
    public void updateEntity()
    {
        litAngle = open ? Math.min(litAngle + OPEN_SPEED, MAX_OPEN) : Math.max(litAngle - CLOSE_SPEED, MIN_OPEN);

        if (!worldObj.isRemote)
        {
            for (int i = 0; i < inventory.length; i++)
            {
                if (inventory[i] != null)
                {
                    if (processes[i] >= compostTime[i])
                    {
                        compostedAmount += compostAmount[i];
                        inventory[i] = null;
                        processes[i] = 0;
                        compostTime[i] = 0;
                        compostAmount[0] = 0;

                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        markDirty();
                    }
                    else
                        processes[i]++;
                }
            }
        }
    }

    public boolean removeCompost(int amount)
    {
        if (compostedAmount >= amount)
        {
            compostedAmount -= amount;
            return true;
        }
        return false;
    }

    public int addItemToBin(ItemStack stack, int compostAmount, int compostTime, boolean doSimulate)
    {
        if (getTotalCompostAmount() + compostAmount <= maxCompostAmount)
        {
            for (int i = 0; i < this.inventory.length; i++)
            {
                if (inventory[i] == null)
                {
                    if (!doSimulate)
                    {
                        this.inventory[i] = stack.copy();
                        this.inventory[i].stackSize = 1;
                        this.compostAmount[i] = compostAmount;
                        this.compostTime[i] = compostTime;
                        processes[i] = 0;

                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        markDirty();
                    }
                    return 1;
                }
            }
        }
        else if (getTotalCompostAmount() < maxCompostAmount)
        {
            int newCompostAmount = maxCompostAmount - getTotalCompostingAmount();
            for (int i = 0; i < this.inventory.length; i++)
            {
                if (inventory[i] == null)
                {
                    if (!doSimulate)
                    {
                        this.inventory[i] = stack.copy();
                        this.inventory[i].stackSize = 1;
                        this.compostAmount[i] = newCompostAmount;
                        this.compostTime[i] = compostTime;
                        processes[i] = 0;

                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        NBTTagList inventoryTags = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        NBTTagList processTags = nbt.getTagList("Processes", Constants.NBT.TAG_COMPOUND);
        NBTTagList compostTags = nbt.getTagList("Compost", Constants.NBT.TAG_COMPOUND);
        NBTTagList compostTimeTags = nbt.getTagList("CompostTime", Constants.NBT.TAG_COMPOUND);
        this.inventory = new ItemStack[inventory.length];

        for (int i = 0; i < inventoryTags.tagCount(); i++)
        {
            NBTTagCompound data = inventoryTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.inventory[j] = ItemStack.loadItemStackFromNBT(data);
        }

        for (int i = 0; i < processTags.tagCount(); i++)
        {
            NBTTagCompound data = processTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.processes[j] = data.getInteger("processes");
        }
        for (int i = 0; i < compostTags.tagCount(); i++)
        {
            NBTTagCompound data = processTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.processes[j] = data.getInteger("compostAmount");
        }

        for (int i = 0; i < compostTimeTags.tagCount(); i++)
        {
            NBTTagCompound data = processTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.compostTime[j] = data.getInteger("compostTime");
        }

        compostedAmount = nbt.getInteger("compostedAmount");
        open = nbt.getBoolean("open");
        litAngle = nbt.getFloat("litAngle");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagList inventoryTags = new NBTTagList();
        NBTTagList processTags = new NBTTagList();
        NBTTagList compostTags = new NBTTagList();
        NBTTagList compostTimeTags = new NBTTagList();

        for (int i = 0; i < this.inventory.length; i++)
        {
            if (this.inventory[i] != null)
            {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(data);
                inventoryTags.appendTag(data);
            }
        }

        for (int i = 0; i < this.processes.length; i++)
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setByte("Slot", (byte) i);
            data.setInteger("processes", processes[i]);
            processTags.appendTag(data);
        }

        for (int i = 0; i < this.compostAmount.length; i++)
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setByte("Slot", (byte) i);
            data.setInteger("compostAmount", compostAmount[i]);
            compostTags.appendTag(data);
        }

        for (int i = 0; i < this.compostTime.length; i++)
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setByte("Slot", (byte) i);
            data.setInteger("compostTime", compostTime[i]);
            compostTags.appendTag(data);
        }

        nbt.setInteger("compostedAmount", compostedAmount);
        nbt.setTag("Items", inventoryTags);
        nbt.setTag("Processes", processTags);
        nbt.setTag("Compost", compostTags);
        nbt.setTag("CompostTime", compostTimeTags);
        nbt.setBoolean("open", open);
        nbt.setFloat("litAngle", litAngle);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
    }

    public float getLitAngle(float lerp)
    {
        return open ? Math.min(litAngle + OPEN_SPEED * lerp, MAX_OPEN) : Math.max(litAngle - CLOSE_SPEED * lerp, MIN_OPEN);
    }

    public int getTotalCompostAmount()
    {
        return getTotalCompostingAmount() + getTotalCompostedAmount();
    }

    public int getTotalCompostedAmount()
    {
        return compostedAmount;
    }

    public int getTotalCompostingAmount()
    {
        int c = 0;
        for (int i = 0; i < compostAmount.length; i++)
            c += compostAmount[i];
        return c;
    }

    public int getSizeInventory()
    {
        return inventory.length;
    }

    public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot];
    }
}
