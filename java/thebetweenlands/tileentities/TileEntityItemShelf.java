package thebetweenlands.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityItemShelf extends TileEntityBasicInventory {
	private boolean updated = false;

	public TileEntityItemShelf() {
		super(4, "container.itemShelf");
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !updated) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			updated = true;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < getSizeInventory(); i++) {
			if(inventory[i] != null) {
				NBTTagCompound itemStackCompound = inventory[i].writeToNBT(new NBTTagCompound());
				nbt.setTag("slotItem" + i, itemStackCompound);
			} else
				nbt.setTag("slotItem" + i, null);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		for (int i = 0; i < getSizeInventory(); i++) {
			NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("slotItem" + i);
			if(itemStackCompound != null && itemStackCompound.getShort("id") != 0)
				inventory[i] = ItemStack.loadItemStackFromNBT(itemStackCompound);
			else
				inventory[i] = null;
		}
	}
}
