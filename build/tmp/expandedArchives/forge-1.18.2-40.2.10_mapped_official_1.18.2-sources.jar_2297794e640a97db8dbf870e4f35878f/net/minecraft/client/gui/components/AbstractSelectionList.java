package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler implements Widget, NarratableEntry {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft p_93404_, int p_93405_, int p_93406_, int p_93407_, int p_93408_, int p_93409_) {
      this.minecraft = p_93404_;
      this.width = p_93405_;
      this.height = p_93406_;
      this.y0 = p_93407_;
      this.y1 = p_93408_;
      this.itemHeight = p_93409_;
      this.x0 = 0;
      this.x1 = p_93405_;
   }

   public void setRenderSelection(boolean p_93472_) {
      this.renderSelection = p_93472_;
   }

   protected void setRenderHeader(boolean p_93474_, int p_93475_) {
      this.renderHeader = p_93474_;
      this.headerHeight = p_93475_;
      if (!p_93474_) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E p_93462_) {
      this.selected = p_93462_;
   }

   public void setRenderBackground(boolean p_93489_) {
      this.renderBackground = p_93489_;
   }

   public void setRenderTopAndBottom(boolean p_93497_) {
      this.renderTopAndBottom = p_93497_;
   }

   @Nullable
   public E getFocused() {
      return (E)(super.getFocused());
   }

   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<E> p_93470_) {
      this.children.clear();
      this.children.addAll(p_93470_);
   }

   protected E getEntry(int p_93501_) {
      return this.children().get(p_93501_);
   }

   protected int addEntry(E p_93487_) {
      this.children.add(p_93487_);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int p_93504_) {
      return Objects.equals(this.getSelected(), this.children().get(p_93504_));
   }

   @Nullable
   protected final E getEntryAtPosition(double p_93413_, double p_93414_) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int i1 = Mth.floor(p_93414_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int j1 = i1 / this.itemHeight;
      return (E)(p_93413_ < (double)this.getScrollbarPosition() && p_93413_ >= (double)k && p_93413_ <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
   }

   public void updateSize(int p_93438_, int p_93439_, int p_93440_, int p_93441_) {
      this.width = p_93438_;
      this.height = p_93439_;
      this.y0 = p_93440_;
      this.y1 = p_93441_;
      this.x0 = 0;
      this.x1 = p_93438_;
   }

   public void setLeftPos(int p_93508_) {
      this.x0 = p_93508_;
      this.x1 = p_93508_ + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int p_93431_, int p_93432_) {
   }

   protected void renderHeader(PoseStack p_93458_, int p_93459_, int p_93460_, Tesselator p_93461_) {
   }

   protected void renderBackground(PoseStack p_93442_) {
   }

   protected void renderDecorations(PoseStack p_93443_, int p_93444_, int p_93445_) {
   }

   public void render(PoseStack p_93447_, int p_93448_, int p_93449_, float p_93450_) {
      this.renderBackground(p_93447_);
      int i = this.getScrollbarPosition();
      int j = i + 6;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      this.hovered = this.isMouseOver((double)p_93448_, (double)p_93449_) ? this.getEntryAtPosition((double)p_93448_, (double)p_93449_) : null;
      if (this.renderBackground) {
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float f = 32.0F;
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         tesselator.end();
      }

      int j1 = this.getRowLeft();
      int k = this.y0 + 4 - (int)this.getScrollAmount();
      if (this.renderHeader) {
         this.renderHeader(p_93447_, j1, k, tesselator);
      }

      this.renderList(p_93447_, j1, k, p_93448_, p_93449_, p_93450_);
      if (this.renderTopAndBottom) {
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(519);
         float f1 = 32.0F;
         int l = -100;
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)this.y0, -100.0D).uv(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y0, -100.0D).uv((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), 0.0D, -100.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.height, -100.0D).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.height, -100.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)(this.x0 + this.width), (double)this.y1, -100.0D).uv((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, -100.0D).uv(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
         tesselator.end();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         RenderSystem.disableTexture();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int i1 = 4;
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         bufferbuilder.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         tesselator.end();
      }

      int k1 = this.getMaxScroll();
      if (k1 > 0) {
         RenderSystem.disableTexture();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int l1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         l1 = Mth.clamp(l1, 32, this.y1 - this.y0 - 8);
         int i2 = (int)this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
         if (i2 < this.y0) {
            i2 = this.y0;
         }

         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         bufferbuilder.vertex((double)i, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)j, (double)i2, 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).color(128, 128, 128, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).color(192, 192, 192, 255).endVertex();
         bufferbuilder.vertex((double)i, (double)i2, 0.0D).color(192, 192, 192, 255).endVertex();
         tesselator.end();
      }

      this.renderDecorations(p_93447_, p_93448_, p_93449_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void centerScrollOn(E p_93495_) {
      this.setScrollAmount((double)(this.children().indexOf(p_93495_) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(E p_93499_) {
      int i = this.getRowTop(this.children().indexOf(p_93499_));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.y1 - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int p_93430_) {
      this.setScrollAmount(this.getScrollAmount() + (double)p_93430_);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double p_93411_) {
      this.scrollAmount = Mth.clamp(p_93411_, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double p_93482_, double p_93483_, int p_93484_) {
      this.scrolling = p_93484_ == 0 && p_93482_ >= (double)this.getScrollbarPosition() && p_93482_ < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double p_93420_, double p_93421_, int p_93422_) {
      this.updateScrollingState(p_93420_, p_93421_, p_93422_);
      if (!this.isMouseOver(p_93420_, p_93421_)) {
         return false;
      } else {
         E e = this.getEntryAtPosition(p_93420_, p_93421_);
         if (e != null) {
            if (e.mouseClicked(p_93420_, p_93421_, p_93422_)) {
               this.setFocused(e);
               this.setDragging(true);
               return true;
            }
         } else if (p_93422_ == 0) {
            this.clickedHeader((int)(p_93420_ - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(p_93421_ - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double p_93491_, double p_93492_, int p_93493_) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(p_93491_, p_93492_, p_93493_);
      }

      return false;
   }

   public boolean mouseDragged(double p_93424_, double p_93425_, int p_93426_, double p_93427_, double p_93428_) {
      if (super.mouseDragged(p_93424_, p_93425_, p_93426_, p_93427_, p_93428_)) {
         return true;
      } else if (p_93426_ == 0 && this.scrolling) {
         if (p_93425_ < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if (p_93425_ > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double d0 = (double)Math.max(1, this.getMaxScroll());
            int i = this.y1 - this.y0;
            int j = Mth.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0D, d0 / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + p_93428_ * d1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_93416_, double p_93417_, double p_93418_) {
      this.setScrollAmount(this.getScrollAmount() - p_93418_ * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int p_93434_, int p_93435_, int p_93436_) {
      if (super.keyPressed(p_93434_, p_93435_, p_93436_)) {
         return true;
      } else if (p_93434_ == 264) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
         return true;
      } else if (p_93434_ == 265) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.UP);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection p_93463_) {
      this.moveSelection(p_93463_, (p_93510_) -> {
         return true;
      });
   }

   protected void refreshSelection() {
      E e = this.getSelected();
      if (e != null) {
         this.setSelected(e);
         this.ensureVisible(e);
      }

   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection p_93465_, Predicate<E> p_93466_) {
      int i = p_93465_ == AbstractSelectionList.SelectionDirection.UP ? -1 : 1;
      if (!this.children().isEmpty()) {
         int j = this.children().indexOf(this.getSelected());

         while(true) {
            int k = Mth.clamp(j + i, 0, this.getItemCount() - 1);
            if (j == k) {
               break;
            }

            E e = this.children().get(k);
            if (p_93466_.test(e)) {
               this.setSelected(e);
               this.ensureVisible(e);
               break;
            }

            j = k;
         }
      }

   }

   public boolean isMouseOver(double p_93479_, double p_93480_) {
      return p_93480_ >= (double)this.y0 && p_93480_ <= (double)this.y1 && p_93479_ >= (double)this.x0 && p_93479_ <= (double)this.x1;
   }

   protected void renderList(PoseStack p_93452_, int p_93453_, int p_93454_, int p_93455_, int p_93456_, float p_93457_) {
      int i = this.getItemCount();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();

      for(int j = 0; j < i; ++j) {
         int k = this.getRowTop(j);
         int l = this.getRowBottom(j);
         if (l >= this.y0 && k <= this.y1) {
            int i1 = p_93454_ + j * this.itemHeight + this.headerHeight;
            int j1 = this.itemHeight - 4;
            E e = this.getEntry(j);
            int k1 = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
               int l1 = this.x0 + this.width / 2 - k1 / 2;
               int i2 = this.x0 + this.width / 2 + k1 / 2;
               RenderSystem.disableTexture();
               RenderSystem.setShader(GameRenderer::getPositionShader);
               float f = this.isFocused() ? 1.0F : 0.5F;
               RenderSystem.setShaderColor(f, f, f, 1.0F);
               bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
               bufferbuilder.vertex((double)l1, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)i2, (double)(i1 - 2), 0.0D).endVertex();
               bufferbuilder.vertex((double)l1, (double)(i1 - 2), 0.0D).endVertex();
               tesselator.end();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(i2 - 1), (double)(i1 - 1), 0.0D).endVertex();
               bufferbuilder.vertex((double)(l1 + 1), (double)(i1 - 1), 0.0D).endVertex();
               tesselator.end();
               RenderSystem.enableTexture();
            }

            int j2 = this.getRowLeft();
            e.render(p_93452_, j, k, j2, k1, j1, p_93455_, p_93456_, Objects.equals(this.hovered, e), p_93457_);
         }
      }

   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int p_93512_) {
      return this.y0 + 4 - (int)this.getScrollAmount() + p_93512_ * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int p_93486_) {
      return this.getRowTop(p_93486_) + this.itemHeight;
   }

   protected boolean isFocused() {
      return false;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int p_93515_) {
      E e = this.children.get(p_93515_);
      return (E)(this.removeEntry(this.children.get(p_93515_)) ? e : null);
   }

   protected boolean removeEntry(E p_93503_) {
      boolean flag = this.children.remove(p_93503_);
      if (flag && p_93503_ == this.getSelected()) {
         this.setSelected((E)null);
      }

      return flag;
   }

   @Nullable
   protected E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(AbstractSelectionList.Entry<E> p_93506_) {
      p_93506_.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput p_168791_, E p_168792_) {
      List<E> list = this.children();
      if (list.size() > 1) {
         int i = list.indexOf(p_168792_);
         if (i != -1) {
            p_168791_.add(NarratedElementType.POSITION, new TranslatableComponent("narrator.position.list", i + 1, list.size()));
         }
      }

   }

   public int getWidth() { return this.width; }
   public int getHeight() { return this.height; }
   public int getTop() { return this.y0; }
   public int getBottom() { return this.y1; }
   public int getLeft() { return this.x0; }
   public int getRight() { return this.x1; }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends AbstractSelectionList.Entry<E>> implements GuiEventListener {
      /** @deprecated */
      @Deprecated
      protected AbstractSelectionList<E> list;

      public abstract void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_);

      public boolean isMouseOver(double p_93537_, double p_93538_) {
         return Objects.equals(this.list.getEntryAtPosition(p_93537_, p_93538_), this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected static enum SelectionDirection {
      UP,
      DOWN;
   }

   @OnlyIn(Dist.CLIENT)
   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      public E get(int p_93557_) {
         return this.delegate.get(p_93557_);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int p_93559_, E p_93560_) {
         E e = this.delegate.set(p_93559_, p_93560_);
         AbstractSelectionList.this.bindEntryToSelf(p_93560_);
         return e;
      }

      public void add(int p_93567_, E p_93568_) {
         this.delegate.add(p_93567_, p_93568_);
         AbstractSelectionList.this.bindEntryToSelf(p_93568_);
      }

      public E remove(int p_93565_) {
         return this.delegate.remove(p_93565_);
      }
   }
}
