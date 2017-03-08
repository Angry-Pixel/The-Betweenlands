package thebetweenlands.common.recipe.custom;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.recipe.misc.CompostRecipe;

public class CustomCompostBinRecipe extends CustomRecipe {
	public CustomCompostBinRecipe() {
		super("compost_bin", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "input/composting_time", RecipeArg.INT, "output", RecipeArg.INT), ImmutableMap.of());
	}

	@Override
	public void register() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		int compostingTime = this.get("input/composting_time", RecipeArg.INT).get().create();
		int compostAmount = this.get("output", RecipeArg.INT).get().create();

		CompostRecipe.addRecipe(compostAmount, compostingTime, input);
	}
}
