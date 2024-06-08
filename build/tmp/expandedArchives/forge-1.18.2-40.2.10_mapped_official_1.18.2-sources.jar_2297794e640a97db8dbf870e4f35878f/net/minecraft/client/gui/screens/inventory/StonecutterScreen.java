package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StonecutterScreen extends AbstractContainerScreen<StonecutterMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int SCROLLER_FULL_HEIGHT = 54;
   private static final int RECIPES_X = 52;
   private static final int RECIPES_Y = 14;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private boolean displayRecipes;

   public StonecutterScreen(StonecutterMenu p_99310_, Inventory p_99311_, Component p_99312_) {
      super(p_99310_, p_99311_, p_99312_);
      p_99310_.registerUpdateListener(this::containerChanged);
      --this.titleLabelY;
   }

   public void render(PoseStack p_99337_, int p_99338_, int p_99339_, float p_99340_) {
      super.render(p_99337_, p_99338_, p_99339_, p_99340_);
      this.renderTooltip(p_99337_, p_99338_, p_99339_);
   }

   protected void renderBg(PoseStack p_99328_, float p_99329_, int p_99330_, int p_99331_) {
      this.renderBackground(p_99328_);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(p_99328_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      int k = (int)(41.0F * this.scrollOffs);
      this.blit(p_99328_, i + 119, j + 15 + k, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
      int l = this.leftPos + 52;
      int i1 = this.topPos + 14;
      int j1 = this.startIndex + 12;
      this.renderButtons(p_99328_, p_99330_, p_99331_, l, i1, j1);
      this.renderRecipes(l, i1, j1);
   }

   protected void renderTooltip(PoseStack p_99333_, int p_99334_, int p_99335_) {
      super.renderTooltip(p_99333_, p_99334_, p_99335_);
      if (this.displayRecipes) {
         int i = this.leftPos + 52;
         int j = this.topPos + 14;
         int k = this.startIndex + 12;
         List<StonecutterRecipe> list = this.menu.getRecipes();

         for(int l = this.startIndex; l < k && l < this.menu.getNumRecipes(); ++l) {
            int i1 = l - this.startIndex;
            int j1 = i + i1 % 4 * 16;
            int k1 = j + i1 / 4 * 18 + 2;
            if (p_99334_ >= j1 && p_99334_ < j1 + 16 && p_99335_ >= k1 && p_99335_ < k1 + 18) {
               this.renderTooltip(p_99333_, list.get(l).getResultItem(), p_99334_, p_99335_);
            }
         }
      }

   }

   private void renderButtons(PoseStack p_99342_, int p_99343_, int p_99344_, int p_99345_, int p_99346_, int p_99347_) {
      for(int i = this.startIndex; i < p_99347_ && i < this.menu.getNumRecipes(); ++i) {
         int j = i - this.startIndex;
         int k = p_99345_ + j % 4 * 16;
         int l = j / 4;
         int i1 = p_99346_ + l * 18 + 2;
         int j1 = this.imageHeight;
         if (i == this.menu.getSelectedRecipeIndex()) {
            j1 += 18;
         } else if (p_99343_ >= k && p_99344_ >= i1 && p_99343_ < k + 16 && p_99344_ < i1 + 18) {
            j1 += 36;
         }

         this.blit(p_99342_, k, i1 - 1, 0, j1, 16, 18);
      }

   }

   private void renderRecipes(int p_99349_, int p_99350_, int p_99351_) {
      List<StonecutterRecipe> list = this.menu.getRecipes();

      for(int i = this.startIndex; i < p_99351_ && i < this.menu.getNumRecipes(); ++i) {
         int j = i - this.startIndex;
         int k = p_99349_ + j % 4 * 16;
         int l = j / 4;
         int i1 = p_99350_ + l * 18 + 2;
         this.minecraft.getItemRenderer().renderAndDecorateItem(list.get(i).getResultItem(), k, i1);
      }

   }

   public boolean mouseClicked(double p_99318_, double p_99319_, int p_99320_) {
      this.scrolling = false;
      if (this.displayRecipes) {
         int i = this.leftPos + 52;
         int j = this.topPos + 14;
         int k = this.startIndex + 12;

         for(int l = this.startIndex; l < k; ++l) {
            int i1 = l - this.startIndex;
            double d0 = p_99318_ - (double)(i + i1 % 4 * 16);
            double d1 = p_99319_ - (double)(j + i1 / 4 * 18);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
               return true;
            }
         }

         i = this.leftPos + 119;
         j = this.topPos + 9;
         if (p_99318_ >= (double)i && p_99318_ < (double)(i + 12) && p_99319_ >= (double)j && p_99319_ < (double)(j + 54)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(p_99318_, p_99319_, p_99320_);
   }

   public boolean mouseDragged(double p_99322_, double p_99323_, int p_99324_, double p_99325_, double p_99326_) {
      if (this.scrolling && this.isScrollBarActive()) {
         int i = this.topPos + 14;
         int j = i + 54;
         this.scrollOffs = ((float)p_99323_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5D) * 4;
         return true;
      } else {
         return super.mouseDragged(p_99322_, p_99323_, p_99324_, p_99325_, p_99326_);
      }
   }

   public boolean mouseScrolled(double p_99314_, double p_99315_, double p_99316_) {
      if (this.isScrollBarActive()) {
         int i = this.getOffscreenRows();
         float f = (float)p_99316_ / (float)i;
         this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   private boolean isScrollBarActive() {
      return this.displayRecipes && this.menu.getNumRecipes() > 12;
   }

   protected int getOffscreenRows() {
      return (this.menu.getNumRecipes() + 4 - 1) / 4 - 3;
   }

   private void containerChanged() {
      this.displayRecipes = this.menu.hasInputItem();
      if (!this.displayRecipes) {
         this.scrollOffs = 0.0F;
         this.startIndex = 0;
      }

   }
}