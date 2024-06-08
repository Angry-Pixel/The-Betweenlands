package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBundleTooltip implements ClientTooltipComponent {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
   private static final int MARGIN_Y = 4;
   private static final int BORDER_WIDTH = 1;
   private static final int TEX_SIZE = 128;
   private static final int SLOT_SIZE_X = 18;
   private static final int SLOT_SIZE_Y = 20;
   private final NonNullList<ItemStack> items;
   private final int weight;

   public ClientBundleTooltip(BundleTooltip p_169873_) {
      this.items = p_169873_.getItems();
      this.weight = p_169873_.getWeight();
   }

   public int getHeight() {
      return this.gridSizeY() * 20 + 2 + 4;
   }

   public int getWidth(Font p_169901_) {
      return this.gridSizeX() * 18 + 2;
   }

   public void renderImage(Font p_194042_, int p_194043_, int p_194044_, PoseStack p_194045_, ItemRenderer p_194046_, int p_194047_) {
      int i = this.gridSizeX();
      int j = this.gridSizeY();
      boolean flag = this.weight >= 64;
      int k = 0;

      for(int l = 0; l < j; ++l) {
         for(int i1 = 0; i1 < i; ++i1) {
            int j1 = p_194043_ + i1 * 18 + 1;
            int k1 = p_194044_ + l * 20 + 1;
            this.renderSlot(j1, k1, k++, flag, p_194042_, p_194045_, p_194046_, p_194047_);
         }
      }

      this.drawBorder(p_194043_, p_194044_, i, j, p_194045_, p_194047_);
   }

   private void renderSlot(int p_194027_, int p_194028_, int p_194029_, boolean p_194030_, Font p_194031_, PoseStack p_194032_, ItemRenderer p_194033_, int p_194034_) {
      if (p_194029_ >= this.items.size()) {
         this.blit(p_194032_, p_194027_, p_194028_, p_194034_, p_194030_ ? ClientBundleTooltip.Texture.BLOCKED_SLOT : ClientBundleTooltip.Texture.SLOT);
      } else {
         ItemStack itemstack = this.items.get(p_194029_);
         this.blit(p_194032_, p_194027_, p_194028_, p_194034_, ClientBundleTooltip.Texture.SLOT);
         p_194033_.renderAndDecorateItem(itemstack, p_194027_ + 1, p_194028_ + 1, p_194029_);
         p_194033_.renderGuiItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
         if (p_194029_ == 0) {
            AbstractContainerScreen.renderSlotHighlight(p_194032_, p_194027_ + 1, p_194028_ + 1, p_194034_);
         }

      }
   }

   private void drawBorder(int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int p_194025_) {
      this.blit(p_194024_, p_194020_, p_194021_, p_194025_, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
      this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1, p_194021_, p_194025_, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);

      for(int i = 0; i < p_194022_; ++i) {
         this.blit(p_194024_, p_194020_ + 1 + i * 18, p_194021_, p_194025_, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(p_194024_, p_194020_ + 1 + i * 18, p_194021_ + p_194023_ * 20, p_194025_, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
      }

      for(int j = 0; j < p_194023_; ++j) {
         this.blit(p_194024_, p_194020_, p_194021_ + j * 20 + 1, p_194025_, ClientBundleTooltip.Texture.BORDER_VERTICAL);
         this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1, p_194021_ + j * 20 + 1, p_194025_, ClientBundleTooltip.Texture.BORDER_VERTICAL);
      }

      this.blit(p_194024_, p_194020_, p_194021_ + p_194023_ * 20, p_194025_, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
      this.blit(p_194024_, p_194020_ + p_194022_ * 18 + 1, p_194021_ + p_194023_ * 20, p_194025_, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
   }

   private void blit(PoseStack p_194036_, int p_194037_, int p_194038_, int p_194039_, ClientBundleTooltip.Texture p_194040_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
      GuiComponent.blit(p_194036_, p_194037_, p_194038_, p_194039_, (float)p_194040_.x, (float)p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
   }

   private int gridSizeX() {
      return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0D)));
   }

   private int gridSizeY() {
      return (int)Math.ceil(((double)this.items.size() + 1.0D) / (double)this.gridSizeX());
   }

   @OnlyIn(Dist.CLIENT)
   static enum Texture {
      SLOT(0, 0, 18, 20),
      BLOCKED_SLOT(0, 40, 18, 20),
      BORDER_VERTICAL(0, 18, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
         this.x = p_169928_;
         this.y = p_169929_;
         this.w = p_169930_;
         this.h = p_169931_;
      }
   }
}