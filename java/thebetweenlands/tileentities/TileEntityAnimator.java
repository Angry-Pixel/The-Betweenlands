package thebetweenlands.tileentities;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Items;
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
	public static final ArrayList<ItemStack> viable = new ArrayList<ItemStack>();
	private int prevStackSize = 0;
	private Item prevItem;
	public TileEntityAnimator() {
		super(3, "animator");
	}

	public static void addItems() {
		viable.add(new ItemStack(Items.spawn_egg));
		viable.add(new ItemStack(BLItemRegistry.scroll));
	}

	// Progress (0-100). Used for rendering
	public int progress, life;
	public boolean isAnimating = false;
	public int itemsConsumed = 0, itemCount = 4;
	Random rand = new Random();

	public float crystalVelocity = 0.0F;
	public float crystalRotation = 0.0F;
	public int lifeDepleted = 0;

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			crystalVelocity -= Math.signum(crystalVelocity) * 0.05F;
			crystalRotation += crystalVelocity;
			if (crystalRotation >= 360.0F)
				crystalRotation -= 360.0F;
			else if (crystalRotation <= 360.0F)
				crystalRotation += 360.0F;

			if (Math.abs(crystalVelocity) <= 1.0F && getWorldObj().rand.nextInt(15) == 0)
				crystalVelocity = worldObj.rand.nextFloat() * 18.0F - 9.0F;

			if (getStackInSlot(1) != null && getStackInSlot(1).getItem() == BLItemRegistry.lifeCrystal)
				life = 4 - getStackInSlot(1).getItemDamage();

			if (isAnimating) {
				progress++;
				if (progress >= 44) {
					progress = 0;
					itemsConsumed++;
				}
			}
		} else {
			if (getStackInSlot(0) != null && getStackInSlot(1) != null && getStackInSlot(2) != null && itemsConsumed < itemCount && (getStackInSlot(0).getItem().equals(Items.spawn_egg) || getStackInSlot(0).getItem().equals(BLItemRegistry.scroll) || getStackInSlot(0).getItem().equals(BLItemRegistry.spawnEggs))) {
				++progress;
				if (progress >= 44) {
					progress = 0;
					decrStackSize(2, 1);
					itemsConsumed++;
					if (getStackInSlot(0) == null || getStackInSlot(1) == null || getStackInSlot(2) == null || itemsConsumed >= itemCount)
						stopCraftingProcess();
				}
			}
			if (isAnimating && getStackInSlot(2) != null && lifeDepleted == 0) {
				if (getStackInSlot(0) == null || getStackInSlot(1) == null) {
					ItemStack stack2 = getStackInSlot(2).copy();
					stack2.stackSize += itemsConsumed;
					setInventorySlotContents(2, stack2);
					stopCraftingProcess();
					itemsConsumed = 0;
				}
			}
			if (itemsConsumed >= itemCount && getStackInSlot(0) != null  && getStackInSlot(1) != null && lifeDepleted == 0) {
				if (getStackInSlot(0).getItem().equals(BLItemRegistry.scroll)) {
					setInventorySlotContents(0, ((WeightedRandomItem) WeightedRandom.getRandomItem(rand, items)).getItem(rand));
					getStackInSlot(1).setItemDamage(getStackInSlot(1).getItemDamage() + 1);
				} else if (getStackInSlot(0).getItem() instanceof ItemMonsterPlacer)
					getStackInSlot(1).setItemDamage(4);
				else
					getStackInSlot(1).setItemDamage(getStackInSlot(1).getItemDamage() + 1);

				if (getStackInSlot(1).getItemDamage() >= 4)
					decrStackSize(1, 1);

				lifeDepleted = 1;
			}
		}

		if (prevStackSize != (inventory[0] != null ? inventory[0].stackSize : 0))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		if (prevItem != (inventory[0] != null ? inventory[0].getItem() : null))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		prevItem = inventory[0] != null ? inventory[0].getItem() : null;
		prevStackSize = inventory[0] != null ? inventory[0].stackSize : 0;
		updateContainingBlockInfo();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		inventory[slot] = is;
		if (is != null && is.stackSize > getInventoryStackLimit()) {
			is.stackSize = getInventoryStackLimit();
		}
		if (is != null) {
			if (inventory[0] != null && inventory[1] != null && inventory[2] != null) {
				if (!worldObj.isRemote) {
					if (!isAnimating || (isAnimating && itemsConsumed < itemCount))
						startCraftingProcess();
				}
			}
		}
	}

	private void startCraftingProcess() {
		isAnimating = true;
		progress = 0;
		if(lifeDepleted == 1)
			lifeDepleted = 0;
	}

	private void stopCraftingProcess() {
		isAnimating = false;
		progress = 0;
	//	System.out.println("Crafting Process Stopped");
	}

	public void sendGUIData(ContainerAnimator animator, ICrafting craft) {
		craft.sendProgressBarUpdate(animator, 0, progress);
		craft.sendProgressBarUpdate(animator, 1, life);
		craft.sendProgressBarUpdate(animator, 2, lifeDepleted);
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
			lifeDepleted = value;
			break;
		}
	}

	/**
	 * @param nbt
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("life", life);
		nbt.setInteger("progress", progress);
		nbt.setInteger("itemsConsumed", itemsConsumed);
		nbt.setInteger("lifeDepleted", lifeDepleted);
	}

	/**
	 * @param nbt
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		life = nbt.getInteger("life");
		progress = nbt.getInteger("progress");
		itemsConsumed = nbt.getInteger("itemsConsumed");
		lifeDepleted = nbt.getInteger("lifeDepleted");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", life);
		nbt.setInteger("progress", progress);
		nbt.setInteger("lifeDepleted", lifeDepleted);
		if(inventory[0] != null) {
			NBTTagCompound itemStackCompound = inventory[0].writeToNBT(new NBTTagCompound());
			nbt.setTag("outputItem", itemStackCompound);
		} else {
			nbt.setTag("outputItem", null);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		life = packet.func_148857_g().getInteger("life");
		progress = packet.func_148857_g().getInteger("progress");
		lifeDepleted = packet.func_148857_g().getInteger("lifeDepleted");
		NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("outputItem");
		if(itemStackCompound != null && itemStackCompound.getShort("id") != 0) {
			inventory[0] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		} else {
			inventory[0] = null;
		}
	}
}
