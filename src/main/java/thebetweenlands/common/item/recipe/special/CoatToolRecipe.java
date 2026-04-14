package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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

public class CoatToolRecipe extends CustomRecipe {
	public static final int COATING_PER_SCABYST = 75;
	
	public CoatToolRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack tool = ItemStack.EMPTY;
		ICorrosionHandler toolHandler = null;
		int scabyst = 0;
		
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SCABYST)) {
				scabyst++;
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

		// If we have a tool and at least one scabyst
		if(!tool.isEmpty() && scabyst > 0 && toolHandler != null) {
			// Only match if the tool can have coating added
			return toolHandler.addCoating(scabyst * COATING_PER_SCABYST, true) > 0;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int scabyst = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SCABYST)) {
				scabyst++;
			} else if(!tool.isEmpty()) {
				return ItemStack.EMPTY; // Only accept 1 tool
			} else if (CorrosionHelper.isCorrodible(stack)) {
				tool = stack;
			}
		}

		tool = tool.copy();
		ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(tool);
		handler.addCoating(scabyst * COATING_PER_SCABYST, false);
		return tool;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		final int inputSize = input.size();
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inputSize, ItemStack.EMPTY);

		int scabyst = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < inputSize; ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SCABYST)) {
				scabyst++;
			} else if(tool.isEmpty() && CorrosionHelper.isCorrodible(stack)) {
				tool = stack; // Only accept 1 tool
			}
		}

		ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(tool);
		final int currentCoating = handler.getCoating();
		final int maxCoating = handler.getMaxCoating();

		// If the tool doesn't end up fully coated, all scabyst is consumed
		{
			// How much coating the recipe adds to the tool
			final int appliedCoating = handler.addCoating(scabyst * COATING_PER_SCABYST, true);

			// If the tool doesn't end up fully coated, all scabyst is consumed
			if(currentCoating + appliedCoating < maxCoating) {
				return nonnulllist;
			}
		}

		// The tool *was* fully coated by the scabyst, meaning there might be scabyst that went unused
		int scabystChecked = 0;
		boolean fullyCoated = false;
		for (int i = 0; i < inputSize; i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SAP_SPIT)) {
				if(fullyCoated) {
					// If we've met the coating quota, scabyst is no longer consumed
					nonnulllist.set(i, stack.copyWithCount(1));
					continue;
				}
				
				++scabystChecked;

				// How much coating this amount of scabyst adds to the tool
				final int partialCoating = handler.addCoating(scabystChecked * COATING_PER_SCABYST, true);
				
				// If the tool ends up fully coated due to this amount of scabyst,
				// then we don't consume any further scabyst
				if(currentCoating + partialCoating >= maxCoating) {
					fullyCoated = true;
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
		return RecipeRegistry.COAT_TOOL.get();
	}
}
