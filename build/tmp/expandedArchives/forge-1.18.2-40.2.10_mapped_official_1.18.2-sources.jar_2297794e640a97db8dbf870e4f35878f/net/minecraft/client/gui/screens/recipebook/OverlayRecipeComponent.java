package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayRecipeComponent extends GuiComponent implements Widget, GuiEventListener {
   static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   private final List<OverlayRecipeComponent.OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   Minecraft minecraft;
   private RecipeCollection collection;
   @Nullable
   private Recipe<?> lastRecipeClicked;
   float time;
   boolean isFurnaceMenu;

   public void init(Minecraft p_100195_, RecipeCollection p_100196_, int p_100197_, int p_100198_, int p_100199_, int p_100200_, float p_100201_) {
      this.minecraft = p_100195_;
      this.collection = p_100196_;
      if (p_100195_.player.containerMenu instanceof AbstractFurnaceMenu) {
         this.isFurnaceMenu = true;
      }

      boolean flag = p_100195_.player.getRecipeBook().isFiltering((RecipeBookMenu)p_100195_.player.containerMenu);
      List<Recipe<?>> list = p_100196_.getDisplayRecipes(true);
      List<Recipe<?>> list1 = flag ? Collections.emptyList() : p_100196_.getDisplayRecipes(false);
      int i = list.size();
      int j = i + list1.size();
      int k = j <= 16 ? 4 : 5;
      int l = (int)Math.ceil((double)((float)j / (float)k));
      this.x = p_100197_;
      this.y = p_100198_;
      int i1 = 25;
      float f = (float)(this.x + Math.min(j, k) * 25);
      float f1 = (float)(p_100199_ + 50);
      if (f > f1) {
         this.x = (int)((float)this.x - p_100201_ * (float)((int)((f - f1) / p_100201_)));
      }

      float f2 = (float)(this.y + l * 25);
      float f3 = (float)(p_100200_ + 50);
      if (f2 > f3) {
         this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil((f2 - f3) / p_100201_));
      }

      float f4 = (float)this.y;
      float f5 = (float)(p_100200_ - 100);
      if (f4 < f5) {
         this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil((f4 - f5) / p_100201_));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int j1 = 0; j1 < j; ++j1) {
         boolean flag1 = j1 < i;
         Recipe<?> recipe = flag1 ? list.get(j1) : list1.get(j1 - i);
         int k1 = this.x + 4 + 25 * (j1 % k);
         int l1 = this.y + 5 + 25 * (j1 / k);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(k1, l1, recipe, flag1));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayRecipeButton(k1, l1, recipe, flag1));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean p_100224_) {
      return false;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   @Nullable
   public Recipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double p_100186_, double p_100187_, int p_100188_) {
      if (p_100188_ != 0) {
         return false;
      } else {
         for(OverlayRecipeComponent.OverlayRecipeButton overlayrecipecomponent$overlayrecipebutton : this.recipeButtons) {
            if (overlayrecipecomponent$overlayrecipebutton.mouseClicked(p_100186_, p_100187_, p_100188_)) {
               this.lastRecipeClicked = overlayrecipecomponent$overlayrecipebutton.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double p_100208_, double p_100209_) {
      return false;
   }

   public void render(PoseStack p_100190_, int p_100191_, int p_100192_, float p_100193_) {
      if (this.isVisible) {
         this.time += p_100193_;
         RenderSystem.enableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
         p_100190_.pushPose();
         p_100190_.translate(0.0D, 0.0D, 170.0D);
         int i = this.recipeButtons.size() <= 16 ? 4 : 5;
         int j = Math.min(this.recipeButtons.size(), i);
         int k = Mth.ceil((float)this.recipeButtons.size() / (float)i);
         int l = 24;
         int i1 = 4;
         int j1 = 82;
         int k1 = 208;
         this.nineInchSprite(p_100190_, j, k, 24, 4, 82, 208);
         RenderSystem.disableBlend();

         for(OverlayRecipeComponent.OverlayRecipeButton overlayrecipecomponent$overlayrecipebutton : this.recipeButtons) {
            overlayrecipecomponent$overlayrecipebutton.render(p_100190_, p_100191_, p_100192_, p_100193_);
         }

         p_100190_.popPose();
      }
   }

   private void nineInchSprite(PoseStack p_100214_, int p_100215_, int p_100216_, int p_100217_, int p_100218_, int p_100219_, int p_100220_) {
      this.blit(p_100214_, this.x, this.y, p_100219_, p_100220_, p_100218_, p_100218_);
      this.blit(p_100214_, this.x + p_100218_ * 2 + p_100215_ * p_100217_, this.y, p_100219_ + p_100217_ + p_100218_, p_100220_, p_100218_, p_100218_);
      this.blit(p_100214_, this.x, this.y + p_100218_ * 2 + p_100216_ * p_100217_, p_100219_, p_100220_ + p_100217_ + p_100218_, p_100218_, p_100218_);
      this.blit(p_100214_, this.x + p_100218_ * 2 + p_100215_ * p_100217_, this.y + p_100218_ * 2 + p_100216_ * p_100217_, p_100219_ + p_100217_ + p_100218_, p_100220_ + p_100217_ + p_100218_, p_100218_, p_100218_);

      for(int i = 0; i < p_100215_; ++i) {
         this.blit(p_100214_, this.x + p_100218_ + i * p_100217_, this.y, p_100219_ + p_100218_, p_100220_, p_100217_, p_100218_);
         this.blit(p_100214_, this.x + p_100218_ + (i + 1) * p_100217_, this.y, p_100219_ + p_100218_, p_100220_, p_100218_, p_100218_);

         for(int j = 0; j < p_100216_; ++j) {
            if (i == 0) {
               this.blit(p_100214_, this.x, this.y + p_100218_ + j * p_100217_, p_100219_, p_100220_ + p_100218_, p_100218_, p_100217_);
               this.blit(p_100214_, this.x, this.y + p_100218_ + (j + 1) * p_100217_, p_100219_, p_100220_ + p_100218_, p_100218_, p_100218_);
            }

            this.blit(p_100214_, this.x + p_100218_ + i * p_100217_, this.y + p_100218_ + j * p_100217_, p_100219_ + p_100218_, p_100220_ + p_100218_, p_100217_, p_100217_);
            this.blit(p_100214_, this.x + p_100218_ + (i + 1) * p_100217_, this.y + p_100218_ + j * p_100217_, p_100219_ + p_100218_, p_100220_ + p_100218_, p_100218_, p_100217_);
            this.blit(p_100214_, this.x + p_100218_ + i * p_100217_, this.y + p_100218_ + (j + 1) * p_100217_, p_100219_ + p_100218_, p_100220_ + p_100218_, p_100217_, p_100218_);
            this.blit(p_100214_, this.x + p_100218_ + (i + 1) * p_100217_ - 1, this.y + p_100218_ + (j + 1) * p_100217_ - 1, p_100219_ + p_100218_, p_100220_ + p_100218_, p_100218_ + 1, p_100218_ + 1);
            if (i == p_100215_ - 1) {
               this.blit(p_100214_, this.x + p_100218_ * 2 + p_100215_ * p_100217_, this.y + p_100218_ + j * p_100217_, p_100219_ + p_100217_ + p_100218_, p_100220_ + p_100218_, p_100218_, p_100217_);
               this.blit(p_100214_, this.x + p_100218_ * 2 + p_100215_ * p_100217_, this.y + p_100218_ + (j + 1) * p_100217_, p_100219_ + p_100217_ + p_100218_, p_100220_ + p_100218_, p_100218_, p_100218_);
            }
         }

         this.blit(p_100214_, this.x + p_100218_ + i * p_100217_, this.y + p_100218_ * 2 + p_100216_ * p_100217_, p_100219_ + p_100218_, p_100220_ + p_100217_ + p_100218_, p_100217_, p_100218_);
         this.blit(p_100214_, this.x + p_100218_ + (i + 1) * p_100217_, this.y + p_100218_ * 2 + p_100216_ * p_100217_, p_100219_ + p_100218_, p_100220_ + p_100217_ + p_100218_, p_100218_, p_100218_);
      }

   }

   public void setVisible(boolean p_100205_) {
      this.isVisible = p_100205_;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   @OnlyIn(Dist.CLIENT)
   class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
      final Recipe<?> recipe;
      private final boolean isCraftable;
      protected final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

      public OverlayRecipeButton(int p_100232_, int p_100233_, Recipe<?> p_100234_, boolean p_100235_) {
         super(p_100232_, p_100233_, 200, 20, TextComponent.EMPTY);
         this.width = 24;
         this.height = 24;
         this.recipe = p_100234_;
         this.isCraftable = p_100235_;
         this.calculateIngredientsPositions(p_100234_);
      }

      protected void calculateIngredientsPositions(Recipe<?> p_100236_) {
         this.placeRecipe(3, 3, -1, p_100236_, p_100236_.getIngredients().iterator(), 0);
      }

      public void updateNarration(NarrationElementOutput p_170040_) {
         this.defaultButtonNarrationText(p_170040_);
      }

      public void addItemToSlot(Iterator<Ingredient> p_100240_, int p_100241_, int p_100242_, int p_100243_, int p_100244_) {
         ItemStack[] aitemstack = p_100240_.next().getItems();
         if (aitemstack.length != 0) {
            this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + p_100244_ * 7, 3 + p_100243_ * 7, aitemstack));
         }

      }

      public void renderButton(PoseStack p_100246_, int p_100247_, int p_100248_, float p_100249_) {
         RenderSystem.setShaderTexture(0, OverlayRecipeComponent.RECIPE_BOOK_LOCATION);
         int i = 152;
         if (!this.isCraftable) {
            i += 26;
         }

         int j = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
         if (this.isHoveredOrFocused()) {
            j += 26;
         }

         this.blit(p_100246_, this.x, this.y, i, j, this.width, this.height);
         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         posestack.translate((double)(this.x + 2), (double)(this.y + 2), 125.0D);

         for(OverlayRecipeComponent.OverlayRecipeButton.Pos overlayrecipecomponent$overlayrecipebutton$pos : this.ingredientPos) {
            posestack.pushPose();
            posestack.translate((double)overlayrecipecomponent$overlayrecipebutton$pos.x, (double)overlayrecipecomponent$overlayrecipebutton$pos.y, 0.0D);
            posestack.scale(0.375F, 0.375F, 1.0F);
            posestack.translate(-8.0D, -8.0D, 0.0D);
            RenderSystem.applyModelViewMatrix();
            OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem(overlayrecipecomponent$overlayrecipebutton$pos.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % overlayrecipecomponent$overlayrecipebutton$pos.ingredients.length], 0, 0);
            posestack.popPose();
         }

         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
      }

      @OnlyIn(Dist.CLIENT)
      protected class Pos {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Pos(int p_100256_, int p_100257_, ItemStack[] p_100258_) {
            this.x = p_100256_;
            this.y = p_100257_;
            this.ingredients = p_100258_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      public OverlaySmeltingRecipeButton(int p_100262_, int p_100263_, Recipe<?> p_100264_, boolean p_100265_) {
         super(p_100262_, p_100263_, p_100264_, p_100265_);
      }

      protected void calculateIngredientsPositions(Recipe<?> p_100267_) {
         ItemStack[] aitemstack = p_100267_.getIngredients().get(0).getItems();
         this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(10, 10, aitemstack));
      }
   }
}