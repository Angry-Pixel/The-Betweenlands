package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.item.misc.MiddleGemItem;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class CircleGemRecipe extends CustomRecipe {

	public CircleGemRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack gem = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof MiddleGemItem) {
					if (!gem.isEmpty()) {
						return false;
					}
					gem = stack;
				} else {
					if (!stack.is(BLItemTagProvider.CIRCLE_GEM_APPLICABLE)) {
						return false;
					} else {
						if (!tool.isEmpty()) {
							return false;
						}
						tool = stack;
					}
				}
			}
		}
		return (!tool.isEmpty() && !gem.isEmpty()) && (tool.getOrDefault(DataComponentRegistry.CIRCLE_GEM, CircleGemType.NONE) != ((MiddleGemItem) gem.getItem()).getType());
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack gem = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof MiddleGemItem) {
					gem = stack;
				} else {
					if (stack.is(BLItemTagProvider.CIRCLE_GEM_APPLICABLE)) {
						tool = stack;
					}
				}
			}
		}
		if (!tool.isEmpty() && !gem.isEmpty()) {
			ItemStack result = tool.copy();
			CircleGemType appliedGem = ((MiddleGemItem) gem.getItem()).getType();
			CircleGemType toolGem = tool.getOrDefault(DataComponentRegistry.CIRCLE_GEM, CircleGemType.NONE);
			int gemRelation = appliedGem.getRelation(toolGem);
			if (gemRelation == -1) {
				result.set(DataComponentRegistry.CIRCLE_GEM, CircleGemType.NONE);
			} else {
				result.set(DataComponentRegistry.CIRCLE_GEM, appliedGem);
			}
			return result;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.CIRCLE_GEM.get();
	}
}
