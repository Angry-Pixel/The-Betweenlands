package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.common.block.container.BlockCenser;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.censer.AbstractCenserRecipe;

public class TileEntityCenser extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	private final FluidTank fluidTank;
	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	private static final int INV_SIZE = 3;

	private int remainingItemAmount = 0;

	private float prevDungeonFogStrength = 0.0f;
	private float dungeonFogStrength = 0.0f;

	private float prevEffectStrength = 0.0f;
	private float effectStrength = 0.0f;

	private int maxConsumptionTicks;
	private int consumptionTicks;

	private boolean isFluidRecipe;
	private Object currentRecipeContext;
	private ICenserRecipe<Object> currentRecipe;

	private boolean internalSlotChanged = false;

	private int maxFuelTicks;
	private int fuelTicks; 

	private boolean checkInternalSlotForRecipes = true;
	private boolean checkInputSlotForTransfer = true;

	private boolean isRecipeRunning = false;

	public TileEntityCenser() {
		super("container.censer", NonNullList.withSize(INV_SIZE, ItemStack.EMPTY), (te, inv) -> new ItemStackHandler(inv) {
			@Override
			public void setSize(int size) {
				this.stacks = te.inventory = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
			}

			@Override
			protected void onContentsChanged(int slot) {
				// Don't mark dirty while loading chunk!
				if(te.hasWorld()) {
					te.markDirty();

					//Sync internal slot. Required on client side
					//for rendering stuff
					if(slot == ContainerCenser.SLOT_INTERNAL) {
						((TileEntityCenser) te).internalSlotChanged = true;
					}
				}

				if(slot == ContainerCenser.SLOT_INTERNAL) {
					((TileEntityCenser) te).checkInternalSlotForRecipes = true;
					((TileEntityCenser) te).checkInputSlotForTransfer = true;
				} else if(slot == ContainerCenser.SLOT_INPUT) {
					((TileEntityCenser) te).checkInputSlotForTransfer = true;
				}
			}

			//Internal slot stores at most 1 item and can't be extracted

			@Override
			public int getSlotLimit(int slot) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getSlotLimit(slot);
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getStackLimit(slot, stack);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? stack : super.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
			}
		});
		this.fluidTank = new FluidTank(null, Fluid.BUCKET_VOLUME * 8) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return super.canFillFluidType(fluid) && TileEntityCenser.this.getEffect(fluid) != null;
			}
		};
		this.fluidTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.fluidTank);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
		this.remainingItemAmount = nbt.getInteger("remainingItemAmount");
		this.consumptionTicks = nbt.getInteger("consumptionTicks");
		this.maxConsumptionTicks = nbt.getInteger("maxConsumptionTicks");
		this.fuelTicks = nbt.getInteger("fuelTicks");
		this.maxFuelTicks = nbt.getInteger("maxFuelTicks");
		this.readRecipeNbt(nbt, false);
	}

	@SuppressWarnings("unchecked")
	protected void readRecipeNbt(NBTTagCompound nbt, boolean packet) {
		this.isFluidRecipe = nbt.getBoolean("fluidRecipe");

		this.currentRecipe = null;
		this.currentRecipeContext = null;

		if(nbt.hasKey("recipeId", Constants.NBT.TAG_STRING)) {
			ResourceLocation id = new ResourceLocation(nbt.getString("recipeId"));

			ICenserRecipe<Object> recipe = null;
			Object recipeContext = null;

			if(this.isFluidRecipe && this.fluidTank.getFluid() != null) {
				recipe = (ICenserRecipe<Object>) this.getEffect(this.fluidTank.getFluid());
				if(recipe != null) {
					recipeContext = recipe.createContext(this.fluidTank.getFluid());
				}
			} else if(!this.isFluidRecipe) {
				recipe = (ICenserRecipe<Object>) this.getEffect(this.getStackInSlot(ContainerCenser.SLOT_INTERNAL));
				if(recipe != null) {
					recipeContext = recipe.createContext(this.getStackInSlot(ContainerCenser.SLOT_INTERNAL));
				}
			}

			if(recipe != null && this.canRecipeRun(this.isFluidRecipe, recipe)) {
				this.currentRecipe = recipe;
				this.currentRecipeContext = recipeContext;

				if(recipeContext != null && id.equals(recipe.getId()) && nbt.hasKey("recipeNbt", Constants.NBT.TAG_COMPOUND)) {
					NBTTagCompound recipeNbt = nbt.getCompoundTag("recipeNbt");
					recipe.read(recipeContext, recipeNbt, packet);
				}
			} else {
				this.consumptionTicks = -1;
				this.maxConsumptionTicks = -1;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setTag("fluidTank", this.fluidTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("remainingItemAmount", this.remainingItemAmount);
		nbt.setInteger("consumptionTicks", this.consumptionTicks);
		nbt.setInteger("maxConsumptionTicks", this.maxConsumptionTicks);
		nbt.setInteger("fuelTicks", this.fuelTicks);
		nbt.setInteger("maxFuelTicks", this.maxFuelTicks);
		this.writeRecipeNbt(nbt, false);
		return nbt;
	}

	protected void writeRecipeNbt(NBTTagCompound nbt, boolean packet) {
		nbt.setBoolean("fluidRecipe", this.isFluidRecipe);

		if(this.currentRecipe != null) {
			nbt.setString("recipeId", this.currentRecipe.getId().toString());

			if(this.currentRecipeContext != null) {
				NBTTagCompound contextNbt = new NBTTagCompound();
				this.currentRecipe.save(this.currentRecipeContext, contextNbt, packet);
				nbt.setTag("recipeNbt", contextNbt);
			}
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writePacketNbt(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readPacketNbt(packet.getNbtCompound());
	}

	protected NBTTagCompound writePacketNbt(NBTTagCompound nbt) {
		this.writeInventoryNBT(nbt);
		nbt.setTag("fluidTank", fluidTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("remainingItemAmount", this.remainingItemAmount);
		nbt.setInteger("consumptionTicks", this.consumptionTicks);
		nbt.setInteger("maxConsumptionTicks", this.maxConsumptionTicks);
		nbt.setInteger("fuelTicks", this.fuelTicks);
		nbt.setInteger("maxFuelTicks", this.maxFuelTicks);
		this.writeRecipeNbt(nbt, true);
		return nbt;
	}

	protected void readPacketNbt(NBTTagCompound nbt) {
		this.readInventoryNBT(nbt);
		this.fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
		this.remainingItemAmount = nbt.getInteger("remainingItemAmount");
		this.consumptionTicks = nbt.getInteger("consumptionTicks");
		this.maxConsumptionTicks = nbt.getInteger("maxConsumptionTicks");
		this.fuelTicks = nbt.getInteger("fuelTicks");
		this.maxFuelTicks = nbt.getInteger("maxFuelTicks");
		this.readRecipeNbt(nbt, true);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writePacketNbt(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		this.readPacketNbt(nbt);
	}

	public void receiveGUIData(int id, int value) {
		switch (id) {
		case 0:
			this.remainingItemAmount = value;
			break;
		case 1:
			if(this.fluidTank.getFluid() != null) {
				this.fluidTank.getFluid().amount = value;
			}
			break;
		case 2:
			this.consumptionTicks = value;
			break;
		case 3:
			this.maxConsumptionTicks = value;
			break;
		case 4:
			this.fuelTicks = value;
			break;
		case 5:
			this.maxFuelTicks = value;
			break;
		}
	}

	public void sendGUIData(ContainerCenser censer, IContainerListener craft) {
		craft.sendWindowProperty(censer, 0, this.remainingItemAmount);
		craft.sendWindowProperty(censer, 1, this.fluidTank.getFluid() != null ? this.fluidTank.getFluid().amount : 0);
		craft.sendWindowProperty(censer, 2, this.consumptionTicks);
		craft.sendWindowProperty(censer, 3, this.maxConsumptionTicks);
		craft.sendWindowProperty(censer, 4, this.fuelTicks);
		craft.sendWindowProperty(censer, 5, this.maxFuelTicks);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if(!this.world.isRemote) {
			if(this.fuelTicks > 0) {
				this.fuelTicks--;

				this.markDirty();
				if(this.fuelTicks <= 0) {
					IBlockState stat = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, stat, stat, 2);
				}
			} else {
				this.fuelTicks = 0;
			}

			if(this.checkInternalSlotForRecipes) {
				this.checkInternalSlotForRecipes = false;

				FluidStack fluid = this.fluidTank.getFluid();
				ItemStack internalStack = this.getStackInSlot(ContainerCenser.SLOT_INTERNAL);

				if(fluid != null && fluid.amount > 0) {
					ICenserRecipe<?> recipe = this.getEffect(fluid);

					if(recipe != null && recipe.matchesInput(fluid)) {
						this.isFluidRecipe = true;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(fluid);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						this.markDirty();
						IBlockState stat = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(this.pos, stat, stat, 2);
					}
				} else if(!internalStack.isEmpty()) {
					ICenserRecipe<?> recipe = this.getEffect(internalStack);

					if(recipe != null && recipe.matchesInput(internalStack)) {
						this.isFluidRecipe = false;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(internalStack);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						this.markDirty();
						IBlockState stat = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(this.pos, stat, stat, 2);
					}
				}
			}
		}

		this.isRecipeRunning = false;

		if(this.currentRecipe != null) {
			IBlockState state = this.world.getBlockState(this.pos);
			boolean isDisabled = state.getBlock() instanceof BlockCenser && !state.getValue(BlockCenser.ENABLED);

			if(!this.world.isRemote && !isDisabled && this.fuelTicks <= 0 && this.isFilled()) {
				ItemStack fuelStack = this.getStackInSlot(ContainerCenser.SLOT_FUEL);

				if(!fuelStack.isEmpty() && TileEntityFurnace.isItemFuel(fuelStack)) {
					this.maxFuelTicks = this.fuelTicks = TileEntityFurnace.getItemBurnTime(fuelStack) * 4;

					fuelStack.shrink(1);
					if(fuelStack.getCount() <= 0) {
						this.setInventorySlotContents(ContainerCenser.SLOT_FUEL, ItemStack.EMPTY);
					}

					this.markDirty();
					IBlockState stat = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, stat, stat, 2);
				}
			}

			if(!this.canRecipeRun(this.isFluidRecipe, this.currentRecipe)) {
				if(!this.world.isRemote) {
					this.currentRecipe.onStop(this.currentRecipeContext);
					this.currentRecipe = null;
					this.currentRecipeContext = null;
					this.maxConsumptionTicks = this.consumptionTicks = -1;
					this.markDirty();
					IBlockState stat = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, stat, stat, 2);
				}
			} else {
				if(this.fuelTicks > 0 && !isDisabled) {
					this.isRecipeRunning = true;

					int toRemove = this.currentRecipe.update(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);

					if(!this.world.isRemote) {
						if(--this.consumptionTicks <= 0) {
							toRemove += this.currentRecipe.getConsumptionAmount(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						}

						if(toRemove > 0) {
							if(this.isFluidRecipe) {
								this.fluidTank.drainInternal(toRemove, true);
								this.checkInputSlotForTransfer = true;
							} else {
								this.remainingItemAmount = Math.max(0, this.remainingItemAmount - toRemove);
								if(this.remainingItemAmount <= 0) {
									this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
								}
							}
							this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						}
					}
				}
			}
		}

		if(!this.world.isRemote) {
			if(this.checkInputSlotForTransfer) {
				this.checkInputSlotForTransfer = false;

				ItemStack inputStack = this.getStackInSlot(ContainerCenser.SLOT_INPUT);
				if(!inputStack.isEmpty()) {
					FluidActionResult fillResult;

					if(this.getStackInSlot(ContainerCenser.SLOT_INTERNAL).isEmpty() && (fillResult = FluidUtil.tryEmptyContainer(inputStack, this, Integer.MAX_VALUE, null, true)).isSuccess()) {
						this.setInventorySlotContents(ContainerCenser.SLOT_INPUT, fillResult.getResult());
					} else if(!this.isFilled()) {
						ICenserRecipe<?> recipe = this.getEffect(inputStack);
						
						if(recipe != null) {
							this.remainingItemAmount = Math.min(recipe.getInputAmount(inputStack), 1000);
							
							ItemStack internalStack = inputStack.copy();
							internalStack.setCount(1);
							this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, internalStack);
							this.setInventorySlotContents(ContainerCenser.SLOT_INPUT, recipe.consumeInput(inputStack));
						}
					}
				}
			}

			if(this.currentRecipe == null) {
				ItemStack internalStack = this.getStackInSlot(ContainerCenser.SLOT_INTERNAL);
				if(!internalStack.isEmpty() && this.getEffect(internalStack) == null) {
					this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
				}
			}
		}

		if(!this.world.isRemote) {
			//Sync internal slot. Required on client side
			//for rendering stuff
			if(this.internalSlotChanged) {
				this.internalSlotChanged = false;
				BlockPos pos = this.getPos();
				IBlockState stat = this.getWorld().getBlockState(pos);
				this.getWorld().notifyBlockUpdate(pos, stat, stat, 2);
			}
		}

		boolean isCreatingDungeonFog = this.isRecipeRunning && this.currentRecipe.isCreatingDungeonFog(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);

		this.prevDungeonFogStrength = this.dungeonFogStrength;
		if(isCreatingDungeonFog && this.dungeonFogStrength < 1.0F) {
			this.dungeonFogStrength += 0.01F;
			if(this.dungeonFogStrength > 1.0F) {
				this.dungeonFogStrength = 1.0F;
			}
		} else if(!isCreatingDungeonFog && this.dungeonFogStrength > 0.0F) {
			this.dungeonFogStrength -= 0.01F;
			if(this.dungeonFogStrength < 0.0F) {
				this.dungeonFogStrength = 0.0F;
			}
		}

		this.prevEffectStrength = this.effectStrength;
		if(this.isRecipeRunning && this.effectStrength < 1.0F) {
			this.effectStrength += 0.01F;
			if(this.effectStrength > 1.0F) {
				this.effectStrength = 1.0F;
			}
		} else if(!this.isRecipeRunning && this.effectStrength > 0.0F) {
			this.effectStrength -= 0.01F;
			if(this.effectStrength < 0.0F) {
				this.effectStrength = 0.0F;
			}
		}
	}

	protected boolean canRecipeRun(boolean isFluidRecipe, ICenserRecipe<?> recipe) {
		return !((isFluidRecipe && (this.fluidTank.getFluidAmount() <= 0 || !recipe.matchesInput(this.fluidTank.getFluid()))) ||
				(!isFluidRecipe && (this.getStackInSlot(ContainerCenser.SLOT_INTERNAL).isEmpty() || this.remainingItemAmount <= 0 || !recipe.matchesInput(this.getStackInSlot(ContainerCenser.SLOT_INTERNAL)))));
	}

	protected ICenserRecipe<?> getEffect(FluidStack stack) {
		return AbstractCenserRecipe.getRecipe(stack);
	}

	protected ICenserRecipe<?> getEffect(ItemStack stack) {
		return AbstractCenserRecipe.getRecipe(stack);
	}

	protected boolean isFilled() {
		return this.fluidTank.getFluidAmount() > 0 || !this.getStackInSlot(ContainerCenser.SLOT_INTERNAL).isEmpty();
	}

	private void extractFluids(FluidStack fluid) {
		if(fluid.isFluidEqual(fluidTank.getFluid())) {
			fluidTank.drain(fluid.amount, true);
		}
		markDirty();
	}

	public boolean hasFuel() {
		return !getStackInSlot(ContainerCenser.SLOT_FUEL).isEmpty() && EnumItemMisc.SULFUR.isItemOf(getStackInSlot(ContainerCenser.SLOT_FUEL)) && getStackInSlot(ContainerCenser.SLOT_FUEL).getCount() >= 1;
	}

	@Nullable
	public ICenserRecipe<Object> getCurrentRecipe() {
		return this.currentRecipe;
	}

	@Nullable
	public Object getCurrentRecipeContext() {
		return this.currentRecipeContext;
	}

	public int getCurrentRecipeInputAmount() {
		return this.isFluidRecipe ? this.fluidTank.getFluidAmount() : this.remainingItemAmount;
	}

	public int getMaxCurrentRecipeInputAmount() {
		return this.isFluidRecipe ? this.fluidTank.getCapacity() : 1000;
	}

	public int getMaxFuelTicks() {
		return this.maxFuelTicks;
	}

	public int getFuelTicks() {
		return this.fuelTicks;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		switch(facing) {
		case DOWN:
		case UP:
			return new int[]{ ContainerCenser.SLOT_INPUT };
		default:
			return new int[]{ ContainerCenser.SLOT_FUEL };
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		if(super.canInsertItem(slot, stack, side)) {
			//Only allow automatic extraction from input slot if item has no censer recipe, i.e. was used
			if(slot == ContainerCenser.SLOT_INPUT) {
				FluidStack fluid = FluidUtil.getFluidContained(stack);

				if((fluid != null && this.getEffect(fluid) != null) || this.getEffect(stack) != null) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(this.remainingItemAmount > 0) {
			return 0;
		}
		//TODO Send custom packet that only updates fluid
		if (doFill) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.fluidTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.fluidTank.drain(maxDrain, doDrain);
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
	public AxisAlignedBB getRenderBoundingBox() {
		return this.getFogRenderArea();
	}

	public float getDungeonFogStrength(float partialTicks) {
		return this.prevDungeonFogStrength + (this.dungeonFogStrength - this.prevDungeonFogStrength) * partialTicks;
	}

	public float getEffectStrength(float partialTicks) {
		return this.prevEffectStrength + (this.effectStrength - this.prevEffectStrength) * partialTicks;
	}

	public boolean isRecipeRunning() {
		return this.isRecipeRunning;
	}

	public AxisAlignedBB getFogRenderArea() {
		float width = 13.0F;
		float height = 12.0F;
		BlockPos pos = this.getPos();
		return new AxisAlignedBB(pos.getX() + 0.5D - width / 2, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2, pos.getX() + 0.5D + width / 2, pos.getY() - 0.1D + height, pos.getZ() + 0.5D + width / 2);
	}
}
