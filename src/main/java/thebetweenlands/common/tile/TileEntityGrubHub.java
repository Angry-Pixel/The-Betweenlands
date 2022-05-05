package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.block.plant.BlockWeedwoodBushInfested;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityGrubHub extends TileEntityBasicInventory implements ITickable {
	
	public FluidTank tank;
	private IItemHandler itemHandler;
	public TileEntityGrubHub() {
		super(1, "container.bl.grub_hub");
		this.tank = new FluidTank(Fluid.BUCKET_VOLUME * 8) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return canFill() && fluid.getFluid() == FluidRegistry.DRINKABLE_BREW && fluid.tag != null && fluid.tag.hasKey("color") && fluid.tag.getInteger("color") == 2;
			}
		}; //ewww
        this.tank.setTileEntity(this);
	}

	@Override
	public void update() {
		if (getWorld().getTotalWorldTime()%20 == 0)
			checkCanInfestOrHarvest(getWorld());
	}

	private void checkCanInfestOrHarvest(World world) {
		AxisAlignedBB axisalignedbb = areaOfEffect();
		int minX = MathHelper.floor(axisalignedbb.minX);
		int maxX = MathHelper.floor(axisalignedbb.maxX);
		int minY = MathHelper.floor(axisalignedbb.minY);
		int maxY = MathHelper.floor(axisalignedbb.maxY);
		int minZ = MathHelper.floor(axisalignedbb.minZ);
		int maxZ = MathHelper.floor(axisalignedbb.maxZ);
		MutableBlockPos mutablePos = new MutableBlockPos();

		for (int x = minX; x < maxX; x++)
			for (int y = minY; y < maxY; y++)
				for (int z = minZ; z < maxZ; z++) {
					IBlockState state = getWorld().getBlockState(mutablePos.setPos(x, y, z));
						if (state.getBlock() instanceof BlockWeedwoodBush && !(state.getBlock() instanceof BlockWeedwoodBushInfested) && tank.getFluidAmount() > 0) {
							if(!world.isRemote) {
								infestBush(mutablePos);
							}
							if (world.isRemote) {
								Vec3d vector = new Vec3d((mutablePos.getX() + 0.5D) - (pos.getX() + 0.5D), (mutablePos.getY() + 1D) - (pos.getY() + 0.325D), (mutablePos.getZ() + 0.5D) - (pos.getZ() + 0.5D));
								for(int i = 0; i < 20 + world.rand.nextInt(5); i++) {
									BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 0.325F, pos.getZ() + 0.5F, 
											ParticleArgs.get()
											.withMotion(vector.x * 0.08f, vector.y * 0.08F, vector.z * 0.08F)
											.withScale(0.6f + world.rand.nextFloat() * 5.0F)
											.withColor(1F, 1.0F, 1.0F, 0.05f)
											.withData(80, true, 0.01F, true)));
								}
							}
						}
						else if (!world.isRemote && state.getBlock() instanceof BlockWeedwoodBushInfested && state.getBlock() == BlockRegistry.WEEDWOOD_BUSH_INFESTED_2) {
							harvestGrub(mutablePos);
						}
					}
	}

	private void harvestGrub(MutableBlockPos mutablePos) {
		//play suck sound
		getWorld().setBlockState(mutablePos, BlockRegistry.WEEDWOOD_BUSH.getDefaultState(), 3);
		ItemStack contents = getStackInSlot(0);
		ItemStack silk_grub = new ItemStack(ItemRegistry.SILK_GRUB, 1);
		
		if (contents.isEmpty())
			setInventorySlotContents(0, silk_grub);

		if (!contents.isEmpty() && contents.getItem() == ItemRegistry.SILK_GRUB) {
			if(contents.getCount() < getInventoryStackLimit())
				contents.grow(1);
			else {
				EntityItem item = new EntityItem(world, mutablePos.getX() + 0.5D, mutablePos.getY() + 1.0D, mutablePos.getZ() + 0.5D, silk_grub);
				//item.motionX = item.motionY = item.motionZ = 0D;
				item.motionY = 0.003D;
				world.spawnEntity(item);
			}
		}
		markForUpdate();
	}

	private void infestBush(MutableBlockPos mutablePos) {
		//play exhale noise
		getWorld().setBlockState(mutablePos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_0.getDefaultState(), 3);
		tank.drain(100, true);
		markForUpdate();
	}

	public AxisAlignedBB areaOfEffect() {
		return new AxisAlignedBB(pos.down()).grow(1D, 0D, 1D);	
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tank.writeToNBT(tagCompound);
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
