package thebetweenlands.compat.jei.recipes.crab_pot_filter_bubbler;

import java.util.ArrayList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeBubbler;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeBubbler;

public class CrabPotFilterBubblerRecipeJEI implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;

    public CrabPotFilterBubblerRecipeJEI(CrabPotFilterRecipeBubbler recipe) {
        this.input = recipe.getInput();
        this.output = recipe.getOutput(input);
    }

    public CrabPotFilterBubblerRecipeJEI(ICrabPotFilterRecipeBubbler recipe) {
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
