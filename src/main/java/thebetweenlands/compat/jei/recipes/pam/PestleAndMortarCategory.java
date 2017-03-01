package thebetweenlands.compat.jei.recipes.pam;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PestleAndMortarCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/manual/pam_grid.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 107, 34);

    @Override
    public String getUid() {
        return ModInfo.ID + ":pestle_and_mortar";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.betweenlands.recipe.pestle_and_mortar");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 0, 6);
        recipeLayout.getItemStacks().init(1, true, 44, 6);
        recipeLayout.getItemStacks().init(2, false, 88, 6);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(ItemStack.class).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).get(0));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }
}
