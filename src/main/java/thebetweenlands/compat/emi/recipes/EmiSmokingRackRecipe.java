package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmokingRecipe;
import thebetweenlands.api.recipes.SmokingRackRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.PurifierRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;

public class EmiSmokingRackRecipe extends BLEmiRecipe<SmokingRackRecipe> {

	public EmiSmokingRackRecipe(RecipeHolder<SmokingRackRecipe> recipe) {
		super(BetweenlandsEMICompat.SMOKING_RACK, recipe, 126, 18);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), 36, 0).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(BlockRegistry.FALLEN_LEAVES)), 0, 0).drawBack(false);
		widgets.addSlot(this.getOutputs().getFirst(), 108, 0).recipeContext(this).drawBack(false);

		String time = String.format("%ds", this.getRecipe().smokingTime() / 20);
		int x = 66;
		x -= Minecraft.getInstance().font.width(time) / 2;
		widgets.addText(Component.literal(time), x, 8, 0xFFFFFFFF, true);
	}
}
