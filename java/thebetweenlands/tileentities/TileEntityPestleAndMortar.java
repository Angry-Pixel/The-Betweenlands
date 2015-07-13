package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import thebetweenlands.inventory.container.ContainerPestleAndMortar;

public class TileEntityPestleAndMortar extends TileEntityBasicInventory { 
	
	public int progress;
	public TileEntityPestleAndMortar() {
		super(3, "pestleAndMortar");
	}
	
	@Override
    public boolean canUpdate()
    {
        return true;
    }

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		++progress;
		if (progress >= 84)
			progress = 0;
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
