package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.registries.FluidRegistry;

public class TileEntityRubberTap extends TileEntity implements IFluidHandler, ITickable {
	private final FluidTank tank;

	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	private int fillProgress = 0;

	public TileEntityRubberTap() {
		this.tank = new FluidTank(FluidRegistry.RUBBER, 0, Fluid.BUCKET_VOLUME);
		this.tank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.tank);
	}

	@Override
	public void update() {
		if(!this.world.isRemote && this.getBlockType() instanceof BlockRubberTap) {
			FluidStack drained = this.tank.drain(Fluid.BUCKET_VOLUME, false);
			final int ticksPerStep = ((BlockRubberTap)this.getBlockType()).ticksPerStep;
			if(drained == null || drained.amount < Fluid.BUCKET_VOLUME) {
				this.fillProgress++;

				if(this.fillProgress >= ticksPerStep) {
					this.tank.fill(new FluidStack(FluidRegistry.RUBBER, 67), true);
					this.fillProgress = 0;

					IBlockState stat = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
					this.markDirty();
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		this.tank.writeToNBT(tagCompound);
		tagCompound.setInteger("FillProgress", this.fillProgress);
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.tank.readFromNBT(tagCompound);
		this.fillProgress = tagCompound.getInteger("FillProgress");
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.tank.writeToNBT(nbt);
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.tank.readFromNBT(pkt.getNbtCompound());
		this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(doFill) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this;
		return super.getCapability(capability, facing);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.tank.writeToNBT(nbt);
		nbt.setInteger("FillProgress", this.fillProgress);
		return nbt;
	}
}
