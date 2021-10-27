package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ISmokingRackRecipe;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;

public class CustomSmokingRackRecipes extends CustomRecipes<ISmokingRackRecipe> {
	public CustomSmokingRackRecipes() {
		super("smoking_rack", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "input/smoking_time", RecipeArg.INT, "output/item", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public ISmokingRackRecipe load() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		int smokingTime = this.get("input/smoking_time", RecipeArg.INT).get().create();
		ItemStack output = this.get("output/item", RecipeArg.ITEM_OUTPUT).get().create();

		return new SmokingRackRecipe(output, smokingTime, input);
	}

	@Override
	public IRecipeRegistrar<ISmokingRackRecipe> createRegistrar() {
		return new IRecipeRegistrar<ISmokingRackRecipe>() {
			@Override
			public boolean register(ISmokingRackRecipe recipe) {
				SmokingRackRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(ISmokingRackRecipe recipe) {
				SmokingRackRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
