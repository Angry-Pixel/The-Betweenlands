package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityItemShelf extends TileEntityBasicInventory {
	public TileEntityItemShelf() {
		super(4, "container.itemShelf");
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeInventoryNBT(nbt);
		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readInventoryNBT(packet.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		this.readInventoryNBT(nbt);
	}
}
