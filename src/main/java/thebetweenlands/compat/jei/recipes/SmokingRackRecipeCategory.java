package thebetweenlands.compat.jei.recipes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.recipes.SmokingRackRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class SmokingRackRecipeCategory implements IRecipeCategory<SmokingRackRecipe> {

	public static final RecipeType<SmokingRackRecipe> SMOKING_RACK = RecipeType.create(TheBetweenlands.ID, "smoking_rack", SmokingRackRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;

	public SmokingRackRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/smoking_rack_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 126, 18);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.SMOKING_RACK.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.smoking_rack");
	}

	@Override
	public RecipeType<SmokingRackRecipe> getRecipeType() {
		return SMOKING_RACK;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SmokingRackRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addIngredients(recipe.getIngredients().getFirst());
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(BlockRegistry.FALLEN_LEAVES.toStack());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 1).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}

	@Override
	public void draw(SmokingRackRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		this.background.draw(graphics);
		String time = String.format("%ds", recipe.smokingTime() / 20);
		int x = 66;
		x -= Minecraft.getInstance().font.width(time) / 2;
		graphics.drawString(Minecraft.getInstance().font, time, x, 8, 0xFFFFFFFF);
	}
}
