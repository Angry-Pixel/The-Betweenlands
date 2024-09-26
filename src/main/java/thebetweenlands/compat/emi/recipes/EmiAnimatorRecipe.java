package thebetweenlands.compat.emi.recipes;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.item.recipe.BasicAnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.emi.BetweenlandsEMICompat;
import thebetweenlands.util.LootTableFetcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class EmiAnimatorRecipe extends BLEmiRecipe<AnimatorRecipe> {

	public static final DecimalFormat LIFE_CRYSTAL_PERCENTAGE = new DecimalFormat("##%");

	public EmiAnimatorRecipe(RecipeHolder<AnimatorRecipe> recipe) {
		super(BetweenlandsEMICompat.ANIMATOR, recipe, 108, 67);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		SingleRecipeInput currentInput = new SingleRecipeInput(this.getInputs().getFirst().getEmiStacks().getFirst().getItemStack());
		widgets.addTexture(this.getBackgroundTexture(), 0, 0);
		widgets.addSlot(this.getInputs().getFirst(), 0, 15).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(ItemRegistry.LIFE_CRYSTAL, ItemRegistry.LIFE_CRYSTAL_FRAGMENT)), 0, 49).drawBack(false);
		widgets.addSlot(EmiIngredient.of(Ingredient.of(ItemRegistry.SULFUR)), 90, 49).drawBack(false);

		if (this.getRecipe().getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty() && this.getRecipe().getSpawnEntity() != null) {
			//render entity
			widgets.addTooltipText(List.of(Component.translatable("jei.thebetweenlands.animator.entity_spawn", this.getRecipe().getSpawnEntity(new SingleRecipeInput(ItemStack.EMPTY)).getDescription().getString())), 37, 7, 34, 34);
		} else {
			if (this.getRecipe() instanceof BasicAnimatorRecipe basic && basic.lootTable().isPresent()) {
				widgets.addSlot(EmiIngredient.of(Ingredient.of(LootTableFetcher.getDropsForTable(basic.lootTable().get()).stream())), 45, 15).recipeContext(this).drawBack(false);
			} else {
				widgets.addSlot(this.getOutputs().getFirst(), 45, 15).recipeContext(this).drawBack(false);
			}
		}

		float lifeAmount = this.getRecipe().getRequiredLife(currentInput);
		widgets.addTooltipText(List.of(Component.translatable("jei.thebetweenlands.animator.life", LIFE_CRYSTAL_PERCENTAGE.format((lifeAmount / 128F)))), 18, 42, 33, 24);
		widgets.addTooltipText(List.of(Component.translatable("jei.thebetweenlands.animator.fuel", this.getRecipe().getRequiredFuel(currentInput))), 57, 42, 33, 24);
	}

	static {
		LIFE_CRYSTAL_PERCENTAGE.setRoundingMode(RoundingMode.CEILING);
	}
}
