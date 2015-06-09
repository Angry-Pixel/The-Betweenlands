package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.inventory.container.ContainerPurifier;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.PurifierRecipe;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler {

	public final FluidTank waterTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
	public int time = 0;
	private static final int MAX_TIME = 432;

	public TileEntityPurifier() {
		super(3, "container.purifier");
		waterTank.setFluid(new FluidStack(BLFluidRegistry.swampWater, 0));
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

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
	}

	public int getPurifyingProgress() {
		return time / 36;
	}

	public boolean isPurifying() {
		return time > 0;
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

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			time = value;
			break;
		case 1:
			if (waterTank.getFluid() == null)
				waterTank.setFluid(new FluidStack(BLFluidRegistry.swampWater, value));
			else
				waterTank.getFluid().amount = value;
			break;
		}
	}

	public void sendGUIData(ContainerPurifier purifier, ICrafting craft) {
		craft.sendProgressBarUpdate(purifier, 0, time);
		craft.sendProgressBarUpdate(purifier, 1, waterTank.getFluid() != null ? waterTank.getFluid().amount : 0);
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		ItemStack output = PurifierRecipe.getOutput(inventory[1]);
		if(hasFuel() && !outputIsFull()) {
			if (output != null && getWaterAmount() > 0 && inventory[2] == null || output != null && getWaterAmount() > 0 && inventory[2] != null && inventory[2].isItemEqual(output)) {
				time++;
				if (time >= MAX_TIME) {
					for (int i = 0; i < 2; i++)
						if (inventory[i] != null)
							if (--inventory[i].stackSize <= 0)
								inventory[i] = null;
					extractFluids(new FluidStack(BLFluidRegistry.swampWater, FluidContainerRegistry.BUCKET_VOLUME));
					if (inventory[2] == null)
						inventory[2] = output.copy();
					else if (inventory[2].isItemEqual(output))
						inventory[2].stackSize += output.stackSize;
					time = 0;
					markDirty();
				}
			}
		}
		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
			time = 0;
			markDirty();
		}
	}

	private void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid()))
			waterTank.drain(fluid.amount, true);
	}

	public boolean hasFuel() {
		return getStackInSlot(0) != null && getStackInSlot(0).getItem() == BLItemRegistry.materialsBL && getStackInSlot(0).getItemDamage() == EnumMaterialsBL.SULFUR.ordinal() && getStackInSlot(0).stackSize >= 1;
	}

	private boolean outputIsFull() {
		return getStackInSlot(2) != null && getStackInSlot(2).stackSize >= getInventoryStackLimit();
	}
}
