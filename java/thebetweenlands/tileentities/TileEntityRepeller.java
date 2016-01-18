package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRepeller extends TileEntity {
	public static final float MAX_FUEL = 10.0F;

	private boolean hasShimmerstone = false;
	private float fuel = 0.0F;

	public void addShimmerstone() {
		this.hasShimmerstone = true;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean hasShimmerstone() {
		return this.hasShimmerstone;
	}

	public void removeShimmerstone() {
		this.hasShimmerstone = false;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean addFuel(float amount) {
		if(this.fuel + amount > MAX_FUEL)
			return false;
		this.fuel += amount;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	public boolean isRunning() {
		return this.hasShimmerstone && this.fuel > 0.0F;
	}

	@Override
	public void updateEntity() {
		if(!this.worldObj.isRemote) {
			if(this.fuel > 0) {
				this.fuel -= 0.0001F;
				if(this.fuel <= 0.0F) {
					this.fuel = 0.0F;
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.fuel = nbt.getFloat("fuel");
		this.hasShimmerstone = nbt.getBoolean("hasShimmerstone");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("fuel", this.fuel);
		nbt.setBoolean("hasShimmerstone", this.hasShimmerstone);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbt = packet.func_148857_g();
		this.readFromNBT(nbt);
	}
}
