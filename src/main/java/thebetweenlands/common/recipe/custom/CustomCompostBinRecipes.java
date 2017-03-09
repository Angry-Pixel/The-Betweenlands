package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

public class CustomCompostBinRecipes extends CustomRecipes<ICompostBinRecipe> {
	public CustomCompostBinRecipes() {
		super("compost_bin", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "input/composting_time", RecipeArg.INT, "output", RecipeArg.INT), ImmutableMap.of());
	}

	@Override
	public ICompostBinRecipe load() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		int compostingTime = this.get("input/composting_time", RecipeArg.INT).get().create();
		int compostAmount = this.get("output", RecipeArg.INT).get().create();

		return new CompostRecipe(compostAmount, compostingTime, input);
	}

	@Override
	public IRecipeRegistrar<ICompostBinRecipe> createRegistrar() {
		return new IRecipeRegistrar<ICompostBinRecipe>() {
			@Override
			public boolean register(ICompostBinRecipe recipe) {
				CompostRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(ICompostBinRecipe recipe) {
				CompostRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
