package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.item.recipe.BasicAnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;
import thebetweenlands.util.LootTableFetcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class EmiDruidAltarRecipe extends BLEmiRecipe<DruidAltarRecipe> {

	public EmiDruidAltarRecipe(RecipeHolder<DruidAltarRecipe> recipe) {
		super(BetweenlandsEMICompat.DRUID_ALTAR, recipe, 75, 75);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getIngredientOrEmpty(0), 0, 0).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(1), 56, 0).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(2), 0, 56).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(3), 56, 56).drawBack(false);

		if (this.getOutputs().getFirst().isEmpty()) {
			Component string = Component.translatable("jei.thebetweenlands.druid_circle_reactivate");

			int stringWidth = Minecraft.getInstance().font.width(string);
			int fontX = 37 - (stringWidth / 2);
			int fontY = 37;
			widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> draw.fill(fontX - 1, fontY - 5, fontX + stringWidth + 1, fontY + 10 - 5, 0xAA000000));
			widgets.addText(string, fontX, fontY - 4, 0xFFFFFFFF, true);
			widgets.addSlot(28, 28).recipeContext(this).drawBack(false);
		} else {
			widgets.addSlot(this.getOutputs().getFirst(), 28, 28).recipeContext(this).drawBack(false);
		}
	}

	private EmiIngredient getIngredientOrEmpty(int index) {
		if (index < this.getInputs().size()) {
			return this.getInputs().get(index);
		}
		return EmiIngredient.of(Ingredient.EMPTY);
	}
}
