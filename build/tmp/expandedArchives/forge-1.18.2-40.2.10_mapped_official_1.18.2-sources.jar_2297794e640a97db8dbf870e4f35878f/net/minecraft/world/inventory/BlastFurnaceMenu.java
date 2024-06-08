package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastFurnaceMenu extends AbstractFurnaceMenu {
   public BlastFurnaceMenu(int p_39079_, Inventory p_39080_) {
      super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, p_39079_, p_39080_);
   }

   public BlastFurnaceMenu(int p_39082_, Inventory p_39083_, Container p_39084_, ContainerData p_39085_) {
      super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, p_39082_, p_39083_, p_39084_, p_39085_);
   }
}