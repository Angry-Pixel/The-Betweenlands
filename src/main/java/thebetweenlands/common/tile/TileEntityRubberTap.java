package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class TileEntityRubberTap extends TileEntity implements IFluidHandler, ITickable {
	private final FluidTank tank;

	private final IFluidTankProperties[] properties = new IFluidTankProperties[] { 
			new IFluidTankProperties() {
				@Override
				public FluidStack getContents() {
					return TileEntityRubberTap.this.tank.getFluid();
				}

				@Override
				public int getCapacity() {
					return TileEntityRubberTap.this.tank.getCapacity();
				}

				@Override
				public boolean canFill() {
					return TileEntityRubberTap.this.tank.canFill();
				}

				@Override
				public boolean canDrain() {
					return TileEntityRubberTap.this.tank.canDrain();
				}

				@Override
				public boolean canFillFluidType(FluidStack fluidStack) {
					return TileEntityRubberTap.this.tank.canFillFluidType(fluidStack);
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluidStack) {
					return TileEntityRubberTap.this.tank.canDrainFluidType(fluidStack);
				}
			}
	};

	private int fillProgress = 0;

	public TileEntityRubberTap() {
		this.tank = new FluidTank(FluidRegistry.RUBBER, 0, Fluid.BUCKET_VOLUME);
	}

	@Override
	public void update() {
		if(!this.worldObj.isRemote) {
			this.fillProgress++;
			FluidStack drained = this.tank.drain(Fluid.BUCKET_VOLUME, false);
			final int ticksPerFill = this.getBlockType() == BlockRegistry.WEEDWOOD_RUBBER_TAP ? 800 : 400; //Weedwood tap is slower
			if((drained == null || drained.amount < Fluid.BUCKET_VOLUME) && this.fillProgress >= ticksPerFill) {
				this.tank.fill(new FluidStack(FluidRegistry.RUBBER, 100), true);
				this.fillProgress = 0;

				IBlockState stat = this.worldObj.getBlockState(this.pos);
				this.worldObj.notifyBlockUpdate(this.pos, stat, stat, 3);
				this.markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		this.tank.writeToNBT(tagCompound);
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.tank.readFromNBT(tagCompound);
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
		this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(doFill)
			this.markDirty();
		return this.tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(doDrain)
			this.markDirty();
		return this.tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(doDrain)
			this.markDirty();
		return this.tank.drain(maxDrain, doDrain);
	}
}
