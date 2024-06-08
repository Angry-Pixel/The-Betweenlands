package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private float xMouse;
   private float yMouse;
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean recipeBookComponentInitialized;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(Player p_98839_) {
      super(p_98839_.inventoryMenu, p_98839_.getInventory(), new TranslatableComponent("container.crafting"));
      this.passEvents = true;
      this.titleLabelX = 97;
   }

   public void containerTick() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
      } else {
         this.recipeBookComponent.tick();
      }
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
         this.recipeBookComponentInitialized = true;
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_98880_) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ((ImageButton)p_98880_).setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
         this.addWidget(this.recipeBookComponent);
         this.setInitialFocus(this.recipeBookComponent);
      }
   }

   protected void renderLabels(PoseStack p_98889_, int p_98890_, int p_98891_) {
      this.font.draw(p_98889_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
   }

   public void render(PoseStack p_98875_, int p_98876_, int p_98877_, float p_98878_) {
      this.renderBackground(p_98875_);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(p_98875_, p_98878_, p_98876_, p_98877_);
         this.recipeBookComponent.render(p_98875_, p_98876_, p_98877_, p_98878_);
      } else {
         this.recipeBookComponent.render(p_98875_, p_98876_, p_98877_, p_98878_);
         super.render(p_98875_, p_98876_, p_98877_, p_98878_);
         this.recipeBookComponent.renderGhostRecipe(p_98875_, this.leftPos, this.topPos, false, p_98878_);
      }

      this.renderTooltip(p_98875_, p_98876_, p_98877_);
      this.recipeBookComponent.renderTooltip(p_98875_, this.leftPos, this.topPos, p_98876_, p_98877_);
      this.xMouse = (float)p_98876_;
      this.yMouse = (float)p_98877_;
   }

   protected void renderBg(PoseStack p_98870_, float p_98871_, int p_98872_, int p_98873_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_98870_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      renderEntityInInventory(i + 51, j + 75, 30, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);
   }

   public static void renderEntityInInventory(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
      float f = (float)Math.atan((double)(p_98854_ / 40.0F));
      float f1 = (float)Math.atan((double)(p_98855_ / 40.0F));
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate((double)p_98851_, (double)p_98852_, 1050.0D);
      posestack.scale(1.0F, 1.0F, -1.0F);
      RenderSystem.applyModelViewMatrix();
      PoseStack posestack1 = new PoseStack();
      posestack1.translate(0.0D, 0.0D, 1000.0D);
      posestack1.scale((float)p_98853_, (float)p_98853_, (float)p_98853_);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
      quaternion.mul(quaternion1);
      posestack1.mulPose(quaternion);
      float f2 = p_98856_.yBodyRot;
      float f3 = p_98856_.getYRot();
      float f4 = p_98856_.getXRot();
      float f5 = p_98856_.yHeadRotO;
      float f6 = p_98856_.yHeadRot;
      p_98856_.yBodyRot = 180.0F + f * 20.0F;
      p_98856_.setYRot(180.0F + f * 40.0F);
      p_98856_.setXRot(-f1 * 20.0F);
      p_98856_.yHeadRot = p_98856_.getYRot();
      p_98856_.yHeadRotO = p_98856_.getYRot();
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderdispatcher.overrideCameraOrientation(quaternion1);
      entityrenderdispatcher.setRenderShadow(false);
      MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> {
         entityrenderdispatcher.render(p_98856_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
      });
      multibuffersource$buffersource.endBatch();
      entityrenderdispatcher.setRenderShadow(true);
      p_98856_.yBodyRot = f2;
      p_98856_.setYRot(f3);
      p_98856_.setXRot(f4);
      p_98856_.yHeadRotO = f5;
      p_98856_.yHeadRot = f6;
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   protected boolean isHovering(int p_98858_, int p_98859_, int p_98860_, int p_98861_, double p_98862_, double p_98863_) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(p_98858_, p_98859_, p_98860_, p_98861_, p_98862_, p_98863_);
   }

   public boolean mouseClicked(double p_98841_, double p_98842_, int p_98843_) {
      if (this.recipeBookComponent.mouseClicked(p_98841_, p_98842_, p_98843_)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? false : super.mouseClicked(p_98841_, p_98842_, p_98843_);
      }
   }

   public boolean mouseReleased(double p_98893_, double p_98894_, int p_98895_) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(p_98893_, p_98894_, p_98895_);
      }
   }

   protected boolean hasClickedOutside(double p_98845_, double p_98846_, int p_98847_, int p_98848_, int p_98849_) {
      boolean flag = p_98845_ < (double)p_98847_ || p_98846_ < (double)p_98848_ || p_98845_ >= (double)(p_98847_ + this.imageWidth) || p_98846_ >= (double)(p_98848_ + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(p_98845_, p_98846_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_98849_) && flag;
   }

   protected void slotClicked(Slot p_98865_, int p_98866_, int p_98867_, ClickType p_98868_) {
      super.slotClicked(p_98865_, p_98866_, p_98867_, p_98868_);
      this.recipeBookComponent.slotClicked(p_98865_);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void removed() {
      if (this.recipeBookComponentInitialized) {
         this.recipeBookComponent.removed();
      }

      super.removed();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}