package thebetweenlands.common.recipe.misc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public class DruidAltarRecipe {
    public static ArrayList<DruidAltarRecipe> druidAltarRecipes = new ArrayList<DruidAltarRecipe>();

    private ItemStack input1;
    private ItemStack input2;
    private ItemStack input3;
    private ItemStack input4;
    private ItemStack output;

    public DruidAltarRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack output) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.output = output;
    }

    public static void addRecipe(DruidAltarRecipe recipe) {
        druidAltarRecipes.add(recipe);
    }
    
    public static void addRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack output) {
        druidAltarRecipes.add(new DruidAltarRecipe(input1, input2, input3, input4, output));
    }

    public static Item getItem(Block block) {
        return Item.getItemFromBlock(block);
    }

    public static DruidAltarRecipe getDruidAltarRecipe(ItemStack output) {
        for (DruidAltarRecipe druidAltarRecipe : druidAltarRecipes) {
            if (druidAltarRecipe.output.getItem() == output.getItem() && druidAltarRecipe.output.getItemDamage() == output.getItemDamage())
                return druidAltarRecipe;
        }
        return null;
    }


    public static DruidAltarRecipe getOutput(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4) {
        for (DruidAltarRecipe druidAltarRecipe : druidAltarRecipes) {
            ArrayList<ItemStack> recipeStacks = new ArrayList<ItemStack>();
            recipeStacks.add(druidAltarRecipe.input1);
            recipeStacks.add(druidAltarRecipe.input2);
            recipeStacks.add(druidAltarRecipe.input3);
            recipeStacks.add(druidAltarRecipe.input4);
            boolean next = false;
            if (input1 == null || input2 == null || input3 == null || input4 == null)
                break;
            for (ItemStack itemStack : recipeStacks) {
                if (matches(itemStack, input1)) {
                    next = true;
                    recipeStacks.remove(itemStack);
                }
                if (next)
                    break;
            }
            if (next) {
                next = false;
                for (ItemStack itemStack : recipeStacks) {
                    if (matches(itemStack, input2)) {
                        next = true;
                        recipeStacks.remove(itemStack);
                    }
                    if (next)
                        break;
                }
            }
            if (next) {
                next = false;
                for (ItemStack itemStack : recipeStacks) {
                    if (matches(itemStack, input3)) {
                        next = true;
                        recipeStacks.remove(itemStack);
                    }
                    if (next)
                        break;
                }
            }
            if (next) {
                next = false;
                for (ItemStack itemStack : recipeStacks) {
                    if (matches(itemStack, input4)) {
                        next = true;
                        recipeStacks.remove(itemStack);
                    }
                    if (next)
                        break;
                }
            }
            if (next && recipeStacks.size() == 0) {
                return druidAltarRecipe;
            }
        }
        return null;
    }

    public static boolean isValidItem(ItemStack stack) {
        for (DruidAltarRecipe recipe : druidAltarRecipes) {
            if (matches(recipe.input1, stack) || matches(recipe.input2, stack) || matches(recipe.input3, stack) || matches(recipe.input4, stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(ItemStack input, ItemStack toCheck) {
        return toCheck.getItem() == input.getItem() && (input.getItemDamage() == OreDictionary.WILDCARD_VALUE || toCheck.getItemDamage() == input.getItemDamage());
    }

    public ArrayList<ItemStack> getInputs(){
        ArrayList<ItemStack> l = new ArrayList();
        l.add(input1);
        l.add(input2);
        l.add(input3);
        l.add(input4);
        return l;
    }

    public ItemStack getOutput(){
        return output;
    }
}
