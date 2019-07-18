package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityCenser extends TileEntityBasicInventory implements IFluidHandler, ITickable {
	private final FluidTank fluidTank;
	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	private static final int INV_SIZE = 3;

	private int remainingItemAmount = 0;

	private float prevFogStrength = 0.0f;
	private float fogStrength = 0.0f;

	private int consumptionTicks;

	private boolean isFluidRecipe;
	private Object currentRecipeContext;
	private ICenserRecipe<Object> currentRecipe;

	private boolean internalSlotChanged = false;

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
		this.fluidTank = new FluidTank(null, Fluid.BUCKET_VOLUME * 4) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return super.canFillFluidType(fluid) && TileEntityCenser.this.getEffect(fluid) != null;
			}
		};
		this.fluidTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.fluidTank);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
		this.remainingItemAmount = nbt.getInteger("remainingItemAmount");
		this.consumptionTicks = nbt.getInteger("consumptionTicks");
		this.readRecipeNbt(nbt);
	}

	@SuppressWarnings("unchecked")
	protected void readRecipeNbt(NBTTagCompound nbt) {
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
					recipe.read(recipeContext, recipeNbt);
				}
			} else {
				this.consumptionTicks = -1;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setTag("fluidTank", this.fluidTank.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("remainingItemAmount", this.remainingItemAmount);
		nbt.setInteger("consumptionTicks", this.consumptionTicks);
		this.writeRecipeNbt(nbt);
		return nbt;
	}

	protected void writeRecipeNbt(NBTTagCompound nbt) {
		nbt.setBoolean("fluidRecipe", this.isFluidRecipe);

		if(this.currentRecipe != null) {
			nbt.setString("recipeId", this.currentRecipe.getId().toString());

			if(this.currentRecipeContext != null) {
				NBTTagCompound contextNbt = new NBTTagCompound();
				this.currentRecipe.save(this.currentRecipeContext, contextNbt);
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
		this.writeRecipeNbt(nbt);
		return nbt;
	}

	protected void readPacketNbt(NBTTagCompound nbt) {
		this.readInventoryNBT(nbt);
		this.fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
		this.remainingItemAmount = nbt.getInteger("remainingItemAmount");
		this.consumptionTicks = nbt.getInteger("consumptionTicks");
		this.readRecipeNbt(nbt);
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
		}
	}

	public void sendGUIData(ContainerCenser censer, IContainerListener craft) {
		craft.sendWindowProperty(censer, 0, this.remainingItemAmount);
		craft.sendWindowProperty(censer, 1, this.fluidTank.getFluid() != null ? this.fluidTank.getFluid().amount : 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if(!this.world.isRemote) {
			ItemStack inputStack = this.getStackInSlot(ContainerCenser.SLOT_INPUT);
			if(!inputStack.isEmpty()) {
				FluidActionResult fillResult = FluidUtil.tryEmptyContainer(inputStack, this, Integer.MAX_VALUE, null, true);

				if(fillResult.isSuccess()) {
					this.setInventorySlotContents(ContainerCenser.SLOT_INPUT, fillResult.getResult());
				} else if(!this.isFilled() && this.getEffect(inputStack) != null) {
					this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, inputStack.splitStack(1));
					this.remainingItemAmount = 1000;
				}
			}

			ItemStack internalStack = this.getStackInSlot(ContainerCenser.SLOT_INTERNAL);
			if(!internalStack.isEmpty() && this.getEffect(internalStack) == null) {
				this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
			}
		}

		if(this.currentRecipe == null) {
			if(!this.world.isRemote) {
				FluidStack fluid = this.fluidTank.getFluid();
				ItemStack internalStack = this.getStackInSlot(ContainerCenser.SLOT_INTERNAL);
				if(fluid != null && fluid.amount > 0) {
					ICenserRecipe<?> recipe = this.getEffect(fluid);
					if(recipe != null && recipe.matchesInput(fluid)) {
						this.isFluidRecipe = true;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(fluid);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						this.markDirty();
						IBlockState stat = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
					}
				} else if(!internalStack.isEmpty()) {
					ICenserRecipe<?> recipe = this.getEffect(internalStack);
					if(recipe != null && recipe.matchesInput(internalStack)) {
						this.isFluidRecipe = false;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(internalStack);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
						this.markDirty();
						IBlockState stat = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
					}
				}
			}
		} else {
			if(!this.canRecipeRun(this.isFluidRecipe, this.currentRecipe)) {
				if(!this.world.isRemote) {
					this.currentRecipe.onStop(this.currentRecipeContext);
					this.currentRecipe = null;
					this.currentRecipeContext = null;
					this.consumptionTicks = -1;
					this.markDirty();
					IBlockState stat = this.world.getBlockState(this.pos);
					this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
				}
			} else {
				this.currentRecipe.update(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);

				if(--this.consumptionTicks <= 0) {
					int toRemove = this.currentRecipe.getConsumptionAmount(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
					if(this.isFluidRecipe) {
						this.fluidTank.drainInternal(toRemove, true);
					} else {
						this.remainingItemAmount = Math.max(0, this.remainingItemAmount - toRemove);
						if(this.remainingItemAmount <= 0) {
							this.setInventorySlotContents(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
						}
					}
					this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this.getCurrentRecipeInputAmount(), this);
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
				this.getWorld().notifyBlockUpdate(pos, stat, stat, 3);
			}
		}

		this.prevFogStrength = this.fogStrength;
		if(this.isFilled() && this.fogStrength < 1.0F) {
			this.fogStrength += 0.01F;
			if(this.fogStrength > 1.0F) {
				this.fogStrength = 1.0F;
			}
		} else if(!this.isFilled() && this.fogStrength > 0.0F) {
			this.fogStrength -= 0.01F;
			if(this.fogStrength < 0.0F) {
				this.fogStrength = 0.0F;
			}
		}
	}

	protected boolean canRecipeRun(boolean isFluidRecipe, ICenserRecipe<?> recipe) {
		return !((isFluidRecipe && (this.fluidTank.getFluidAmount() <= 0 || !recipe.matchesInput(this.fluidTank.getFluid()))) ||
				(!isFluidRecipe && (this.getStackInSlot(ContainerCenser.SLOT_INTERNAL).isEmpty() || this.remainingItemAmount <= 0 || !recipe.matchesInput(this.getStackInSlot(ContainerCenser.SLOT_INTERNAL)))));
	}

	protected int getCurrentRecipeInputAmount() {
		return this.isFluidRecipe ? this.fluidTank.getFluidAmount() : this.remainingItemAmount;
	}

	private static class TestRecipeContext {
		private final ItemStack item;
		private final FluidStack fluid;

		private int ticks;

		private TestRecipeContext(ItemStack stack) {
			this.item = stack;
			this.fluid = null;
		}

		private TestRecipeContext(FluidStack stack) {
			this.item = ItemStack.EMPTY;
			this.fluid = stack;
		}

		@Override
		public String toString() {
			return "(Item: " + (this.item.isEmpty() ? "null" : this.item) + ", " + this.fluid + ")";
		}
	}

	private static class TestRecipe implements ICenserRecipe<TestRecipeContext> {
		@Override
		public ResourceLocation getId() {
			return new ResourceLocation(ModInfo.ID, "test");
		}

		@Override
		public boolean matchesInput(ItemStack stack) {
			return stack.getItem() == ItemRegistry.SAP_BALL;
		}

		@Override
		public boolean matchesInput(FluidStack stack) {
			return stack.getFluid() == FluidRegistry.FOG;
		}

		@Override
		public TestRecipeContext createContext(ItemStack stack) {
			return new TestRecipeContext(stack);
		}

		@Override
		public TestRecipeContext createContext(FluidStack stack) {
			return new TestRecipeContext(stack);
		}

		@Override
		public void onStart(TestRecipeContext context) {
			System.out.println("START RECIPE: " + context);
		}

		@Override
		public void onStop(TestRecipeContext context) {
			System.out.println("STOP RECIPE: " + context);
		}

		@Override
		public void save(TestRecipeContext context, NBTTagCompound nbt) {
			System.out.println("SAVE RECIPE: " + context);
			nbt.setInteger("ticks", context.ticks);
		}

		@Override
		public void read(TestRecipeContext context, NBTTagCompound nbt) {
			System.out.println("READ RECIPE: " + context);
			context.ticks = nbt.getInteger("ticks");
		}

		@Override
		public void update(TestRecipeContext context, int amountLeft, TileEntity censer) {
			System.out.println("UPDATE RECIPE: " + context.ticks + " " + context);
			context.ticks++;
		}

		@Override
		public int getConsumptionDuration(TestRecipeContext context, int amountLeft, TileEntity censer) {
			return 1;
		}

		@Override
		public int getConsumptionAmount(TestRecipeContext context, int amountLeft, TileEntity censer) {
			return 1;
		}
	}

	private static final ICenserRecipe<?> TEST_RECIPE = new TestRecipe();

	protected ICenserRecipe<?> getEffect(FluidStack stack) {
		return TEST_RECIPE.matchesInput(stack) ? TEST_RECIPE : null;
	}

	protected ICenserRecipe<?> getEffect(ItemStack stack) {
		return TEST_RECIPE.matchesInput(stack) ? TEST_RECIPE : null;
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

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		if (facing == EnumFacing.UP) {
			return new int[]{ ContainerCenser.SLOT_INPUT };
		}
		return new int[]{ ContainerCenser.SLOT_FUEL };
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		//TODO Send custom packet that only updates fluid
		if (doFill) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
		}
		return this.fluidTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (doDrain) {
			this.markDirty();
			IBlockState stat = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, stat, stat, 3);
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

	public float getFogStrength(float partialTicks) {
		return this.prevFogStrength + (this.fogStrength - this.prevFogStrength) * partialTicks;
	}

	public AxisAlignedBB getFogRenderArea() {
		float width = 13.0F;
		float height = 12.0F;
		BlockPos pos = this.getPos();
		return new AxisAlignedBB(pos.getX() + 0.5D - width / 2, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2, pos.getX() + 0.5D + width / 2, pos.getY() - 0.1D + height, pos.getZ() + 0.5D + width / 2);
	}
}
