package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class TileEntityCenser extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	private final FluidTank fluidTank;
	private final IFluidTankProperties[] properties = new IFluidTankProperties[]{
			new IFluidTankProperties() {
				@Override
				public FluidStack getContents() {
					return TileEntityCenser.this.fluidTank.getFluid();
				}

				@Override
				public int getCapacity() {
					return TileEntityCenser.this.fluidTank.getCapacity();
				}

				@Override
				public boolean canFill() {
					return TileEntityCenser.this.fluidTank.canFill();
				}

				@Override
				public boolean canDrain() {
					return TileEntityCenser.this.fluidTank.canDrain();
				}

				@Override
				public boolean canFillFluidType(FluidStack fluidStack) {
					return TileEntityCenser.this.fluidTank.canFillFluidType(fluidStack);
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluidStack) {
					return TileEntityCenser.this.fluidTank.canDrainFluidType(fluidStack);
				}
			}
	};

	private static final int INV_SIZE = 3;

	private int remainingItemAmount = 0;

	public TileEntityCenser() {
		super("container.censer", NonNullList.withSize(INV_SIZE, ItemStack.EMPTY), (te, inv) -> new ItemStackHandler(inv) {
			@Override
			public void setSize(int size) {
				this.stacks = te.inventory = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
			}

			@Override
			protected void onContentsChanged(int slot) {
				// Don't mark dirty while loading chunk!
				if(te.hasWorld()) {
					te.markDirty();
				}
			}

			//Internal slot stores at most 1 item and can't be extracted

			@Override
			public int getSlotLimit(int slot) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getSlotLimit(slot);
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getStackLimit(slot, stack);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? stack : super.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
			}
		});
		this.fluidTank = new FluidTank(null, Fluid.BUCKET_VOLUME * 4);
		this.fluidTank.setTileEntity(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setTag("fluidTank", fluidTank.writeToNBT(new NBTTagCompound()));
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
		nbt.setTag("fluidTank", fluidTank.writeToNBT(new NBTTagCompound()));
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	protected void readPacketNbt(NBTTagCompound nbt) {
		NBTTagCompound compound = nbt;
		fluidTank.readFromNBT(compound.getCompoundTag("fluidTank"));
		this.readInventoryNBT(nbt);
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

	public void receiveGUIData(int id, int value) {
		switch (id) {
		case 0:
			this.remainingItemAmount = value;
			break;
		case 1:
			if(this.fluidTank.getFluid() != null) {
				this.fluidTank.getFluid().amount = value;
			}
			break;
		}
	}

	public void sendGUIData(ContainerCenser censer, IContainerListener craft) {
		craft.sendWindowProperty(censer, 0, this.remainingItemAmount);
		craft.sendWindowProperty(censer, 1, this.fluidTank.getFluid() != null ? this.fluidTank.getFluid().amount : 0);
	}

	@Override
	public void update() {

	}

	private void extractFluids(FluidStack fluid) {
		if(fluid.isFluidEqual(fluidTank.getFluid())) {
			fluidTank.drain(fluid.amount, true);
		}
		markDirty();
	}

	public boolean hasFuel() {
		return !getStackInSlot(ContainerCenser.SLOT_FUEL).isEmpty() && EnumItemMisc.SULFUR.isItemOf(getStackInSlot(ContainerCenser.SLOT_FUEL)) && getStackInSlot(ContainerCenser.SLOT_FUEL).getCount() >= 1;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		if (facing == EnumFacing.UP) {
			return new int[]{ ContainerCenser.SLOT_INPUT };
		}
		return new int[]{ ContainerCenser.SLOT_FUEL };
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (doFill) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.fluidTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.fluidTank.drain(maxDrain, doDrain);
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
	public AxisAlignedBB getRenderBoundingBox() {
		return this.getFogRenderArea();
	}
	
	public float getFogStrength(float partialTicks) {
		return 1.0F;
	}
	
	public AxisAlignedBB getFogRenderArea() {
		float width = 13.0F;
		float height = 12.0F;
		BlockPos pos = this.getPos();
		return new AxisAlignedBB(pos.getX() + 0.5D - width / 2, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2, pos.getX() + 0.5D + width / 2, pos.getY() - 0.1D + height, pos.getZ() + 0.5D + width / 2);
	}
}
