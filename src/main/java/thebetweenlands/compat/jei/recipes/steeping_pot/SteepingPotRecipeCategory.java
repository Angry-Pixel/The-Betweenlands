package thebetweenlands.compat.jei.recipes.steeping_pot;

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
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

public class SteepingPotRecipeCategory implements IRecipeCategory {
    private final ResourceLocation backgroundLocation = new ResourceLocation(ModInfo.ID + ":textures/gui/steeping_pot_jei.png");
    @Nonnull
    protected final IDrawable background = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 0, 0, 100, 100);

    @Nonnull
    private final IDrawable tank = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 168, 1, 10, 58);
    private final IDrawableStatic backgroundArrow = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 100, 14, 16, 14);
    private final IDrawableAnimated arrow = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createAnimatedDrawable(backgroundArrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
    @Nonnull
    protected final IDrawableStatic bundle = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createDrawable(backgroundLocation, 100, 0, 14, 14);

    @Override
    public String getUid() {
        return ModInfo.ID + ":steeping_pot";
    }

    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jei.thebetweenlands.recipe.steeping_pot");
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
		arrow.draw(minecraft, 42, 78);
		bundle.draw(minecraft, 43, 32);
	}
    
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
    	recipeLayout.getFluidStacks().init(0, true, 7, 77, 16, 16, Fluid.BUCKET_VOLUME, true, tank);
    	recipeLayout.getItemStacks().init(0, true, 41, 6); //north slot
    	recipeLayout.getItemStacks().init(1, true, 17, 30); //west slot
    	recipeLayout.getItemStacks().init(2, true, 65, 30); //east slot
    	recipeLayout.getItemStacks().init(3, true, 41, 54); //south slot
    	recipeLayout.getFluidStacks().init(1, false, 77, 77, 16, 16, Fluid.BUCKET_VOLUME, true, tank); //output slot

    	recipeLayout.getFluidStacks().set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        recipeLayout.getItemStacks().set(2, ingredients.getInputs(VanillaTypes.ITEM).get(2));
        recipeLayout.getItemStacks().set(3, ingredients.getInputs(VanillaTypes.ITEM).get(3));
        recipeLayout.getFluidStacks().set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0)); 
    }
}
