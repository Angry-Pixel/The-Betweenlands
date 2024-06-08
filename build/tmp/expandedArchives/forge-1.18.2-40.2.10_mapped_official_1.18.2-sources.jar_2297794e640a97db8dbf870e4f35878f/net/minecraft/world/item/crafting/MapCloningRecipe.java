package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MapCloningRecipe extends CustomRecipe {
   public MapCloningRecipe(ResourceLocation p_43968_) {
      super(p_43968_);
   }

   public boolean matches(CraftingContainer p_43980_, Level p_43981_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_43980_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_43980_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(Items.FILLED_MAP)) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.MAP)) {
                  return false;
               }

               ++i;
            }
         }
      }

      return !itemstack.isEmpty() && i > 0;
   }

   public ItemStack assemble(CraftingContainer p_43978_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_43978_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_43978_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(Items.FILLED_MAP)) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.MAP)) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && i >= 1) {
         ItemStack itemstack2 = itemstack.copy();
         itemstack2.setCount(i + 1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int p_43970_, int p_43971_) {
      return p_43970_ >= 3 && p_43971_ >= 3;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_CLONING;
   }
}