package thebetweenlands.herblore.elixirs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.items.BLItemRegistry;

public class ElixirRecipes {
	private static final List<ElixirRecipe> REGISTERED_RECIPES = new ArrayList<ElixirRecipe>();

	public static void registerRecipe(ElixirRecipe recipe) {
		REGISTERED_RECIPES.add(recipe);
	}

	public static ElixirRecipe getFromAspects(List<IAspect> aspects) {
		for(ElixirRecipe recipe : REGISTERED_RECIPES) {
			boolean matches = true;
			for(IAspect recipeAspect : recipe.aspects) {
				if(!aspects.contains(recipeAspect)) {
					matches = false;
					break;
				}
			}
			if(matches) {
				return recipe;
			}
		}
		return null;
	}

	public static void init() {
		//Just for testing
		registerRecipe(new ElixirRecipe("Test Elixir", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.BYARIIS, AspectRegistry.CELAWYNN}));
	}
}
