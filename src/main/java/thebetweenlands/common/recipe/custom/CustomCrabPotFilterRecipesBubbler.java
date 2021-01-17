package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeBubbler;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeBubbler;

public class CustomCrabPotFilterRecipesBubbler extends CustomRecipes<ICrabPotFilterRecipeBubbler> {
	public CustomCrabPotFilterRecipesBubbler() {
		super("crab_pot_filter", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "output/item", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public ICrabPotFilterRecipeBubbler load() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output/item", RecipeArg.ITEM_OUTPUT).get().create();

		return new CrabPotFilterRecipeBubbler(output, input);
	}

	@Override
	public IRecipeRegistrar<ICrabPotFilterRecipeBubbler> createRegistrar() {
		return new IRecipeRegistrar<ICrabPotFilterRecipeBubbler>() {
			@Override
			public boolean register(ICrabPotFilterRecipeBubbler recipe) {
				CrabPotFilterRecipeBubbler.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(ICrabPotFilterRecipeBubbler recipe) {
				CrabPotFilterRecipeBubbler.removeRecipe(recipe);
				return true;
			}
		};
	}
}
