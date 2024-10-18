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

public class CleanToolRecipe extends CustomRecipe {

	public CleanToolRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BLItemTagProvider.CORRODIBLE)) {
					if (!tool.isEmpty())
						return false;
					if (stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY).corrosion() == 0)
						return false;
					tool = stack;
				} else if (stack.is(ItemRegistry.SAP_SPIT)) {
					sap++;
				} else {
					return false;
				}
			}
		}
		return sap > 0 && !tool.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BLItemTagProvider.CORRODIBLE)) {
					tool = stack;
				} else if (stack.is(ItemRegistry.SAP_SPIT)) {
					sap++;
				}
			}
		}
		tool = tool.copy();
		var data = tool.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY);
		tool.set(DataComponentRegistry.CORROSION, data.withCorrosion(Math.max(0, data.corrosion() - Mth.ceil(sap * tool.getOrDefault(DataComponentRegistry.MAX_CORROSION, 255) / 3.0F))));
		return tool;
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
