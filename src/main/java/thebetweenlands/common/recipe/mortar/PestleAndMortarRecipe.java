package thebetweenlands.common.recipe.mortar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;

public class PestleAndMortarRecipe implements IPestleAndMortarRecipe {
    private static final List<IPestleAndMortarRecipe> recipes = new ArrayList<IPestleAndMortarRecipe>();

    /**
     *
     * @param output
     *            what will be produced by the recipe
     * @param input
     *            the input item for the recipe
     */
    public static void addRecipe(ItemStack output, ItemStack input) {
        recipes.add(new PestleAndMortarRecipe(output, input));
    }
    
    public static void addRecipe(IPestleAndMortarRecipe recipe) {
        recipes.add(recipe);
    }

    public static void removeRecipe(IPestleAndMortarRecipe recipe) {
    	recipes.remove(recipe);
    }

    public static boolean isOutputUsedInAnyRecipe(ItemStack output) {
    	 for (IPestleAndMortarRecipe recipe : recipes) {
             if (recipe.isOutputUsed(output)) {
                 return true;
             }
         }
    	 return false;
    }
    
    public static ItemStack getResult(ItemStack input, ItemStack output, boolean inputOnly) {
        for (IPestleAndMortarRecipe recipe : recipes) {
            if (recipe.matchesInput(input, output, inputOnly)) {
                return recipe.getOutput(input, output);
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public static IPestleAndMortarRecipe getRecipe(ItemStack input, ItemStack output, boolean inputOnly) {
    	 for (IPestleAndMortarRecipe recipe : recipes) {
             if (recipe.matchesInput(input, output, inputOnly)) {
                 return recipe;
             }
         }
    	 return null;
    }
    
    public static ItemStack getInput(ItemStack output) {
        for (IPestleAndMortarRecipe recipe : recipes) {
            if (recipe.matchesOutput(output))
                return recipe.getInputs();
        }
        return ItemStack.EMPTY;
    }

    public static List<IPestleAndMortarRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    private final ItemStack output;
    private final ItemStack input;

    public PestleAndMortarRecipe(ItemStack output, ItemStack input) {
        this.output = output.copy();
        this.input = input.copy();

        if (input instanceof ItemStack)
            input = input.copy();

        else
            throw new IllegalArgumentException("Input must be an ItemStack");
    }

    @Override
    public ItemStack getInputs() {
        return input.copy();
    }

    public boolean matches(ItemStack stacks) {
        if (stacks != null)
            if (areStacksTheSame(getInputs(), stacks)) {
                stacks = null;
                return true;
            }
        return false;
    }

    @Override
    public boolean matchesOutput(ItemStack stacks) {
        if (stacks != null)
            if (areStacksTheSame(output, stacks)) {
                stacks = null;
                return true;
            }
        return false;
    }

    private boolean areStacksTheSame(ItemStack stack, ItemStack target) {
        return doesInputMatch(stack, target, false);
    }

    public static boolean doesInputMatch(ItemStack input, ItemStack toCheck, boolean matchSize) {
        if (input == null || toCheck == null)
            return false;

        if (input.getItem() == toCheck.getItem())
            if (input.getItemDamage() == OreDictionary.WILDCARD_VALUE || input.getItemDamage() == toCheck.getItemDamage())
                if (!matchSize || input.getCount() == toCheck.getCount()) {
                    if (input.hasTagCompound())
                        return toCheck.hasTagCompound() && input.getTagCompound().equals(toCheck.getTagCompound());
                    return true;
                }
        return false;
    }

	@Override
	public ItemStack getOutput(ItemStack input) {
		return this.output;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return doesInputMatch(this.input, stack, false);
	}
}
