package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;

public class CustomCrabPotFilterRecipesSilt extends CustomRecipes<ICrabPotFilterRecipeSilt> {
	public CustomCrabPotFilterRecipesSilt() {
		super("crab_pot_filter", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "output/item", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public ICrabPotFilterRecipeSilt load() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output/item", RecipeArg.ITEM_OUTPUT).get().create();

		return new CrabPotFilterRecipeSilt(output, input);
	}

	@Override
	public IRecipeRegistrar<ICrabPotFilterRecipeSilt> createRegistrar() {
		return new IRecipeRegistrar<ICrabPotFilterRecipeSilt>() {
			@Override
			public boolean register(ICrabPotFilterRecipeSilt recipe) {
				CrabPotFilterRecipeSilt.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(ICrabPotFilterRecipeSilt recipe) {
				CrabPotFilterRecipeSilt.removeRecipe(recipe);
				return true;
			}
		};
	}
}
