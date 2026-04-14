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
import thebetweenlands.api.capability.corrosion.ICorrosionHandler;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class CleanToolRecipe extends CustomRecipe {

	public CleanToolRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack tool = ItemStack.EMPTY;
		ICorrosionHandler toolHandler = null;
		int sap = 0;
		
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SAP_SPIT)) {
				sap++;
			} else if (!tool.isEmpty()) {
				return false; // Only accept 1 tool
			} else {
				ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(stack);
				if(handler == null || handler.getCorrosion() <= 0)
					return false;
				tool = stack;
				toolHandler = handler;
			}
		}

		// If we have a tool and at least one sap spit
		if(!tool.isEmpty() && sap > 0 && toolHandler != null) {
			// Figure out how much cleaning to attempt
			int cleaning = Mth.ceil(sap * toolHandler.getMaxCorrosion() / 3.0f);
			// Only match if the tool can have corrosion removed
			return toolHandler.removeCorrosion(cleaning, true) > 0;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SAP_SPIT)) {
				sap++;
			} else if(!tool.isEmpty()) {
				return ItemStack.EMPTY; // Only accept 1 tool
			} else if (CorrosionHelper.isCorrodible(stack)) {
				tool = stack;
			}
		}
		
		tool = tool.copy();
		ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(tool);
		handler.removeCorrosion(Mth.ceil(sap * handler.getMaxCorrosion() / 3.0f), false);
		return tool;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		final int inputSize = input.size();
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inputSize, ItemStack.EMPTY);

		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < inputSize; ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SAP_SPIT)) {
				sap++;
			} else if(tool.isEmpty() && CorrosionHelper.isCorrodible(stack)) {
				tool = stack; // Only accept 1 tool
			}
		}
		
		ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(tool);
		final int currentCorrosion = handler.getCorrosion();
		final int maxCorrosion = handler.getMaxCorrosion();

		// If the tool doesn't end up fully cleaned, all sap spit is consumed
		{
			// How much the recipe attempts to clean the tool
			final int totalCleaning = Mth.ceil(sap * maxCorrosion / 3.0f);
			// How much it actually gets cleaned by
			final int actualTotalCleaning = handler.removeCorrosion(totalCleaning, true);

			// If the tool doesn't end up fully cleaned, all sap spit is consumed
			if(currentCorrosion - actualTotalCleaning > 0) {
				return nonnulllist;
			}
		}

		// The tool *was* fully cleaned by the sap spit, meaning there might be sap spit that went unused
		int sapChecked = 0;
		boolean fullyCleaned = false;
		for (int i = 0; i < inputSize; i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SAP_SPIT)) {
				if(fullyCleaned) {
					// If we've met the cleaning quota, sap spit is no longer consumed
					nonnulllist.set(i, stack.copyWithCount(1));
					continue;
				}
				
				++sapChecked;

				// How much this amount of sap spit attempts to clean the tool
				final int partialCleaning = Mth.ceil(sapChecked * maxCorrosion / 3.0f);
				// How much this amount of sap spit actually cleans the tool
				final int actualPartialCleaning = handler.removeCorrosion(partialCleaning, true);
				
				// If the crystal ends up fully cleaned due to this amount of sap spit,
				// then we don't consume any further sap spit
				if(currentCorrosion - actualPartialCleaning <= 0) {
					fullyCleaned = true;
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
		return RecipeRegistry.CLEAN_TOOL.get();
	}
}
