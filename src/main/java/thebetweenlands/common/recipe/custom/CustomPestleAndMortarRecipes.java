package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;

public class CustomPestleAndMortarRecipes extends CustomRecipes<PestleAndMortarRecipe> {
	public CustomPestleAndMortarRecipes() {
		super("pestle_and_mortar", ImmutableMap.of("input", RecipeArg.ITEM_INPUT, "output", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public PestleAndMortarRecipe load() {
		ItemStack input = this.get("input", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output", RecipeArg.ITEM_OUTPUT).get().create();

		return new PestleAndMortarRecipe(output, input);
	}

	@Override
	public IRecipeRegistrar<PestleAndMortarRecipe> createRegistrar() {
		return new IRecipeRegistrar<PestleAndMortarRecipe>() {
			@Override
			public boolean register(PestleAndMortarRecipe recipe) {
				PestleAndMortarRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(PestleAndMortarRecipe recipe) {
				PestleAndMortarRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
