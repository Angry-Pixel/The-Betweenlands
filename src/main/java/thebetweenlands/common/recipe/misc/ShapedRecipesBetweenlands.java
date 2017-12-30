package thebetweenlands.common.recipe.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;

public class ShapedRecipesBetweenlands extends ShapedRecipes {
	public ShapedRecipesBetweenlands(int width, int height, NonNullList<Ingredient> inputs, ItemStack output) {
		super("thebetweenlands", width, height, inputs, output);
	}
}
