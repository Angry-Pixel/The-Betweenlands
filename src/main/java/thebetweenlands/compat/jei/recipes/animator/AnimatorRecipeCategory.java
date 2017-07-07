package thebetweenlands.compat.jei.recipes.animator;

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

public class AnimatorRecipeCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/manual/animator_grid.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 108, 67);

    @Override
    public String getUid() {
        return ModInfo.ID + ":animator";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.thebetweenlands.recipe.animator");
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
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 0, 15);
        recipeLayout.getItemStacks().init(1, true, 0, 49);
        recipeLayout.getItemStacks().init(2, true, 90, 49);

        recipeLayout.getItemStacks().init(3, false, 45, 15);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(ItemStack.class).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getInputs(ItemStack.class).get(2));
        if (ingredients.getOutputs(ItemStack.class).size() > 0)
            recipeLayout.getItemStacks().set(3, ingredients.getOutputs(ItemStack.class).get(0)); // probably broke something
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<String>();
    }
}
