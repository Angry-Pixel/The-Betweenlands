package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.registries.AspectRegistry;

public class TileEntityGeckoCage extends TileEntity implements ITickable {
	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	private IAspectType aspectType = null;
	private int geckoUsages = 0;

	@Override
	public void update() {
		this.prevTicks = this.ticks;
		++this.ticks;
		if(!this.worldObj.isRemote) {
			if(this.recoverTicks > 0) {
				--this.recoverTicks;
				if(this.recoverTicks == 0) {
					IBlockState state = this.worldObj.getBlockState(this.pos);
					this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
				}
			} else {
				if(this.aspectType != null && this.geckoUsages > 0) {
					--this.geckoUsages;
					if(this.geckoUsages == 0) {
						IBlockState state = this.worldObj.getBlockState(this.pos);
						this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
					}
				}
				if(this.aspectType != null) {
					this.aspectType = null;
					IBlockState state = this.worldObj.getBlockState(this.pos);
					this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
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
		IBlockState state = this.worldObj.getBlockState(this.pos);
		this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
	}

	public boolean hasGecko() {
		return this.geckoUsages > 0;
	}

	public void addGecko(int usages) {
		this.geckoUsages = usages;
		this.ticks = 0;
		IBlockState state = this.worldObj.getBlockState(this.pos);
		this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("RecoverTicks", this.recoverTicks);
		nbt.setInteger("GeckoUsages", this.geckoUsages);
		nbt.setString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		nbt.setInteger("Ticks", this.ticks);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.recoverTicks = nbt.getInteger("RecoverTicks");
		this.geckoUsages = nbt.getInteger("GeckoUsages");
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("AspectType"));
		this.ticks = nbt.getInteger("Ticks");
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("GeckoUsages", this.geckoUsages);
		nbt.setString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		this.geckoUsages = nbt.getInteger("GeckoUsages");
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("AspectType"));
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("GeckoUsages", this.geckoUsages);
		nbt.setString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return nbt;
	}
}
