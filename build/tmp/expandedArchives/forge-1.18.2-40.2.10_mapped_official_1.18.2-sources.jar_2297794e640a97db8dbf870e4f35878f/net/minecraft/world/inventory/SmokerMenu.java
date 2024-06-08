package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class SmokerMenu extends AbstractFurnaceMenu {
   public SmokerMenu(int p_40274_, Inventory p_40275_) {
      super(MenuType.SMOKER, RecipeType.SMOKING, RecipeBookType.SMOKER, p_40274_, p_40275_);
   }

   public SmokerMenu(int p_40277_, Inventory p_40278_, Container p_40279_, ContainerData p_40280_) {
      super(MenuType.SMOKER, RecipeType.SMOKING, RecipeBookType.SMOKER, p_40277_, p_40278_, p_40279_, p_40280_);
   }
}