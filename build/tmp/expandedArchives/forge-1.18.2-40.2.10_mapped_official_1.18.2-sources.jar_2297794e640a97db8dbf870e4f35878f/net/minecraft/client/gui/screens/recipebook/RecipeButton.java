package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   public static final int TICKS_TO_SWAP = 30;
   private static final Component MORE_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.moreRecipes");
   private RecipeBookMenu<?> menu;
   private RecipeBook book;
   private RecipeCollection collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeButton() {
      super(0, 0, 25, 25, TextComponent.EMPTY);
   }

   public void init(RecipeCollection p_100480_, RecipeBookPage p_100481_) {
      this.collection = p_100480_;
      this.menu = (RecipeBookMenu)p_100481_.getMinecraft().player.containerMenu;
      this.book = p_100481_.getRecipeBook();
      List<Recipe<?>> list = p_100480_.getRecipes(this.book.isFiltering(this.menu));

      for(Recipe<?> recipe : list) {
         if (this.book.willHighlight(recipe)) {
            p_100481_.recipesShown(list);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   public void setPosition(int p_100475_, int p_100476_) {
      this.x = p_100475_;
      this.y = p_100476_;
   }

   public void renderButton(PoseStack p_100484_, int p_100485_, int p_100486_, float p_100487_) {
      if (!Screen.hasControlDown()) {
         this.time += p_100487_;
      }

      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
      int i = 29;
      if (!this.collection.hasCraftable()) {
         i += 25;
      }

      int j = 206;
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         j += 25;
      }

      boolean flag = this.animationTime > 0.0F;
      PoseStack posestack = RenderSystem.getModelViewStack();
      if (flag) {
         float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
         posestack.pushPose();
         posestack.translate((double)(this.x + 8), (double)(this.y + 12), 0.0D);
         posestack.scale(f, f, 1.0F);
         posestack.translate((double)(-(this.x + 8)), (double)(-(this.y + 12)), 0.0D);
         RenderSystem.applyModelViewMatrix();
         this.animationTime -= p_100487_;
      }

      this.blit(p_100484_, this.x, this.y, i, j, this.width, this.height);
      List<Recipe<?>> list = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % list.size();
      ItemStack itemstack = list.get(this.currentIndex).getResultItem();
      int k = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         minecraft.getItemRenderer().renderAndDecorateItem(itemstack, this.x + k + 1, this.y + k + 1, 0, 10);
         --k;
      }

      minecraft.getItemRenderer().renderAndDecorateFakeItem(itemstack, this.x + k, this.y + k);
      if (flag) {
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
      }

   }

   private List<Recipe<?>> getOrderedRecipes() {
      List<Recipe<?>> list = this.collection.getDisplayRecipes(true);
      if (!this.book.isFiltering(this.menu)) {
         list.addAll(this.collection.getDisplayRecipes(false));
      }

      return list;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public Recipe<?> getRecipe() {
      List<Recipe<?>> list = this.getOrderedRecipes();
      return list.get(this.currentIndex);
   }

   public List<Component> getTooltipText(Screen p_100478_) {
      ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getResultItem();
      List<Component> list = Lists.newArrayList(p_100478_.getTooltipFromItem(itemstack));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         list.add(MORE_RECIPES_TOOLTIP);
      }

      return list;
   }

   public void updateNarration(NarrationElementOutput p_170060_) {
      ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getResultItem();
      p_170060_.add(NarratedElementType.TITLE, new TranslatableComponent("narration.recipe", itemstack.getHoverName()));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         p_170060_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.button.usage.hovered"), new TranslatableComponent("narration.recipe.usage.more"));
      } else {
         p_170060_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.button.usage.hovered"));
      }

   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int p_100473_) {
      return p_100473_ == 0 || p_100473_ == 1;
   }
}