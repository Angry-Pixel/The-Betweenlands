package thebetweenlands.common.recipe.purifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IPurifierRecipe;

public abstract class PurifierRecipe implements IPurifierRecipe {
	private static final List<IPurifierRecipe> RECIPES = new ArrayList<IPurifierRecipe>();

	private static final PurifierRecipeCorrodible CORRODIBLE_ITEMS_RECIPE = new PurifierRecipeCorrodible();
	//private static final PurifierRecipeAspectVial ASPECT_VIAL_ITEMS_RECIPE = new PurifierRecipeAspectVial();
	
	@Override
	public abstract ItemStack getOutput(ItemStack input);

	@Override
	public abstract boolean matchesInput(ItemStack stack);

	static {
		addRecipe(CORRODIBLE_ITEMS_RECIPE);
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
		RECIPES.add(recipe);
	}
	
	public static ItemStack getRecipeOutput(ItemStack input) {
		for (IPurifierRecipe recipe : RECIPES) {
			if (recipe.matchesInput(input)) {
				return recipe.getOutput(input);
			}
		}
		return null;
	}

	@Deprecated //This needs to be removed or rewritten, is only used by HL book currently
	public static ItemStack getRecipeInput(ItemStack output) {
		return null;
	}

	public static List<IPurifierRecipe> getRecipeList() {
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