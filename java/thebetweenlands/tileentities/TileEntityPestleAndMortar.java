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
		if (pestleInstalled() && !outputIsFull()) {
			if (output != null && inventory[2] == null || output != null && inventory[2] != null && inventory[2].isItemEqual(output)) {
				progress++;
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
					if(inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
						getStackInSlot(1).getTagCompound().setBoolean("active", false);
					markDirty();
				}
			}
		}

		if(progress > 0)
			markDirty();

		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
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

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("progress", progress);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		progress = nbt.getInteger("progress");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("progress", progress);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		progress = packet.func_148857_g().getInteger("progress");
	}
}
