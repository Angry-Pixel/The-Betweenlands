package thebetweenlands.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;

import java.util.ArrayList;

/**
 * Created by Bart on 13-7-2015.
 */
public class CompostRegistry {
    public static ArrayList<CompostItem> compostableItems = new ArrayList<CompostItem>();

    public static void registerCompostItems(){
        addItem(10, getItem(BLBlockRegistry.saplingPurpleRain));
    }

    public static void addItem(int compostAmount, Item compostItem, int meta){
        compostableItems.add(new CompostItem(compostAmount, compostItem, meta));
    }
    public static void addItem(int compostAmount, Item compostItem){
        compostableItems.add(new CompostItem(compostAmount, compostItem));
    }

    public static Item getItem(Block block){
        return Item.getItemFromBlock(block);
    }

    public static int hasCompostValue(ItemStack stack){
        for(CompostItem compostItem:compostableItems){
            if(compostItem.compostItem == stack.getItem() && compostItem.itemDamage == stack.getItemDamage())
                    return compostItem.compostAmount;
        }
        return 0;
    }
}
