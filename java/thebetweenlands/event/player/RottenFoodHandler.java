/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package thebetweenlands.event.player;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class RottenFoodHandler
{
    public static final RottenFoodHandler INSTANCE = new RottenFoodHandler();

    private static final ImmutableList<Item> EXCEPTION_INSTS;

    static {
        List<Item> items = new ArrayList<>();
        items.add(BLItemRegistry.rottenFood);
        items.add(Items.rotten_flesh);
        items.add(Items.potionitem);

        items.addAll(BLItemRegistry.ITEMS);

        EXCEPTION_INSTS = ImmutableList.copyOf(items);
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if( (event.entity instanceof EntityPlayer) && event.entity.dimension == ModInfo.DIMENSION_ID ) {
            EntityPlayer player = (EntityPlayer) event.entity;

            int invCount = player.inventory.getSizeInventory();
            for( int i = 0; i < invCount; i++ ) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if( stack != null && stack.getItem() instanceof ItemFood && !EXCEPTION_INSTS.contains(stack.getItem()) ) {
                    player.inventory.setInventorySlotContents(i, new ItemStack(BLItemRegistry.rottenFood, stack.stackSize));
                }
            }
        }
    }
}
