package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftingScreen extends AbstractContainerScreen<CraftingMenu> implements RecipeUpdateListener {
   private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;

   public CraftingScreen(CraftingMenu p_98448_, Inventory p_98449_, Component p_98450_) {
      super(p_98448_, p_98449_, p_98450_);
   }

   protected void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_98484_) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         ((ImageButton)p_98484_).setPosition(this.leftPos + 5, this.height / 2 - 49);
      }));
      this.addWidget(this.recipeBookComponent);
      this.setInitialFocus(this.recipeBookComponent);
      this.titleLabelX = 29;
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void render(PoseStack p_98479_, int p_98480_, int p_98481_, float p_98482_) {
      this.renderBackground(p_98479_);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_98479_, p_98482_, p_98480_, p_98481_);
         this.recipeBookComponent.render(p_98479_, p_98480_, p_98481_, p_98482_);
      } else {
         this.recipeBookComponent.render(p_98479_, p_98480_, p_98481_, p_98482_);
         super.render(p_98479_, p_98480_, p_98481_, p_98482_);
         this.recipeBookComponent.renderGhostRecipe(p_98479_, this.leftPos, this.topPos, true, p_98482_);
      }

      this.renderTooltip(p_98479_, p_98480_, p_98481_);
      this.recipeBookComponent.renderTooltip(p_98479_, this.leftPos, this.topPos, p_98480_, p_98481_);
   }

   protected void renderBg(PoseStack p_98474_, float p_98475_, int p_98476_, int p_98477_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, CRAFTING_TABLE_LOCATION);
      int i = this.leftPos;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98474_, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected boolean isHovering(int p_98462_, int p_98463_, int p_98464_, int p_98465_, double p_98466_, double p_98467_) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(p_98462_, p_98463_, p_98464_, p_98465_, p_98466_, p_98467_);
   }

   public boolean mouseClicked(double p_98452_, double p_98453_, int p_98454_) {
      if (this.recipeBookComponent.mouseClicked(p_98452_, p_98453_, p_98454_)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(p_98452_, p_98453_, p_98454_);
      }
   }

   protected boolean hasClickedOutside(double p_98456_, double p_98457_, int p_98458_, int p_98459_, int p_98460_) {
      boolean flag = p_98456_ < (double)p_98458_ || p_98457_ < (double)p_98459_ || p_98456_ >= (double)(p_98458_ + this.imageWidth) || p_98457_ >= (double)(p_98459_ + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(p_98456_, p_98457_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_98460_) && flag;
   }

   protected void slotClicked(Slot p_98469_, int p_98470_, int p_98471_, ClickType p_98472_) {
      super.slotClicked(p_98469_, p_98470_, p_98471_, p_98472_);
      this.recipeBookComponent.slotClicked(p_98469_);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void removed() {
      this.recipeBookComponent.removed();
      super.removed();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}