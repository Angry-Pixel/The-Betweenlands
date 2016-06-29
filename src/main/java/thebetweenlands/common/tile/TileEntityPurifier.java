package thebetweenlands.common.tile;

import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.registries.FluidRegistry;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	private static final int MAX_TIME = 432;
	public final FluidTank waterTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
	public int time = 0;
	public boolean lightOn = false;
	private int prevStackSize = 0;
	private Item prevItem;
	private boolean isPurifyingClient = false;

	public TileEntityPurifier() {
		super(3, "container.purifier");
		waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER, 0));
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
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("state", lightOn);
		compound.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		NBTTagCompound itemStackCompound = new NBTTagCompound();
		if (inventory[2] != null) {
			inventory[2].writeToNBT(itemStackCompound);
		}
		compound.setTag("outputItem", itemStackCompound);
		compound.setBoolean("isPurifying", this.isPurifying());
		return new SPacketUpdateTileEntity(pos, 0, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		NBTTagCompound compound = packet.getNbtCompound();
		lightOn = compound.getBoolean("state");
		waterTank.readFromNBT(compound.getCompoundTag("waterTank"));
		NBTTagCompound itemStackCompound = compound.getCompoundTag("outputItem");
		if (itemStackCompound.getShort("id") != 0) {
			inventory[2] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		} else {
			inventory[2] = null;
		}
		isPurifyingClient = compound.getBoolean("isPurifying");
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
					worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation("thebetweenlands:purifier")), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				if (!lightOn)
					setIlluminated(true);
				if (time >= MAX_TIME) {
					for (int i = 0; i < 2; i++)
						if (inventory[i] != null)
							if (--inventory[i].stackSize <= 0)
								inventory[i] = null;
					extractFluids(new FluidStack(thebetweenlands.common.registries.FluidRegistry.SWAMP_WATER, FluidContainerRegistry.BUCKET_VOLUME));
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

	public ItemStack fillTankWithBucket(ItemStack bucket) {
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
		if (fluid != null) {
			int amountFilled = fill(null, fluid, false);
			if (amountFilled == fluid.amount) {
				fill(null, fluid, true);
				return FluidContainerRegistry.drainFluidContainer(bucket);
			}
		}
		return bucket;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource == null)
			return 0;
		else if (resource.getFluid() == FluidRegistry.SWAMP_WATER)
			return waterTank.fill(resource, doFill);
		else
			return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		FluidTankInfo[] infos = new FluidTankInfo[1];
		infos[0] = new FluidTankInfo(waterTank.getFluid(), waterTank.getCapacity());
		return infos;
	}
}
