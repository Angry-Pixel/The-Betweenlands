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

	public static int[] readIntArrayFixedSize(String id, int length, NBTTagCompound compound) {
		int[] array = compound.getIntArray(id);
		return array.length != length ? new int[length] : array;
	}

	@Override
	public void updateEntity() {
		lidAngle = open ? Math.min(lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(lidAngle - CLOSE_SPEED, MIN_OPEN);

		if (!worldObj.isRemote) {
			if(!this.open) {
				for (int i = 0; i < inventory.length; i++) {
					if (inventory[i] != null) {
						if (processes[i] >= compostTimes[i]) {
							compostedAmount += compostAmounts[i];
							inventory[i] = null;
							processes[i] = 0;
							compostTimes[i] = 0;
							compostAmounts[i] = 0;

							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
							markDirty();
						}  else{
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
	}

	public boolean removeCompost(int amount) {
		if (compostedAmount >= amount) {
			compostedAmount -= amount;
			totalCompostAmount -= amount;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

						totalCompostAmount += compostAmount;

						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

						totalCompostAmount += compostAmount;

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
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList inventoryTags = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		this.inventory = new ItemStack[inventory.length];

		for (int i = 0; i < inventoryTags.tagCount(); i++) {
			NBTTagCompound data = inventoryTags.getCompoundTagAt(i);
			int j = data.getByte("Slot") & 255;
			this.inventory[j] = ItemStack.loadItemStackFromNBT(data);
		}

		processes = readIntArrayFixedSize("Processes", inventory.length, nbt);
		compostAmounts = readIntArrayFixedSize("CompostAmounts", inventory.length, nbt);
		compostTimes = readIntArrayFixedSize("CompostTimes", inventory.length, nbt);

		totalCompostAmount = nbt.getInteger("totalCompostAmount");

		compostedAmount = nbt.getInteger("compostedAmount");
		open = nbt.getBoolean("open");
		lidAngle = nbt.getFloat("lidAngle");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
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

		nbt.setIntArray("Processes", processes);
		nbt.setIntArray("CompostAmounts", compostAmounts);
		nbt.setIntArray("CompostTimes", compostTimes);

		nbt.setInteger("totalCompostAmount", totalCompostAmount);

		nbt.setInteger("compostedAmount", compostedAmount);
		nbt.setTag("Items", inventoryTags);
		nbt.setBoolean("open", open);
		nbt.setFloat("lidAngle", lidAngle);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	public boolean hasCompostableItems() {
		for(ItemStack stack : inventory) {
			if(stack != null) return true;
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

	public int getSizeInventory() {
		return inventory.length;
	}

	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}
}
