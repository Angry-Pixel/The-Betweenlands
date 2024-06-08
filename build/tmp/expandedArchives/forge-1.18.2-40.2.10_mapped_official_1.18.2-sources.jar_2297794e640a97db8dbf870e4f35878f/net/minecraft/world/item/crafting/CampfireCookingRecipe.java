package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(ResourceLocation p_43822_, String p_43823_, Ingredient p_43824_, ItemStack p_43825_, float p_43826_, int p_43827_) {
      super(RecipeType.CAMPFIRE_COOKING, p_43822_, p_43823_, p_43824_, p_43825_, p_43826_, p_43827_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CAMPFIRE);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
   }
}