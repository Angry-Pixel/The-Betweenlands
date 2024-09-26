package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.BasicAnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.CompostRecipe;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;
import thebetweenlands.compat.jei.recipes.CompostRecipeCategory;
import thebetweenlands.util.LootTableFetcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EmiCompostRecipe implements EmiRecipe {

	private final CompostRecipe recipe;
	private static final EmiTexture BACKGROUND = new EmiTexture(TheBetweenlands.prefix("textures/gui/viewer/compost_grid.png"), 0, 0, 58, 21);

	public EmiCompostRecipe(CompostRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return BetweenlandsEMICompat.COMPOST;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return this.recipe.uid();
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiIngredient.of(Ingredient.of(this.recipe.input())));
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(ItemRegistry.COMPOST));
	}

	@Override
	public int getDisplayWidth() {
		return 58;
	}

	@Override
	public int getDisplayHeight() {
		return 21;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), -1, 2).drawBack(false);
		widgets.addSlot(this.getOutputs().getFirst(), 41, 2).drawBack(false);

		int minutes = (this.recipe.time() / 20) / 60;
		int seconds = (this.recipe.time() / 20) % 60;

		List<Component> tooltips = new ArrayList<>();
		if (seconds > 0 && minutes > 0) {
			tooltips.add(Component.translatable("jei.thebetweenlands.time", minutes, seconds));
		} else if (minutes > 0) {
			tooltips.add(Component.translatable("jei.thebetweenlands.time.minutes", minutes));
		} else {
			tooltips.add(Component.translatable("jei.thebetweenlands.time.seconds", minutes));
		}
		tooltips.add(Component.translatable("jei.thebetweenlands.compost.amount", this.recipe.amount()));

		widgets.addTooltipText(tooltips, 18, 3, 21, 15);
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}
}
