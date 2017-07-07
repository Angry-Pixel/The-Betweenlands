package thebetweenlands.compat.jei.recipes.purifier;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PurifierRecipeCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/manual/purifier_grid.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 107, 61);

    @Nonnull
    private final IDrawable tank = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 168, 1, 10, 58);

    @Override
    public String getUid() {
        return ModInfo.ID + ":purifier";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.thebetweenlands.recipe.purifier");
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
        recipeLayout.getItemStacks().init(0, true, 24, 1);
        recipeLayout.getItemStacks().init(1, true, 24, 41);
        recipeLayout.getItemStacks().init(2, false, 84, 21);
        recipeLayout.getFluidStacks().init(3, true, 1, 1, 10, 58, Fluid.BUCKET_VOLUME * 16, true, tank);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(ItemStack.class).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).get(0));
        recipeLayout.getFluidStacks().set(3, ingredients.getInputs(FluidStack.class).get(0));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }
}
