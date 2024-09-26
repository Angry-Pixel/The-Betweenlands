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
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.api.recipes.SteepingPotRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SteepingPotRecipeCategory implements IRecipeCategory<SteepingPotRecipe> {

	public static final RecipeType<SteepingPotRecipe> STEEPING_POT = RecipeType.create(TheBetweenlands.ID, "steeping_pot", SteepingPotRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;
	private final IDrawable arrow;

	public SteepingPotRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/steeping_pot_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 100, 100);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.STEEPING_POT.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.steeping_pot");
		var arrow = helper.createDrawable(location, 100, 14, 16, 14);
		this.arrow = helper.createAnimatedDrawable(arrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public RecipeType<SteepingPotRecipe> getRecipeType() {
		return STEEPING_POT;
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
	public void setRecipe(IRecipeLayoutBuilder builder, SteepingPotRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 42, 7).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 0));
		builder.addSlot(RecipeIngredientRole.INPUT, 18, 31).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 1));
		builder.addSlot(RecipeIngredientRole.INPUT, 66, 31).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 2));
		builder.addSlot(RecipeIngredientRole.INPUT, 42, 55).addIngredients(this.getIngredientOrEmpty(recipe.getIngredients(), 3));
		if (!recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 77).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 7, 77).addFluidStack(recipe.getInputFluid().getStacks()[0].getFluid(), FluidType.BUCKET_VOLUME);
		if (!recipe.getResultFluid(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 77).addFluidStack(recipe.getResultFluid(Minecraft.getInstance().level.registryAccess()).getFluid(), FluidType.BUCKET_VOLUME);
		}
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 42, 31).addItemStack(ItemRegistry.SILK_BUNDLE.toStack());
	}

	@Override
	public void draw(SteepingPotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		this.arrow.draw(graphics, 42, 78);
	}

	private Ingredient getIngredientOrEmpty(NonNullList<Ingredient> items, int index) {
		if (index < items.size()) {
			return items.get(index);
		}
		return Ingredient.EMPTY;
	}
}
