package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityLootPot extends TileEntityBasicInventory {
	private byte modelType;
	private int rotationOffset;
	private boolean updated = false;

	public TileEntityLootPot() {
		super(3, "container.lootPot");
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("rotationOffset", rotationOffset);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("rotationOffset", rotationOffset);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		rotationOffset = packet.getNbtCompound().getInteger("rotationOffset");
	}
}
