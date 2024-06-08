package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SmokingRecipe extends AbstractCookingRecipe {
   public SmokingRecipe(ResourceLocation p_44469_, String p_44470_, Ingredient p_44471_, ItemStack p_44472_, float p_44473_, int p_44474_) {
      super(RecipeType.SMOKING, p_44469_, p_44470_, p_44471_, p_44472_, p_44473_, p_44474_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMOKER);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMOKING_RECIPE;
   }
}