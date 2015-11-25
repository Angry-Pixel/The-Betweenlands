package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import thebetweenlands.inventory.container.ContainerPestleAndMortar;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.recipes.PestleAndMortarRecipe;

public class TileEntityPestleAndMortar extends TileEntityBasicInventory { 

	public int progress;
	public boolean hasPestle;
	public boolean hasCrystal;
	public boolean manualGrinding = false;
	public float crystalVelocity;
	public float crystalRotation;
	public int itemBob;
	public boolean countUp = true;

	public TileEntityPestleAndMortar() {
		super(4, "pestleAndMortar");
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (hasCrystal) {
				crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
				crystalRotation += this.crystalVelocity;
				if (crystalRotation >= 360.0F)
					crystalRotation -= 360.0F;
				else if (this.crystalRotation <= 360.0F)
					this.crystalRotation += 360.0F;
				if (Math.abs(crystalVelocity) <= 1.0F && this.getWorldObj().rand.nextInt(15) == 0)
					crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
				if(countUp && itemBob <= 20) {
					itemBob++;
					if(itemBob == 20)
						countUp = false;
				}
				if(!countUp && itemBob >= 0) {
					itemBob--;
					if(itemBob == 0)
						countUp = true;
				}
			}
			return;
		}
		ItemStack output = PestleAndMortarRecipe.getOutput(inventory[0]);
		if (pestleInstalled() && !outputIsFull()) {
			if(isCrystalInstalled() && getStackInSlot(3).getItemDamage() < getStackInSlot(3).getMaxDamage() || manualGrinding) {
				if (output != null && inventory[2] == null || output != null && inventory[2] != null && inventory[2].isItemEqual(output)) {
					progress++;
					if (progress == 1)
						worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thebetweenlands:grind", 1F, 1F);
					if (progress == 64 || progress == 84) {
						worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "dig.grass", 0.5F, 1F);
						worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "dig.stone", 1F, 1F);
					}
					if (inventory[1] != null && !getStackInSlot(1).getTagCompound().getBoolean("active"))
						getStackInSlot(1).getTagCompound().setBoolean("active", true);
					if (progress > 84) {
						if (inventory[0] != null)
							if (--inventory[0].stackSize <= 0)
								inventory[0] = null;
						if (inventory[2] == null)
							inventory[2] = output.copy();
						else if (inventory[2].isItemEqual(output))
							inventory[2].stackSize += output.stackSize;
						inventory[1].setItemDamage(inventory[1].getItemDamage() + 1);
						if(!manualGrinding)
							inventory[3].setItemDamage(inventory[3].getItemDamage() + 1);
						progress = 0;
						manualGrinding = false;
						if (inventory[1].getItemDamage() >= inventory[1].getMaxDamage()) {
							inventory[1] = null;
							hasPestle = false;
						}
						if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
							getStackInSlot(1).getTagCompound().setBoolean("active", false);
						markDirty();
					}
				}
			}
		}
		if (progress > 0)
			markDirty();
		if (pestleInstalled()) {
			hasPestle = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else {
			hasPestle = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
			if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
				getStackInSlot(1).getTagCompound().setBoolean("active", false);
			progress = 0;
			markDirty();
		}
		if (getStackInSlot(3) == null && progress > 0 && !manualGrinding) {
			if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
				getStackInSlot(1).getTagCompound().setBoolean("active", false);
			progress = 0;
			markDirty();
		}
		if(isCrystalInstalled()) {
			hasCrystal = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else {
			hasCrystal = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean pestleInstalled() {
		return getStackInSlot(1) != null && getStackInSlot(1).getItem() == BLItemRegistry.pestle;
	}

	public boolean isCrystalInstalled() {
		return getStackInSlot(3) != null && getStackInSlot(3).getItem() == BLItemRegistry.lifeCrystal && getStackInSlot(3).getItemDamage() <= getStackInSlot(3).getMaxDamage();
	}

	private boolean outputIsFull() {
		return getStackInSlot(2) != null && getStackInSlot(2).stackSize >= getInventoryStackLimit();
	}

	public void sendGUIData(ContainerPestleAndMortar animator, ICrafting craft) {
		craft.sendProgressBarUpdate(animator, 0, progress);
	}

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			progress = value;
			break;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("progress", progress);
		nbt.setBoolean("hasPestle", hasPestle);
		nbt.setBoolean("hasCrystal", hasCrystal);
		nbt.setBoolean("manualGrinding", manualGrinding);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		progress = nbt.getInteger("progress");
		hasPestle = nbt.getBoolean("hasPestle");
		hasCrystal = nbt.getBoolean("hasCrystal");
		manualGrinding = nbt.getBoolean("manualGrinding");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("progress", progress);
		nbt.setBoolean("hasPestle", hasPestle);
		nbt.setBoolean("hasCrystal", hasCrystal);
		nbt.setBoolean("manualGrinding", manualGrinding);
		NBTTagCompound itemStackCompound = new NBTTagCompound();
		if(inventory[3] != null) {
			inventory[3].writeToNBT(itemStackCompound);
		}
		nbt.setTag("outputItem", itemStackCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		progress = packet.func_148857_g().getInteger("progress");
		hasPestle = packet.func_148857_g().getBoolean("hasPestle");
		hasCrystal = packet.func_148857_g().getBoolean("hasCrystal");
		manualGrinding = packet.func_148857_g().getBoolean("manualGrinding");
		NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("outputItem");
		if(itemStackCompound.getShort("id") != 0) {
			inventory[3] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		} else {
			inventory[3] = null;
		}
	}
}
