package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.WeightedRandom;
import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.utils.WeightedRandomItem;

public class TileEntityAnimator extends TileEntityBasicInventory {

	public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD), 20), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 30), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR), 40) };
	private int prevStackSize = 0;
	private Item prevItem;
	public int progress, life, itemsConsumed = 0, itemCount = 32;
	public boolean lifeDepleted = false;

	public TileEntityAnimator() {
		super(3, "animator");
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		if (isCrystalInslot())
			life = 4 - getCrystalPower();
		if (isSlotInUse(0) && isCrystalInslot() && isSulfurInslot() && itemsConsumed < itemCount && isValidFocalItem()) {
			if (isFocalItemSpawnEgg() && life >= 4 || !isFocalItemSpawnEgg() && life >= 1) {
				++progress;
				if (progress >= 42) {
					progress = 0;
					decrStackSize(2, 1);
					itemsConsumed++;
					if (!isSlotInUse(0) || !isSlotInUse(1) || !isSlotInUse(2) || itemsConsumed >= itemCount)
						progress = 0;
				}
			}
		}
		if (isSlotInUse(2) && !lifeDepleted) {
			if (!isSlotInUse(0) || !isSlotInUse(1)) {
				ItemStack stack2 = inventory[2].copy();
				stack2.stackSize += itemsConsumed;
				setInventorySlotContents(2, stack2);
				progress = 0;
				itemsConsumed = 0;
			}
		}
		if (itemsConsumed >= itemCount && isSlotInUse(0) && isSlotInUse(1) && !lifeDepleted) {
			if (inventory[0].getItem().equals(BLItemRegistry.scroll)) {
				setInventorySlotContents(0, ((WeightedRandomItem) WeightedRandom.getRandomItem(worldObj.rand, items)).getItem(worldObj.rand));
				inventory[1].setItemDamage(inventory[1].getItemDamage() + 1);
			} else if (inventory[0].getItem() instanceof ItemMonsterPlacer)
				inventory[1].setItemDamage(4);
			else
				inventory[1].setItemDamage(inventory[1].getItemDamage() + 1);
			lifeDepleted = true;
		}
		if (prevStackSize != (isSlotInUse(0) ? inventory[0].stackSize : 0))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (prevItem != (isSlotInUse(0) ? inventory[0].getItem() : null))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		prevItem = isSlotInUse(0) ? inventory[0].getItem() : null;
		prevStackSize = isSlotInUse(0) ? inventory[0].stackSize : 0;
		updateContainingBlockInfo();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isCrystalInslot() {
		return isSlotInUse(1) && inventory[1].getItem() == BLItemRegistry.lifeCrystal;
	}

	public int getCrystalPower() {
		if(isCrystalInslot())
			return inventory[1].getItemDamage();
		return 0;
	}

	public boolean isSulfurInslot() {
		return isSlotInUse(2) && inventory[2].getItem() == BLItemRegistry.materialsBL && inventory[2].getItemDamage() == EnumMaterialsBL.SULFUR.ordinal();
	}

	public boolean isSlotInUse(int slot) {
		return inventory[slot] != null;
	}

	public boolean isValidFocalItem() {
		return inventory[0].getItem() instanceof ItemMonsterPlacer ? true : inventory[0].getItem().equals(BLItemRegistry.scroll) ? true : false;
	}
	
	public boolean isFocalItemSpawnEgg() {
		return inventory[0].getItem() instanceof ItemMonsterPlacer;
	}

	public void sendGUIData(ContainerAnimator animator, ICrafting craft) {
		craft.sendProgressBarUpdate(animator, 0, progress);
		craft.sendProgressBarUpdate(animator, 1, life);
		craft.sendProgressBarUpdate(animator, 2, lifeDepleted ? 1 : 0);
		craft.sendProgressBarUpdate(animator, 3, itemsConsumed);
	}

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			progress = value;
			break;
		case 1:
			life = value;
			break;
		case 2:
			if (value == 1)
				lifeDepleted = true;
			else
				lifeDepleted = false;
			break;
		case 3:
			itemsConsumed = value;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("life", life);
		nbt.setInteger("progress", progress);
		nbt.setInteger("itemsConsumed", itemsConsumed);
		nbt.setBoolean("lifeDepleted", lifeDepleted);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		life = nbt.getInteger("life");
		progress = nbt.getInteger("progress");
		itemsConsumed = nbt.getInteger("itemsConsumed");
		lifeDepleted = nbt.getBoolean("lifeDepleted");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", life);
		nbt.setInteger("progress", progress);
		nbt.setBoolean("lifeDepleted", lifeDepleted);
		if(isSlotInUse(0)) {
			NBTTagCompound itemStackCompound = inventory[0].writeToNBT(new NBTTagCompound());
			nbt.setTag("outputItem", itemStackCompound);
		} else
			nbt.setTag("outputItem", null);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		life = packet.func_148857_g().getInteger("life");
		progress = packet.func_148857_g().getInteger("progress");
		lifeDepleted = packet.func_148857_g().getBoolean("lifeDepleted");
		NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("outputItem");
		if(itemStackCompound != null && itemStackCompound.getShort("id") != 0)
			inventory[0] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		else
			inventory[0] = null;
	}
}
