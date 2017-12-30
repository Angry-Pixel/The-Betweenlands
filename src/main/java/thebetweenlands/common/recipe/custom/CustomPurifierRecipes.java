package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipeStandard;

public class CustomPurifierRecipes extends CustomRecipes<IPurifierRecipe> {
	public CustomPurifierRecipes() {
		super("purifier", ImmutableMap.of("input", RecipeArg.ITEM_INPUT, "output", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public IPurifierRecipe load() {
		ItemStack input = this.get("input", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output", RecipeArg.ITEM_OUTPUT).get().create();

		return new PurifierRecipeStandard(output, input);
	}

	@Override
	public IRecipeRegistrar<IPurifierRecipe> createRegistrar() {
		return new IRecipeRegistrar<IPurifierRecipe>() {
			@Override
			public boolean register(IPurifierRecipe recipe) {
				PurifierRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(IPurifierRecipe recipe) {
				PurifierRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
