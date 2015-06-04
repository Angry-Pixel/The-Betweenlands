package thebetweenlands.utils;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

/**
 * @author OnyxDarkKnight
 */
public class WeightedRandomItem extends WeightedRandom.Item
{
    private final ItemStack item;
    private int maxMeta, minMeta, minItem, maxItem;

    public WeightedRandomItem(ItemStack item, int weight)
    {
        super(weight);
        this.item = item;
        this.minItem = 1;
        this.maxItem = 1;
        this.maxMeta = 0;
        this.minMeta = 0;
    }

    public WeightedRandomItem(ItemStack item, int maxMetadata, int weight)
    {
        this(item, weight);
        this.maxMeta = maxMetadata;
    }

    public WeightedRandomItem(ItemStack item, int weight, int minItem, int maxItem)
    {
        this(item, weight);
        this.minItem = minItem;
        this.maxItem = maxItem;
    }
    
    public WeightedRandomItem setMaxMetadata(int meta)
    {
        this.maxMeta = meta;
        return this;        
    }
    
    public WeightedRandomItem setMinMetadata(int meta)
    {
        this.minMeta = meta;
        return this;        
    }
    
    public WeightedRandomItem setMinItem(int min)
    {
        this.minItem = min;
        return this;        
    }
    
    public WeightedRandomItem setMaxItem(int max)
    {
        this.maxItem = max;
        return this;        
    }

    public ItemStack getItem(Random random)
    {
        ItemStack itemstack = this.item.copy();
        if(maxMeta > 0) itemstack.setItemDamage(minMeta + random.nextInt(maxMeta - minMeta));
        if(maxItem > 1) itemstack.stackSize = this.minItem + random.nextInt(this.maxItem - this.minItem + 1);
        return itemstack;
    }
}