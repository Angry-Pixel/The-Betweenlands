package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeCollection {
   private final List<Recipe<?>> recipes;
   private final boolean singleResultItem;
   private final Set<Recipe<?>> craftable = Sets.newHashSet();
   private final Set<Recipe<?>> fitsDimensions = Sets.newHashSet();
   private final Set<Recipe<?>> known = Sets.newHashSet();

   public RecipeCollection(List<Recipe<?>> p_100497_) {
      this.recipes = ImmutableList.copyOf(p_100497_);
      if (p_100497_.size() <= 1) {
         this.singleResultItem = true;
      } else {
         this.singleResultItem = allRecipesHaveSameResult(p_100497_);
      }

   }

   private static boolean allRecipesHaveSameResult(List<Recipe<?>> p_100509_) {
      int i = p_100509_.size();
      ItemStack itemstack = p_100509_.get(0).getResultItem();

      for(int j = 1; j < i; ++j) {
         ItemStack itemstack1 = p_100509_.get(j).getResultItem();
         if (!ItemStack.isSame(itemstack, itemstack1) || !ItemStack.tagMatches(itemstack, itemstack1)) {
            return false;
         }
      }

      return true;
   }

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook p_100500_) {
      for(Recipe<?> recipe : this.recipes) {
         if (p_100500_.contains(recipe)) {
            this.known.add(recipe);
         }
      }

   }

   public void canCraft(StackedContents p_100502_, int p_100503_, int p_100504_, RecipeBook p_100505_) {
      for(Recipe<?> recipe : this.recipes) {
         boolean flag = recipe.canCraftInDimensions(p_100503_, p_100504_) && p_100505_.contains(recipe);
         if (flag) {
            this.fitsDimensions.add(recipe);
         } else {
            this.fitsDimensions.remove(recipe);
         }

         if (flag && p_100502_.canCraft(recipe, (IntList)null)) {
            this.craftable.add(recipe);
         } else {
            this.craftable.remove(recipe);
         }
      }

   }

   public boolean isCraftable(Recipe<?> p_100507_) {
      return this.craftable.contains(p_100507_);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List<Recipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<Recipe<?>> getRecipes(boolean p_100511_) {
      List<Recipe<?>> list = Lists.newArrayList();
      Set<Recipe<?>> set = p_100511_ ? this.craftable : this.fitsDimensions;

      for(Recipe<?> recipe : this.recipes) {
         if (set.contains(recipe)) {
            list.add(recipe);
         }
      }

      return list;
   }

   public List<Recipe<?>> getDisplayRecipes(boolean p_100514_) {
      List<Recipe<?>> list = Lists.newArrayList();

      for(Recipe<?> recipe : this.recipes) {
         if (this.fitsDimensions.contains(recipe) && this.craftable.contains(recipe) == p_100514_) {
            list.add(recipe);
         }
      }

      return list;
   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}