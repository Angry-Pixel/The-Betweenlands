package thebetweenlands.tileentities;

import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
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
import thebetweenlands.inventory.container.ContainerPurifier;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.PurifierRecipe;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler {

	public final FluidTank waterTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
	public int time = 0;
	private static final int MAX_TIME = 432;
	public boolean lightOn = false;
	private int prevStackSize = 0;
	private Item prevItem;

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
		lightOn = nbt.getBoolean("state");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setBoolean("state", lightOn);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("state", lightOn);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		if(inventory[2] != null) {
			NBTTagCompound itemStackCompound = inventory[2].writeToNBT(new NBTTagCompound());
			nbt.setTag("outputItem", itemStackCompound);
		} else {
			nbt.setTag("outputItem", null);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		lightOn = packet.func_148857_g().getBoolean("state");
		waterTank.readFromNBT(packet.func_148857_g().getCompoundTag("waterTank"));
		NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("outputItem");
		if(itemStackCompound != null && itemStackCompound.getShort("id") != 0) {
			inventory[2] = ItemStack.loadItemStackFromNBT(itemStackCompound);
		} else {
			inventory[2] = null;
		}
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
				if(time% 108 == 0)
					worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thebetweenlands:purifier", 1F, 1F);
				if(!lightOn)
					setIlluminated(true);
				if (time >= MAX_TIME) {
					for (int i = 0; i < 2; i++)
						if (inventory[i] != null)
							if (--inventory[i].stackSize <= 0)
								inventory[i] = null;
					extractFluids(new FluidStack(BLFluidRegistry.swampWater, FluidContainerRegistry.BUCKET_VOLUME));
					if (inventory[2] == null) {
						inventory[2] = output.copy();
					} else if (inventory[2].isItemEqual(output)) {
						inventory[2].stackSize += output.stackSize;
					}
					time = 0;
					markDirty();
					boolean canRun = output != null && getWaterAmount() > 0 && inventory[2] == null || output != null && getWaterAmount() > 0 && inventory[2] != null && inventory[2].isItemEqual(output);
					if(!canRun) setIlluminated(false);
				}
			}
		}
		if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
			time = 0;
			markDirty();
			setIlluminated(false);
		}
		if(this.prevStackSize != (inventory[2] != null ? inventory[2].stackSize : 0)) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if(this.prevItem != (inventory[2] != null ? inventory[2].getItem() : null)) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		this.prevItem = inventory[2] != null ? inventory[2].getItem() : null;
		this.prevStackSize = inventory[2] != null ? inventory[2].stackSize : 0;
	}

	private void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid()))
			waterTank.drain(fluid.amount, true);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean hasFuel() {
		return getStackInSlot(0) != null && getStackInSlot(0).getItem() == BLItemRegistry.materialsBL && getStackInSlot(0).getItemDamage() == EnumMaterialsBL.SULFUR.ordinal() && getStackInSlot(0).stackSize >= 1;
	}

	private boolean outputIsFull() {
		return getStackInSlot(2) != null && getStackInSlot(2).stackSize >= getInventoryStackLimit();
	}

	public void setIlluminated(boolean state) {
		lightOn = state;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 0, lightOn ? 1 : 0);
	}

	@Override
	public boolean receiveClientEvent(int eventId, int eventData) {
		switch (eventId) {
		case 0:
			lightOn = eventData == 1;
			worldObj.func_147451_t(xCoord, yCoord, zCoord);
			return true;
		default:
			return false;
		}
	}
}
