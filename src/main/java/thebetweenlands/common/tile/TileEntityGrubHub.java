package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.block.plant.BlockWeedwoodBushInfested;
import thebetweenlands.common.network.clientbound.MessageInfestWeedwoodBush;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityGrubHub extends TileEntityBasicInventory implements ITickable {

	public FluidTank tank;
	private IItemHandler itemHandler;

	public int switchTextureCount = 0;

	public TileEntityGrubHub() {
		super(1, "container.bl.grub_hub");
		this.tank = new FluidTank(Fluid.BUCKET_VOLUME * 8) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return canFill() && fluid.getFluid() == FluidRegistry.DRINKABLE_BREW && fluid.tag != null && fluid.tag.hasKey("type") && fluid.tag.getInteger("type") == 2;
			}
		}; //ewww
		this.tank.setTileEntity(this);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		if (!world.isRemote && getWorld().getTotalWorldTime() % 10 == 0)
			checkCanInfestOrHarvest(getWorld());

		if(world.isRemote && switchTextureCount > 0)
			switchTextureCount--;
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

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					IBlockState state = getWorld().getBlockState(mutablePos.setPos(x, y, z));
					if (state.getBlock() instanceof BlockWeedwoodBush && !(state.getBlock() instanceof BlockWeedwoodBushInfested) && tank.getFluidAmount() >= 50) {
						infestBush(mutablePos);
					}
					else if (state.getBlock() instanceof BlockWeedwoodBushInfested && state.getBlock() == BlockRegistry.WEEDWOOD_BUSH_INFESTED_2 && canAddGrub(0)) {
						harvestGrub(mutablePos);
					}
				}
			}
		}
	}

	private void harvestGrub(MutableBlockPos mutablePos) {
		getWorld().playSound(null, this.pos, SoundRegistry.GRUB_HUB_SUCK, SoundCategory.BLOCKS, 0.5F, 1F);
		getWorld().setBlockState(mutablePos, BlockRegistry.WEEDWOOD_BUSH.getDefaultState(), 3);
		ItemStack contents = getStackInSlot(0);
		ItemStack silk_grub = new ItemStack(ItemRegistry.SILK_GRUB, 1);
		if (contents.isEmpty())
			setInventorySlotContents(0, silk_grub);
		else
			contents.grow(1);
		markForUpdate();
	}

	private boolean canAddGrub(int slot) {
		// this check could be used to stop the infestation of the bush beforehand, but atm I'm letting the bush spawn nasties if inventory is full
		ItemStack contents = getStackInSlot(slot);
		return contents.isEmpty() ? true : (!contents.isEmpty() && contents.getItem() == ItemRegistry.SILK_GRUB && contents.getCount() < getInventoryStackLimit()) ? true : false;
	}

	private void infestBush(MutableBlockPos mutablePos) {
		getWorld().playSound(null, this.pos, SoundRegistry.GRUB_HUB_MIST, SoundCategory.BLOCKS, 0.5F, 1F);
		getWorld().setBlockState(mutablePos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_0.getDefaultState(), 3);

		tank.drain(50, true);

		markForUpdate();

		TheBetweenlands.networkWrapper.sendToAllAround(new MessageInfestWeedwoodBush(this, mutablePos.toImmutable()), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 32D));
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
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		if(stack.getItem() != ItemRegistry.SILK_GRUB)
			return false;
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
		if(stack.getItem() != ItemRegistry.SILK_GRUB)
			return false;
		return true;
	}
}
