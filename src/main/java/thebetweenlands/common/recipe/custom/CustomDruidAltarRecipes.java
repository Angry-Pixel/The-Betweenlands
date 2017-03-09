package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class CustomDruidAltarRecipes extends CustomRecipes<IDruidAltarRecipe> {
	public CustomDruidAltarRecipes() {
		super("druid_altar", ImmutableMap.of("input/item_1", RecipeArg.ITEM_INPUT, "input/item_2", RecipeArg.ITEM_INPUT, "input/item_3", RecipeArg.ITEM_INPUT, "input/item_4", RecipeArg.ITEM_INPUT, "output", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public IDruidAltarRecipe load() {
		ItemStack input1 = this.get("input/item_1", RecipeArg.ITEM_INPUT).get().create();
		ItemStack input2 = this.get("input/item_2", RecipeArg.ITEM_INPUT).get().create();
		ItemStack input3 = this.get("input/item_3", RecipeArg.ITEM_INPUT).get().create();
		ItemStack input4 = this.get("input/item_4", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output", RecipeArg.ITEM_OUTPUT).get().create();

		return new DruidAltarRecipe(input1, input2, input3, input4, output);
	}

	@Override
	public IRecipeRegistrar<IDruidAltarRecipe> createRegistrar() {
		return new IRecipeRegistrar<IDruidAltarRecipe>() {
			@Override
			public boolean register(IDruidAltarRecipe recipe) {
				DruidAltarRecipe.addRecipe(recipe);
				return true;
			}

			@Override
			public boolean unregister(IDruidAltarRecipe recipe) {
				DruidAltarRecipe.removeRecipe(recipe);
				return true;
			}
		};
	}
}
