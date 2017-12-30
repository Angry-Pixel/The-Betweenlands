package thebetweenlands.common.recipe.misc;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;

public class ShapelessRecipesBetweenlands extends ShapelessRecipes {
	public ShapelessRecipesBetweenlands(ItemStack output, NonNullList<Ingredient> inputList) {
		super("thebetweenlands", output, inputList);
	}
}
