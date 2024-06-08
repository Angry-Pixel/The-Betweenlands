package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipe<T> {
   default void placeRecipe(int p_135409_, int p_135410_, int p_135411_, Recipe<?> p_135412_, Iterator<T> p_135413_, int p_135414_) {
      int i = p_135409_;
      int j = p_135410_;
      if (p_135412_ instanceof net.minecraftforge.common.crafting.IShapedRecipe) {
         net.minecraftforge.common.crafting.IShapedRecipe shapedrecipe = (net.minecraftforge.common.crafting.IShapedRecipe)p_135412_;
         i = shapedrecipe.getRecipeWidth();
         j = shapedrecipe.getRecipeHeight();
      }

      int k1 = 0;

      for(int k = 0; k < p_135410_; ++k) {
         if (k1 == p_135411_) {
            ++k1;
         }

         boolean flag = (float)j < (float)p_135410_ / 2.0F;
         int l = Mth.floor((float)p_135410_ / 2.0F - (float)j / 2.0F);
         if (flag && l > k) {
            k1 += p_135409_;
            ++k;
         }

         for(int i1 = 0; i1 < p_135409_; ++i1) {
            if (!p_135413_.hasNext()) {
               return;
            }

            flag = (float)i < (float)p_135409_ / 2.0F;
            l = Mth.floor((float)p_135409_ / 2.0F - (float)i / 2.0F);
            int j1 = i;
            boolean flag1 = i1 < i;
            if (flag) {
               j1 = l + i;
               flag1 = l <= i1 && i1 < l + i;
            }

            if (flag1) {
               this.addItemToSlot(p_135413_, k1, p_135414_, k, i1);
            } else if (j1 == i1) {
               k1 += p_135409_ - i1;
               break;
            }

            ++k1;
         }
      }

   }

   void addItemToSlot(Iterator<T> p_135415_, int p_135416_, int p_135417_, int p_135418_, int p_135419_);
}
