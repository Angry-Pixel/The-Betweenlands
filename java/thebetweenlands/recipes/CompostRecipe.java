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

    int compostAmount;
    Item compostItem;
    int itemDamage;

    public CompostRecipe(int compostAmount, Item compostItem, int itemDamage){
        this.compostAmount = compostAmount;
        this.compostItem = compostItem;
        this.itemDamage = itemDamage;
    }

    public CompostRecipe(int compostAmount, Item compostItem){
        this.compostAmount = compostAmount;
        this.compostItem = compostItem;
        this.itemDamage = 0;
    }



    public static void addRecipe(int compostAmount, Item compostItem, int meta){
        compostRecipes.add(new CompostRecipe(compostAmount, compostItem, meta));
    }
    public static void addRecipe(int compostAmount, Item compostItem){
        compostRecipes.add(new CompostRecipe(compostAmount, compostItem));
    }

    public static Item getItem(Block block){
        return Item.getItemFromBlock(block);
    }

    public static int hasCompostValue(ItemStack stack){
        for(CompostRecipe compostRecipe: compostRecipes){
            if(compostRecipe.compostItem == stack.getItem() && compostRecipe.itemDamage == stack.getItemDamage())
                return compostRecipe.compostAmount;
        }
        return 0;
    }
}
