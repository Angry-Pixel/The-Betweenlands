package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;

public class CustomAnimatorRepairableRecipes extends CustomRecipes<IAnimatorRecipe> {
	public CustomAnimatorRepairableRecipes() {
		super("animator_repairable", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "input/min_fuel", RecipeArg.INT, "input/min_life", RecipeArg.INT, "input/max_fuel", RecipeArg.INT, "input/max_life", RecipeArg.INT), ImmutableMap.of());
	}

	@Override
	public IAnimatorRecipe load() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		int minFuel = this.get("input/min_fuel", RecipeArg.INT).get().create();
		int minLife = this.get("input/min_life", RecipeArg.INT).get().create();
		int maxFuel = this.get("input/max_fuel", RecipeArg.INT).get().create();
		int maxLife = this.get("input/max_life", RecipeArg.INT).get().create();
		return new ToolRepairAnimatorRecipe(input.getItem(), minLife, maxLife, minFuel, maxFuel);
	}

	@Override
	public IRecipeRegistrar<IAnimatorRecipe> createRegistrar() {
		return new IRecipeRegistrar<IAnimatorRecipe>() {
			@Override
			public boolean register(IAnimatorRecipe recipe) {
				AnimatorRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(IAnimatorRecipe recipe) {
				AnimatorRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
