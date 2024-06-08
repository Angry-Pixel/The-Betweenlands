package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BookViewScreen extends Screen {
   public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
   public static final int PAGE_TEXT_X_OFFSET = 36;
   public static final int PAGE_TEXT_Y_OFFSET = 30;
   public static final BookViewScreen.BookAccess EMPTY_ACCESS = new BookViewScreen.BookAccess() {
      public int getPageCount() {
         return 0;
      }

      public FormattedText getPageRaw(int p_98306_) {
         return FormattedText.EMPTY;
      }
   };
   public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
   protected static final int TEXT_WIDTH = 114;
   protected static final int TEXT_HEIGHT = 128;
   protected static final int IMAGE_WIDTH = 192;
   protected static final int IMAGE_HEIGHT = 192;
   private BookViewScreen.BookAccess bookAccess;
   private int currentPage;
   private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
   private int cachedPage = -1;
   private Component pageMsg = TextComponent.EMPTY;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(BookViewScreen.BookAccess p_98264_) {
      this(p_98264_, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(BookViewScreen.BookAccess p_98266_, boolean p_98267_) {
      super(NarratorChatListener.NO_TITLE);
      this.bookAccess = p_98266_;
      this.playTurnSound = p_98267_;
   }

   public void setBookAccess(BookViewScreen.BookAccess p_98289_) {
      this.bookAccess = p_98289_;
      this.currentPage = Mth.clamp(this.currentPage, 0, p_98289_.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(int p_98276_) {
      int i = Mth.clamp(p_98276_, 0, this.bookAccess.getPageCount() - 1);
      if (i != this.currentPage) {
         this.currentPage = i;
         this.updateButtonVisibility();
         this.cachedPage = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean forcePage(int p_98295_) {
      return this.setPage(p_98295_);
   }

   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   protected void createMenuControls() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 200, 20, CommonComponents.GUI_DONE, (p_98299_) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   protected void createPageControlButtons() {
      int i = (this.width - 192) / 2;
      int j = 2;
      this.forwardButton = this.addRenderableWidget(new PageButton(i + 116, 159, true, (p_98297_) -> {
         this.pageForward();
      }, this.playTurnSound));
      this.backButton = this.addRenderableWidget(new PageButton(i + 43, 159, false, (p_98287_) -> {
         this.pageBack();
      }, this.playTurnSound));
      this.updateButtonVisibility();
   }

   private int getNumPages() {
      return this.bookAccess.getPageCount();
   }

   protected void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
      }

      this.updateButtonVisibility();
   }

   protected void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      }

      this.updateButtonVisibility();
   }

   private void updateButtonVisibility() {
      this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
      this.backButton.visible = this.currentPage > 0;
   }

   public boolean keyPressed(int p_98278_, int p_98279_, int p_98280_) {
      if (super.keyPressed(p_98278_, p_98279_, p_98280_)) {
         return true;
      } else {
         switch(p_98278_) {
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         default:
            return false;
         }
      }
   }

   public void render(PoseStack p_98282_, int p_98283_, int p_98284_, float p_98285_) {
      this.renderBackground(p_98282_);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BOOK_LOCATION);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.blit(p_98282_, i, 2, 0, 0, 192, 192);
      if (this.cachedPage != this.currentPage) {
         FormattedText formattedtext = this.bookAccess.getPage(this.currentPage);
         this.cachedPageComponents = this.font.split(formattedtext, 114);
         this.pageMsg = new TranslatableComponent("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
      }

      this.cachedPage = this.currentPage;
      int i1 = this.font.width(this.pageMsg);
      this.font.draw(p_98282_, this.pageMsg, (float)(i - i1 + 192 - 44), 18.0F, 0);
      int k = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int l = 0; l < k; ++l) {
         FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
         this.font.draw(p_98282_, formattedcharsequence, (float)(i + 36), (float)(32 + l * 9), 0);
      }

      Style style = this.getClickedComponentStyleAt((double)p_98283_, (double)p_98284_);
      if (style != null) {
         this.renderComponentHoverEffect(p_98282_, style, p_98283_, p_98284_);
      }

      super.render(p_98282_, p_98283_, p_98284_, p_98285_);
   }

   public boolean mouseClicked(double p_98272_, double p_98273_, int p_98274_) {
      if (p_98274_ == 0) {
         Style style = this.getClickedComponentStyleAt(p_98272_, p_98273_);
         if (style != null && this.handleComponentClicked(style)) {
            return true;
         }
      }

      return super.mouseClicked(p_98272_, p_98273_, p_98274_);
   }

   public boolean handleComponentClicked(Style p_98293_) {
      ClickEvent clickevent = p_98293_.getClickEvent();
      if (clickevent == null) {
         return false;
      } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String s = clickevent.getValue();

         try {
            int i = Integer.parseInt(s) - 1;
            return this.forcePage(i);
         } catch (Exception exception) {
            return false;
         }
      } else {
         boolean flag = super.handleComponentClicked(p_98293_);
         if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.closeScreen();
         }

         return flag;
      }
   }

   protected void closeScreen() {
      this.minecraft.setScreen((Screen)null);
   }

   @Nullable
   public Style getClickedComponentStyleAt(double p_98269_, double p_98270_) {
      if (this.cachedPageComponents.isEmpty()) {
         return null;
      } else {
         int i = Mth.floor(p_98269_ - (double)((this.width - 192) / 2) - 36.0D);
         int j = Mth.floor(p_98270_ - 2.0D - 30.0D);
         if (i >= 0 && j >= 0) {
            int k = Math.min(128 / 9, this.cachedPageComponents.size());
            if (i <= 114 && j < 9 * k + k) {
               int l = j / 9;
               if (l >= 0 && l < this.cachedPageComponents.size()) {
                  FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
                  return this.minecraft.font.getSplitter().componentStyleAtWidth(formattedcharsequence, i);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   static List<String> loadPages(CompoundTag p_169695_) {
      Builder<String> builder = ImmutableList.builder();
      loadPages(p_169695_, builder::add);
      return builder.build();
   }

   public static void loadPages(CompoundTag p_169697_, Consumer<String> p_169698_) {
      ListTag listtag = p_169697_.getList("pages", 8).copy();
      IntFunction<String> intfunction;
      if (Minecraft.getInstance().isTextFilteringEnabled() && p_169697_.contains("filtered_pages", 10)) {
         CompoundTag compoundtag = p_169697_.getCompound("filtered_pages");
         intfunction = (p_169702_) -> {
            String s = String.valueOf(p_169702_);
            return compoundtag.contains(s) ? compoundtag.getString(s) : listtag.getString(p_169702_);
         };
      } else {
         intfunction = listtag::getString;
      }

      for(int i = 0; i < listtag.size(); ++i) {
         p_169698_.accept(intfunction.apply(i));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public interface BookAccess {
      int getPageCount();

      FormattedText getPageRaw(int p_98307_);

      default FormattedText getPage(int p_98311_) {
         return p_98311_ >= 0 && p_98311_ < this.getPageCount() ? this.getPageRaw(p_98311_) : FormattedText.EMPTY;
      }

      static BookViewScreen.BookAccess fromItem(ItemStack p_98309_) {
         if (p_98309_.is(Items.WRITTEN_BOOK)) {
            return new BookViewScreen.WrittenBookAccess(p_98309_);
         } else {
            return (BookViewScreen.BookAccess)(p_98309_.is(Items.WRITABLE_BOOK) ? new BookViewScreen.WritableBookAccess(p_98309_) : BookViewScreen.EMPTY_ACCESS);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WritableBookAccess implements BookViewScreen.BookAccess {
      private final List<String> pages;

      public WritableBookAccess(ItemStack p_98314_) {
         this.pages = readPages(p_98314_);
      }

      private static List<String> readPages(ItemStack p_98319_) {
         CompoundTag compoundtag = p_98319_.getTag();
         return (List<String>)(compoundtag != null ? BookViewScreen.loadPages(compoundtag) : ImmutableList.of());
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public FormattedText getPageRaw(int p_98317_) {
         return FormattedText.of(this.pages.get(p_98317_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WrittenBookAccess implements BookViewScreen.BookAccess {
      private final List<String> pages;

      public WrittenBookAccess(ItemStack p_98322_) {
         this.pages = readPages(p_98322_);
      }

      private static List<String> readPages(ItemStack p_98327_) {
         CompoundTag compoundtag = p_98327_.getTag();
         return (List<String>)(compoundtag != null && WrittenBookItem.makeSureTagIsValid(compoundtag) ? BookViewScreen.loadPages(compoundtag) : ImmutableList.of(Component.Serializer.toJson((new TranslatableComponent("book.invalid.tag")).withStyle(ChatFormatting.DARK_RED))));
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public FormattedText getPageRaw(int p_98325_) {
         String s = this.pages.get(p_98325_);

         try {
            FormattedText formattedtext = Component.Serializer.fromJson(s);
            if (formattedtext != null) {
               return formattedtext;
            }
         } catch (Exception exception) {
         }

         return FormattedText.of(s);
      }
   }
}