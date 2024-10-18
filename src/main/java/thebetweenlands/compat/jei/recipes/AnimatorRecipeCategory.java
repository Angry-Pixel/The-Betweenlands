package thebetweenlands.compat.jei.recipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.BasicAnimatorRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.LootTableFetcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class AnimatorRecipeCategory extends AbstractRecipeCategory<AnimatorRecipe> {

	public static final DecimalFormat LIFE_CRYSTAL_PERCENTAGE = new DecimalFormat("##%");

	public static final RecipeType<AnimatorRecipe> ANIMATOR = RecipeType.create(TheBetweenlands.ID, "animator", AnimatorRecipe.class);
	private final IDrawable background;

	public AnimatorRecipeCategory(IGuiHelper helper) {
		super(ANIMATOR, Component.translatable("jei.thebetweenlands.recipe.animator"), helper.createDrawableItemLike(BlockRegistry.ANIMATOR), 108, 67);
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/animator_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 108, 67);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AnimatorRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 16).addIngredients(recipe.getIngredients().getFirst());
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 50).addItemStacks(List.of(ItemRegistry.LIFE_CRYSTAL.toStack(), ItemRegistry.LIFE_CRYSTAL_FRAGMENT.toStack()));
		builder.addSlot(RecipeIngredientRole.INPUT, 91, 50).addItemStacks(List.of(ItemRegistry.SULFUR.toStack()));

		var result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
		if (result.isEmpty() && recipe.getSpawnEntity() != null) {
			//render entity
		} else {
			if (recipe instanceof BasicAnimatorRecipe basic && basic.lootTable().isPresent()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 46, 16).addItemStacks(LootTableFetcher.getDropsForTable(basic.lootTable().get()));
			}
			builder.addSlot(RecipeIngredientRole.OUTPUT, 46, 16).addItemStack(result);
		}
	}

	@Override
	public void draw(AnimatorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		this.background.draw(graphics);
		SingleRecipeInput currentInput = new SingleRecipeInput(recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).getFirst().getDisplayedItemStack().orElse(ItemStack.EMPTY));
		if (mouseX >= 18 && mouseX <= 51 && mouseY >= 42 && mouseY <= 66) {
			float lifeAmount = recipe.getRequiredLife(currentInput);
			graphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("jei.thebetweenlands.animator.life", LIFE_CRYSTAL_PERCENTAGE.format((lifeAmount / 128F))), (int)mouseX, (int)mouseY);
		}
		if (mouseX >= 57 && mouseX <= 90 && mouseY >= 42 && mouseY <= 66) {
			graphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("jei.thebetweenlands.animator.fuel", recipe.getRequiredFuel(currentInput)), (int)mouseX, (int)mouseY);
		}
		if (recipe.getSpawnEntity(currentInput) != null && mouseX >= 37 && mouseX <= 71 && mouseY >= 7 && mouseY <= 41) {
			graphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("jei.thebetweenlands.animator.entity_spawn", recipe.getSpawnEntity(currentInput).getDescription().getString()), (int)mouseX, (int)mouseY);
		}
	}

	static {
		LIFE_CRYSTAL_PERCENTAGE.setRoundingMode(RoundingMode.CEILING);
	}
}
