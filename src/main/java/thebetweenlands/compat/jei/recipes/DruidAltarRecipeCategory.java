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
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class DruidAltarRecipeCategory implements IRecipeCategory<DruidAltarRecipe> {

	public static final RecipeType<DruidAltarRecipe> ALTAR = RecipeType.create(TheBetweenlands.ID, "druid_altar", DruidAltarRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;

	public DruidAltarRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/druid_altar_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 75, 75);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.DRUID_ALTAR.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.druid_altar");
	}

	@Override
	public RecipeType<DruidAltarRecipe> getRecipeType() {
		return ALTAR;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DruidAltarRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 0));
		builder.addSlot(RecipeIngredientRole.INPUT, 57, 1).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 1));
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 57).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 2));
		builder.addSlot(RecipeIngredientRole.INPUT, 57, 57).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 3));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 29, 29).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}

	@Override
	public void draw(DruidAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		if (recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
			String string = Component.translatable("jei.thebetweenlands.druid_circle_reactivate").getString();

			int stringWidth = Minecraft.getInstance().font.width(string);
			int fontX = 36 - (stringWidth / 2);
			int fontY = 36;
			graphics.fill(fontX - 1, fontY - 5, fontX + stringWidth + 1, fontY + 10 - 5, 0xAA000000);
			graphics.drawString(Minecraft.getInstance().font, string, fontX, fontY - 4, 0xFFFFFFFF);
		}
	}

	private Ingredient getIngredientOrEmpty(NonNullList<Ingredient> items, int index) {
		if (index < items.size()) {
			return items.get(index);
		}
		return Ingredient.EMPTY;
	}
}
