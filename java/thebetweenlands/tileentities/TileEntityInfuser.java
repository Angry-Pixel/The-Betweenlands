package thebetweenlands.tileentities;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thebetweenlands.blocks.BLFluidRegistry;

public class TileEntityInfuser extends TileEntityBasicInventory implements IFluidHandler {

	public final FluidTank waterTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 3);
	public int stirProgress = 90;
	public int stirCount;
	public int temp;

	public TileEntityInfuser() {
		super(4, "infuser");
		// 4 slots for ingredients (0 - 3)
		waterTank.setFluid(new FluidStack(BLFluidRegistry.swampWater, 0));
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		if (stirProgress == 0) {
			stirCount++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if (stirProgress < 90) {
			stirProgress++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if(worldObj.getBlock(xCoord, yCoord - 1, zCoord) == Blocks.fire && temp < 100 && getWaterAmount() > 0) {
			if(worldObj.getWorldTime()%12 == 0) {
				temp++;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		if(worldObj.getBlock(xCoord, yCoord - 1, zCoord) != Blocks.fire && temp > 0) {
			if(worldObj.getWorldTime()%6 == 0) {
				temp--;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource == null)
			return 0;
		else if (resource.getFluid() == BLFluidRegistry.swampWater)
			return waterTank.fill(resource, doFill);
		else
			return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidTankInfo[] infos = new FluidTankInfo[1];
		infos[0] = new FluidTankInfo(waterTank.getFluid(), waterTank.getCapacity());
		return infos;
	}

	public ItemStack fillTankWithBucket(ItemStack bucket) {
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
		if (fluid != null) {
			int amountFilled = fill(ForgeDirection.UNKNOWN, fluid, false);
			if (amountFilled == fluid.amount) {
				fill(ForgeDirection.UNKNOWN, fluid, true);
				return FluidContainerRegistry.drainFluidContainer(bucket);
			}
		}
		return bucket;
	}

	private void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid()))
			waterTank.drain(fluid.amount, true);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public int getWaterAmount() {
		return waterTank.getFluidAmount();
	}

	public int getTanksFullValue() {
		return waterTank.getCapacity();
	}

	public int getScaledWaterAmount(int scale) {
		return waterTank.getFluid() != null ? (int) ((float) waterTank.getFluid().amount / (float) waterTank.getCapacity() * scale) : 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("stirProgress", stirProgress);
		nbt.setInteger("stirCount", stirCount);
		nbt.setInteger("temp", temp);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		stirProgress = nbt.getInteger("stirProgress");
		stirCount = nbt.getInteger("stirCount");
		temp = nbt.getInteger("temp");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("stirProgress", stirProgress);
		nbt.setInteger("stirCount", stirCount);
		nbt.setInteger("temp", temp);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		waterTank.readFromNBT(packet.func_148857_g().getCompoundTag("waterTank"));
		stirProgress = packet.func_148857_g().getInteger("stirProgress");
		stirCount = packet.func_148857_g().getInteger("stirCount");
		temp = packet.func_148857_g().getInteger("temp");
	}

}
