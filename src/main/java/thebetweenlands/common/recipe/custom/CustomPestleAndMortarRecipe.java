package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;

public class CustomPestleAndMortarRecipe extends CustomRecipe {
	public CustomPestleAndMortarRecipe() {
		super("pestle_and_mortar", ImmutableMap.of("input", RecipeArg.ITEM_INPUT, "output", RecipeArg.ITEM_OUTPUT), ImmutableMap.of());
	}

	@Override
	public void register() {
		ItemStack input = this.get("input", RecipeArg.ITEM_INPUT).get().create();
		ItemStack output = this.get("output", RecipeArg.ITEM_OUTPUT).get().create();

		PestleAndMortarRecipe.addRecipe(output, input);
	}
}
