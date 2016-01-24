package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.recipes.AnimatorRecipe;

public class TileEntityAnimator extends TileEntityBasicInventory {
	//public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 20), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 30), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 40) };
	private int prevStackSize = 0;
	public ItemStack itemToAnimate = null;
	public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32;
	public boolean itemAnimated = false;
	private ItemStack prevItem;
	private ItemStack prevToAnimateItem;

	public TileEntityAnimator() {
		super(3, "animator");
	}

	@Override
	public void updateEntity() {
		if(this.itemToAnimate != null) {
			AnimatorRecipe recipe = AnimatorRecipe.getRecipe(this.itemToAnimate);
			if(recipe != null) {
				this.requiredFuelCount = recipe.requiredFuel;
			}
		}
		if(worldObj.isRemote)
			return;
		if (isCrystalInslot())
			lifeCrystalLife = 128 - getCrystalPower();
		if (isSlotInUse(0) && isCrystalInslot() && isSulfurInslot() && fuelConsumed < requiredFuelCount && isValidFocalItem()) {
			this.itemToAnimate = this.inventory[0];
			if (lifeCrystalLife >= 1) {
				++fuelBurnProgress;
				if (fuelBurnProgress >= 42) {
					fuelBurnProgress = 0;
					decrStackSize(2, 1);
					fuelConsumed++;
					if (!isSlotInUse(0) || !isSlotInUse(1) || !isSlotInUse(2) || fuelConsumed >= requiredFuelCount)
						fuelBurnProgress = 0;
				}
			}
		}
		if (isSlotInUse(2) && !this.itemAnimated) {
			if (!isSlotInUse(0) || !isSlotInUse(1)) {
				fuelBurnProgress = 0;
				fuelConsumed = 0;
			}
		}
		if (fuelConsumed >= requiredFuelCount && isSlotInUse(0) && isSlotInUse(1) && !this.itemAnimated) {
			AnimatorRecipe recipe = AnimatorRecipe.getRecipe(inventory[0]);
			ItemStack result = recipe.onAnimated(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			if(result == null) result = recipe.getResult();
			if(result != null) {
				setInventorySlotContents(0, result.copy());
			}
			inventory[1].setItemDamage(inventory[1].getItemDamage() + recipe.requiredLife);
			this.itemAnimated = true;
		}
		if (prevStackSize != (isSlotInUse(0) ? inventory[0].stackSize : 0))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (prevItem != (isSlotInUse(0) ? inventory[0] : null))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		prevItem = isSlotInUse(0) ? inventory[0] : null;
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
		return isSlotInUse(2) && inventory[2].getItem() == BLItemRegistry.itemsGeneric && inventory[2].getItemDamage() == EnumItemGeneric.SULFUR.id;
	}

	public boolean isSlotInUse(int slot) {
		return inventory[slot] != null;
	}

	public boolean isValidFocalItem() {
		return AnimatorRecipe.getRecipe(inventory[0]) != null;
	}

	public void sendGUIData(ContainerAnimator animator, ICrafting craft) {
		craft.sendProgressBarUpdate(animator, 0, fuelBurnProgress);
		craft.sendProgressBarUpdate(animator, 1, lifeCrystalLife);
		craft.sendProgressBarUpdate(animator, 2, itemAnimated ? 1 : 0);
		craft.sendProgressBarUpdate(animator, 3, fuelConsumed);
	}

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			fuelBurnProgress = value;
			break;
		case 1:
			lifeCrystalLife = value;
			break;
		case 2:
			if (value == 1)
				itemAnimated = true;
			else
				itemAnimated = false;
			break;
		case 3:
			fuelConsumed = value;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("life", lifeCrystalLife);
		nbt.setInteger("progress", fuelBurnProgress);
		nbt.setInteger("itemsConsumed", fuelConsumed);
		nbt.setBoolean("lifeDepleted", itemAnimated);
		NBTTagCompound toAnimateCompound = new NBTTagCompound();
		if(this.itemToAnimate != null) {
			this.itemToAnimate.writeToNBT(toAnimateCompound);
		}
		nbt.setTag("toAnimate", toAnimateCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		lifeCrystalLife = nbt.getInteger("life");
		fuelBurnProgress = nbt.getInteger("progress");
		fuelConsumed = nbt.getInteger("itemsConsumed");
		itemAnimated = nbt.getBoolean("lifeDepleted");
		NBTTagCompound toAnimateStackCompound = nbt.getCompoundTag("toAnimate");
		if(toAnimateStackCompound.getShort("id") != 0)
			this.itemToAnimate = ItemStack.loadItemStackFromNBT(toAnimateStackCompound);
		else
			this.itemToAnimate = null;		
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", lifeCrystalLife);
		nbt.setInteger("progress", fuelBurnProgress);
		nbt.setBoolean("lifeDepleted", itemAnimated);
		NBTTagCompound outputItemCompound = new NBTTagCompound();
		if(isSlotInUse(0)) {
			inventory[0].writeToNBT(outputItemCompound);
		}
		nbt.setTag("outputItem", outputItemCompound);
		NBTTagCompound toAnimateCompound = new NBTTagCompound();
		if(this.itemToAnimate != null) {
			this.itemToAnimate.writeToNBT(toAnimateCompound);
		}
		nbt.setTag("toAnimate", toAnimateCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		lifeCrystalLife = packet.func_148857_g().getInteger("life");
		fuelBurnProgress = packet.func_148857_g().getInteger("progress");
		itemAnimated = packet.func_148857_g().getBoolean("lifeDepleted");
		NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("outputItem");
		if(itemStackCompound.getShort("id") != 0)
			inventory[0] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		else
			inventory[0] = null;
		NBTTagCompound toAnimateStackCompound = packet.func_148857_g().getCompoundTag("toAnimate");
		if(toAnimateStackCompound.getShort("id") != 0)
			this.itemToAnimate = ItemStack.loadItemStackFromNBT(toAnimateStackCompound);
		else
			this.itemToAnimate = null;		
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if (slot == 1 && stack != null && stack.getItem().equals(BLItemRegistry.lifeCrystal))
			return true;
		else if (slot == 2 && stack != null && stack.getItem().equals(BLItemRegistry.itemsGeneric) && stack.getItemDamage() == EnumItemGeneric.SULFUR.id)
			return true;
		else if (slot == 0)
			return true;
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == 0)
			return new int[]{0};
		if (side == 1)
			return new int[]{0};
		return new int[]{1, 2};
	}
}
