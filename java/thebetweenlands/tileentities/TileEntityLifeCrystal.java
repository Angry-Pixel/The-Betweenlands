package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLifeCrystal extends TileEntity { 
	public static final int MAX_LIFE = 128;
	private int life = MAX_LIFE;
	
	public void setLife(int life) {
		this.life = life;
		this.markDirty();
	}
	
	public void decrLife() {
		this.life--;
		this.markDirty();
	}
	
	public int getLife() {
		return this.life;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("life", this.life);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.life = nbt.getInteger("life");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", this.life);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		this.life = packet.func_148857_g().getInteger("life");
	}
}
