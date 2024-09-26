package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;

public class EmiMortarRecipe extends BLEmiRecipe<MortarRecipe> {

	public EmiMortarRecipe(RecipeHolder<MortarRecipe> recipe) {
		super(BetweenlandsEMICompat.MORTAR, recipe, 107, 34);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), 0, 6).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(new ItemStack(ItemRegistry.PESTLE, 1, DataComponentPatch.builder().set(DataComponentRegistry.PESTLE_ACTIVE.get(), Unit.INSTANCE).build()))), 44, 6).drawBack(false);
		widgets.addSlot(this.getOutputs().getFirst(), 88, 6).recipeContext(this).drawBack(false);
	}
}
