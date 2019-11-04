package thebetweenlands.common.tile;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityLootPot extends TileEntityLootInventory {
	private int rotationOffset;

	public TileEntityLootPot() {
		super(3, "container.bl.loot_pot");
	}

	public void setModelRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return this.rotationOffset;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.rotationOffset = nbt.getInteger("rotationOffset");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("rotationOffset", this.rotationOffset);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("rotationOffset", this.rotationOffset);
		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.rotationOffset = packet.getNbtCompound().getInteger("rotationOffset");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("rotationOffset", this.rotationOffset);
		return nbt;
	}
}
