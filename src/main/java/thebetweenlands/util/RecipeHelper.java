package thebetweenlands.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.common.recipe.misc.ShapedRecipesBetweenlands;
import thebetweenlands.common.recipe.misc.ShapelessRecipesBetweenlands;

public class RecipeHelper {
//	public static void addRecipe(IForgeRegistry<IRecipe> register, ItemStack output, Object... recipeComponents) {
//		addShapedRecipe(register, output, recipeComponents);
//	}
//
//	public static IRecipe addShapedRecipe(IForgeRegistry<IRecipe> registry, ItemStack output, Object... recipeComponents) {
//		ShapedRecipes recipe = CraftingManager.getInstance().addRecipe(output, recipeComponents);
//		CraftingManager.getInstance().getRecipeList().remove(recipe);
//		ShapedRecipes recipe = new ShapedRecipesBetweenlands(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, output);
//		registry.register(recipe);
//		return recipe;
//	}
//
//	public static IRecipe addShapelessRecipe(IForgeRegistry<IRecipe> registry, ItemStack output, Object... recipeComponents) {
//		NonNullList<ItemStack> list = NonNullList.create();
//
//		for (Object object : recipeComponents) {
//			if (object instanceof ItemStack) {
//				list.add(((ItemStack)object).copy());
//			} else if (object instanceof Item) {
//				list.add(new ItemStack((Item)object));
//			} else {
//				if (!(object instanceof Block)) {
//					throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
//				}
//
//				list.add(new ItemStack((Block)object));
//			}
//		}
//
//		ShapelessRecipes recipe = new ShapelessRecipesBetweenlands(output, list);
//		registry.register(recipe);
//		return recipe;
//	}
}
