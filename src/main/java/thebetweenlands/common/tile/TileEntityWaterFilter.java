package thebetweenlands.common.tile;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class TileEntityWaterFilter extends TileEntityBasicInventory implements ITickable {

	public FluidTank tank;
	private IItemHandler itemHandler;
	public boolean showFluidAnimation;

	public TileEntityWaterFilter() {
		super(5, "container.bl.water_filter");
        this.tank = new FluidTank(Fluid.BUCKET_VOLUME * 4) {
        	@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return canFill() && (fluid.getFluid() == FluidRegistry.SWAMP_WATER || fluid.getFluid() == FluidRegistry.STAGNANT_WATER);
			}
        };
        this.tank.setTileEntity(this);
	}

	@Override
	public void update() {
		if (!getWorld().isRemote && getWorld().getTotalWorldTime()%10 == 0 && (hasMossFilter() || hasSilkFilter())) {
			EnumFacing facing = EnumFacing.DOWN;
			TileEntity tileToFill = getWorld().getTileEntity(pos.offset(facing));
			if (tileToFill != null && tileToFill.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
				IFluidHandler recepticle = tileToFill.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
				IFluidTankProperties[] tankProperties = recepticle.getTankProperties();
				if (tankProperties != null) {
					for (IFluidTankProperties properties : tankProperties) {
						if (properties != null && properties.canFill() && properties.getCapacity() > 0) {
							FluidStack contents = properties.getContents();
							if (tank.getFluid() != null) {
								if (contents == null || contents.amount <= properties.getCapacity() - 20 && contents.containsFluid(new FluidStack(getResultFluid(), 0))) {
									recepticle.fill(new FluidStack(getResultFluid(), 20), true);
									addByProductRandom(getWorld().rand, tank.getFluid().getFluid());
									damageFilter(1);
									tank.drain(new FluidStack(tank.getFluid(), 20), true);

									if(!getFluidAnimation())
										setFluidAnimation(true);

									markForUpdate();
								}
							}
							else if(tank.getFluid() == null && getFluidAnimation()) {
								setFluidAnimation(false);
								markForUpdate();
							}	
						}
					}
				}
			}
		}

		if (!getWorld().isRemote && (!hasMossFilter() && !hasSilkFilter()) && getFluidAnimation()) {
			setFluidAnimation(false);
			markForUpdate();
		}
	}

	public FluidStack getResultFluid() {
		if (tank.getFluid().getFluid() == FluidRegistry.STAGNANT_WATER)
			return new FluidStack(FluidRegistry.SWAMP_WATER, 0);
		if (tank.getFluid().getFluid() == FluidRegistry.SWAMP_WATER)
			return new FluidStack(FluidRegistry.CLEAN_WATER, 0);
		return tank.getFluid();
	}

	public void setFluidAnimation(boolean showFluid) {
		showFluidAnimation = showFluid;
	}

	public boolean getFluidAnimation() {
		return showFluidAnimation;
	}

	private void addByProductRandom(Random rand, Fluid fluid) {
		if (rand.nextInt(50) == 0) {
			ItemStack stack = ItemStack.EMPTY;
			if (fluid == FluidRegistry.STAGNANT_WATER)
				stack = chooseRandomItemFromLootTable(getStagnantWaterLootTable());

			if (fluid == FluidRegistry.SWAMP_WATER)
				stack = chooseRandomItemFromLootTable(getSwampWaterLootTable());

			for (int slot = 1; slot < 5; slot++) {
				if (inventoryHandler.insertItem(slot, stack, false).isEmpty()) {
					break;
				}
			}
		}
	}

	protected ResourceLocation getStagnantWaterLootTable() {
		return LootTableRegistry.FILTERED_STAGNANT_WATER;
	}

	protected ResourceLocation getSwampWaterLootTable() {
		return LootTableRegistry.FILTERED_SWAMP_WATER;
	}

	public ItemStack chooseRandomItemFromLootTable(ResourceLocation resourceLocation) {
		LootTable lootTable = getWorld().getLootTableManager().getLootTableFromLocation(resourceLocation);
		if (lootTable != null) {
			LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);
			List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot);
				return loot.get(0);
			}
		}
		return new ItemStack(ItemRegistry.GLUE); // to stop null;
	}

	private void damageFilter(int damage) {
		ItemStack mesh = getStackInSlot(0);
		if(!mesh.isEmpty()) {
			mesh.setItemDamage(mesh.getItemDamage() +1);
			if(mesh.getItemDamage() > mesh.getMaxDamage()) {
				mesh.shrink(1);
				getWorld().playEvent(2001, getPos(), Block.getIdFromBlock(BlockRegistry.WEEDWOOD_PLANKS));
			}
		}
	}

	private boolean hasMossFilter() {
		return !getStackInSlot(0).isEmpty() && (getStackInSlot(0).getItem() == ItemRegistry.MOSS_FILTER);
	}

	private boolean hasSilkFilter() {
		return !getStackInSlot(0).isEmpty() && (getStackInSlot(0).getItem() == ItemRegistry.SILK_FILTER);
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
        nbt.setBoolean("showFluidAnimation", showFluidAnimation);
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    protected void readPacketNbt(NBTTagCompound nbt) {
        NBTTagCompound compound = nbt;
        tank.readFromNBT(compound.getCompoundTag("tank"));
        setFluidAnimation(compound.getBoolean("showFluidAnimation"));
        this.readInventoryNBT(nbt);
    }

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		tank.readFromNBT(tagCompound);
		setFluidAnimation(tagCompound.getBoolean("showFluidAnimation"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tank.writeToNBT(tagCompound);
		tagCompound.setBoolean("showFluidAnimation", showFluidAnimation);
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
		return 64;
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
