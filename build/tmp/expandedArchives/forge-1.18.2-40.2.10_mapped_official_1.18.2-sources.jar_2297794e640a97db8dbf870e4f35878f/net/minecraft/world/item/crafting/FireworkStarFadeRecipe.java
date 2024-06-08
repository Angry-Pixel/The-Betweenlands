package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

   public FireworkStarFadeRecipe(ResourceLocation p_43861_) {
      super(p_43861_);
   }

   public boolean matches(CraftingContainer p_43873_, Level p_43874_) {
      boolean flag = false;
      boolean flag1 = false;

      for(int i = 0; i < p_43873_.getContainerSize(); ++i) {
         ItemStack itemstack = p_43873_.getItem(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() instanceof DyeItem) {
               flag = true;
            } else {
               if (!STAR_INGREDIENT.test(itemstack)) {
                  return false;
               }

               if (flag1) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag1 && flag;
   }

   public ItemStack assemble(CraftingContainer p_43871_) {
      List<Integer> list = Lists.newArrayList();
      ItemStack itemstack = null;

      for(int i = 0; i < p_43871_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_43871_.getItem(i);
         Item item = itemstack1.getItem();
         if (item instanceof DyeItem) {
            list.add(((DyeItem)item).getDyeColor().getFireworkColor());
         } else if (STAR_INGREDIENT.test(itemstack1)) {
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
         }
      }

      if (itemstack != null && !list.isEmpty()) {
         itemstack.getOrCreateTagElement("Explosion").putIntArray("FadeColors", list);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int p_43863_, int p_43864_) {
      return p_43863_ * p_43864_ >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR_FADE;
   }
}