package thebetweenlands.common.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class DruidAltarRecipe {
    public static ArrayList<DruidAltarRecipe> druidAltarRecipes = new ArrayList<DruidAltarRecipe>();

    public ItemStack input1;
    public ItemStack input2;
    public ItemStack input3;
    public ItemStack input4;
    public ItemStack output;

    public DruidAltarRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack output) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.output = output;
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
                if (matches(input1, itemStack)) {
                    next = true;
                    recipeStacks.remove(itemStack);
                }
                if (next)
                    break;
            }
            if (next) {
                next = false;
                for (ItemStack itemStack : recipeStacks) {
                    if (matches(input2, itemStack)) {
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
                    if (matches(input3, itemStack)) {
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
                    if (matches(input4, itemStack)) {
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
        for (DruidAltarRecipe recipe : druidAltarRecipes)
            if (matches(recipe.input1, stack) || matches(recipe.input2, stack) || matches(recipe.input3, stack) || matches(recipe.input4, stack))
                return true;
        return false;
    }

    private static boolean matches(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack2.getItem() == itemStack1.getItem() && itemStack2.getItemDamage() == itemStack1.getItemDamage();
    }
}
