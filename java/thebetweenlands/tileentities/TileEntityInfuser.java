package thebetweenlands.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.items.BLItemRegistry;

public class TileEntityInfuser extends TileEntityBasicInventory implements IFluidHandler {

	public final static int MAX_INGREDIENTS = 6;

	public final FluidTank waterTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 3);
	private int infusionTime = 0;
	private int stirProgress = 90;
	private int temp = 0;
	private int evaporation = 0;
	private int itemBob = 0;
	private boolean countUp = true;
	private boolean hasInfusion = false;
	private boolean hasCrystal = false;
	private float crystalVelocity = 0.0F;
	private float crystalRotation = 0.0F;
	private ElixirRecipe infusingRecipe = null;
	private boolean checkedRecipe = false;

	public TileEntityInfuser() {
		super(MAX_INGREDIENTS + 2, "infuser");
		waterTank.setFluid(new FluidStack(BLFluidRegistry.swampWater, 0));
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if(this.hasInfusion) {
			if(!this.checkedRecipe) {
				this.infusingRecipe = ElixirRecipes.getFromAspects(this.getInfusingAspects());
				this.checkedRecipe = true;
			}
			this.infusionTime++;
		} else {
			this.checkedRecipe = false;
			this.infusionTime = 0;
		}
		
		if (worldObj.isRemote) {
			if (isCrystalInstalled()) {
				crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
				crystalRotation += this.crystalVelocity;
				if (crystalRotation >= 360.0F) {
					crystalRotation -= 360.0F;
				} else if (this.crystalRotation <= 360.0F) {
					this.crystalRotation += 360.0F;
				}
				if (Math.abs(crystalVelocity) <= 1.0F && this.getWorldObj().rand.nextInt(15) == 0) {
					crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
				}
			}
			if(countUp && itemBob <= 20) {
				itemBob++;
				if(itemBob == 20)
					countUp = false;
			}
			if(!countUp && itemBob >= 0) {
				itemBob--;
				if(itemBob == 0)
					countUp = true;
			}
			return;
		}

		//To keep infusion time on client in sync
		if(this.infusionTime % 20 == 0) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if (stirProgress < 90) {
			stirProgress++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if (stirProgress == 89) {
			if(temp == 100 && !hasInfusion) {
				if(this.hasIngredients()) {
					hasInfusion = true;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			evaporation = 0;
		}
		if(worldObj.getBlock(xCoord, yCoord - 1, zCoord) == Blocks.fire && temp < 100 && getWaterAmount() > 0) {
			if(worldObj.getWorldTime()%12 == 0) {
				temp++;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		if(worldObj.getBlock(xCoord, yCoord - 1, zCoord) != Blocks.fire && temp > 0) {
			if(worldObj.getWorldTime()%6 == 0) {
				temp--;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		if(temp == 100) {
			evaporation++;
			if(evaporation == 600 && getWaterAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
				extractFluids(new FluidStack(BLFluidRegistry.swampWater, FluidContainerRegistry.BUCKET_VOLUME));
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if(temp < 100 && evaporation > 0) {
			evaporation--;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		if(isCrystalInstalled() && inventory[MAX_INGREDIENTS + 1].getItemDamage() < inventory[MAX_INGREDIENTS + 1].getMaxDamage()) {
			if (temp == 100 && evaporation == 500 && stirProgress >= 90 && hasInfusion) {
				inventory[MAX_INGREDIENTS + 1].setItemDamage(inventory[MAX_INGREDIENTS + 1].getItemDamage() + 1);
				stirProgress = 0;
			}
			hasCrystal = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else {
			hasCrystal = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
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
				if(temp >= 3) {
					temp = temp - temp / 3;
					evaporation = 0;
				}
				return FluidContainerRegistry.drainFluidContainer(bucket);
			}
		}
		return bucket;
	}

	public void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid())) {
			waterTank.drain(fluid.amount, true);
		}
		if (getWaterAmount() == 0) {
			if (hasInfusion) {
				for (int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS; i++) {
					setInventorySlotContents(i, null);
				}
				this.infusingRecipe = null;
				if (evaporation == 600) {
					// TODO Make this a toxic cloud entity - a job for Sam's expert render skills :P
					EntityPig piggy = new EntityPig(worldObj);
					piggy.setLocationAndAngles(xCoord + 0.5D, yCoord + 1D, zCoord + 0.5D, MathHelper.wrapAngleTo180_float(worldObj.rand.nextFloat() * 360.0F), 0.0F);
					worldObj.spawnEntityInWorld(piggy);
				}
			}
			hasInfusion = false;
			temp = 0;
		}
		evaporation = 0;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

	public boolean isCrystalInstalled() {
		return inventory[MAX_INGREDIENTS + 1] != null && inventory[MAX_INGREDIENTS + 1].getItem() == BLItemRegistry.lifeCrystal && inventory[MAX_INGREDIENTS + 1].getItemDamage() <= inventory[MAX_INGREDIENTS + 1].getMaxDamage();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("stirProgress", stirProgress);
		nbt.setInteger("evaporation", evaporation);
		nbt.setInteger("temp", temp);
		nbt.setInteger("infusionTime", infusionTime);
		nbt.setBoolean("hasInfusion", hasInfusion);
		nbt.setBoolean("hasCrystal", hasCrystal);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		stirProgress = nbt.getInteger("stirProgress");
		evaporation = nbt.getInteger("evaporation");
		temp = nbt.getInteger("temp");
		infusionTime = nbt.getInteger("infusionTime");
		hasInfusion = nbt.getBoolean("hasInfusion");
		hasCrystal = nbt.getBoolean("hasCrystal");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("stirProgress", stirProgress);
		nbt.setInteger("evaporation", evaporation);
		nbt.setInteger("temp", temp);
		nbt.setInteger("infusionTime", infusionTime);
		nbt.setBoolean("hasInfusion", hasInfusion);
		nbt.setBoolean("hasCrystal", hasCrystal);
		for (int i = 0; i < getSizeInventory(); i++) {
			if(inventory[i] != null) {
				NBTTagCompound itemStackCompound = inventory[i].writeToNBT(new NBTTagCompound());
				nbt.setTag("slotItem" + i, itemStackCompound);
			} else
				nbt.setTag("slotItem" + i, null);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		waterTank.readFromNBT(packet.func_148857_g().getCompoundTag("waterTank"));
		stirProgress = packet.func_148857_g().getInteger("stirProgress");
		evaporation = packet.func_148857_g().getInteger("evaporation");
		temp = packet.func_148857_g().getInteger("temp");
		infusionTime = packet.func_148857_g().getInteger("infusionTime");
		hasInfusion = packet.func_148857_g().getBoolean("hasInfusion");
		hasCrystal = packet.func_148857_g().getBoolean("hasCrystal");
		for (int i = 0; i < getSizeInventory(); i++) {
			NBTTagCompound itemStackCompound = packet.func_148857_g().getCompoundTag("slotItem" + i);
			if(itemStackCompound != null && itemStackCompound.getShort("id") != 0)
				inventory[i] = ItemStack.loadItemStackFromNBT(itemStackCompound);
			else
				inventory[i] = null;
		}
	}

	public boolean hasIngredients() {
		for(int i = 0; i <= MAX_INGREDIENTS; i++) {
			if(inventory[i] != null) return true;
		}
		return false;
	}

	public List<IAspect> getInfusingAspects() {
		List<IAspect> infusingAspects = new ArrayList<IAspect>();
		for(int i = 0; i <= MAX_INGREDIENTS; i++) {
			if(inventory[i] != null) {
				infusingAspects.addAll(AspectRecipes.REGISTRY.getAspects(new ItemEntry(inventory[i])));
			}
		}
		return infusingAspects;
	}

	public boolean hasFullIngredients() {
		for(int i = 0; i <= MAX_INGREDIENTS; i++) {
			if(inventory[i] == null) return false;
		}
		return true;
	}

	public int getInfusionTime() {
		return this.infusionTime;
	}

	public float getCrystalRotation() {
		return this.crystalRotation;
	}

	public int getEvaporation() {
		return this.evaporation;
	}

	public boolean hasInfusion() {
		return this.hasInfusion;
	}

	public int getItemBob() {
		return this.itemBob;
	}

	public int getStirProgress() {
		return this.stirProgress;
	}

	public int getTemperature() {
		return this.temp;
	}

	public void setStirProgress(int progress) {
		this.stirProgress = progress;
	}

	public ElixirRecipe getInfusingRecipe() {
		return this.infusingRecipe;
	}
}
