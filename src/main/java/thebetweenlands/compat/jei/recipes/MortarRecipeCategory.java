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
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class MortarRecipeCategory implements IRecipeCategory<MortarRecipe> {

	public static final RecipeType<MortarRecipe> MORTAR = RecipeType.create(TheBetweenlands.ID, "mortar", MortarRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;

	public MortarRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/mortar_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 107, 34);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.MORTAR.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.mortar");
	}

	@Override
	public RecipeType<MortarRecipe> getRecipeType() {
		return MORTAR;
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
	public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 7).addIngredients(recipe.getIngredients().getFirst());
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 45, 7).addItemStack(new ItemStack(ItemRegistry.PESTLE, 1, DataComponentPatch.builder().set(DataComponentRegistry.PESTLE_ACTIVE.get(), Unit.INSTANCE).build()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 7).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}

	@Override
	public void draw(MortarRecipe recipe, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY) {
		this.background.draw(graphics);
	}
}
