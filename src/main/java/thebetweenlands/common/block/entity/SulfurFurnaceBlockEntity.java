package thebetweenlands.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import thebetweenlands.common.block.container.SulfurFurnaceBlock;
import thebetweenlands.common.item.datamaps.FluxMultiplier;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class SulfurFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {

	private static final String NBT_BURN_TIME = "BurnTime";
	private static final String NBT_COOK_TIME = "CookTime";
	protected NonNullList<ItemStack> items;
	private final ArrayList<FurnaceData> furnaceData = new ArrayList<>();

	private final int[] sideSlots;
	private final int[] bottomSlots;
	private final int[] inputSlots;
	private final int[] outputSlots;
	private final int[] fuelSlots;
	private final int[] fluxSlots;
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);

	public SulfurFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, 1);
	}

	public SulfurFurnaceBlockEntity(BlockPos pos, BlockState state, int furnaceAmount) {
		super(BlockEntityRegistry.SULFUR_FURNACE.get(), pos, state);
		this.items = NonNullList.withSize(furnaceAmount * 4, ItemStack.EMPTY);
		this.inputSlots = this.furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getInputSlot())).toArray();
		this.outputSlots = this.furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getOutputSlot())).toArray();
		this.fuelSlots = this.furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getFuelSlot())).toArray();
		this.fluxSlots = this.furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getFluxSlot())).toArray();
		this.bottomSlots = ArrayUtils.addAll(this.outputSlots, this.fuelSlots);
		this.sideSlots = ArrayUtils.addAll(this.fuelSlots, this.fluxSlots);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SulfurFurnaceBlockEntity entity) {
		boolean isDirty = false;

		boolean wasBurning = false;

		for (FurnaceData data : entity.furnaceData) {
			wasBurning |= entity.isBurning(data.index);
		}

		boolean isBurning = false;

		for (FurnaceData data : entity.furnaceData) {
			if (data.getFurnaceBurnTime() > 0) {
				data.setFurnaceBurnTime(Math.max(0, data.getFurnaceBurnTime() - 1));
			} else if (data.getFurnaceBurnTime() < 0) {
				data.setFurnaceBurnTime(0);
			}

			ItemStack fuelStack = entity.getItems().get(data.getFuelSlot());
			ItemStack inputStack = entity.getItems().get(data.getInputSlot());
			boolean flag2 = !fuelStack.isEmpty();
			boolean flag3 = !inputStack.isEmpty();
			if (data.getFurnaceBurnTime() != 0 || flag3 && flag2) {
				RecipeHolder<SmeltingRecipe> recipeholder;
				if (flag2) {
					recipeholder = entity.quickCheck.getRecipeFor(new SingleRecipeInput(inputStack), level).orElse(null);
				} else {
					recipeholder = null;
				}

				if (data.getFurnaceBurnTime() == 0 && entity.canBurn(level.registryAccess(), recipeholder, data)) {
					data.currentItemBurnTime = data.furnaceBurnTime = entity.getBurnDuration(fuelStack);

					if (data.getFurnaceBurnTime() > 0) {
						isDirty = true;

						if (fuelStack.hasCraftingRemainingItem()) {
							entity.getItems().set(data.getFuelSlot(), fuelStack.getCraftingRemainingItem());
						} else if (flag3) {
							fuelStack.shrink(1);
							if (fuelStack.isEmpty()) {
								entity.getItems().set(data.getFuelSlot(), fuelStack.getCraftingRemainingItem());
							}
						}
					}
				}

				if (entity.isBurning(data.index) && entity.canBurn(level.registryAccess(), recipeholder, data)) {
					++data.furnaceCookTime;

					if (data.getFurnaceCookTime() == recipeholder.value().getCookingTime()) {
						data.setFurnaceCookTime(0);
						if (entity.burn(level, recipeholder, data)) {
							entity.setRecipeUsed(recipeholder);
						}
						isDirty = true;
					}
				} else {
					data.setFurnaceCookTime(0);
				}
			}

			if (data.getFurnaceBurnTime() > 0) {
				isBurning = true;
			}
		}

		if (wasBurning != isBurning) {
			isDirty = true;
			state = state.setValue(SulfurFurnaceBlock.LIT, isBurning);
			level.setBlock(pos, state, 3);
		}

		if (isDirty) {
			setChanged(level, pos, state);
		}
	}

	public boolean isBurning(int index) {
		return this.furnaceData.get(index).getFurnaceBurnTime() > 0;
	}

	private boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<SmeltingRecipe> recipe, FurnaceData data) {
		if (!this.getItem(data.getInputSlot()).isEmpty() && recipe != null) {
			ItemStack recipeOutput = recipe.value().assemble(new SingleRecipeInput(this.getItem(data.getInputSlot())), registryAccess);
			if (recipeOutput.isEmpty()) {
				return false;
			} else {
				ItemStack output = this.getItems().get(data.getOutputSlot());
				if (output.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItemSameComponents(output, recipeOutput)) {
					return false;
				} else {
					return output.getCount() + recipeOutput.getCount() <= this.getMaxStackSize() && output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize() || output.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private boolean burn(Level level, @Nullable RecipeHolder<SmeltingRecipe> recipe, FurnaceData data) {
		RegistryAccess access = level.registryAccess();
		if (recipe != null && this.canBurn(access, recipe, data)) {
			ItemStack input = this.getItem(data.getInputSlot());
			ItemStack recipeOutput = recipe.value().assemble(new SingleRecipeInput(this.getItem(data.getInputSlot())), access);
			ItemStack existingOutput = this.getItem(data.getOutputSlot());
			if (existingOutput.isEmpty()) {
				this.getItems().set(data.getOutputSlot(), recipeOutput.copy());
			} else if (ItemStack.isSameItemSameComponents(existingOutput, recipeOutput)) {
				existingOutput.grow(recipeOutput.getCount());
			}

			if (input.is(Items.WET_SPONGE) && !this.getItem(data.getFuelSlot()).isEmpty() && this.getItem(data.getFuelSlot()).is(Items.BUCKET)) {
				this.getItems().set(data.getFuelSlot(), new ItemStack(Items.WATER_BUCKET));
			}

			if (!this.getItem(data.getFluxSlot()).isEmpty()) {
				FluxMultiplier multiplier = input.getItem().builtInRegistryHolder().getData(DataMapRegistry.FLUX_MULTIPLIER);
				if (multiplier != null && level.getRandom().nextFloat() <= multiplier.multiplyChance()) {
					existingOutput.setCount(Math.min(this.getMaxStackSize(), existingOutput.getCount() + recipeOutput.getCount() * multiplier.multiplier()));
				}
				this.getItem(data.getFluxSlot()).shrink(1);
			}

			input.shrink(1);
			return true;
		} else {
			return false;
		}
	}

	protected int getBurnDuration(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		} else {
			return fuel.getBurnTime(RecipeType.SMELTING);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		this.writeFurnaceData(tag);
	}

	protected void writeFurnaceData(CompoundTag tag) {
		for (FurnaceData data : furnaceData) {
			tag.putShort(NBT_BURN_TIME + data.index, (short) data.furnaceBurnTime);
			tag.putShort(NBT_COOK_TIME + data.index, (short) data.furnaceCookTime);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.furnaceData.size() * 4, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.readFurnaceData(tag);
	}

	protected void readFurnaceData(CompoundTag tag) {
		for (FurnaceData data : this.furnaceData) {
			if (tag.contains(NBT_BURN_TIME, Tag.TAG_SHORT) && tag.contains(NBT_COOK_TIME, Tag.TAG_SHORT)) {
				data.furnaceBurnTime = tag.getShort(NBT_BURN_TIME);
				data.furnaceCookTime = tag.getShort(NBT_COOK_TIME);
			} else {
				data.furnaceBurnTime = tag.getShort(NBT_BURN_TIME + data.index);
				data.furnaceCookTime = tag.getShort(NBT_COOK_TIME + data.index);
			}
			data.currentItemBurnTime = this.getBurnDuration(this.getItem(data.getFuelSlot()));
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? this.bottomSlots : (side == Direction.UP ? this.inputSlots : this.sideSlots);
	}

	/**
	 * Returns {@code true} if automation can insert the given item in the given slot from the given side.
	 */
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (direction == Direction.DOWN && this.getFuelSlots().anyMatch(this.slotMatch(index))) {
			return stack.is(Items.BUCKET) || stack.is(Items.WATER_BUCKET);
		}
		return true;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.sulfur_furnace");
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
	public void setItem(int index, ItemStack stack) {
		ItemStack itemstack = this.items.get(index);
		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
		this.items.set(index, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		if (index % 4 == 0 && !flag) {
			FurnaceData data = this.furnaceData.get(Mth.floor(index / 4.0F));
			data.setFurnaceCookTime(0);
			this.setChanged();
		}
	}

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return this.getOutputSlots().noneMatch(this.slotMatch(index)) &&
			(this.getFuelSlots().anyMatch(this.slotMatch(index)) ? AbstractFurnaceBlockEntity.isFuel(stack) :
				this.getFluxSlots().noneMatch(this.slotMatch(index)) || stack.is(ItemRegistry.LIMESTONE_FLUX));
	}

	private IntPredicate slotMatch(int slot) {
		return x -> x == slot;
	}

	private IntStream getOutputSlots() {
		return IntStream.of(this.outputSlots);
	}

	private IntStream getFuelSlots() {
		return IntStream.of(this.fuelSlots);
	}

	private IntStream getFluxSlots() {
		return IntStream.of(this.fluxSlots);
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
		if (recipe != null) {
			ResourceLocation resourcelocation = recipe.id();
			this.recipesUsed.addTo(resourcelocation, 1);
		}
	}

	@Nullable
	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return null;
	}

	@Override
	public void awardUsedRecipes(Player player, List<ItemStack> items) {
	}

	public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
		List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
		player.awardRecipes(list);

		for (RecipeHolder<?> recipeholder : list) {
			if (recipeholder != null) {
				player.triggerRecipeCrafted(recipeholder, this.items);
			}
		}

		this.recipesUsed.clear();
	}

	public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
		List<RecipeHolder<?>> list = Lists.newArrayList();

		for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			level.getRecipeManager().byKey(entry.getKey()).ifPresent(holder -> {
				list.add(holder);
				createExperience(level, popVec, entry.getIntValue(), ((SmeltingRecipe) holder.value()).getExperience());
			});
		}

		return list;
	}

	private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
		int i = Mth.floor((float) recipeIndex * experience);
		float f = Mth.frac((float) recipeIndex * experience);
		if (f != 0.0F && Math.random() < (double) f) {
			i++;
		}

		ExperienceOrb.award(level, popVec, i);
	}

	@Override
	public void fillStackedContents(StackedContents helper) {
		for (ItemStack itemstack : this.items) {
			helper.accountStack(itemstack);
		}
	}

	public static class FurnaceData implements Cloneable {
		private int furnaceBurnTime;
		private int currentItemBurnTime;
		private int furnaceCookTime;
		private final int index;

		public FurnaceData(int index) {
			this.index = index;
		}

		public int getFurnaceBurnTime() {
			return this.furnaceBurnTime;
		}

		public void setFurnaceBurnTime(int furnaceBurnTime) {
			this.furnaceBurnTime = furnaceBurnTime;
		}

		public int getCurrentItemBurnTime() {
			return this.currentItemBurnTime;
		}

		public void setCurrentItemBurnTime(int currentItemBurnTime) {
			this.currentItemBurnTime = currentItemBurnTime;
		}

		public int getFurnaceCookTime() {
			return this.furnaceCookTime;
		}

		public void setFurnaceCookTime(int furnaceCookTime) {
			this.furnaceCookTime = furnaceCookTime;
		}

		public final int getInputSlot() {
			return this.index * 4;
		}

		public final int getOutputSlot() {
			return this.getInputSlot() + 2;
		}

		public final int getFuelSlot() {
			return this.getInputSlot() + 1;
		}

		public final int getFluxSlot() {
			return this.getInputSlot() + 3;
		}

		@Override
		public FurnaceData clone() {
			try {
				return (FurnaceData) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
