package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;

public class CustomPestleAndMortarRecipes extends CustomRecipes<IPestleAndMortarRecipe> {
	public CustomPestleAndMortarRecipes() {
		super("pestle_and_mortar", ImmutableMap.of("input", RecipeArg.ITEM_INPUT, "output", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public IPestleAndMortarRecipe load() {
		ItemStack input = this.get("input", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output", RecipeArg.ITEM_OUTPUT).get().create();

		return new PestleAndMortarRecipe(output, input);
	}

	@Override
	public IRecipeRegistrar<IPestleAndMortarRecipe> createRegistrar() {
		return new IRecipeRegistrar<IPestleAndMortarRecipe>() {
			@Override
			public boolean register(IPestleAndMortarRecipe recipe) {
				PestleAndMortarRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(IPestleAndMortarRecipe recipe) {
				PestleAndMortarRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
