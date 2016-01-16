package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thebetweenlands.herblore.aspects.Aspect;

public class TileEntityAspectCrop extends TileEntity {
	private Aspect seedAspect = null;

	public void setAspect(Aspect aspect) {
		this.seedAspect = aspect;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public Aspect getAspect() {
		return this.seedAspect;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.seedAspect != null)
			this.seedAspect.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.seedAspect = Aspect.readFromNBT(nbt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		if(this.seedAspect != null)
			this.seedAspect.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbt = packet.func_148857_g();
		this.seedAspect = Aspect.readFromNBT(nbt);
	}
}
