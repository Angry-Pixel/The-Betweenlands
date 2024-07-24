package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.api.recipes.SteepingPotRecipe;
import thebetweenlands.common.items.recipe.FluidRecipeInput;
import thebetweenlands.common.registries.*;

import java.util.Optional;

public class SteepingPotBlockEntity extends NoMenuContainerBlockEntity implements IFluidHandler {

	public FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME);
	public int tempFluidColour;
	public boolean hasCraftResult = false;
	private int heatProgress = 0;

	public int itemRotate = 0;
	public int prevItemRotate = 0;
	public int prevItemBob = 0;
	public int itemBob = 0;
	private boolean countUp = true;
	private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

	public SteepingPotBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.STEEPING_POT.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SteepingPotBlockEntity entity) {
		entity.prevItemRotate = entity.itemRotate;
		entity.prevItemBob = entity.itemBob;

		if (level.isClientSide()) {
			if (entity.countUp && entity.itemBob <= 20) {
				entity.itemBob++;
				if (entity.itemBob == 20)
					entity.countUp = false;
			}
			if (!entity.countUp && entity.itemBob >= -20) {
				entity.itemBob--;
				if (entity.itemBob == -20)
					entity.countUp = true;
			}

			if (entity.getHeatProgress() > 80 && entity.hasBundle()) {
				if (entity.itemRotate < 180)
					entity.itemRotate += 1;
				if (entity.itemRotate >= 180) {
					entity.itemRotate = 0;
					entity.prevItemRotate = 0;
				}
			}
		} else {
			if (entity.getTankFluidAmount() >= FluidType.BUCKET_VOLUME && !entity.hasCraftResult) {
				if (entity.hasBundle()) {
					FluidRecipeInput input = new FluidRecipeInput(entity.tank.getFluid(), entity.getBundleItems());
					Optional<RecipeHolder<SteepingPotRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.STEEPING_POT_RECIPE.get(), input, level);
					if (recipe.isPresent()) {
						FluidStack outputFluid = recipe.get().value().getResultFluid(level.registryAccess());
						if (!outputFluid.isEmpty()) {
							entity.setTempFluidColour(outputFluid.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(0, false)).rgb());
							entity.hasCraftResult = true;
						}
					}
				} else {
					entity.setTempFluidColour(entity.tank.getFluid().getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(0, false)).rgb());
					entity.hasCraftResult = true;
				}
				level.sendBlockUpdated(pos, state, state, 2);
			}

			if (entity.isHeatSource(level.getBlockState(pos.below()))) {
				if (entity.getHeatProgress() < 100 && entity.getTankFluidAmount() > 0) {
					if (level.getGameTime() % 10 == 0) {
						entity.setHeatProgress(entity.getHeatProgress() + 1);
						level.sendBlockUpdated(pos, state, state, 2);
					}
				} else if (entity.tank.getFluid().isEmpty() && entity.getHeatProgress() != 0) {
					entity.setHeatProgress(0);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			} else {
				if (entity.getHeatProgress() > 0) {
					if (level.getGameTime() % 5 == 0) {
						entity.setHeatProgress(entity.getHeatProgress() - 1);
						level.sendBlockUpdated(pos, state, state, 2);
					} else if (entity.tank.getFluid().isEmpty() && entity.getHeatProgress() != 0) {
						entity.setHeatProgress(0);
						level.sendBlockUpdated(pos, state, state, 2);
					}
				}
			}

			if (entity.getTankFluidAmount() >= FluidType.BUCKET_VOLUME && entity.getHeatProgress() >= 100 && entity.hasBundle()) {
				FluidRecipeInput input = new FluidRecipeInput(entity.tank.getFluid(), entity.getBundleItems());
				Optional<RecipeHolder<SteepingPotRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.STEEPING_POT_RECIPE.get(), input, level);

				if (recipe.isEmpty()) {
					entity.setHeatProgress(0);
					if (!entity.getItem(0).isEmpty())
						entity.setItem(0, new ItemStack(ItemRegistry.DIRTY_SILK_BUNDLE.get()));
					level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3F, 0.9F + level.getRandom().nextFloat() * 0.3F);
					entity.drain(FluidType.BUCKET_VOLUME, FluidAction.EXECUTE);
				} else {
					ItemStack output = recipe.get().value().getResultItem(level.registryAccess());
					FluidStack outputFluid = recipe.get().value().getResultFluid(level.registryAccess());

					if (!entity.getItem(0).isEmpty())
						entity.setItem(0, new ItemStack(ItemRegistry.DIRTY_SILK_BUNDLE.get()));

					entity.drain(FluidType.BUCKET_VOLUME, FluidAction.EXECUTE);

					if (!outputFluid.isEmpty()) {
						entity.fill(outputFluid, FluidAction.EXECUTE);
					} else {
						entity.spawnItemStack(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, output);
					}

					ExperienceOrb orb = new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1);
					level.addFreshEntity(orb);
					entity.hasCraftResult = false;
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
	}

	private void setTempFluidColour(int type) {
		this.tempFluidColour = type;
	}

	public boolean hasBundle() {
		ItemStack bundle = this.getItem(0);
		return !bundle.isEmpty() && bundle.is(ItemRegistry.SILK_BUNDLE) && bundle.has(DataComponents.CONTAINER);
	}

	private NonNullList<ItemStack> getBundleItems() {
		ItemStack bundle = this.getItem(0);
		NonNullList<ItemStack> inventoryBundle = NonNullList.withSize(4, ItemStack.EMPTY);
		if (!bundle.isEmpty() && bundle.is(ItemRegistry.SILK_BUNDLE) && bundle.has(DataComponents.CONTAINER)) {
			bundle.get(DataComponents.CONTAINER).copyInto(inventoryBundle);
		}

		return inventoryBundle;
	}

	public void spawnItemStack(Level level, double x, double y, double z, ItemStack stack) {
		ItemEntity item = new ItemEntity(level, x, y, z, stack);
		item.setDeltaMovement(Vec3.ZERO);
		item.setPickUpDelay(20);
		level.addFreshEntity(item);
	}

	private boolean isHeatSource(BlockState state) {
		return state.is(BlockRegistry.SMOULDERING_PEAT) || state.is(BlockTags.FIRE);
	}

	public void setHeatProgress(int heat) {
		this.heatProgress = heat;
	}

	public int getHeatProgress() {
		return this.heatProgress;
	}

	public int getTankFluidAmount() {
		return this.tank.getFluidAmount();
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return 1;
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
		return this.tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return this.tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return this.tank.drain(maxDrain, action);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		this.tank.writeToNBT(registries, tag);
		tag.putInt("heat_progress", this.getHeatProgress());
		tag.putInt("fluid_color", this.tempFluidColour);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.tank.readFromNBT(registries, tag);
		this.setHeatProgress(tag.getInt("heat_progress"));
		this.tempFluidColour = tag.getInt("fluid_color");
	}
}
