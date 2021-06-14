package thebetweenlands.compat.jei.recipes.smoking_rack;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

public class SmokingRackRecipeCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/manual/smoking_rack_grid.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 126, 18);

    @Override
    public String getUid() {
        return ModInfo.ID + ":smoking_rack";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.thebetweenlands.recipe.smoking_rack");
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
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 36, 0);
        recipeLayout.getItemStacks().init(1, true, 0, 0);
        recipeLayout.getItemStacks().init(2, false, 108, 0);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
