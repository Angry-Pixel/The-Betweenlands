package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.block.Censer;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.common.block.container.CenserBlock;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.inventory.CenserMenu;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.CenserRecipeRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class CenserBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IFluidHandler, Censer {

	public static final int FUEL_SLOT = 0;
	public static final int INPUT_SLOT = 1;
	public static final int INTERNAL_SLOT = 2;
	private static final int INV_SIZE = 3;

	private int remainingItemAmount = 0;

	private float prevDungeonFogStrength = 0.0f;
	private float dungeonFogStrength = 0.0f;

	private float prevEffectStrength = 0.0f;
	private float effectStrength = 0.0f;

	private int maxConsumptionTicks;
	private int consumptionTicks;

	private boolean isFluidRecipe;
	@Nullable
	private Object currentRecipeContext;
	@Nullable
	private CenserRecipe<Object> currentRecipe;

	public boolean internalSlotChanged = false;

	private int maxFuelTicks;
	private int fuelTicks;

	public boolean checkInternalSlotForRecipes = true;
	public boolean checkInputSlotForTransfer = true;

	private boolean isRecipeRunning = false;

	public final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 8);
	private NonNullList<ItemStack> items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);

	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> CenserBlockEntity.this.fuelTicks;
				case 1 -> CenserBlockEntity.this.maxFuelTicks;
				case 2 -> CenserBlockEntity.this.getCurrentRemainingInputAmount();
				case 3 -> CenserBlockEntity.this.getCurrentMaxInputAmount();
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> CenserBlockEntity.this.fuelTicks = value;
				case 1 -> CenserBlockEntity.this.maxFuelTicks = value;
			}
		}

		public int getCount() {
			return 4;
		}
	};


	public CenserBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.CENSER.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CenserBlockEntity entity) {
		if (state.getValue(CenserBlock.WATER_TYPE) != SwampWaterLoggable.WaterType.NONE) return;
		if (!level.isClientSide()) {
			if (entity.fuelTicks > 0) {
				entity.fuelTicks--;

				if (entity.fuelTicks <= 0) {
					entity.setChanged();
				}
			} else {
				entity.fuelTicks = 0;
			}

			if (entity.checkInternalSlotForRecipes) {
				entity.checkInternalSlotForRecipes = false;

				FluidStack fluid = entity.tank.getFluid();
				ItemStack internalStack = entity.getItem(CenserBlockEntity.INTERNAL_SLOT);

				if (fluid.getAmount() > 0) {
					CenserRecipe<?> recipe = entity.getEffect(fluid);

					if (recipe != null && recipe.matchesInput(fluid)) {
						entity.isFluidRecipe = true;
						entity.currentRecipe = (CenserRecipe<Object>) recipe;
						entity.currentRecipeContext = recipe.createContext(fluid);
						entity.currentRecipe.onStart(entity.currentRecipeContext);
						entity.maxConsumptionTicks = entity.consumptionTicks = entity.currentRecipe.getConsumptionDuration(entity.currentRecipeContext, entity);
						entity.setChanged();
					}
				} else if (!internalStack.isEmpty()) {
					CenserRecipe<?> recipe = entity.getEffect(internalStack);

					if (recipe != null && recipe.matchesInput(internalStack)) {
						entity.isFluidRecipe = false;
						entity.currentRecipe = (CenserRecipe<Object>) recipe;
						entity.currentRecipeContext = recipe.createContext(internalStack);
						entity.currentRecipe.onStart(entity.currentRecipeContext);
						entity.maxConsumptionTicks = entity.consumptionTicks = entity.currentRecipe.getConsumptionDuration(entity.currentRecipeContext, entity);
						entity.setChanged();
					}
				}
			}

			entity.isRecipeRunning = false;

			if (entity.currentRecipe != null) {
				boolean isDisabled = state.getBlock() instanceof CenserBlock && !state.getValue(CenserBlock.ENABLED);

				if (!isDisabled && entity.fuelTicks <= 0 && entity.isFilled()) {
					ItemStack fuelStack = entity.getItem(CenserBlockEntity.FUEL_SLOT);

					if (!fuelStack.isEmpty() && fuelStack.is(ItemRegistry.SULFUR)) {
						entity.maxFuelTicks = entity.fuelTicks = fuelStack.getBurnTime(RecipeType.SMELTING) * 4;

						fuelStack.shrink(1);
						if (fuelStack.getCount() <= 0) {
							entity.setItem(CenserBlockEntity.FUEL_SLOT, ItemStack.EMPTY);
						}

						entity.setChanged();
					}
				}

				if (!entity.canRecipeRun(entity.isFluidRecipe, entity.currentRecipe)) {
					entity.currentRecipe.onStop(entity.currentRecipeContext);
					entity.currentRecipe = null;
					entity.currentRecipeContext = null;
					entity.maxConsumptionTicks = entity.consumptionTicks = -1;
					entity.setChanged();
				} else {
					if (entity.fuelTicks > 0 && !isDisabled) {
						entity.isRecipeRunning = true;

						int toRemove = entity.currentRecipe.update(entity.currentRecipeContext, entity);

						if (--entity.consumptionTicks <= 0) {
							toRemove += entity.currentRecipe.getConsumptionAmount(entity.currentRecipeContext, entity);
						}

						if (toRemove > 0) {
							if (entity.isFluidRecipe) {
								entity.tank.drain(toRemove, IFluidHandler.FluidAction.EXECUTE);
								entity.checkInputSlotForTransfer = true;
							} else {
								entity.remainingItemAmount = Math.max(0, entity.remainingItemAmount - toRemove);
								if (entity.remainingItemAmount == 0) {
									entity.setItem(CenserBlockEntity.INTERNAL_SLOT, ItemStack.EMPTY);
								}
							}
							entity.maxConsumptionTicks = entity.consumptionTicks = entity.currentRecipe.getConsumptionDuration(entity.currentRecipeContext, entity);
						}
					}
				}
			}

			if (entity.checkInputSlotForTransfer) {
				entity.checkInputSlotForTransfer = false;

				ItemStack inputStack = entity.getItem(CenserBlockEntity.INPUT_SLOT);
				if (!inputStack.isEmpty()) {
					FluidActionResult fillResult;

					if (entity.getItem(CenserBlockEntity.INTERNAL_SLOT).isEmpty() && (fillResult = FluidUtil.tryEmptyContainer(inputStack, entity, Integer.MAX_VALUE, null, true)).isSuccess()) {
						entity.setItem(CenserBlockEntity.INPUT_SLOT, fillResult.getResult());
					} else if (!entity.isFilled()) {
						CenserRecipe<?> recipe = entity.getEffect(inputStack);

						if (recipe != null) {
							entity.remainingItemAmount = Math.min(recipe.getInputAmount(inputStack), 1000);

							ItemStack internalStack = inputStack.copy();
							internalStack.setCount(1);
							entity.setItem(CenserBlockEntity.INTERNAL_SLOT, internalStack);
							entity.setItem(CenserBlockEntity.INPUT_SLOT, recipe.consumeInput(inputStack));
						}
					}
				}
			}

			if (entity.currentRecipe == null) {
				ItemStack internalStack = entity.getItem(CenserBlockEntity.INTERNAL_SLOT);
				if (!internalStack.isEmpty() && entity.getEffect(internalStack) == null) {
					entity.setItem(CenserBlockEntity.INTERNAL_SLOT, ItemStack.EMPTY);
				}
			}

			//Sync internal slot. Required on client side
			//for rendering stuff
			if (entity.internalSlotChanged) {
				entity.internalSlotChanged = false;
				entity.setChanged();
			}
		} else {
			boolean isCreatingDungeonFog = entity.isRecipeRunning && entity.currentRecipe.isCreatingDungeonFog(entity.currentRecipeContext, entity);

			entity.prevDungeonFogStrength = entity.dungeonFogStrength;
			if (isCreatingDungeonFog && entity.dungeonFogStrength < 1.0F) {
				entity.dungeonFogStrength += 0.01F;
				if (entity.dungeonFogStrength > 1.0F) {
					entity.dungeonFogStrength = 1.0F;
				}
			} else if (!isCreatingDungeonFog && entity.dungeonFogStrength > 0.0F) {
				entity.dungeonFogStrength -= 0.01F;
				if (entity.dungeonFogStrength < 0.0F) {
					entity.dungeonFogStrength = 0.0F;
				}
			}

			entity.prevEffectStrength = entity.effectStrength;
			if (entity.isRecipeRunning && entity.effectStrength < 1.0F) {
				entity.effectStrength += 0.01F;
				if (entity.effectStrength > 1.0F) {
					entity.effectStrength = 1.0F;
				}
			} else if (!entity.isRecipeRunning && entity.effectStrength > 0.0F) {
				entity.effectStrength -= 0.01F;
				if (entity.effectStrength < 0.0F) {
					entity.effectStrength = 0.0F;
				}
			}
		}
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		if (this.getLevel() != null && slot == INTERNAL_SLOT) {
			this.internalSlotChanged = true;
		}
		if (slot == INTERNAL_SLOT) {
			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		} else if (slot == INPUT_SLOT) {
			this.checkInputSlotForTransfer = true;
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.saveTag(tag, registries, false);
	}

	protected void saveTag(CompoundTag tag, HolderLookup.Provider registries, boolean packet) {
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.put("fluid_tank", this.tank.writeToNBT(registries, new CompoundTag()));
		tag.putInt("remaining_item_amount", this.remainingItemAmount);
		tag.putInt("consumption_ticks", this.consumptionTicks);
		tag.putInt("max_consumption_ticks", this.maxConsumptionTicks);
		tag.putInt("fuel_ticks", this.fuelTicks);
		tag.putInt("max_fuel_ticks", this.maxFuelTicks);
		this.writeRecipeNbt(tag, packet);
	}

	protected void writeRecipeNbt(CompoundTag tag, boolean packet) {
		tag.putBoolean("fluid_recipe", this.isFluidRecipe);

		if (this.currentRecipe != null) {
			tag.putString("recipe_id", BLRegistries.CENSER_RECIPES.getKey(this.currentRecipe).toString());

			if (this.currentRecipeContext != null) {
				CompoundTag contextNbt = new CompoundTag();
				this.currentRecipe.save(this.currentRecipeContext, contextNbt, packet);
				tag.put("recipe_nbt", contextNbt);
			}
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.loadTag(tag, registries, false);
	}

	protected void loadTag(CompoundTag tag, HolderLookup.Provider registries, boolean packet) {
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.tank.readFromNBT(registries, tag.getCompound("fluid_tank"));
		this.remainingItemAmount = tag.getInt("remaining_item_amount");
		this.consumptionTicks = tag.getInt("consumption_ticks");
		this.maxConsumptionTicks = tag.getInt("max_consumption_ticks");
		this.fuelTicks = tag.getInt("fuel_ticks");
		this.maxFuelTicks = tag.getInt("max_fuel_ticks");
		this.readRecipeNbt(tag, packet);
	}

	protected void readRecipeNbt(CompoundTag tag, boolean packet) {
		this.isFluidRecipe = tag.getBoolean("fluid_recipe");

		this.currentRecipe = null;
		this.currentRecipeContext = null;

		if (tag.contains("recipe_id", Tag.TAG_STRING)) {
			ResourceLocation id = ResourceLocation.parse(tag.getString("recipe_id"));

			CenserRecipe<Object> recipe;
			Object recipeContext = null;

			if (this.isFluidRecipe) {
				recipe = (CenserRecipe<Object>) this.getEffect(this.tank.getFluid());
				if (recipe != null) {
					recipeContext = recipe.createContext(this.tank.getFluid());
				}
			} else {
				recipe = (CenserRecipe<Object>) this.getEffect(this.getItem(INTERNAL_SLOT));
				if (recipe != null) {
					recipeContext = recipe.createContext(this.getItem(INTERNAL_SLOT));
				}
			}

			if (recipe != null && this.canRecipeRun(this.isFluidRecipe, recipe)) {
				this.currentRecipe = recipe;
				this.currentRecipeContext = recipeContext;

				if (recipeContext != null && id.equals(BLRegistries.CENSER_RECIPES.getKey(recipe)) && tag.contains("recipe_nbt", Tag.TAG_COMPOUND)) {
					CompoundTag recipeNbt = tag.getCompound("recipe_nbt");
					recipe.read(recipeContext, recipeNbt, packet);
				}
			} else {
				this.consumptionTicks = -1;
				this.maxConsumptionTicks = -1;
			}
		}
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadTag(packet.getTag(), registries, true);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveTag(tag, registries, true);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
		this.loadTag(tag, registries, true);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.censer");
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new CenserMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return INV_SIZE;
	}

	protected boolean canRecipeRun(boolean isFluidRecipe, CenserRecipe<?> recipe) {
		return !((isFluidRecipe && (this.tank.getFluidAmount() <= 0 || !recipe.matchesInput(this.tank.getFluid()))) ||
			(!isFluidRecipe && (this.getItem(CenserBlockEntity.INTERNAL_SLOT).isEmpty() || this.remainingItemAmount <= 0 || !recipe.matchesInput(this.getItem(CenserBlockEntity.INTERNAL_SLOT)))));
	}

	@Nullable
	protected CenserRecipe<?> getEffect(FluidStack stack) {
		return CenserRecipeRegistry.getRecipe(stack);
	}

	@Nullable
	protected CenserRecipe<?> getEffect(ItemStack stack) {
		return CenserRecipeRegistry.getRecipe(stack);
	}

	protected boolean isFilled() {
		return this.tank.getFluidAmount() > 0 || !this.getItem(CenserBlockEntity.INTERNAL_SLOT).isEmpty();
	}

	@Nullable
	public CenserRecipe<Object> getCurrentRecipe() {
		return this.currentRecipe;
	}

	@Nullable
	public Object getCurrentRecipeContext() {
		return this.currentRecipeContext;
	}

	@Override
	public int getCurrentRemainingInputAmount() {
		return this.isFluidRecipe ? this.tank.getFluidAmount() : this.remainingItemAmount;
	}

	@Override
	public int getCurrentMaxInputAmount() {
		return this.isFluidRecipe ? this.tank.getCapacity() : 1000;
	}

	public int getFuelTicks() {
		return this.fuelTicks;
	}

	public float getDungeonFogStrength(float partialTicks) {
		return this.prevDungeonFogStrength + (this.dungeonFogStrength - this.prevDungeonFogStrength) * partialTicks;
	}

	@Override
	public float getEffectStrength(float partialTicks) {
		return this.prevEffectStrength + (this.effectStrength - this.prevEffectStrength) * partialTicks;
	}

	@Override
	public boolean isRecipeRunning() {
		return this.isRecipeRunning;
	}

	public AABB getFogRenderArea() {
		float width = 13.0F;
		float height = 12.0F;
		BlockPos pos = this.getBlockPos();
		return new AABB(pos.getX() + 0.5D - width / 2, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2, pos.getX() + 0.5D + width / 2, pos.getY() - 0.1D + height, pos.getZ() + 0.5D + width / 2);
	}

	@Override
	public ItemStack getInputStack() {
		return this.getItem(CenserBlockEntity.INPUT_SLOT);
	}

	@Override
	public int getTanks() {
		return this.tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (this.remainingItemAmount > 0) {
			return 0;
		}
		if (action.execute()) {
			this.setChanged();

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (action.execute()) {
			this.setChanged();

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (action.execute()) {
			this.setChanged();

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.tank.drain(maxDrain, action);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side.getAxis() == Direction.Axis.Y ? new int[]{INPUT_SLOT} : new int[]{FUEL_SLOT};
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == FUEL_SLOT ? stack.is(ItemRegistry.SULFUR) : slot == INPUT_SLOT;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (this.canPlaceItemThroughFace(index, stack, direction)) {
			//Only allow automatic extraction from input slot if item has no censer recipe, i.e. was used
			if (index == INPUT_SLOT) {
				Optional<FluidStack> fluid = FluidUtil.getFluidContained(stack);
				return (fluid.isEmpty() || this.getEffect(fluid.get()) == null) && this.getEffect(stack) == null;
			}
			return true;
		}
		return false;
	}
}
