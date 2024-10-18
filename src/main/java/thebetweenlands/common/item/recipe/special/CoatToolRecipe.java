package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.item.CorrosionData;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.DataComponentRegistry;
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
			if (!stack.isEmpty()) {
				if (stack.is(BLItemTagProvider.CORRODIBLE)) {
					if (!tool.isEmpty())
						return false;
					if (stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY).coating() >= stack.getOrDefault(DataComponentRegistry.MAX_COATING, 600))
						return false;
					tool = stack;
				} else if (stack.is(ItemRegistry.SCABYST)) {
					scabyst++;
				} else {
					return false;
				}
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
				if (stack.is(BLItemTagProvider.CORRODIBLE)) {
					tool = stack;
				} else if (stack.is(ItemRegistry.SCABYST)) {
					scabyst++;
				}
			}
		}
		tool = tool.copy();
		var data = tool.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY);
		tool.set(DataComponentRegistry.CORROSION, data.withCoating(Math.min(tool.getOrDefault(DataComponentRegistry.MAX_COATING, 600), data.coating() + scabyst * 75)));
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
