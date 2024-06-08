package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(ResourceLocation p_43757_) {
      super(p_43757_);
   }

   public boolean matches(CraftingContainer p_43769_, Level p_43770_) {
      ItemStack itemstack = ItemStack.EMPTY;
      List<ItemStack> list = Lists.newArrayList();

      for(int i = 0; i < p_43769_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_43769_.getItem(i);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() instanceof DyeableLeatherItem) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!(itemstack1.getItem() instanceof DyeItem)) {
                  return false;
               }

               list.add(itemstack1);
            }
         }
      }

      return !itemstack.isEmpty() && !list.isEmpty();
   }

   public ItemStack assemble(CraftingContainer p_43767_) {
      List<DyeItem> list = Lists.newArrayList();
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_43767_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_43767_.getItem(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (item instanceof DyeableLeatherItem) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1.copy();
            } else {
               if (!(item instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               list.add((DyeItem)item);
            }
         }
      }

      return !itemstack.isEmpty() && !list.isEmpty() ? DyeableLeatherItem.dyeArmor(itemstack, list) : ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int p_43759_, int p_43760_) {
      return p_43759_ * p_43760_ >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}