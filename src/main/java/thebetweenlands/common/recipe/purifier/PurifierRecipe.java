package thebetweenlands.common.recipe.purifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IPurifierRecipe;

public abstract class PurifierRecipe implements IPurifierRecipe {
	private static final List<IPurifierRecipe> RECIPES = new ArrayList<IPurifierRecipe>();

	private static final PurifierRecipeCorrodible CORRODIBLE_ITEMS_RECIPE = new PurifierRecipeCorrodible();
	private static final PurifierRecipeAspectVial ASPECT_VIAL_ITEMS_RECIPE = new PurifierRecipeAspectVial();
	
	@Override
	public abstract ItemStack getOutput(ItemStack input);

	@Override
	public abstract boolean matchesInput(ItemStack stack);

	static {
		addRecipe(CORRODIBLE_ITEMS_RECIPE);
		addRecipe(ASPECT_VIAL_ITEMS_RECIPE);
	}

	/**
	 * @param output what will be produced by the recipe
	 * @param input the input item for the recipe
	 */
	public static void addRecipe(ItemStack output, ItemStack input) {
		RECIPES.add(new PurifierRecipeStandard(output, input));
	}

	public static void addRecipe(IPurifierRecipe recipe) {
		RECIPES.add(recipe);
	}
	
	public static void removeRecipe(IPurifierRecipe recipe) {
		RECIPES.remove(recipe);
	}

	@MethodsReturnNonnullByDefault
	public static ItemStack getRecipeOutput(ItemStack input) {
		for (IPurifierRecipe recipe : RECIPES) {
			if (recipe.matchesInput(input)) {
				return recipe.getOutput(input);
			}
		}
		return ItemStack.EMPTY;
	}

	public static List<IPurifierRecipe> getRecipeList() {
		return Collections.unmodifiableList(RECIPES);
	}

	public static boolean doesInputMatch(ItemStack input, ItemStack toCheck) {
		return doesInputMatch(input, toCheck, false);
	}

	public static boolean doesInputMatch(ItemStack input, ItemStack toCheck, boolean matchStackSize) {
		if (input == null || toCheck == null) {
			return false;
		}

		if (input.getItem() == toCheck.getItem()) {
			if (input.getItemDamage() == OreDictionary.WILDCARD_VALUE || input.getItemDamage() == toCheck.getItemDamage()) {
				if (!matchStackSize || input.getCount() == toCheck.getCount()) {
					if (input.hasTag() && toCheck.hasTag()) {
						return input.getTag().equals(toCheck.getTag());
					}
					return input.hasTag() == toCheck.hasTag();
				}
			}
		}
		return false;
	}

}