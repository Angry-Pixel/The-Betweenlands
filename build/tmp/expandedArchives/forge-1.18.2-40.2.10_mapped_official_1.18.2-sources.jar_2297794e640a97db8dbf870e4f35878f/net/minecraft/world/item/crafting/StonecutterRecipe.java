package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterRecipe extends SingleItemRecipe {
   public StonecutterRecipe(ResourceLocation p_44478_, String p_44479_, Ingredient p_44480_, ItemStack p_44481_) {
      super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTER, p_44478_, p_44479_, p_44480_, p_44481_);
   }

   public boolean matches(Container p_44483_, Level p_44484_) {
      return this.ingredient.test(p_44483_.getItem(0));
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.STONECUTTER);
   }
}