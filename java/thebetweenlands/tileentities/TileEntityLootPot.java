package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;


public class TileEntityLootPot extends TileEntityBasicInventory {
	private byte modelType;
	private int rotationOffset;
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

	public void setPotModelType(byte type) {
		modelType = type;
	}

	public byte getPotModelType() {
		return modelType;
	}

	public void setModelRotationOffset(int rotation) {
		rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return rotationOffset;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		modelType = nbt.getByte("modelType");
		rotationOffset = nbt.getInteger("rotationOffset");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("modelType", modelType);
		nbt.setInteger("rotationOffset", rotationOffset);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("modelType", modelType);
		nbt.setInteger("rotationOffset", rotationOffset);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		modelType = packet.func_148857_g().getByte("modelType");
		rotationOffset = packet.func_148857_g().getInteger("rotationOffset");
	}


}