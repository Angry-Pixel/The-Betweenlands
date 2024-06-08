package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   public final AbstractFurnaceRecipeBookComponent recipeBookComponent;
   private boolean widthTooNarrow;
   private final ResourceLocation texture;

   public AbstractFurnaceScreen(T p_97825_, AbstractFurnaceRecipeBookComponent p_97826_, Inventory p_97827_, Component p_97828_, ResourceLocation p_97829_) {
      super(p_97825_, p_97827_, p_97828_);
      this.recipeBookComponent = p_97826_;
      this.texture = p_97829_;
   }

   public void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.addRenderableWidget(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_97863_) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         ((ImageButton)p_97863_).setPosition(this.leftPos + 20, this.height / 2 - 49);
      }));
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void render(PoseStack p_97858_, int p_97859_, int p_97860_, float p_97861_) {
      this.renderBackground(p_97858_);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_97858_, p_97861_, p_97859_, p_97860_);
         this.recipeBookComponent.render(p_97858_, p_97859_, p_97860_, p_97861_);
      } else {
         this.recipeBookComponent.render(p_97858_, p_97859_, p_97860_, p_97861_);
         super.render(p_97858_, p_97859_, p_97860_, p_97861_);
         this.recipeBookComponent.renderGhostRecipe(p_97858_, this.leftPos, this.topPos, true, p_97861_);
      }

      this.renderTooltip(p_97858_, p_97859_, p_97860_);
      this.recipeBookComponent.renderTooltip(p_97858_, this.leftPos, this.topPos, p_97859_, p_97860_);
   }

   protected void renderBg(PoseStack p_97853_, float p_97854_, int p_97855_, int p_97856_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.texture);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_97853_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.menu.isLit()) {
         int k = this.menu.getLitProgress();
         this.blit(p_97853_, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = this.menu.getBurnProgress();
      this.blit(p_97853_, i + 79, j + 34, 176, 14, l + 1, 16);
   }

   public boolean mouseClicked(double p_97834_, double p_97835_, int p_97836_) {
      if (this.recipeBookComponent.mouseClicked(p_97834_, p_97835_, p_97836_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(p_97834_, p_97835_, p_97836_);
      }
   }

   protected void slotClicked(Slot p_97848_, int p_97849_, int p_97850_, ClickType p_97851_) {
      super.slotClicked(p_97848_, p_97849_, p_97850_, p_97851_);
      this.recipeBookComponent.slotClicked(p_97848_);
   }

   public boolean keyPressed(int p_97844_, int p_97845_, int p_97846_) {
      return this.recipeBookComponent.keyPressed(p_97844_, p_97845_, p_97846_) ? false : super.keyPressed(p_97844_, p_97845_, p_97846_);
   }

   protected boolean hasClickedOutside(double p_97838_, double p_97839_, int p_97840_, int p_97841_, int p_97842_) {
      boolean flag = p_97838_ < (double)p_97840_ || p_97839_ < (double)p_97841_ || p_97838_ >= (double)(p_97840_ + this.imageWidth) || p_97839_ >= (double)(p_97841_ + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(p_97838_, p_97839_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_97842_) && flag;
   }

   public boolean charTyped(char p_97831_, int p_97832_) {
      return this.recipeBookComponent.charTyped(p_97831_, p_97832_) ? true : super.charTyped(p_97831_, p_97832_);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }

   public void removed() {
      this.recipeBookComponent.removed();
      super.removed();
   }
}