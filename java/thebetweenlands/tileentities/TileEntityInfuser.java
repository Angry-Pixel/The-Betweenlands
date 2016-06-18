package thebetweenlands.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
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
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.EntityGasCloud;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.ColorUtils;

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
	private boolean updateRecipe = false;

	/** 0 = no progress, 1 = in progress, 2 = finished, 3 = failed **/
	private int currentInfusionState = 0;
	private int prevInfusionState = 0;
	private int infusionColorGradientTicks = 0;

	public float[] prevInfusionColor = new float[4];
	public float[] currentInfusionColor = new float[4];
	private float[] currentInfusionColorState2 = new float[4];

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
		if(this.updateRecipe) {
			this.updateInfusingRecipe();
			this.updateRecipe = false;
		}
		boolean updateBlock = false;
		if(this.hasInfusion && this.infusingRecipe != null) {
			if(!this.worldObj.isRemote) {
				this.infusionTime++;
			} else {
				if(this.prevInfusionState != this.currentInfusionState) {
					if(this.currentInfusionState == 2) {
						this.worldObj.playSound(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "thebetweenlands:infuserFinished", 1, 1, false);
					}
					this.prevInfusionColor = this.currentInfusionColor;
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				} else {
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				}
			}
			this.prevInfusionState = this.currentInfusionState;
			if(!this.worldObj.isRemote) {
				if(this.infusionTime > this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//fail
					if(this.currentInfusionState != 3)
						updateBlock = true;
					this.currentInfusionState = 3;
				} else if(this.infusionTime > this.infusingRecipe.idealInfusionTime - this.infusingRecipe.infusionTimeVariation
						&& this.infusionTime < this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//finished
					if(this.currentInfusionState != 2)
						updateBlock = true;
					this.currentInfusionState = 2;
				} else {
					//progress
					if(this.currentInfusionState != 1)
						updateBlock = true;
					this.currentInfusionState = 1;
				}
			}
			if(this.infusionColorGradientTicks > 0) {
				this.infusionColorGradientTicks++;
			}
			if(!this.worldObj.isRemote && this.currentInfusionState != prevInfusionState) {
				//start gradient animation
				this.infusionColorGradientTicks = 1;
				updateBlock = true;
			}
			if(!this.worldObj.isRemote && this.infusionColorGradientTicks > 30) {
				this.infusionColorGradientTicks = 0;
				updateBlock = true;
			}
			if(this.worldObj.isRemote && this.infusionColorGradientTicks > 0) {
				if(this.worldObj.isRemote && this.currentInfusionState == 2) {
					for(int i = 0; i < 10; i++) {
						double x = this.xCoord + 0.25F + this.worldObj.rand.nextFloat() * 0.5F;
						double z = this.zCoord + 0.25F + this.worldObj.rand.nextFloat() * 0.5F;
						BLParticle.STEAM_PURIFIER.spawn(this.worldObj, x, this.yCoord + 1.0D - this.worldObj.rand.nextFloat() * 0.2F, z, 0.0D, 0.0D, 0.0D, 0);
					}
				}
			}
		} else {
			if(this.currentInfusionState != 0)
				updateBlock = true;
			this.currentInfusionState = 0;
			this.infusionTime = 0;
			this.currentInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
			this.prevInfusionColor = this.currentInfusionColor;
		}
		if(!this.worldObj.isRemote && updateBlock)
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		if (worldObj.isRemote) {
			if (isValidCrystalInstalled()) {
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
		if(isValidCrystalInstalled()) {
			if (temp >= 100 && evaporation >= 400 && stirProgress >= 90 && hasInfusion) {
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

	/**
	 * Returns the current infusing state:
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 */
	public int getInfusingState()  {
		return this.currentInfusionState;
	}

	/**
	 * Returns the infusion color gradient ticks
	 * @return
	 */
	public int getInfusionColorGradientTicks() {
		return this.infusionColorGradientTicks;
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
					ItemStack stack = getStackInSlot(i);
					if(stack != null && stack.getItem() == BLItemRegistry.aspectVial) {
						//Return empty vials
						ItemStack ret = null;
						switch(stack.getItemDamage()) {
						case 0:
						default:
							ret = new ItemStack(BLItemRegistry.dentrothystVial, 1, 0);
							break;
						case 1:
							ret = new ItemStack(BLItemRegistry.dentrothystVial, 1, 2);
							break;
						}
						EntityItem entity = new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, ret);
						this.worldObj.spawnEntityInWorld(entity);
					}
					setInventorySlotContents(i, null);
				}
				if (evaporation == 600) {
					EntityGasCloud gasCloud = new EntityGasCloud(this.worldObj);
					if(this.infusingRecipe != null) {
						float[] color = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
						gasCloud.setGasColor(ColorUtils.toHex(color[0], color[1], color[2], 0.66F));
					}
					gasCloud.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1D, this.zCoord + 0.5D, MathHelper.wrapAngleTo180_float(this.worldObj.rand.nextFloat() * 360.0F), 0.0F);
					this.worldObj.spawnEntityInWorld(gasCloud);
				}
				this.infusingRecipe = null;
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

	public boolean isValidCrystalInstalled() {
		return inventory[MAX_INGREDIENTS + 1] != null && inventory[MAX_INGREDIENTS + 1].getItem() == BLItemRegistry.lifeCrystal && inventory[MAX_INGREDIENTS + 1].getItemDamage() < inventory[MAX_INGREDIENTS + 1].getMaxDamage();
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
		nbt.setInteger("infusionState", this.currentInfusionState);
		nbt.setInteger("infusionColorGradientTicks", this.infusionColorGradientTicks);
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
		currentInfusionState = nbt.getInteger("infusionState");
		infusionColorGradientTicks = nbt.getInteger("infusionColorGradientTicks");
		this.updateRecipe = true;
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
			NBTTagCompound itemStackCompound = new NBTTagCompound();
			if(inventory[i] != null) {
				inventory[i].writeToNBT(itemStackCompound);
			} 
			nbt.setTag("slotItem" + i, itemStackCompound);
		}
		nbt.setInteger("infusionState", this.currentInfusionState);
		nbt.setInteger("infusionColorGradientTicks", this.infusionColorGradientTicks);
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
		currentInfusionState = packet.func_148857_g().getInteger("infusionState");
		infusionColorGradientTicks = packet.func_148857_g().getInteger("infusionColorGradientTicks");
		this.updateInfusingRecipe();
	}

	public boolean hasIngredients() {
		for(int i = 0; i <= MAX_INGREDIENTS; i++) {
			if(inventory[i] != null) return true;
		}
		return false;
	}

	public List<IAspectType> getInfusingAspects() {
		List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
		for(int i = 0; i <= MAX_INGREDIENTS; i++) {
			if(inventory[i] != null) {
				infusingAspects.addAll(AspectManager.get(this.worldObj).getDiscoveredAspectTypes(inventory[i], null));
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

	public void updateInfusingRecipe() {
		if(this.worldObj != null)
			this.infusingRecipe = ElixirRecipes.getFromAspects(this.getInfusingAspects());
	}
}
