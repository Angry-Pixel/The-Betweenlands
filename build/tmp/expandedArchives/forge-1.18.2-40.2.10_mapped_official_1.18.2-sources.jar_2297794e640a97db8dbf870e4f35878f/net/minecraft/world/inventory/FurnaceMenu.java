package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class FurnaceMenu extends AbstractFurnaceMenu {
   public FurnaceMenu(int p_39532_, Inventory p_39533_) {
      super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, p_39532_, p_39533_);
   }

   public FurnaceMenu(int p_39535_, Inventory p_39536_, Container p_39537_, ContainerData p_39538_) {
      super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, p_39535_, p_39536_, p_39537_, p_39538_);
   }
}