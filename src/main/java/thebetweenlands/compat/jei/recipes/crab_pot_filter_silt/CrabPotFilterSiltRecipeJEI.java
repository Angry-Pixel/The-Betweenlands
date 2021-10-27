package thebetweenlands.compat.jei.recipes.crab_pot_filter_silt;

import java.util.ArrayList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;

public class CrabPotFilterSiltRecipeJEI implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;

    public CrabPotFilterSiltRecipeJEI(CrabPotFilterRecipeSilt recipe) {
        this.input = recipe.getInput();
        this.output = recipe.getOutput(input);
    }

    public CrabPotFilterSiltRecipeJEI(ICrabPotFilterRecipeSilt recipe) {
        this.input = recipe.getInput();
        this.output = recipe.getOutput(input);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    	ArrayList<ItemStack> inputs = new ArrayList<>();
        inputs.add(input);
        inputs.add(EnumItemMisc.ANADIA_REMAINS.create(1));
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }
}
