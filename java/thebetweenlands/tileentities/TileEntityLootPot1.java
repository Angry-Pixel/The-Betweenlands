package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityLootPot1 extends TileEntityBasicInventory {
	private byte modelType;
	private int rotationOffset;
	private boolean updated = false;

	public TileEntityLootPot1() {
		super(3, "container.lootPot");
	}

	@Override
	public boolean canUpdate() {
		return false;
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
		rotationOffset = nbt.getInteger("rotationOffset");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("rotationOffset", rotationOffset);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("rotationOffset", rotationOffset);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		rotationOffset = packet.func_148857_g().getInteger("rotationOffset");
	}

}