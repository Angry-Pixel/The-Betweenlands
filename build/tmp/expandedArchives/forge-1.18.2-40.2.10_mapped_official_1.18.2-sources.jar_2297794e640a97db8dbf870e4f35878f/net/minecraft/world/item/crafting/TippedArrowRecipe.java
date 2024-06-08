package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
   public TippedArrowRecipe(ResourceLocation p_44503_) {
      super(p_44503_);
   }

   public boolean matches(CraftingContainer p_44515_, Level p_44516_) {
      if (p_44515_.getWidth() == 3 && p_44515_.getHeight() == 3) {
         for(int i = 0; i < p_44515_.getWidth(); ++i) {
            for(int j = 0; j < p_44515_.getHeight(); ++j) {
               ItemStack itemstack = p_44515_.getItem(i + j * p_44515_.getWidth());
               if (itemstack.isEmpty()) {
                  return false;
               }

               if (i == 1 && j == 1) {
                  if (!itemstack.is(Items.LINGERING_POTION)) {
                     return false;
                  }
               } else if (!itemstack.is(Items.ARROW)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingContainer p_44513_) {
      ItemStack itemstack = p_44513_.getItem(1 + p_44513_.getWidth());
      if (!itemstack.is(Items.LINGERING_POTION)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.setPotion(itemstack1, PotionUtils.getPotion(itemstack));
         PotionUtils.setCustomEffects(itemstack1, PotionUtils.getCustomEffects(itemstack));
         return itemstack1;
      }
   }

   public boolean canCraftInDimensions(int p_44505_, int p_44506_) {
      return p_44505_ >= 2 && p_44506_ >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.TIPPED_ARROW;
   }
}