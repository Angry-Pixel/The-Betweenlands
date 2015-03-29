/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemRottenFood
        extends ItemFood
{
    public ItemRottenFood() {
        super(-1, -1.0F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if( player != null ) {
            player.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 200, 1));
            player.addPotionEffect(new PotionEffect(Potion.poison.getId(), 200, 1));
        }
    }
}
