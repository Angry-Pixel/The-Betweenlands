package thebetweenlands.compat.jei.recipes.purifier;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.purifier.PurifierRecipeStandard;
import thebetweenlands.common.registries.FluidRegistry;

import java.util.ArrayList;

public class PurifierRecipeJEI implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;

    public PurifierRecipeJEI(PurifierRecipeStandard recipe) {
        this.input = recipe.getInput();
        this.output = recipe.getOutput(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        inputs.add(input);
        inputs.add(ItemMisc.EnumItemMisc.SULFUR.create(1));
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
        ingredients.setInput(FluidStack.class, new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
    }
}
