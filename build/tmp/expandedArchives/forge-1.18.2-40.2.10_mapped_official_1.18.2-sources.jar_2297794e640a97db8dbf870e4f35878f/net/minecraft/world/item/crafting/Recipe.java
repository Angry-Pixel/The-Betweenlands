package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface Recipe<C extends Container> {
   boolean matches(C p_44002_, Level p_44003_);

   ItemStack assemble(C p_44001_);

   boolean canCraftInDimensions(int p_43999_, int p_44000_);

   ItemStack getResultItem();

   default NonNullList<ItemStack> getRemainingItems(C p_44004_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_44004_.getContainerSize(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack item = p_44004_.getItem(i);
         if (item.hasContainerItem()) {
            nonnulllist.set(i, item.getContainerItem());
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isSpecial() {
      return false;
   }

   default String getGroup() {
      return "";
   }

   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   ResourceLocation getId();

   RecipeSerializer<?> getSerializer();

   RecipeType<?> getType();

   default boolean isIncomplete() {
      NonNullList<Ingredient> nonnulllist = this.getIngredients();
      return nonnulllist.isEmpty() || nonnulllist.stream().anyMatch((p_151268_) -> {
         return net.minecraftforge.common.ForgeHooks.hasNoElements(p_151268_);
      });
   }
}
