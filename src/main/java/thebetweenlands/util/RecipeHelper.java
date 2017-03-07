package thebetweenlands.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.recipe.misc.ShapedRecipesBetweenlands;
import thebetweenlands.common.recipe.misc.ShapelessRecipesBetweenlands;

public class RecipeHelper {
	public static void addRecipe(ItemStack output, Object... recipeComponents) {
		addShapedRecipe(output, recipeComponents);
	}

	public static IRecipe addShapedRecipe(ItemStack output, Object... recipeComponents) {
		ShapedRecipes recipe = CraftingManager.getInstance().addRecipe(output, recipeComponents);
		CraftingManager.getInstance().getRecipeList().remove(recipe);
		recipe = new ShapedRecipesBetweenlands(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, output);
		GameRegistry.addRecipe(recipe);
		return recipe;
	}

	public static IRecipe addShapelessRecipe(ItemStack output, Object... recipeComponents) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (Object object : recipeComponents) {
			if (object instanceof ItemStack) {
				list.add(((ItemStack)object).copy());
			} else if (object instanceof Item) {
				list.add(new ItemStack((Item)object));
			} else {
				if (!(object instanceof Block)) {
					throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
				}

				list.add(new ItemStack((Block)object));
			}
		}

		ShapelessRecipes recipe = new ShapelessRecipesBetweenlands(output, list);
		GameRegistry.addRecipe(recipe);
		return recipe;
	}
}
