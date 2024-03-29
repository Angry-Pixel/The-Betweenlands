package thebetweenlands.common.tile;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.item.EnumBLDrinkableBrew;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.SteepingPotRecipes;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntitySteepingPot extends TileEntityBasicInventory implements ITickable {
    public FluidTank tank;
    public int tempFluidColour;
	private IItemHandler itemHandler;
	public boolean hasCraftResult = false;
    private int heatProgress = 0;
	private boolean heated = false;
	private NonNullList<ItemStack> inventoryBundle;
	
	public int itemRotate = 0;
    public int prevItemRotate  = 0;
    public int prevItemBob = 0;
    public int itemBob = 0;
    private boolean countUp = true;
	
	public TileEntitySteepingPot() {
		super(1, "container.bl.boiling_pot");
        this.tank = new FluidTank(null, Fluid.BUCKET_VOLUME * 1);
        this.tank.setTileEntity(this);
	}

	@Override
	public void update() {

		prevItemRotate = itemRotate;
		prevItemBob = itemBob;
		
		if (getWorld().isRemote) {
				if (countUp && itemBob <= 20) {
					itemBob++;
					if (itemBob == 20)
						countUp = false;
				}
				if (!countUp && itemBob >= -20) {
					itemBob--;
					if (itemBob == -20)
						countUp = true;
				}

			if (getHeatProgress() > 80 && hasBundle()) {
				if (itemRotate < 180)
					itemRotate += 1;
				if (itemRotate >= 180) {
					itemRotate = 0;
					prevItemRotate = 0;
				}
			}
		}

		if (!getWorld().isRemote) {
			if (getTankFluidAmount() >= Fluid.BUCKET_VOLUME && !hasCraftResult) {
				if (hasBundle()) {
					SteepingPotRecipes recipe = getCraftResult(tank, getBundleItems().get(0), getBundleItems().get(1), getBundleItems().get(2), getBundleItems().get(3));
					FluidStack outputFluid = null;
					FluidStack fluidWithTag = null;
					int outputFluidMeta = 0;
					if (recipe != null) {
						outputFluid = recipe.getOutputFluidStack();
						outputFluidMeta = recipe.getOutputFluidMeta();
						if (outputFluid != null) {
							if (outputFluid.getFluid() == FluidRegistry.DYE_FLUID)
								setTempFluidColour(EnumBLDyeColor.byMetadata(outputFluidMeta).getColorValue() | 0xFF000000);
							if (outputFluid.getFluid() == FluidRegistry.DRINKABLE_BREW)
								setTempFluidColour(EnumBLDrinkableBrew.byMetadata(outputFluidMeta).getColorValue() | 0xFF000000);
							hasCraftResult = true;
						}
					}
				} else {
					setTempFluidColour(tank.getFluid().getFluid().getColor());
					hasCraftResult = true;
				}
				this.markForUpdate();
			}

			if (this.isHeatSource(world.getBlockState(pos.down()))) {
				if (getHeatProgress() < 100 && getTankFluidAmount() > 0) {
					if (world.getTotalWorldTime() % 10 == 0) {
						setHeatProgress(getHeatProgress() + 1);
						this.markForUpdate();
					}
				}
				else if(tank.getFluid() == null && getHeatProgress() != 0) {
						setHeatProgress(0);
						this.markForUpdate();
				}
			}

			if (!this.isHeatSource(world.getBlockState(pos.down()))) {
				if (getHeatProgress() > 0) {
					if (world.getTotalWorldTime() % 5 == 0) {
						setHeatProgress(getHeatProgress() - 1);
						this.markForUpdate();
					} else if (tank.getFluid() == null && getHeatProgress() != 0) {
						setHeatProgress(0);
						this.markForUpdate();
					}
				}
			}

			if (getTankFluidAmount() >= Fluid.BUCKET_VOLUME && getHeatProgress() >= 100 && hasBundle()) {
				ItemStack output = ItemStack.EMPTY;
				FluidStack outputFluid = null;
				FluidStack fluidWithTag = null;
				int outputFluidMeta = 0;
				SteepingPotRecipes recipe = getCraftResult(tank, getBundleItems().get(0), getBundleItems().get(1), getBundleItems().get(2), getBundleItems().get(3));

				if (recipe == null) {
					setHeatProgress(0);
					if (!inventory.get(0).isEmpty())
						setInventorySlotContents(0, EnumItemMisc.SILK_BUNDLE_DIRTY.create(1));
					world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.3f);
					tank.drain(Fluid.BUCKET_VOLUME, true);
					return;
				}

				if (recipe != null) {
					output = recipe.getOutputItem();
					outputFluid = recipe.getOutputFluidStack();
					outputFluidMeta = recipe.getOutputFluidMeta();
					
					if (!inventory.get(0).isEmpty())
						setInventorySlotContents(0, EnumItemMisc.SILK_BUNDLE_DIRTY.create(1));

					tank.drain(Fluid.BUCKET_VOLUME, true);

					if (outputFluid != null) {
						if(outputFluid.getFluid() == FluidRegistry.DYE_FLUID || outputFluid.getFluid() == FluidRegistry.DRINKABLE_BREW) {
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setInteger("type", outputFluidMeta);
							fluidWithTag = new FluidStack(outputFluid.getFluid(), Fluid.BUCKET_VOLUME, nbt);
							tank.fill(fluidWithTag , true);
						}
						else
							tank.fill(outputFluid, true);
					}
					else
						spawnItemStack(getWorld(), pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, output);

					EntityXPOrb orb = new EntityXPOrb(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1);
					world.spawnEntity(orb);
					hasCraftResult = false;
					this.markForUpdate();
				}
			}
		}
	}

	private void setTempFluidColour(int type) {
		this.tempFluidColour = type;
	}

	public SteepingPotRecipes getCraftResult(FluidTank tank, ItemStack stack1, ItemStack stack2, ItemStack stack3, ItemStack stack4) {
		return SteepingPotRecipes.getRecipe(tank, stack1, stack2, stack3, stack4);
	}

	public boolean hasBundle() {
		ItemStack bundle = getStackInSlot(0);
		return !bundle.isEmpty() && bundle.getItem() == ItemRegistry.SILK_BUNDLE && bundle.hasTagCompound() && bundle.getTagCompound().hasKey("Items");
	}

	private NonNullList<ItemStack> getBundleItems() {
		ItemStack bundle = getStackInSlot(0);
		inventoryBundle = NonNullList.withSize(4, ItemStack.EMPTY);
		if (!bundle.isEmpty() && bundle.getItem() == ItemRegistry.SILK_BUNDLE && bundle.hasTagCompound() && bundle.getTagCompound().hasKey("Items"))
			ItemStackHelper.loadAllItems(bundle.getTagCompound(), inventoryBundle);
		return inventoryBundle;
	}

	public static void spawnItemStack(World world, double x, double y, double z, ItemStack stack) {
		EntityItem entityitem = new EntityItem(world, x, y, z, stack);
        entityitem.motionX = 0D;
        entityitem.motionY = 0D;
        entityitem.motionZ = 0D;
		entityitem.setPickupDelay(20);
		world.spawnEntity(entityitem);
	}

	private boolean isHeatSource(IBlockState state) {
		return state.getBlock() == BlockRegistry.PEAT_SMOULDERING || state.getBlock().getMaterial(state) == Material.FIRE;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setHeatProgress(int heat) {
		heatProgress = heat;
	}

	public int getHeatProgress() {
		return heatProgress;
	}

	public int getTankFluidAmount() {
		return tank.getFluidAmount();
	}

    public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readPacketNbt(packet.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writePacketNbt(nbt);
        return new SPacketUpdateTileEntity(pos, 0, nbt);
    }

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
        this.writePacketNbt(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt) {
        super.handleUpdateTag(nbt);
        this.readPacketNbt(nbt);
    }

    protected NBTTagCompound writePacketNbt(NBTTagCompound nbt) {
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("heatProgress", getHeatProgress());
        nbt.setInteger("tempFluidColour", tempFluidColour);
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    protected void readPacketNbt(NBTTagCompound nbt) {
        NBTTagCompound compound = nbt;
        tank.readFromNBT(compound.getCompoundTag("tank"));
        setHeatProgress(nbt.getInteger("heatProgress"));
        setTempFluidColour(nbt.getInteger("tempFluidColour"));
        this.readInventoryNBT(nbt);
    }

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		tank.readFromNBT(tagCompound);
		setHeatProgress(tagCompound.getInteger("heatProgress"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tank.writeToNBT(tagCompound);
		tagCompound.setInteger("heatProgress", getHeatProgress());
		return tagCompound;
	}

	// INVENTORY CAPABILITIES STUFF

	protected IItemHandler createUnSidedHandler() {
		return new InvWrapper(this);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
		return super.getCapability(capability, facing);
	}

	// INVENTORY LEGACY SUPPORT

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] SLOTS = new int[getSizeInventory()];
		for (int index = 0; index < SLOTS.length; index++)
			SLOTS[index] = index;
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}
}
