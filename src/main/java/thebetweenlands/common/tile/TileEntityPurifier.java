package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	private static final int MAX_TIME = 432;
	public int time = 0;
	public boolean lightOn = false;
	private int prevStackSize = 0;
	private Item prevItem;
	private boolean isPurifyingClient = false;

	public final FluidTank waterTank;

	private final IFluidTankProperties[] properties = new IFluidTankProperties[] { 
			new IFluidTankProperties() {
				@Override
				public FluidStack getContents() {
					return TileEntityPurifier.this.waterTank.getFluid();
				}

				@Override
				public int getCapacity() {
					return TileEntityPurifier.this.waterTank.getCapacity();
				}

				@Override
				public boolean canFill() {
					return TileEntityPurifier.this.waterTank.canFill();
				}

				@Override
				public boolean canDrain() {
					return TileEntityPurifier.this.waterTank.canDrain();
				}

				@Override
				public boolean canFillFluidType(FluidStack fluidStack) {
					return TileEntityPurifier.this.waterTank.canFillFluidType(fluidStack);
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluidStack) {
					return TileEntityPurifier.this.waterTank.canDrainFluidType(fluidStack);
				}
			}
	};

	public TileEntityPurifier() {
		super(3, "container.purifier");
		this.waterTank = new FluidTank(FluidRegistry.SWAMP_WATER, 0, Fluid.BUCKET_VOLUME * 16);
		this.waterTank.setTileEntity(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		lightOn = nbt.getBoolean("state");
		time = nbt.getInteger("progress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setBoolean("state", lightOn);
		nbt.setInteger("progress", time);
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writePacketNbt(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readPacketNbt(packet.getNbtCompound());
	}

	protected NBTTagCompound writePacketNbt(NBTTagCompound nbt) {
		nbt.setBoolean("state", lightOn);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setBoolean("isPurifying", this.isPurifying());
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	protected void readPacketNbt(NBTTagCompound nbt) {
		NBTTagCompound compound = nbt;
		lightOn = compound.getBoolean("state");
		waterTank.readFromNBT(compound.getCompoundTag("waterTank"));
		this.readInventoryNBT(nbt);
		isPurifyingClient = compound.getBoolean("isPurifying");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writePacketNbt(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		this.readPacketNbt(nbt);
	}

	public int getPurifyingProgress() {
		return time / 36;
	}

	public boolean isPurifying() {
		return time > 0 || this.isPurifyingClient;
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
				waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER, value));
			else
				waterTank.getFluid().amount = value;
			break;
		}
	}

	public void sendGUIData(ContainerPurifier purifier, IContainerListener craft) {
		craft.sendProgressBarUpdate(purifier, 0, time);
		craft.sendProgressBarUpdate(purifier, 1, waterTank.getFluid() != null ? waterTank.getFluid().amount : 0);
	}

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		ItemStack output = PurifierRecipe.getRecipeOutput(inventory[1]);
		if (hasFuel() && !outputIsFull()) {
			if (output != null && getWaterAmount() > 0 && inventory[2] == null || output != null && getWaterAmount() > 0 && inventory[2] != null && inventory[2].isItemEqual(output)) {
				time++;
				if (time % 108 == 0)
					worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.PURIFIER, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!lightOn)
					setIlluminated(true);
				if (time >= MAX_TIME) {
					for (int i = 0; i < 2; i++)
						if (inventory[i] != null)
							if (--inventory[i].stackSize <= 0)
								inventory[i] = null;
					extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
					if (inventory[2] == null) {
						inventory[2] = output.copy();
					} else if (inventory[2].isItemEqual(output)) {
						inventory[2].stackSize += output.stackSize;
					}
					time = 0;
					markDirty();
					boolean canRun = output != null && getWaterAmount() > 0 && inventory[2] == null || output != null && getWaterAmount() > 0 && inventory[2] != null && inventory[2].isItemEqual(output);
					if (!canRun) setIlluminated(false);
				}
			}
		}
		if (time > 0) {
			markDirty();
		}
		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
			time = 0;
			markDirty();
			setIlluminated(false);
		}
		if (this.prevStackSize != (inventory[2] != null ? inventory[2].stackSize : 0)) {
			markDirty();
		}
		if (this.prevItem != (inventory[2] != null ? inventory[2].getItem() : null)) {
			markDirty();
		}
		this.prevItem = inventory[2] != null ? inventory[2].getItem() : null;
		this.prevStackSize = inventory[2] != null ? inventory[2].stackSize : 0;
	}

	private void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid()))
			waterTank.drain(fluid.amount, true);
		markDirty();
	}

	public boolean hasFuel() {
		return getStackInSlot(0) != null && EnumItemMisc.SULFUR.isItemOf(getStackInSlot(0)) && getStackInSlot(0).stackSize >= 1;
	}

	private boolean outputIsFull() {
		return getStackInSlot(2) != null && getStackInSlot(2).stackSize >= getInventoryStackLimit();
	}

	public void setIlluminated(boolean state) {
		lightOn = state;
		worldObj.addBlockEvent(pos, getBlockType(), 0, lightOn ? 1 : 0);
	}

	@Override
	public boolean receiveClientEvent(int eventId, int eventData) {
		switch (eventId) {
		case 0:
			lightOn = eventData == 1;
			worldObj.checkLight(pos);
			return true;
		default:
			return false;
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		if (facing == EnumFacing.DOWN)
			return new int[]{2};
		if (facing == EnumFacing.UP)
			return new int[]{1};
		return new int[]{0};
	}

	/*public ItemStack fillTankWithBucket(ItemStack bucket) {
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
		System.out.println(fluid);
		if (fluid == null && bucket.getItem() instanceof IFluidContainerItem) {
			fluid = ((IFluidContainerItem)bucket.getItem()).getFluid(bucket);
			int amountFilled = fill(null, fluid, false);
			if (amountFilled == fluid.amount) {
				fill(null, fluid, true);
				ItemStack t = bucket;
				((IFluidContainerItem)bucket.getItem()).drain(t, ((IFluidContainerItem)bucket.getItem()).getCapacity(t), true);
				return t;
			}
		}
		if (fluid != null) {
			int amountFilled = fill(null, fluid, false);
			if (amountFilled == fluid.amount) {
				fill(null, fluid, true);
				return FluidContainerRegistry.drainFluidContainer(bucket);
			}
		}
		return bucket;
	}*/

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(doFill) {
			this.markDirty();
			IBlockState stat = this.worldObj.getBlockState(this.pos);
			this.worldObj.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.waterTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(doDrain) {
			this.markDirty();
			IBlockState stat = this.worldObj.getBlockState(this.pos);
			this.worldObj.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.waterTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(doDrain) {
			this.markDirty();
			IBlockState stat = this.worldObj.getBlockState(this.pos);
			this.worldObj.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.waterTank.drain(maxDrain, doDrain);
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
}
