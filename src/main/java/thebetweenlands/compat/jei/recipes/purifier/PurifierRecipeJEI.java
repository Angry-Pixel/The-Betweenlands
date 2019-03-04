package thebetweenlands.compat.jei.recipes.purifier;

import java.util.ArrayList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.purifier.PurifierRecipeStandard;
import thebetweenlands.common.registries.FluidRegistry;

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
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
        ingredients.setInput(VanillaTypes.FLUID, new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME / 4));
    }
}
