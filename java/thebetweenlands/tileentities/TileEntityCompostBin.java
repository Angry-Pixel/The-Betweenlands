package thebetweenlands.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCompostBin extends TileEntity {

    private ItemStack[] inventory = new ItemStack[10];
    private int[] processes = new int[10];
    private int[] compostAmount = new int[10];

    public int totalCompostAmount, compostedAmount, maxCompostAmount = 100;
    int processTime = 120;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        for (int i = 0; i < inventory.length; i++) {
            if(inventory[i] != null) {
                System.out.println("process: " +  processes[i]);
                System.out.println("total compost: " +  totalCompostAmount);
                if (processes[i] >= processTime) {
                    compostedAmount += compostAmount[i];
                    inventory[i] = null;
                    processes[i] = 0;
                    compostAmount[0] = 0;
                } else
                    processes[i]++;
            }
        }
    }

    public boolean removeCompost(int amount){
        if(compostedAmount >= amount){
            compostedAmount -= amount;
            totalCompostAmount -= amount;
            return true;
        }
        return false;
    }

    public boolean addItemToBin(ItemStack stack, int compostAmount, boolean doSimulate) {
        if (totalCompostAmount + compostAmount <= maxCompostAmount) {
            if(!doSimulate) {
                for (int i = 0; i < this.inventory.length; i++) {
                    if(inventory[i] == null) {
                        this.inventory[i] = stack;
                        this.compostAmount[i] = compostAmount;
                        processes[i] = 0;
                        totalCompostAmount += compostAmount;
                        return true;
                    }
                }
            }
            return true;
        } else if(totalCompostAmount <= maxCompostAmount){
            if(!doSimulate){
                compostAmount = maxCompostAmount - totalCompostAmount;
                for (int i = 0; i < this.inventory.length; i++) {
                    if(inventory[i] == null) {
                        this.inventory[i] = stack;
                        this.compostAmount[i] = compostAmount;
                        processes[i] = 0;
                        totalCompostAmount += compostAmount;
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList inventoryTags = nbt.getTagList("Items", 10);
        NBTTagList processTags = nbt.getTagList("Processes", 10);
        NBTTagList compostTags = nbt.getTagList("Compost", 10);
        this.inventory = new ItemStack[inventory.length];

        for (int i = 0; i < inventoryTags.tagCount(); i++) {
            NBTTagCompound data = inventoryTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.inventory[j] = ItemStack.loadItemStackFromNBT(data);
        }

        for (int i = 0; i < processTags.tagCount(); i++) {
            NBTTagCompound data = processTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.processes[j] = data.getInteger("processes");
        }
        for (int i = 0; i < compostTags.tagCount(); i++) {
            NBTTagCompound data = processTags.getCompoundTagAt(i);
            int j = data.getByte("Slot") & 255;
            this.processes[j] = data.getInteger("compostAmount");
        }

        totalCompostAmount = nbt.getInteger("totalCompostAmount");
        compostedAmount = nbt.getInteger("compostedAmount");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList inventoryTags = new NBTTagList();
        NBTTagList processTags = new NBTTagList();
        NBTTagList compostTags = new NBTTagList();

        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(data);
                inventoryTags.appendTag(data);
            }
        }

        for (int i = 0; i < this.processes.length; i++) {
            NBTTagCompound data = new NBTTagCompound();
            data.setByte("Slot", (byte) i);
            data.setInteger("processes", processes[i]);
            processTags.appendTag(data);
        }

        for (int i = 0; i < this.compostAmount.length; i++) {
            NBTTagCompound data = new NBTTagCompound();
            data.setByte("Slot", (byte) i);
            data.setInteger("compostAmount", compostAmount[i]);
            compostTags.appendTag(data);
        }

        nbt.setInteger("totalCompostAmount", totalCompostAmount);
        nbt.setInteger("compostedAmount", compostedAmount);
        nbt.setTag("Items", inventoryTags);
        nbt.setTag("Processes", processTags);
        nbt.setTag("Compost", compostTags);
    }
}
