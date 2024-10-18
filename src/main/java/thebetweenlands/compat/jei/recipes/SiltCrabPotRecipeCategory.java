package thebetweenlands.compat.jei.recipes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.SiltCrabPotFilterRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SiltCrabPotRecipeCategory extends AbstractRecipeCategory<SiltCrabPotFilterRecipe> {
	public static final RecipeType<SiltCrabPotFilterRecipe> FILTER = RecipeType.create(TheBetweenlands.ID, "silt_crab_pot_filter", SiltCrabPotFilterRecipe.class);
	private final IDrawable background;
	private final IDrawable remains;
	private final IDrawable arrow;

	public SiltCrabPotRecipeCategory(IGuiHelper helper) {
		super(FILTER, Component.translatable("jei.thebetweenlands.recipe.silt_crab_pot_filter"), helper.createDrawableItemLike(BlockRegistry.CRAB_POT_FILTER), 91, 83);
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/silt_crab_pot_filter_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 91, 83);
		var remains = helper.createDrawable(location, 92, 4, 16, 10);
		this.remains = helper.createAnimatedDrawable(remains, 200, IDrawableAnimated.StartDirection.TOP, true);
		var arrow = helper.createDrawable(location, 91, 18, 22, 16);
		this.arrow = helper.createAnimatedDrawable(arrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SiltCrabPotFilterRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 30).addIngredients(recipe.input());
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 66).addItemStack(ItemRegistry.ANADIA_REMAINS.toStack());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 48).addItemStack(recipe.result());
	}

	@Override
	public void draw(SiltCrabPotFilterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		this.background.draw(graphics);
		this.arrow.draw(graphics, 31, 47);
		this.remains.draw(graphics, 1, 51);
	}
}
