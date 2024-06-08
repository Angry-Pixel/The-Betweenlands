package net.minecraft.client.gui.screens.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
   @Nullable
   private Ingredient fuels;

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void slotClicked(@Nullable Slot p_100120_) {
      super.slotClicked(p_100120_);
      if (p_100120_ != null && p_100120_.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
      }

   }

   public void setupGhostRecipe(Recipe<?> p_100122_, List<Slot> p_100123_) {
      ItemStack itemstack = p_100122_.getResultItem();
      this.ghostRecipe.setRecipe(p_100122_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_100123_.get(2)).x, (p_100123_.get(2)).y);
      NonNullList<Ingredient> nonnulllist = p_100122_.getIngredients();
      Slot slot = p_100123_.get(1);
      if (slot.getItem().isEmpty()) {
         if (this.fuels == null) {
            this.fuels = Ingredient.of(this.getFuelItems().stream().map(ItemStack::new));
         }

         this.ghostRecipe.addIngredient(this.fuels, slot.x, slot.y);
      }

      Iterator<Ingredient> iterator = nonnulllist.iterator();

      for(int i = 0; i < 2; ++i) {
         if (!iterator.hasNext()) {
            return;
         }

         Ingredient ingredient = iterator.next();
         if (!ingredient.isEmpty()) {
            Slot slot1 = p_100123_.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot1.x, slot1.y);
         }
      }

   }

   protected abstract Set<Item> getFuelItems();
}