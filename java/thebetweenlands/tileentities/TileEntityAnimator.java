package thebetweenlands.tileentities;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.inventory.ICrafting;
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

	public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.LIFE_CRYSTAL), 10), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD), 20), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 30), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR), 40) };
	public static final ArrayList<ItemStack> viable = new ArrayList<ItemStack>();

	public TileEntityAnimator() {
		super(3, "animator");
	}

	public static void addItems() {
		viable.add(new ItemStack(Items.spawn_egg));
		viable.add(new ItemStack(BLItemRegistry.scroll));
	}

	// Progress (0-100). Used for rendering
	public int progress, life = 480, lifeDepletion = 480 / 4;
	public boolean isAnimating = false, isDirty = false, lifeDepleted = false;
	public int itemsConsumed = 0, stackSize = 16;
	Random rand = new Random();

	public float crystalVelocity = 0.0F;
	public float crystalRotation = 0.0F;

	@Override
	public void updateEntity() {
		boolean canStart = true;
		if (isDirty) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			isDirty = false;
		}
		if (this.worldObj.isRemote) {
			this.crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
			this.crystalRotation += this.crystalVelocity;
			if (this.crystalRotation >= 360.0F) {
				this.crystalRotation -= 360.0F;
			} else if (this.crystalRotation <= 360.0F) {
				this.crystalRotation += 360.0F;
			}
			if (Math.abs(this.crystalVelocity) <= 1.0F && this.getWorldObj().rand.nextInt(15) == 0) {
				this.crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
			}
			if (this.isAnimating) {
				this.progress++;
				if (this.progress >= 44) {
					this.progress = 0;
					this.itemsConsumed++;
				}
			}
		} else {
			if (getStackInSlot(0) != null && getStackInSlot(1) != null && getStackInSlot(2) != null && itemsConsumed < stackSize && (getStackInSlot(0).getItem().equals(Items.spawn_egg) || getStackInSlot(0).getItem().equals(BLItemRegistry.scroll) || getStackInSlot(0).getItem().equals(BLItemRegistry.spawnEggs))) {
				++progress;
				if (this.progress >= 44) {
					this.progress = 0;
					this.decrStackSize(2, 1);
					itemsConsumed++;
					if (getStackInSlot(0) == null || getStackInSlot(1) == null || getStackInSlot(2) == null || itemsConsumed >= stackSize)
						this.stopCraftingProcess();
				}
			} else
				this.stopCraftingProcess();
			if (itemsConsumed >= stackSize && getStackInSlot(0) != null && !lifeDepleted) {
				if (getStackInSlot(0).getItem().equals(BLItemRegistry.scroll)) {
					this.setInventorySlotContents(0, ((WeightedRandomItem) WeightedRandom.getRandomItem(rand, items)).getItem(rand));
					lifeDepletion = (480 / 4);
				} else if (getStackInSlot(0).getItem() instanceof ItemMonsterPlacer)
					lifeDepletion = 480;
				else
					lifeDepletion = 480 / 10;
				life -= lifeDepletion;
				if (life <= 0) {
					decrStackSize(1, 1);
					life = 480;
				}
				lifeDepleted = true;
			}
		}
		this.updateContainingBlockInfo();
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
					if (!isAnimating || (isAnimating && itemsConsumed < stackSize))
						this.startCraftingProcess();
				}
			}
		}
	}

	private void startCraftingProcess() {
		isAnimating = true;
		progress = 0;
	}

	private void stopCraftingProcess() {
		isAnimating = false;
		progress = 0;
	}

	public void sendGUIData(ContainerAnimator animator, ICrafting craft) {
		craft.sendProgressBarUpdate(animator, 0, progress);
		craft.sendProgressBarUpdate(animator, 1, life);
	}

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			progress = value;
			break;
		case 1:
			life = value;
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
		nbt.setBoolean("lifeDepleted", lifeDepleted);
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
		lifeDepleted = nbt.getBoolean("lifeDepleted");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", life);
		nbt.setInteger("progress", progress);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		life = packet.func_148857_g().getInteger("life");
		progress = packet.func_148857_g().getInteger("progress");
	}
}
