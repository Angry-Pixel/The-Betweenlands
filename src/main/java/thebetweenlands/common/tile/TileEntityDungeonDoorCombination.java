package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityDungeonDoorCombination extends TileEntity implements ITickable {

	public int top_code = 0, mid_code = 0, bottom_code = 0;
	public int renderTicks = 0;
	
	public TileEntityDungeonDoorCombination() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		top_code = nbt.getInteger("top_code");
		mid_code = nbt.getInteger("mid_code");
		bottom_code = nbt.getInteger("bottom_code");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("top_code", top_code);
		nbt.setInteger("mid_code", mid_code);
		nbt.setInteger("bottom_code", bottom_code);
		return nbt;
	}

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
        return writeToNBT(nbt);
    }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update() {
		this.renderTicks++;
	}

}
