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
import net.minecraft.util.Mth;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.PurifierRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class PurifierRecipeCategory implements IRecipeCategory<PurifierRecipe> {

	public static final RecipeType<PurifierRecipe> PURIFIER = RecipeType.create(TheBetweenlands.ID, "purifier", PurifierRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;
	private static final ResourceLocation TANK = TheBetweenlands.prefix("container/purifier/tank");

	public PurifierRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/purifier_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 107, 65);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.PURIFIER.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.purifier");
	}

	@Override
	public RecipeType<PurifierRecipe> getRecipeType() {
		return PURIFIER;
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
	public void setRecipe(IRecipeLayoutBuilder builder, PurifierRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 25, 5).addIngredients(recipe.getIngredients().getFirst());
		builder.addSlot(RecipeIngredientRole.INPUT, 25, 44).addItemStack(ItemRegistry.SULFUR.toStack());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 24).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}

	@Override
	public void draw(PurifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		int water = Mth.ceil((recipe.requiredWater() / 4000.0F) * 64.0F) + 1;
		graphics.blitSprite(TANK, 12, 65, 0, 65 - water, 0, (65 - water), 12, water);
	}
}
