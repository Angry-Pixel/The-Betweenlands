package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation p_43984_) {
      super(p_43984_, "", 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingContainer p_43993_, Level p_43994_) {
      if (!super.matches(p_43993_, p_43994_)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;

         for(int i = 0; i < p_43993_.getContainerSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = p_43993_.getItem(i);
            if (itemstack1.is(Items.FILLED_MAP)) {
               itemstack = itemstack1;
            }
         }

         if (itemstack.isEmpty()) {
            return false;
         } else {
            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, p_43994_);
            if (mapitemsaveddata == null) {
               return false;
            } else if (mapitemsaveddata.isExplorationMap()) {
               return false;
            } else {
               return mapitemsaveddata.scale < 4;
            }
         }
      }
   }

   public ItemStack assemble(CraftingContainer p_43991_) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_43991_.getContainerSize() && itemstack.isEmpty(); ++i) {
         ItemStack itemstack1 = p_43991_.getItem(i);
         if (itemstack1.is(Items.FILLED_MAP)) {
            itemstack = itemstack1;
         }
      }

      itemstack = itemstack.copy();
      itemstack.setCount(1);
      itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
      return itemstack;
   }

   public boolean isSpecial() {
      return true;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_EXTENDING;
   }
}