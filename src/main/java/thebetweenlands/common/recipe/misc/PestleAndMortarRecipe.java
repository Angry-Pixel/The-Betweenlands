package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;

import javax.annotation.Nonnull;

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

    @MethodsReturnNonnullByDefault
    public static ItemStack getResult(ItemStack input) {
        for (IPestleAndMortarRecipe recipe : recipes) {
            if (recipe.matchesInput(input)) {
                return recipe.getOutput(input);
            }
        }
        return ItemStack.EMPTY;
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
