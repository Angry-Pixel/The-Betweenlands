package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

public class BookCloningRecipe extends CustomRecipe {
   public BookCloningRecipe(ResourceLocation p_43802_) {
      super(p_43802_);
   }

   public boolean matches(CraftingContainer p_43814_, Level p_43815_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_43814_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_43814_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(Items.WRITTEN_BOOK)) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                  return false;
               }

               ++i;
            }
         }
      }

      return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
   }

   public ItemStack assemble(CraftingContainer p_43812_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_43812_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_43812_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(Items.WRITTEN_BOOK)) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && WrittenBookItem.getGeneration(itemstack) < 2) {
         ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
         CompoundTag compoundtag = itemstack.getTag().copy();
         compoundtag.putInt("generation", WrittenBookItem.getGeneration(itemstack) + 1);
         itemstack2.setTag(compoundtag);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingContainer p_43820_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_43820_.getContainerSize(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_43820_.getItem(i);
         if (itemstack.hasContainerItem()) {
            nonnulllist.set(i, itemstack.getContainerItem());
         } else if (itemstack.getItem() instanceof WrittenBookItem) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BOOK_CLONING;
   }

   public boolean canCraftInDimensions(int p_43804_, int p_43805_) {
      return p_43804_ >= 3 && p_43805_ >= 3;
   }
}
