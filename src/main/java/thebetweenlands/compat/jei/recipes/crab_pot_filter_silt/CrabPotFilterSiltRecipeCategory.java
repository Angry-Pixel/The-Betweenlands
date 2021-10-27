package thebetweenlands.compat.jei.recipes.crab_pot_filter_silt;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

public class CrabPotFilterSiltRecipeCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/manual/crab_pot_filter_grid_silt.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 91, 83);
    
    protected final IDrawableStatic anadia_parts_drawable = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 92, 4, 16, 10);
    private final IDrawableAnimated anadia_parts = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createAnimatedDrawable(anadia_parts_drawable, 200, IDrawableAnimated.StartDirection.TOP, true);

	protected final IDrawableStatic arrowDrawable = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 91, 18, 22, 16);
	private final IDrawableAnimated arrow = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

    @Override
    public String getUid() {
        return ModInfo.ID + ":crab_pot_filter_silt";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.thebetweenlands.recipe.crab_pot_filter_silt");
    }

    @Override
    public String getModName() {
        return ModInfo.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

	@Override
	public void drawExtras(Minecraft minecraft) {
		arrow.draw(minecraft, 31, 47);
		anadia_parts.draw(minecraft, 1, 51);
	}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 0, 29);
        recipeLayout.getItemStacks().init(1, true, 0, 65);
        recipeLayout.getItemStacks().init(2, false, 69, 47);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
