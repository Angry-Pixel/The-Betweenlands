package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspectType;

public class TileEntityGeckoCage extends TileEntity {
	private int ticks = 0;
	private int prevTicks = 0;
	//private int walkStartTicks = 0;
	//private boolean walk = false;
	private int recoverTicks = 0;
	private IAspectType aspectType = null;
	private int geckoUsages = 0;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		this.prevTicks = this.ticks;
		++this.ticks;
		if(!this.worldObj.isRemote) {
			if(this.recoverTicks > 0) {
				--this.recoverTicks;
				if(this.recoverTicks == 0) {
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			} else {
				if(this.aspectType != null && this.geckoUsages > 0) {
					--this.geckoUsages;
					if(this.geckoUsages == 0) {
						this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
					}
				}
				if(this.aspectType != null) {
					this.aspectType = null;
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}
		}
	}

	public int getTicks() {
		return this.ticks;
	}

	public float getInterpolatedTicks(float delta) {
		return this.prevTicks + (this.ticks - this.prevTicks) * delta;
	}

	public IAspectType getAspectType() {
		return this.aspectType;
	}

	public void setAspectType(IAspectType type, int recoverTime) {
		this.aspectType = type;
		this.recoverTicks = recoverTime;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean hasGecko() {
		return this.geckoUsages > 0;
	}

	public void addGecko(int usages) {
		this.geckoUsages = usages;
		this.ticks = 0;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("recoverTicks", this.recoverTicks);
		nbt.setInteger("geckoUsages", this.geckoUsages);
		nbt.setString("aspectType", this.aspectType == null ? "" : this.aspectType.getName());
		nbt.setInteger("ticks", this.ticks);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.recoverTicks = nbt.getInteger("recoverTicks");
		this.geckoUsages = nbt.getInteger("geckoUsages");
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("aspectType"));
		this.ticks = nbt.getInteger("ticks");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("geckoUsages", this.geckoUsages);
		nbt.setString("aspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbt = packet.func_148857_g();
		this.geckoUsages = nbt.getInteger("geckoUsages");
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("aspectType"));
	}
}
