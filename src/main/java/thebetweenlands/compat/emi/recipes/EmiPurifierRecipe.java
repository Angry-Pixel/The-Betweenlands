package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.PurifierRecipe;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;

public class EmiPurifierRecipe extends BLEmiRecipe<PurifierRecipe> {

	private static final ResourceLocation TANK = TheBetweenlands.prefix("container/purifier/tank");

	public EmiPurifierRecipe(RecipeHolder<PurifierRecipe> recipe) {
		super(BetweenlandsEMICompat.PURIFIER, recipe, 107, 65);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), 24, 4).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(ItemRegistry.SULFUR)), 24, 43).drawBack(false);
		widgets.addSlot(this.getOutputs().getFirst(), 84, 23).recipeContext(this).drawBack(false);

		int water = Mth.ceil((this.getRecipe().requiredWater() / 4000.0F) * 64.0F) + 1;
		widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> draw.blitSprite(TANK, 12, 65, 0, 65 - water, 0, (65 - water), 12, water));
	}
}
