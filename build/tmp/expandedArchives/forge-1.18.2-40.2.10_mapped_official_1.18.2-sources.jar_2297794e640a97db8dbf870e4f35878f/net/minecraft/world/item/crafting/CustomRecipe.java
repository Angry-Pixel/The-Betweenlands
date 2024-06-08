package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   private final ResourceLocation id;

   public CustomRecipe(ResourceLocation p_43833_) {
      this.id = p_43833_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }
}