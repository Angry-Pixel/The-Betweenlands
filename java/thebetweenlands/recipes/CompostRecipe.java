package thebetweenlands.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by Bart on 13-7-2015.
 */
public class CompostRecipe {
    public static ArrayList<CompostRecipe> compostRecipes = new ArrayList<CompostRecipe>();

    public int compostAmount;
    public Item compostItem;
    public int itemDamage;
    public int compostTime;

    public CompostRecipe(int compostAmount, int compostTime, Item compostItem, int itemDamage) {
        this.compostAmount = compostAmount;
        this.compostTime = compostTime;
        this.compostItem = compostItem;
        this.itemDamage = itemDamage;
    }

    public CompostRecipe(int compostAmount, int compostTime, Item compostItem) {
        this.compostAmount = compostAmount;
        this.compostTime = compostTime;
        this.compostItem = compostItem;
        this.itemDamage = 0;
    }


    public static void addRecipe(int compostAmount, int compostTime, Item compostItem, int meta) {
        compostRecipes.add(new CompostRecipe(compostAmount, compostTime, compostItem, meta));
    }

    public static void addRecipe(int compostAmount, int compostTime, Item compostItem) {
        compostRecipes.add(new CompostRecipe(compostAmount, compostTime, compostItem));
    }

    public static void addRecipe(int compostAmount, int compostTime, ArrayList<ItemStack> compostItems) {
        for (ItemStack compostItem : compostItems)
            compostRecipes.add(new CompostRecipe(compostAmount, compostTime, compostItem.getItem(), compostItem.getItemDamage()));
    }

    public static Item getItem(Block block) {
        return Item.getItemFromBlock(block);
    }

    public static CompostRecipe getCompostRecipe(ItemStack stack) {
        for (CompostRecipe compostRecipe : compostRecipes) {
            if (compostRecipe.compostItem == stack.getItem() && compostRecipe.itemDamage == stack.getItemDamage())
                return compostRecipe;
        }
        return null;
    }
}
