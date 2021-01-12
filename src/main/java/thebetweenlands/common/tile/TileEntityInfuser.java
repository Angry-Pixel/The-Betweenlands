package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO: Send resulting elixir recipe with the NBT
public class TileEntityInfuser extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	public static final int MAX_INGREDIENTS = 6;

	public final FluidTank waterTank;

	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

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

	/**
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 **/
	private int currentInfusionState = 0;
	private int prevInfusionState = 0;
	private int infusionColorGradientTicks = 0;

	public float[] prevInfusionColor = new float[4];
	public float[] currentInfusionColor = new float[4];

	public TileEntityInfuser() {
		super(MAX_INGREDIENTS + 2, "container.bl.infuser");
		this.waterTank = new FluidTank(FluidRegistry.SWAMP_WATER, 0, Fluid.BUCKET_VOLUME * 3);
		this.waterTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.waterTank);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(this.hasInfusion) {
			return 0; //Don't allow refill when infusing has already started
		}
		int filled = this.waterTank.fill(resource, false);
		if (filled == resource.amount && doFill) {
			this.waterTank.fill(resource, true);
			if (temp >= 3) {
				temp = temp - temp / 3;
				evaporation = 0;
			}

			if (doFill) {
				this.markDirty();
				IBlockState stat = this.world.getBlockState(this.pos);
				this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
			}
		}
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
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
	public void update() {
		BlockPos pos = this.getPos();
		
		if (this.updateRecipe) {
			this.updateInfusingRecipe();
			this.updateRecipe = false;
		}
		boolean updateBlock = false;
		if (this.hasInfusion && this.infusingRecipe != null) {
			if (!this.world.isRemote) {
				this.infusionTime++;
			} else {
				if (this.prevInfusionState != this.currentInfusionState) {
					this.prevInfusionColor = this.currentInfusionColor;
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				} else {
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				}
			}
			if(this.prevInfusionState != this.currentInfusionState && this.currentInfusionState == 2) {
				this.world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.INFUSER_FINISHED, SoundCategory.BLOCKS, 1, 1);
			}
			this.prevInfusionState = this.currentInfusionState;
			if (!this.world.isRemote) {
				if (this.infusionTime > this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//fail
					if (this.currentInfusionState != 3)
						updateBlock = true;
					this.currentInfusionState = 3;
				} else if (this.infusionTime > this.infusingRecipe.idealInfusionTime - this.infusingRecipe.infusionTimeVariation
						&& this.infusionTime < this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//finished
					if (this.currentInfusionState != 2)
						updateBlock = true;
					this.currentInfusionState = 2;
				} else {
					//progress
					if (this.currentInfusionState != 1)
						updateBlock = true;
					this.currentInfusionState = 1;
				}
			}
			if (this.infusionColorGradientTicks > 0) {
				this.infusionColorGradientTicks++;
			}
			if (!this.world.isRemote && this.currentInfusionState != prevInfusionState) {
				//start gradient animation
				this.infusionColorGradientTicks = 1;
				updateBlock = true;
			}
			if (!this.world.isRemote && this.infusionColorGradientTicks > 30) {
				this.infusionColorGradientTicks = 0;
				updateBlock = true;
			}
			if (this.world.isRemote && this.infusionColorGradientTicks > 0 && this.currentInfusionState == 2) {
				for (int i = 0; i < 10; i++) {
					double x = pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F;
					double z = pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F;
					BLParticles.STEAM_PURIFIER.spawn(this.world, x, pos.getY() + 1.0D - this.world.rand.nextFloat() * 0.2F, z);
				}
			}
		} else {
			if (this.currentInfusionState != 0)
				updateBlock = true;
			
			this.infusionTime = 0;
			
			if(this.hasIngredients() && this.temp >= 100) {
				if (this.infusionColorGradientTicks > 0) {
					this.infusionColorGradientTicks++;
				}
				
				if (!this.world.isRemote && this.infusionColorGradientTicks == 0 && this.currentInfusionState == 0 && this.stirProgress == 89) {
					//start gradient animation
					this.infusionColorGradientTicks = 1;
					this.currentInfusionState = 1;
					this.world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.INFUSER_FINISHED, SoundCategory.BLOCKS, 1, 1);
					updateBlock = true;
				}
				
				if (!this.world.isRemote && this.infusionColorGradientTicks > 30) {
					this.infusionColorGradientTicks = 0;
					this.currentInfusionState = 2;
					updateBlock = true;
				}
				
				if(this.world.isRemote && (this.infusionColorGradientTicks > 0 || this.currentInfusionState == 2)) {
					this.prevInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
					this.currentInfusionColor = new float[]{0.8F, 0.0F, 0.8F, 1.0F};
				}
				
				if (this.world.isRemote && this.infusionColorGradientTicks > 0) {
					for (int i = 0; i < 10; i++) {
						double x = pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F;
						double z = pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F;
						BLParticles.STEAM_PURIFIER.spawn(this.world, x, pos.getY() + 1.0D - this.world.rand.nextFloat() * 0.2F, z);
					}
				}
			} else {
				this.currentInfusionState = 0;
				this.currentInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
				this.prevInfusionColor = this.currentInfusionColor;
			}
		}
		if (!this.world.isRemote && updateBlock) {
			this.markForUpdate();
		}
		if (world.isRemote) {
			if (isValidCrystalInstalled()) {
				crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
				crystalRotation += this.crystalVelocity;
				if (crystalRotation >= 360.0F) {
					crystalRotation -= 360.0F;
				} else if (this.crystalRotation <= 360.0F) {
					this.crystalRotation += 360.0F;
				}
				if (Math.abs(crystalVelocity) <= 1.0F && this.getWorld().rand.nextInt(15) == 0) {
					crystalVelocity = this.world.rand.nextFloat() * 18.0F - 9.0F;
				}
			}
			if (countUp && itemBob <= 20) {
				itemBob++;
				if (itemBob == 20)
					countUp = false;
			}
			if (!countUp && itemBob >= 0) {
				itemBob--;
				if (itemBob == 0)
					countUp = true;
			}
			return;
		}

		//To keep infusion time on client in sync
		if (this.infusionTime > 0 && this.infusionTime % 20 == 0) {
			this.markForUpdate();
		}

		if (stirProgress < 90) {
			stirProgress++;
			this.markForUpdate();
		}
		if (stirProgress == 89) {
			if (temp == 100 && !hasInfusion) {
				if (this.hasIngredients()) {
					hasInfusion = true;
					this.markForUpdate();
				}
			}
			evaporation = 0;
		}
		if (world.getBlockState(pos.down()).getBlock() == Blocks.FIRE && temp < 100 && getWaterAmount() > 0) {
			if (world.getTotalWorldTime() % 12 == 0) {
				temp++;
				this.markForUpdate();
			}
		}
		if (world.getBlockState(pos.down()).getBlock() != Blocks.FIRE && temp > 0) {
			if (world.getTotalWorldTime() % 6 == 0) {
				temp--;
				this.markForUpdate();
			}
		}
		if (temp == 100) {
			evaporation++;
			if (evaporation == 600 && getWaterAmount() >= Fluid.BUCKET_VOLUME) {
				extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
			}
			this.markForUpdate();
		}
		if (temp < 100 && evaporation > 0) {
			evaporation--;
			this.markForUpdate();
		}
		if (isValidCrystalInstalled()) {
			if (temp >= 100 && evaporation >= 400 && stirProgress >= 90 && this.hasIngredients()) {
				inventory.get(MAX_INGREDIENTS + 1).setItemDamage(inventory.get(MAX_INGREDIENTS + 1).getItemDamage() + 1);
				stirProgress = 0;
			}
			if (!hasCrystal) {
				hasCrystal = true;
				this.markForUpdate();
			}
		} else {
			if (hasCrystal) {
				hasCrystal = false;
				this.markForUpdate();
			}
		}
	}

	/**
	 * Returns the current infusing state:
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 */
	public int getInfusingState() {
		return this.currentInfusionState;
	}

	/**
	 * Returns the infusion color gradient ticks
	 *
	 * @return
	 */
	public int getInfusionColorGradientTicks() {
		return this.infusionColorGradientTicks;
	}

	public void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid())) {
			waterTank.drain(fluid.amount, true);
		}
		if (getWaterAmount() == 0) {
			if (hasInfusion) {
				for (int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS; i++) {
					ItemStack stack = getStackInSlot(i);
					if (!stack.isEmpty() && stack.getItem() == ItemRegistry.ASPECT_VIAL) {
						//Return empty vials
						ItemStack ret = ItemStack.EMPTY;
						switch (stack.getItemDamage()) {
						case 0:
						default:
							ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 0);
							break;
						case 1:
							ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 2);
							break;
						}
						EntityItem entity = new EntityItem(this.world, this.getPos().getX() + 0.5D, this.getPos().getY() + 1.0D, this.getPos().getZ() + 0.5D, ret);
						this.world.spawnEntity(entity);
					}
					setInventorySlotContents(i, ItemStack.EMPTY);
				}
				if (evaporation == 600) {
					EntityGasCloud gasCloud = new EntityGasCloud(this.world);
					if (this.infusingRecipe != null) {
						float[] color = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
						gasCloud.setGasColor((int)(color[0] * 255), (int)(color[1] * 255), (int)(color[2] * 255), 170);
					}
					gasCloud.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY() + 1D, this.pos.getZ() + 0.5D, MathHelper.wrapDegrees(this.world.rand.nextFloat() * 360.0F), 0.0F);
					this.world.spawnEntity(gasCloud);
				}
				this.infusingRecipe = null;
			}
			hasInfusion = false;
			temp = 0;
			waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER, 0));
		}
		evaporation = 0;
		this.markForUpdate();
	}

	public void markForUpdate() {
		IBlockState state = this.world.getBlockState(this.getPos());
		this.world.notifyBlockUpdate(this.getPos(), state, state, 2);
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
		return !inventory.get(MAX_INGREDIENTS + 1).isEmpty() && inventory.get(MAX_INGREDIENTS + 1).getItem() instanceof ItemLifeCrystal && inventory.get(MAX_INGREDIENTS + 1).getItemDamage() < inventory.get(MAX_INGREDIENTS + 1).getMaxDamage();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
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
		return nbt;
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		this.readInventoryNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		stirProgress = nbt.getInteger("stirProgress");
		evaporation = nbt.getInteger("evaporation");
		temp = nbt.getInteger("temp");
		infusionTime = nbt.getInteger("infusionTime");
		hasInfusion = nbt.getBoolean("hasInfusion");
		hasCrystal = nbt.getBoolean("hasCrystal");
		currentInfusionState = nbt.getInteger("infusionState");
		infusionColorGradientTicks = nbt.getInteger("infusionColorGradientTicks");
		this.updateInfusingRecipe();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeInventoryNBT(nbt);
		nbt.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("stirProgress", stirProgress);
		nbt.setInteger("evaporation", evaporation);
		nbt.setInteger("temp", temp);
		nbt.setInteger("infusionTime", infusionTime);
		nbt.setBoolean("hasInfusion", hasInfusion);
		nbt.setBoolean("hasCrystal", hasCrystal);
		nbt.setInteger("infusionState", this.currentInfusionState);
		nbt.setInteger("infusionColorGradientTicks", this.infusionColorGradientTicks);
		return nbt;
	}

	public boolean hasIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!inventory.get(i).isEmpty()) return true;
		}
		return false;
	}

	public List<IAspectType> getInfusingAspects() {
		List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!inventory.get(i).isEmpty()) {
				ItemAspectContainer container = ItemAspectContainer.fromItem(inventory.get(i), AspectManager.get(this.world));
				for (Aspect aspect : container.getAspects()) {
					infusingAspects.add(aspect.type);
				}
			}
		}
		return infusingAspects;
	}

	public boolean hasFullIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (inventory.get(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !hasInfusion() && getStackInSlot(slot).isEmpty() && ((slot <= MAX_INGREDIENTS && ItemAspectContainer.fromItem(itemstack, AspectManager.get(world)).getAspects().size() > 0) || (slot == MAX_INGREDIENTS + 1 && itemstack.getItem() instanceof ItemLifeCrystal));
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		IBlockState state = world.getBlockState(pos);
		this.world.notifyBlockUpdate(pos, state, state, 2);
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
		if (this.world != null)
			this.infusingRecipe = ElixirRecipes.getFromAspects(this.getInfusingAspects());
	}
}
