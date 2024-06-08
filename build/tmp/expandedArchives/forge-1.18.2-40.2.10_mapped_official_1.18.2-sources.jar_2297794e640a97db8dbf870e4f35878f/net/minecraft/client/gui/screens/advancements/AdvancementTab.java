package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementTab extends GuiComponent {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final Component title;
   private final AdvancementWidget root;
   private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = Integer.MAX_VALUE;
   private int minY = Integer.MAX_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private int maxY = Integer.MIN_VALUE;
   private float fade;
   private boolean centered;
   private int page;

   public AdvancementTab(Minecraft p_97145_, AdvancementsScreen p_97146_, AdvancementTabType p_97147_, int p_97148_, Advancement p_97149_, DisplayInfo p_97150_) {
      this.minecraft = p_97145_;
      this.screen = p_97146_;
      this.type = p_97147_;
      this.index = p_97148_;
      this.advancement = p_97149_;
      this.display = p_97150_;
      this.icon = p_97150_.getIcon();
      this.title = p_97150_.getTitle();
      this.root = new AdvancementWidget(this, p_97145_, p_97149_, p_97150_);
      this.addWidget(this.root, p_97149_);
   }

   public AdvancementTab(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, int page, Advancement adv, DisplayInfo info) {
      this(mc, screen, type, index, adv, info);
      this.page = page;
   }

   public int getPage() {
      return page;
   }

   public AdvancementTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }

   public Component getTitle() {
      return this.title;
   }

   public DisplayInfo getDisplay() {
      return this.display;
   }

   public void drawTab(PoseStack p_97166_, int p_97167_, int p_97168_, boolean p_97169_) {
      this.type.draw(p_97166_, this, p_97167_, p_97168_, p_97169_, this.index);
   }

   public void drawIcon(int p_97160_, int p_97161_, ItemRenderer p_97162_) {
      this.type.drawIcon(p_97160_, p_97161_, this.index, p_97162_, this.icon);
   }

   public void drawContents(PoseStack p_97164_) {
      if (!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      p_97164_.pushPose();
      p_97164_.translate(0.0D, 0.0D, 950.0D);
      RenderSystem.enableDepthTest();
      RenderSystem.colorMask(false, false, false, false);
      fill(p_97164_, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      p_97164_.translate(0.0D, 0.0D, -950.0D);
      RenderSystem.depthFunc(518);
      fill(p_97164_, 234, 113, 0, 0, -16777216);
      RenderSystem.depthFunc(515);
      ResourceLocation resourcelocation = this.display.getBackground();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      if (resourcelocation != null) {
         RenderSystem.setShaderTexture(0, resourcelocation);
      } else {
         RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
      }

      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;

      for(int i1 = -1; i1 <= 15; ++i1) {
         for(int j1 = -1; j1 <= 8; ++j1) {
            blit(p_97164_, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.drawConnectivity(p_97164_, i, j, true);
      this.root.drawConnectivity(p_97164_, i, j, false);
      this.root.draw(p_97164_, i, j);
      RenderSystem.depthFunc(518);
      p_97164_.translate(0.0D, 0.0D, -950.0D);
      RenderSystem.colorMask(false, false, false, false);
      fill(p_97164_, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthFunc(515);
      p_97164_.popPose();
   }

   public void drawTooltips(PoseStack p_97184_, int p_97185_, int p_97186_, int p_97187_, int p_97188_) {
      p_97184_.pushPose();
      p_97184_.translate(0.0D, 0.0D, -200.0D);
      fill(p_97184_, 0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean flag = false;
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      if (p_97185_ > 0 && p_97185_ < 234 && p_97186_ > 0 && p_97186_ < 113) {
         for(AdvancementWidget advancementwidget : this.widgets.values()) {
            if (advancementwidget.isMouseOver(i, j, p_97185_, p_97186_)) {
               flag = true;
               advancementwidget.drawHover(p_97184_, i, j, this.fade, p_97187_, p_97188_);
               break;
            }
         }
      }

      p_97184_.popPose();
      if (flag) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean isMouseOver(int p_97155_, int p_97156_, double p_97157_, double p_97158_) {
      return this.type.isMouseOver(p_97155_, p_97156_, this.index, p_97157_, p_97158_);
   }

   @Nullable
   public static AdvancementTab create(Minecraft p_97171_, AdvancementsScreen p_97172_, int p_97173_, Advancement p_97174_) {
      if (p_97174_.getDisplay() == null) {
         return null;
      } else {
         for(AdvancementTabType advancementtabtype : AdvancementTabType.values()) {
            if ((p_97173_ % AdvancementTabType.MAX_TABS) < advancementtabtype.getMax()) {
               return new AdvancementTab(p_97171_, p_97172_, advancementtabtype, p_97173_ % AdvancementTabType.MAX_TABS, p_97173_ / AdvancementTabType.MAX_TABS, p_97174_, p_97174_.getDisplay());
            }

            p_97173_ -= advancementtabtype.getMax();
         }

         return null;
      }
   }

   public void scroll(double p_97152_, double p_97153_) {
      if (this.maxX - this.minX > 234) {
         this.scrollX = Mth.clamp(this.scrollX + p_97152_, (double)(-(this.maxX - 234)), 0.0D);
      }

      if (this.maxY - this.minY > 113) {
         this.scrollY = Mth.clamp(this.scrollY + p_97153_, (double)(-(this.maxY - 113)), 0.0D);
      }

   }

   public void addAdvancement(Advancement p_97179_) {
      if (p_97179_.getDisplay() != null) {
         AdvancementWidget advancementwidget = new AdvancementWidget(this, this.minecraft, p_97179_, p_97179_.getDisplay());
         this.addWidget(advancementwidget, p_97179_);
      }
   }

   private void addWidget(AdvancementWidget p_97176_, Advancement p_97177_) {
      this.widgets.put(p_97177_, p_97176_);
      int i = p_97176_.getX();
      int j = i + 28;
      int k = p_97176_.getY();
      int l = k + 27;
      this.minX = Math.min(this.minX, i);
      this.maxX = Math.max(this.maxX, j);
      this.minY = Math.min(this.minY, k);
      this.maxY = Math.max(this.maxY, l);

      for(AdvancementWidget advancementwidget : this.widgets.values()) {
         advancementwidget.attachToParent();
      }

   }

   @Nullable
   public AdvancementWidget getWidget(Advancement p_97181_) {
      return this.widgets.get(p_97181_);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}
