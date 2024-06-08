package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorGui extends GuiComponent implements SpectatorMenuListener {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
   private static final long FADE_OUT_DELAY = 5000L;
   private static final long FADE_OUT_TIME = 2000L;
   private final Minecraft minecraft;
   private long lastSelectionTime;
   @Nullable
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft p_94767_) {
      this.minecraft = p_94767_;
   }

   public void onHotbarSelected(int p_94772_) {
      this.lastSelectionTime = Util.getMillis();
      if (this.menu != null) {
         this.menu.selectSlot(p_94772_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long i = this.lastSelectionTime - Util.getMillis() + 5000L;
      return Mth.clamp((float)i / 2000.0F, 0.0F, 1.0F);
   }

   public void renderHotbar(PoseStack p_193838_) {
      if (this.menu != null) {
         float f = this.getHotbarAlpha();
         if (f <= 0.0F) {
            this.menu.exit();
         } else {
            int i = this.minecraft.getWindow().getGuiScaledWidth() / 2;
            int j = this.getBlitOffset();
            this.setBlitOffset(-90);
            int k = Mth.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0F * f);
            SpectatorPage spectatorpage = this.menu.getCurrentPage();
            this.renderPage(p_193838_, f, i, k, spectatorpage);
            this.setBlitOffset(j);
         }
      }
   }

   protected void renderPage(PoseStack p_94779_, float p_94780_, int p_94781_, int p_94782_, SpectatorPage p_94783_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, p_94780_);
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      this.blit(p_94779_, p_94781_ - 91, p_94782_, 0, 0, 182, 22);
      if (p_94783_.getSelectedSlot() >= 0) {
         this.blit(p_94779_, p_94781_ - 91 - 1 + p_94783_.getSelectedSlot() * 20, p_94782_ - 1, 0, 22, 24, 22);
      }

      for(int i = 0; i < 9; ++i) {
         this.renderSlot(p_94779_, i, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + i * 20 + 2, (float)(p_94782_ + 3), p_94780_, p_94783_.getItem(i));
      }

      RenderSystem.disableBlend();
   }

   private void renderSlot(PoseStack p_94785_, int p_94786_, int p_94787_, float p_94788_, float p_94789_, SpectatorMenuItem p_94790_) {
      RenderSystem.setShaderTexture(0, SPECTATOR_LOCATION);
      if (p_94790_ != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(p_94789_ * 255.0F);
         p_94785_.pushPose();
         p_94785_.translate((double)p_94787_, (double)p_94788_, 0.0D);
         float f = p_94790_.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.setShaderColor(f, f, f, p_94789_);
         p_94790_.renderIcon(p_94785_, f, i);
         p_94785_.popPose();
         if (i > 3 && p_94790_.isEnabled()) {
            Component component = this.minecraft.options.keyHotbarSlots[p_94786_].getTranslatedKeyMessage();
            this.minecraft.font.drawShadow(p_94785_, component, (float)(p_94787_ + 19 - 2 - this.minecraft.font.width(component)), p_94788_ + 6.0F + 3.0F, 16777215 + (i << 24));
         }
      }

   }

   public void renderTooltip(PoseStack p_94774_) {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         SpectatorMenuItem spectatormenuitem = this.menu.getSelectedItem();
         Component component = spectatormenuitem == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : spectatormenuitem.getName();
         if (component != null) {
            int j = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width(component)) / 2;
            int k = this.minecraft.getWindow().getGuiScaledHeight() - 35;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.minecraft.font.drawShadow(p_94774_, component, (float)j, (float)k, 16777215 + (i << 24));
            RenderSystem.disableBlend();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu p_94792_) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScrolled(int p_205381_) {
      int i;
      for(i = this.menu.getSelectedSlot() + p_205381_; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i += p_205381_) {
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Util.getMillis();
      }

   }

   public void onMouseMiddleClick() {
      this.lastSelectionTime = Util.getMillis();
      if (this.isMenuActive()) {
         int i = this.menu.getSelectedSlot();
         if (i != -1) {
            this.menu.selectSlot(i);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}