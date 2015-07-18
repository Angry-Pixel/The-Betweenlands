package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.inventory.container.ContainerPestleAndMortar;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.recipes.PestleAndMortarRecipe;

public class TileEntityPestleAndMortar extends TileEntityBasicInventory { 
	
	public int progress;
	public boolean hasPestle;
	public boolean manualGrinding = false;
	
	public TileEntityPestleAndMortar() {
		super(3, "pestleAndMortar");
	}
	
	@Override
    public boolean canUpdate() {
        return true;
    }

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		ItemStack output = PestleAndMortarRecipe.getOutput(inventory[0]);
		if (this.isGrinding()) {
				progress++;
				if(progress == 1)
					worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thebetweenlands:grind", 1F, 1F);
				if(progress == 64 || progress == 84) {
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
					progress = 0;
					this.manualGrinding = false;
					boolean hasCrystal = this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord) == BLBlockRegistry.lifeCrystal && BLBlockRegistry.lifeCrystal.getLife(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord) > 0;
					if(hasCrystal) {
						BLBlockRegistry.lifeCrystal.decrLife(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord);
					}
					if(inventory[1].getItemDamage() >= inventory[1].getMaxDamage()) {
						inventory[1] = null;
						hasPestle = false;
					}
					markDirty();
				}
		} else {
			if(inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
				getStackInSlot(1).getTagCompound().setBoolean("active", false);
			progress = 0;
			markDirty();
		}

		if(progress > 0)
			markDirty();
		
		if(pestleInstalled()) {
			hasPestle = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else {
			hasPestle = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
			
		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
			if(inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
				getStackInSlot(1).getTagCompound().setBoolean("active", false);
			progress = 0;
			markDirty();
		}
	}

	public boolean pestleInstalled() {
		return getStackInSlot(1) != null && getStackInSlot(1).getItem() == BLItemRegistry.pestle;
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
	
	public boolean isGrinding() {
		ItemStack output = PestleAndMortarRecipe.getOutput(inventory[0]);
		boolean canGrind = pestleInstalled() && !outputIsFull() && (output != null && inventory[2] == null || output != null && inventory[2] != null && inventory[2].isItemEqual(output));
		boolean hasCrystal = this.worldObj.getBlock(this.xCoord, this.yCoord+1, this.zCoord) == BLBlockRegistry.lifeCrystal && BLBlockRegistry.lifeCrystal.getLife(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord) > 0;
		return (this.manualGrinding || hasCrystal) && canGrind;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("progress", progress);
		nbt.setBoolean("hasPestle", hasPestle);
		nbt.setBoolean("manualGrinding", manualGrinding);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		progress = nbt.getInteger("progress");
		hasPestle = nbt.getBoolean("hasPestle");
		manualGrinding = nbt.getBoolean("manualGrinding");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("progress", progress);
		nbt.setBoolean("hasPestle", hasPestle);
		nbt.setBoolean("manualGrinding", manualGrinding);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		progress = packet.func_148857_g().getInteger("progress");
		hasPestle = packet.func_148857_g().getBoolean("hasPestle");
		manualGrinding = packet.func_148857_g().getBoolean("manualGrinding");
	}
}
