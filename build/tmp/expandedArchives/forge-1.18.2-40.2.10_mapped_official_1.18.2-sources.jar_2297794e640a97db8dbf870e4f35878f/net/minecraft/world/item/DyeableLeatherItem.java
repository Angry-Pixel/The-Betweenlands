package net.minecraft.world.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableLeatherItem {
   String TAG_COLOR = "color";
   String TAG_DISPLAY = "display";
   int DEFAULT_LEATHER_COLOR = 10511680;

   default boolean hasCustomColor(ItemStack p_41114_) {
      CompoundTag compoundtag = p_41114_.getTagElement("display");
      return compoundtag != null && compoundtag.contains("color", 99);
   }

   default int getColor(ItemStack p_41122_) {
      CompoundTag compoundtag = p_41122_.getTagElement("display");
      return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 10511680;
   }

   default void clearColor(ItemStack p_41124_) {
      CompoundTag compoundtag = p_41124_.getTagElement("display");
      if (compoundtag != null && compoundtag.contains("color")) {
         compoundtag.remove("color");
      }

   }

   default void setColor(ItemStack p_41116_, int p_41117_) {
      p_41116_.getOrCreateTagElement("display").putInt("color", p_41117_);
   }

   static ItemStack dyeArmor(ItemStack p_41119_, List<DyeItem> p_41120_) {
      ItemStack itemstack = ItemStack.EMPTY;
      int[] aint = new int[3];
      int i = 0;
      int j = 0;
      DyeableLeatherItem dyeableleatheritem = null;
      Item item = p_41119_.getItem();
      if (item instanceof DyeableLeatherItem) {
         dyeableleatheritem = (DyeableLeatherItem)item;
         itemstack = p_41119_.copy();
         itemstack.setCount(1);
         if (dyeableleatheritem.hasCustomColor(p_41119_)) {
            int k = dyeableleatheritem.getColor(itemstack);
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            i += (int)(Math.max(f, Math.max(f1, f2)) * 255.0F);
            aint[0] += (int)(f * 255.0F);
            aint[1] += (int)(f1 * 255.0F);
            aint[2] += (int)(f2 * 255.0F);
            ++j;
         }

         for(DyeItem dyeitem : p_41120_) {
            float[] afloat = dyeitem.getDyeColor().getTextureDiffuseColors();
            int i2 = (int)(afloat[0] * 255.0F);
            int l = (int)(afloat[1] * 255.0F);
            int i1 = (int)(afloat[2] * 255.0F);
            i += Math.max(i2, Math.max(l, i1));
            aint[0] += i2;
            aint[1] += l;
            aint[2] += i1;
            ++j;
         }
      }

      if (dyeableleatheritem == null) {
         return ItemStack.EMPTY;
      } else {
         int j1 = aint[0] / j;
         int k1 = aint[1] / j;
         int l1 = aint[2] / j;
         float f3 = (float)i / (float)j;
         float f4 = (float)Math.max(j1, Math.max(k1, l1));
         j1 = (int)((float)j1 * f3 / f4);
         k1 = (int)((float)k1 * f3 / f4);
         l1 = (int)((float)l1 * f3 / f4);
         int j2 = (j1 << 8) + k1;
         j2 = (j2 << 8) + l1;
         dyeableleatheritem.setColor(itemstack, j2);
         return itemstack;
      }
   }
}