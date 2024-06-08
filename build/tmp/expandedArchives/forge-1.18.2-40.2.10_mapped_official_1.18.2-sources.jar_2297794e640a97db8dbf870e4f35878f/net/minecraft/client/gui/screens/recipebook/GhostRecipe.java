package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostRecipe {
   @Nullable
   private Recipe<?> recipe;
   private final List<GhostRecipe.GhostIngredient> ingredients = Lists.newArrayList();
   float time;

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient p_100144_, int p_100145_, int p_100146_) {
      this.ingredients.add(new GhostRecipe.GhostIngredient(p_100144_, p_100145_, p_100146_));
   }

   public GhostRecipe.GhostIngredient get(int p_100142_) {
      return this.ingredients.get(p_100142_);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public Recipe<?> getRecipe() {
      return this.recipe;
   }

   public void setRecipe(Recipe<?> p_100148_) {
      this.recipe = p_100148_;
   }

   public void render(PoseStack p_100150_, Minecraft p_100151_, int p_100152_, int p_100153_, boolean p_100154_, float p_100155_) {
      if (!Screen.hasControlDown()) {
         this.time += p_100155_;
      }

      for(int i = 0; i < this.ingredients.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ingredients.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_100152_;
         int k = ghostrecipe$ghostingredient.getY() + p_100153_;
         if (i == 0 && p_100154_) {
            GuiComponent.fill(p_100150_, j - 4, k - 4, j + 20, k + 20, 822018048);
         } else {
            GuiComponent.fill(p_100150_, j, k, j + 16, k + 16, 822018048);
         }

         ItemStack itemstack = ghostrecipe$ghostingredient.getItem();
         ItemRenderer itemrenderer = p_100151_.getItemRenderer();
         itemrenderer.renderAndDecorateFakeItem(itemstack, j, k);
         RenderSystem.depthFunc(516);
         GuiComponent.fill(p_100150_, j, k, j + 16, k + 16, 822083583);
         RenderSystem.depthFunc(515);
         if (i == 0) {
            itemrenderer.renderGuiItemDecorations(p_100151_.font, itemstack, j, k);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public class GhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;

      public GhostIngredient(Ingredient p_100166_, int p_100167_, int p_100168_) {
         this.ingredient = p_100166_;
         this.x = p_100167_;
         this.y = p_100168_;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] aitemstack = this.ingredient.getItems();
         return aitemstack.length == 0 ? ItemStack.EMPTY : aitemstack[Mth.floor(GhostRecipe.this.time / 30.0F) % aitemstack.length];
      }
   }
}