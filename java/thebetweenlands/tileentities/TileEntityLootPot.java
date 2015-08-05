package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;


public class TileEntityLootPot extends TileEntityBasicInventory {
	private byte modelType;
	private boolean updated = false;

	public TileEntityLootPot() {
		super(3, "container.lootPot");
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && !updated) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			updated = true;
		}	
	}

	public byte getPotModelType() {
		return modelType;
	}

	public void setPotModelType(byte type) {
		modelType = type;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		modelType = nbt.getByte("modelType");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("modelType", modelType);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("modelType", modelType);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		modelType = packet.func_148857_g().getByte("modelType");
	}
}