package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.api.capability.corrosion.ICorrosionHandler;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class CoatToolRecipe extends CustomRecipe {

	public CoatToolRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int scabyst = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.SCABYST)) {
				scabyst++;
			} else {
				ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(stack);
				if(handler == null)
					return false;
				if (!tool.isEmpty())
					return false;
				if (handler.getCoating() >= handler.getMaxCoating())
					return false;
				tool = stack;
			}
		}
		return scabyst > 0 && !tool.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int scabyst = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(ItemRegistry.SCABYST)) {
					scabyst++;
				} else if (stack.is(BLItemTagProvider.CORRODIBLE)) {
					tool = stack;
				}
			}
		}
		tool = tool.copy();
		ICorrosionHandler handler = CorrosionHelper.getCorrosionHandler(tool);
		handler.addCoating(scabyst * 75, false);
		return tool;
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
