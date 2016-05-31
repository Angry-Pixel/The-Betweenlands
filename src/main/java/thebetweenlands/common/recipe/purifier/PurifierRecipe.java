package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PurifierRecipe {
	private static final List<PurifierRecipe> RECIPES = new ArrayList<PurifierRecipe>();

	public abstract ItemStack getOutput(ItemStack input);
	public abstract ItemStack getInput(ItemStack output);

	public abstract boolean matches(ItemStack stack);
	public abstract boolean matchesOutput(ItemStack stack);

	static {
	}

	/**
	 * @param output what will be produced by the recipe
	 * @param input the input item for the recipe
	 */
	public static void addRecipe(ItemStack output, ItemStack input) {
		RECIPES.add(new PurifierRecipeStandard(output, input));
	}

	public static ItemStack getRecipeOutput(ItemStack input) {
		for (PurifierRecipe recipe : RECIPES) {
			if (recipe.matches(input)) {
				return recipe.getOutput(input);
			}
		}
		return null;
	}

	public static ItemStack getRecipeInput(ItemStack output) {
		for (PurifierRecipe recipe : RECIPES) {
			if (recipe.matchesOutput(output)) {
				return recipe.getInput(output);
			}
		}
		return null;
	}

	public static List<PurifierRecipe> getRecipeList() {
		return Collections.unmodifiableList(RECIPES);
	}

	public static boolean areStacksTheSame(ItemStack a, ItemStack b) {
		return areStacksTheSame(a, b, false);
	}

	public static boolean areStacksTheSame(ItemStack a, ItemStack b, boolean matchStackSize) {
		if (a == null || b == null) {
			return false;
		}

		if (a.getItem() == b.getItem()) {
			if (a.getItemDamage() == b.getItemDamage()) {
				if (!matchStackSize || a.stackSize == b.stackSize) {
					if (a.hasTagCompound() && b.hasTagCompound()) {
						return a.getTagCompound().equals(b.getTagCompound());
					}
					return a.hasTagCompound() == b.hasTagCompound();
				}
			}
		}
		return false;
	}

}