/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package thebetweenlands.event.player;

import java.util.ArrayList;
import java.util.List;

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

public class RottenFoodHandler
{
	public static final RottenFoodHandler INSTANCE = new RottenFoodHandler();

	private static final ImmutableList<Item> EXCEPTION_INSTS;

	static {
		List<Item> items = new ArrayList<Item>();
		items.add(BLItemRegistry.rottenFood);
		items.add(Items.rotten_flesh);
		items.add(Items.potionitem);

		items.addAll(BLItemRegistry.ITEMS);

		EXCEPTION_INSTS = ImmutableList.copyOf(items);
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;

			int invCount = player.inventory.getSizeInventory();
			if(event.entity.dimension == ModInfo.DIMENSION_ID) {
				//Set to rotten food
				for(int i = 0; i < invCount; i++) {
					ItemStack stack = player.inventory.getStackInSlot(i);
					if(stack != null && stack.getItem() instanceof ItemFood && !EXCEPTION_INSTS.contains(stack.getItem())) {
						ItemStack rottenFoodStack = new ItemStack(BLItemRegistry.rottenFood, stack.stackSize);
						BLItemRegistry.rottenFood.setOriginalStack(rottenFoodStack, stack);
						player.inventory.setInventorySlotContents(i, rottenFoodStack);
					}
				}
			} else {
				//Revert rotten food
				for(int i = 0; i < invCount; i++) {
					ItemStack stack = player.inventory.getStackInSlot(i);
					if(stack != null && stack.getItem() == BLItemRegistry.rottenFood) {
						ItemStack originalStack = BLItemRegistry.rottenFood.getOriginalStack(stack);
						if(originalStack != null) {
							originalStack.stackSize = stack.stackSize;
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, null);
						}
					}
				}
			}
		}
	}
}
