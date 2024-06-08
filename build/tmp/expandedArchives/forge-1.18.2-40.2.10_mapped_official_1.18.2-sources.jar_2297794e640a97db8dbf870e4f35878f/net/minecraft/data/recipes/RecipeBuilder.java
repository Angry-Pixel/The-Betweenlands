package net.minecraft.data.recipes;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
   RecipeBuilder unlockedBy(String p_176496_, CriterionTriggerInstance p_176497_);

   RecipeBuilder group(@Nullable String p_176495_);

   Item getResult();

   void save(Consumer<FinishedRecipe> p_176503_, ResourceLocation p_176504_);

   default void save(Consumer<FinishedRecipe> p_176499_) {
      this.save(p_176499_, getDefaultRecipeId(this.getResult()));
   }

   default void save(Consumer<FinishedRecipe> p_176501_, String p_176502_) {
      ResourceLocation resourcelocation = getDefaultRecipeId(this.getResult());
      ResourceLocation resourcelocation1 = new ResourceLocation(p_176502_);
      if (resourcelocation1.equals(resourcelocation)) {
         throw new IllegalStateException("Recipe " + p_176502_ + " should remove its 'save' argument as it is equal to default one");
      } else {
         this.save(p_176501_, resourcelocation1);
      }
   }

   static ResourceLocation getDefaultRecipeId(ItemLike p_176494_) {
      return Registry.ITEM.getKey(p_176494_.asItem());
   }
}