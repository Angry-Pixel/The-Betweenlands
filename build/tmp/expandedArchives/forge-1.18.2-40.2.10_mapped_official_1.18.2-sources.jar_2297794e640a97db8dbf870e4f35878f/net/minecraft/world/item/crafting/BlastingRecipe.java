package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(ResourceLocation p_43793_, String p_43794_, Ingredient p_43795_, ItemStack p_43796_, float p_43797_, int p_43798_) {
      super(RecipeType.BLASTING, p_43793_, p_43794_, p_43795_, p_43796_, p_43797_, p_43798_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.BLAST_FURNACE);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BLASTING_RECIPE;
   }
}