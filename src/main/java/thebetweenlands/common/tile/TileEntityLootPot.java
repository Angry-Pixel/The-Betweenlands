package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityType;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityLootPot extends TileEntityLootInventory {
	private int rotationOffset;

	public TileEntityLootPot() {
		super(TileEntityRegistry.LOOT_POT, 3, "container.lootPot");
	}
	
	public TileEntityLootPot(TileEntityType<?> type) {
		super(type, 3, "container.lootPot");
	}

	public void setModelRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return this.rotationOffset;
	}

	@Override
	public void read(NBTTagCompound nbt) {
		super.read(nbt);
		this.rotationOffset = nbt.getInt("rotationOffset");
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt) {
		super.write(nbt);
		nbt.setInt("rotationOffset", this.rotationOffset);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInt("rotationOffset", this.rotationOffset);
		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.rotationOffset = packet.getNbtCompound().getInt("rotationOffset");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInt("rotationOffset", this.rotationOffset);
		return nbt;
	}
}
