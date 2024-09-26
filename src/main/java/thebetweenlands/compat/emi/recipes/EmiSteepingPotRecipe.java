package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.api.recipes.SteepingPotRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.PurifierRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;

public class EmiSteepingPotRecipe extends BLEmiRecipe<SteepingPotRecipe> {

	private final EmiTexture ARROW = new EmiTexture(this.getBackgroundTexture().texture, 100, 14, 16, 14);

	public EmiSteepingPotRecipe(RecipeHolder<SteepingPotRecipe> recipe) {
		super(BetweenlandsEMICompat.STEEPING_POT, recipe, 100, 100);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);

		widgets.addSlot(this.getIngredientOrEmpty(0), 41, 6).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(1), 17, 30).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(2), 65, 30).drawBack(false);
		widgets.addSlot(this.getIngredientOrEmpty(3), 41, 54).drawBack(false);
		if (!this.getOutputs().getFirst().isEmpty()) {
			widgets.addSlot(this.getOutputs().getFirst(), 76, 76).recipeContext(this).drawBack(false);
		}
		widgets.addSlot(EmiStack.of(this.getRecipe().getInputFluid().getStacks()[0].getFluid(), FluidType.BUCKET_VOLUME), 6, 76).drawBack(false);
		if (!this.getRecipe().getResultFluid(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
			widgets.addSlot(EmiStack.of(this.getRecipe().getResultFluid(Minecraft.getInstance().level.registryAccess()).getFluid()), 76, 76).recipeContext(this).drawBack(false);
		}
		widgets.addSlot(EmiIngredient.of(Ingredient.of(ItemRegistry.SILK_BUNDLE)), 41, 30).drawBack(false);
		widgets.addAnimatedTexture(ARROW, 42, 78, 6000, true, false, false);
	}

	private EmiIngredient getIngredientOrEmpty(int index) {
		if (index < this.getInputs().size()) {
			return this.getInputs().get(index);
		}
		return EmiIngredient.of(Ingredient.EMPTY);
	}
}
