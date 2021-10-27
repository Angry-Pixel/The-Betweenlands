package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.registries.AspectRegistry;

public class TileEntityGeckoCage extends TileEntity implements ITickable {
	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	private IAspectType aspectType = null;
	private int geckoUsages = 0;
	private String geckoName;

	@Override
	public void update() {
		this.prevTicks = this.ticks;
		++this.ticks;
		if(!this.world.isRemote) {
			if(this.recoverTicks > 0) {
				--this.recoverTicks;
				if(this.recoverTicks == 0) {
					IBlockState state = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, state, state, 2);
				}
			} else {
				if(this.aspectType != null && this.geckoUsages == 0) {
					this.geckoName = "";
					IBlockState state = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, state, state, 2);
				}
				if(this.aspectType != null) {
					this.aspectType = null;
					IBlockState state = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, state, state, 2);
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
		--this.geckoUsages;
		this.aspectType = type;
		this.recoverTicks = recoverTime;
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(this.pos, state, state, 2);
		if (!hasGecko())
			this.recoverTicks = 0;
	}

	public boolean hasGecko() {
		return this.geckoUsages > 0;
	}

	public void setGeckoUsages(int usages) {
		this.geckoUsages = usages;
		this.markDirty();
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(this.pos, state, state, 2);
	}
	
	public int getGeckoUsages() {
		return this.geckoUsages;
	}

	@Nullable
	public String getGeckoName() {
		return this.geckoName;
	}

	public void addGecko(int usages, @Nullable String name) {
		this.geckoUsages = usages;
		this.geckoName = name;
		this.ticks = 0;
		this.markDirty();
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(this.pos, state, state, 2);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("RecoverTicks", this.recoverTicks);
		nbt.setInteger("GeckoUsages", this.geckoUsages);
		if(this.geckoName != null) {
			nbt.setString("GeckoName", this.geckoName);
		}
		nbt.setString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		nbt.setInteger("Ticks", this.ticks);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.recoverTicks = nbt.getInteger("RecoverTicks");
		this.geckoUsages = nbt.getInteger("GeckoUsages");
		if(nbt.hasKey("GeckoName", Constants.NBT.TAG_STRING)) {
			this.geckoName = nbt.getString("GeckoName");
		} else {
			this.geckoName = null;
		}
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
