package thebetweenlands.common.tile;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
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
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.BoilingPotRecipes;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityBoilingPot extends TileEntityBasicInventory implements ITickable {
    public FluidTank tank;
	private IItemHandler itemHandler;
    private int heatProgress = 0;
	private boolean heated = false;
	private NonNullList<ItemStack> inventoryBundle;
	
	public TileEntityBoilingPot() {
		super(1, "container.bl.boiling_pot");
        this.tank = new FluidTank(null, Fluid.BUCKET_VOLUME * 1);
        this.tank.setTileEntity(this);
	}

	@Override
	public void update() {

		if (!getWorld().isRemote) {
			
			if (this.isHeatSource(world.getBlockState(pos.down())) && getHeatProgress()< 100 && getTankFluidAmount() > 0) {
				if (world.getTotalWorldTime() % 10 == 0) {
					setHeatProgress(getHeatProgress() + 1);
					this.markForUpdate();
				}
			}

			if (!this.isHeatSource(world.getBlockState(pos.down())) && getHeatProgress() > 0) {
				if (world.getTotalWorldTime() % 5 == 0) {
					setHeatProgress(getHeatProgress() - 1);
					this.markForUpdate();
				}
			}

			if (getTankFluidAmount() >= Fluid.BUCKET_VOLUME && getHeatProgress() >= 100) {
				ItemStack output = ItemStack.EMPTY;
				FluidStack outputFluid = null;
				BoilingPotRecipes recipe = BoilingPotRecipes.getRecipe(tank, getBundleItems().get(0), getBundleItems().get(1), getBundleItems().get(2), getBundleItems().get(3));

				if (recipe == null) {
					setHeatProgress(0);
					System.out.println("Invalid Recipe");
					return;
				}

				if (recipe != null) {
					output = recipe.getOutputItem();
					outputFluid = recipe.getOutputFluid();

					if (!inventory.get(0).isEmpty())
						setInventorySlotContents(0, EnumItemMisc.SILK_BUNDLE_DIRTY.create(1));

					tank.drain(Fluid.BUCKET_VOLUME, true);
					setHeatProgress(0);
					
					if (outputFluid != null)
						tank.fill(outputFluid, true);
					else
						spawnItemStack(getWorld(), pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, output);

					EntityXPOrb orb = new EntityXPOrb(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1);
					world.spawnEntity(orb);
					System.out.println("Recipe Done");
				}
			}
		}
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
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 2);
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
		NBTTagCompound tag = new NBTTagCompound();
        return writeToNBT(tag);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt) {
        super.handleUpdateTag(nbt);
        this.readPacketNbt(nbt);
    }

    protected NBTTagCompound writePacketNbt(NBTTagCompound nbt) {
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    protected void readPacketNbt(NBTTagCompound nbt) {
        NBTTagCompound compound = nbt;
        tank.readFromNBT(compound.getCompoundTag("tank"));
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
