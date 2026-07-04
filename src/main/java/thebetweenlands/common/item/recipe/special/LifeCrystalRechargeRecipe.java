package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandler;
import thebetweenlands.common.capability.lifecrystal.LifeCrystalHelper;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class LifeCrystalRechargeRecipe extends CustomRecipe {

	public LifeCrystalRechargeRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack crystal = ItemStack.EMPTY;
		ILifeCrystalHandler crystalHandler = null;
		int hearts = 0;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.WIGHT_HEART)) {
				hearts++;
			} else if(!crystal.isEmpty()) {
				return false; // Only accept 1 crystal
			} else {
				ILifeCrystalHandler handler = LifeCrystalHelper.getLifeCrystalHandler(stack);
				if(handler == null || handler.getLifePower() >= handler.getMaxLifePower())
					return false;
				crystal = stack;
				crystalHandler = handler;
			}
		}

		// If we have a crystal and at least one wight heart
		if(!crystal.isEmpty() && hearts > 0 && crystalHandler != null) {
			// Figure out how much charging to attempt
			int charge = Mth.ceil(hearts * crystalHandler.getMaxLifePower() / 8.0F);
			// Only match if the crystal can be charged
			return crystalHandler.chargeLifePower(charge, true) != charge;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.is(ItemRegistry.WIGHT_HEART)) {
				hearts++; // Track hearts to determine the amount to repair
			} else if(!crystal.isEmpty()) {
				return ItemStack.EMPTY; // Only accept 1 crystal
			} else {
				crystal = stack;
			}
		}

		if (hearts == 0 || crystal.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ILifeCrystalHandler handler = LifeCrystalHelper.getLifeCrystalHandler(crystal);
		if(handler == null) return ItemStack.EMPTY; // This should never happen due to our matches method
		return LifeCrystalHelper.withLifeCharge(crystal, Mth.ceil(hearts * handler.getMaxLifePower() / 8.0F));
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		final int inputSize = input.size();
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inputSize, ItemStack.EMPTY);

		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < inputSize; ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.is(ItemRegistry.WIGHT_HEART)) {
				hearts++; // Track hearts to determine the amount to repair
			} else if(crystal.isEmpty() && LifeCrystalHelper.isValidLifeCrystal(stack)) {
				crystal = stack; // Only accept 1 crystal
			}
		}
		
		ILifeCrystalHandler handler = LifeCrystalHelper.getLifeCrystalHandler(crystal);
		final int currentCharge = handler.getLifePower();
		final int maxCharge = handler.getMaxLifePower();

		// If the crystal doesn't end up fully charged, all hearts are consumed
		{
			// How much the recipe attempts to charge the crystal
			final int totalCharge = Mth.ceil(hearts * handler.getMaxLifePower() / 8.0F);
			// How much it actually gets charged by
			final int actualTotalCharge = totalCharge - handler.chargeLifePower(totalCharge, true);

			// If crystal doesn't end up fully charged, all hearts are consumed
			if(currentCharge + actualTotalCharge < maxCharge) {
				return nonnulllist;
			}
		}

		// The crystal *was* fully charged by the hearts, meaning there might be hearts that went unused
		int heartsChecked = 0;
		boolean fullyCharged = false;
		for (int i = 0; i < inputSize; i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.WIGHT_HEART)) {
				if(fullyCharged) {
					// If we've met the charge quota, hearts are no longer consumed
					nonnulllist.set(i, stack.copyWithCount(1));
					continue;
				}
				
				++heartsChecked;

				// How much this many hearts would attempt to charge the crystal
				final int partialCharge = Mth.ceil(heartsChecked * handler.getMaxLifePower() / 8.0F);
				// How much this many hearts actually charges the crystal
				final int actualPartialCharge = partialCharge - handler.chargeLifePower(partialCharge, true);
				
				// If the crystal ends up fully charged due to these hearts, then we don't consume any further hearts
				if(currentCharge + actualPartialCharge >= maxCharge) {
					fullyCharged = true;
				}
			}
		}

		return nonnulllist;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.LIFE_CRYSTAL_RECHARGE.get();
	}
}
