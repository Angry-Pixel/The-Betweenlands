package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

@OnlyIn(Dist.CLIENT)
public class BookEditScreen extends Screen {
   private static final int TEXT_WIDTH = 114;
   private static final int TEXT_HEIGHT = 128;
   private static final int IMAGE_WIDTH = 192;
   private static final int IMAGE_HEIGHT = 192;
   private static final Component EDIT_TITLE_LABEL = new TranslatableComponent("book.editTitle");
   private static final Component FINALIZE_WARNING_LABEL = new TranslatableComponent("book.finalizeWarning");
   private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
   private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
   private final Player owner;
   private final ItemStack book;
   private boolean isModified;
   private boolean isSigning;
   private int frameTick;
   private int currentPage;
   private final List<String> pages = Lists.newArrayList();
   private String title = "";
   private final TextFieldHelper pageEdit = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, (p_98179_) -> {
      return p_98179_.length() < 1024 && this.font.wordWrapHeight(p_98179_, 114) <= 128;
   });
   private final TextFieldHelper titleEdit = new TextFieldHelper(() -> {
      return this.title;
   }, (p_98175_) -> {
      this.title = p_98175_;
   }, this::getClipboard, this::setClipboard, (p_98170_) -> {
      return p_98170_.length() < 16;
   });
   private long lastClickTime;
   private int lastIndex = -1;
   private PageButton forwardButton;
   private PageButton backButton;
   private Button doneButton;
   private Button signButton;
   private Button finalizeButton;
   private Button cancelButton;
   private final InteractionHand hand;
   @Nullable
   private BookEditScreen.DisplayCache displayCache = BookEditScreen.DisplayCache.EMPTY;
   private Component pageMsg = TextComponent.EMPTY;
   private final Component ownerText;

   public BookEditScreen(Player p_98076_, ItemStack p_98077_, InteractionHand p_98078_) {
      super(NarratorChatListener.NO_TITLE);
      this.owner = p_98076_;
      this.book = p_98077_;
      this.hand = p_98078_;
      CompoundTag compoundtag = p_98077_.getTag();
      if (compoundtag != null) {
         BookViewScreen.loadPages(compoundtag, this.pages::add);
      }

      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

      this.ownerText = (new TranslatableComponent("book.byAuthor", p_98076_.getName())).withStyle(ChatFormatting.DARK_GRAY);
   }

   private void setClipboard(String p_98148_) {
      if (this.minecraft != null) {
         TextFieldHelper.setClipboardContents(this.minecraft, p_98148_);
      }

   }

   private String getClipboard() {
      return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
   }

   private int getNumPages() {
      return this.pages.size();
   }

   public void tick() {
      super.tick();
      ++this.frameTick;
   }

   protected void init() {
      this.clearDisplayCache();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.signButton = this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 98, 20, new TranslatableComponent("book.signButton"), (p_98177_) -> {
         this.isSigning = true;
         this.updateButtonVisibility();
      }));
      this.doneButton = this.addRenderableWidget(new Button(this.width / 2 + 2, 196, 98, 20, CommonComponents.GUI_DONE, (p_98173_) -> {
         this.minecraft.setScreen((Screen)null);
         this.saveChanges(false);
      }));
      this.finalizeButton = this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 98, 20, new TranslatableComponent("book.finalizeButton"), (p_98168_) -> {
         if (this.isSigning) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.cancelButton = this.addRenderableWidget(new Button(this.width / 2 + 2, 196, 98, 20, CommonComponents.GUI_CANCEL, (p_98157_) -> {
         if (this.isSigning) {
            this.isSigning = false;
         }

         this.updateButtonVisibility();
      }));
      int i = (this.width - 192) / 2;
      int j = 2;
      this.forwardButton = this.addRenderableWidget(new PageButton(i + 116, 159, true, (p_98144_) -> {
         this.pageForward();
      }, true));
      this.backButton = this.addRenderableWidget(new PageButton(i + 43, 159, false, (p_98113_) -> {
         this.pageBack();
      }, true));
      this.updateButtonVisibility();
   }

   private void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
      }

      this.updateButtonVisibility();
      this.clearDisplayCacheAfterPageChange();
   }

   private void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      } else {
         this.appendPageToBook();
         if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
         }
      }

      this.updateButtonVisibility();
      this.clearDisplayCacheAfterPageChange();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void updateButtonVisibility() {
      this.backButton.visible = !this.isSigning && this.currentPage > 0;
      this.forwardButton.visible = !this.isSigning;
      this.doneButton.visible = !this.isSigning;
      this.signButton.visible = !this.isSigning;
      this.cancelButton.visible = this.isSigning;
      this.finalizeButton.visible = this.isSigning;
      this.finalizeButton.active = !this.title.trim().isEmpty();
   }

   private void eraseEmptyTrailingPages() {
      ListIterator<String> listiterator = this.pages.listIterator(this.pages.size());

      while(listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
         listiterator.remove();
      }

   }

   private void saveChanges(boolean p_98161_) {
      if (this.isModified) {
         this.eraseEmptyTrailingPages();
         this.updateLocalCopy(p_98161_);
         int i = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
         this.minecraft.getConnection().send(new ServerboundEditBookPacket(i, this.pages, p_98161_ ? Optional.of(this.title.trim()) : Optional.empty()));
      }
   }

   private void updateLocalCopy(boolean p_182575_) {
      ListTag listtag = new ListTag();
      this.pages.stream().map(StringTag::valueOf).forEach(listtag::add);
      if (!this.pages.isEmpty()) {
         this.book.addTagElement("pages", listtag);
      }

      if (p_182575_) {
         this.book.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
         this.book.addTagElement("title", StringTag.valueOf(this.title.trim()));
      }

   }

   private void appendPageToBook() {
      if (this.getNumPages() < 100) {
         this.pages.add("");
         this.isModified = true;
      }
   }

   public boolean keyPressed(int p_98100_, int p_98101_, int p_98102_) {
      if (super.keyPressed(p_98100_, p_98101_, p_98102_)) {
         return true;
      } else if (this.isSigning) {
         return this.titleKeyPressed(p_98100_, p_98101_, p_98102_);
      } else {
         boolean flag = this.bookKeyPressed(p_98100_, p_98101_, p_98102_);
         if (flag) {
            this.clearDisplayCache();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean charTyped(char p_98085_, int p_98086_) {
      if (super.charTyped(p_98085_, p_98086_)) {
         return true;
      } else if (this.isSigning) {
         boolean flag = this.titleEdit.charTyped(p_98085_);
         if (flag) {
            this.updateButtonVisibility();
            this.isModified = true;
            return true;
         } else {
            return false;
         }
      } else if (SharedConstants.isAllowedChatCharacter(p_98085_)) {
         this.pageEdit.insertText(Character.toString(p_98085_));
         this.clearDisplayCache();
         return true;
      } else {
         return false;
      }
   }

   private boolean bookKeyPressed(int p_98153_, int p_98154_, int p_98155_) {
      if (Screen.isSelectAll(p_98153_)) {
         this.pageEdit.selectAll();
         return true;
      } else if (Screen.isCopy(p_98153_)) {
         this.pageEdit.copy();
         return true;
      } else if (Screen.isPaste(p_98153_)) {
         this.pageEdit.paste();
         return true;
      } else if (Screen.isCut(p_98153_)) {
         this.pageEdit.cut();
         return true;
      } else {
         switch(p_98153_) {
         case 257:
         case 335:
            this.pageEdit.insertText("\n");
            return true;
         case 259:
            this.pageEdit.removeCharsFromCursor(-1);
            return true;
         case 261:
            this.pageEdit.removeCharsFromCursor(1);
            return true;
         case 262:
            this.pageEdit.moveByChars(1, Screen.hasShiftDown());
            return true;
         case 263:
            this.pageEdit.moveByChars(-1, Screen.hasShiftDown());
            return true;
         case 264:
            this.keyDown();
            return true;
         case 265:
            this.keyUp();
            return true;
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         case 268:
            this.keyHome();
            return true;
         case 269:
            this.keyEnd();
            return true;
         default:
            return false;
         }
      }
   }

   private void keyUp() {
      this.changeLine(-1);
   }

   private void keyDown() {
      this.changeLine(1);
   }

   private void changeLine(int p_98098_) {
      int i = this.pageEdit.getCursorPos();
      int j = this.getDisplayCache().changeLine(i, p_98098_);
      this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
   }

   private void keyHome() {
      int i = this.pageEdit.getCursorPos();
      int j = this.getDisplayCache().findLineStart(i);
      this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
   }

   private void keyEnd() {
      BookEditScreen.DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
      int i = this.pageEdit.getCursorPos();
      int j = bookeditscreen$displaycache.findLineEnd(i);
      this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
   }

   private boolean titleKeyPressed(int p_98164_, int p_98165_, int p_98166_) {
      switch(p_98164_) {
      case 257:
      case 335:
         if (!this.title.isEmpty()) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

         return true;
      case 259:
         this.titleEdit.removeCharsFromCursor(-1);
         this.updateButtonVisibility();
         this.isModified = true;
         return true;
      default:
         return false;
      }
   }

   private String getCurrentPageText() {
      return this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.get(this.currentPage) : "";
   }

   private void setCurrentPageText(String p_98159_) {
      if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         this.pages.set(this.currentPage, p_98159_);
         this.isModified = true;
         this.clearDisplayCache();
      }

   }

   public void render(PoseStack p_98104_, int p_98105_, int p_98106_, float p_98107_) {
      this.renderBackground(p_98104_);
      this.setFocused((GuiEventListener)null);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.blit(p_98104_, i, 2, 0, 0, 192, 192);
      if (this.isSigning) {
         boolean flag = this.frameTick / 6 % 2 == 0;
         FormattedCharSequence formattedcharsequence = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), flag ? BLACK_CURSOR : GRAY_CURSOR);
         int k = this.font.width(EDIT_TITLE_LABEL);
         this.font.draw(p_98104_, EDIT_TITLE_LABEL, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
         int l = this.font.width(formattedcharsequence);
         this.font.draw(p_98104_, formattedcharsequence, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
         int i1 = this.font.width(this.ownerText);
         this.font.draw(p_98104_, this.ownerText, (float)(i + 36 + (114 - i1) / 2), 60.0F, 0);
         this.font.drawWordWrap(FINALIZE_WARNING_LABEL, i + 36, 82, 114, 0);
      } else {
         int j1 = this.font.width(this.pageMsg);
         this.font.draw(p_98104_, this.pageMsg, (float)(i - j1 + 192 - 44), 18.0F, 0);
         BookEditScreen.DisplayCache bookeditscreen$displaycache = this.getDisplayCache();

         for(BookEditScreen.LineInfo bookeditscreen$lineinfo : bookeditscreen$displaycache.lines) {
            this.font.draw(p_98104_, bookeditscreen$lineinfo.asComponent, (float)bookeditscreen$lineinfo.x, (float)bookeditscreen$lineinfo.y, -16777216);
         }

         this.renderHighlight(bookeditscreen$displaycache.selection);
         this.renderCursor(p_98104_, bookeditscreen$displaycache.cursor, bookeditscreen$displaycache.cursorAtEnd);
      }

      super.render(p_98104_, p_98105_, p_98106_, p_98107_);
   }

   private void renderCursor(PoseStack p_98109_, BookEditScreen.Pos2i p_98110_, boolean p_98111_) {
      if (this.frameTick / 6 % 2 == 0) {
         p_98110_ = this.convertLocalToScreen(p_98110_);
         if (!p_98111_) {
            GuiComponent.fill(p_98109_, p_98110_.x, p_98110_.y - 1, p_98110_.x + 1, p_98110_.y + 9, -16777216);
         } else {
            this.font.draw(p_98109_, "_", (float)p_98110_.x, (float)p_98110_.y, 0);
         }
      }

   }

   private void renderHighlight(Rect2i[] p_98139_) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionShader);
      RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

      for(Rect2i rect2i : p_98139_) {
         int i = rect2i.getX();
         int j = rect2i.getY();
         int k = i + rect2i.getWidth();
         int l = j + rect2i.getHeight();
         bufferbuilder.vertex((double)i, (double)l, 0.0D).endVertex();
         bufferbuilder.vertex((double)k, (double)l, 0.0D).endVertex();
         bufferbuilder.vertex((double)k, (double)j, 0.0D).endVertex();
         bufferbuilder.vertex((double)i, (double)j, 0.0D).endVertex();
      }

      tesselator.end();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   private BookEditScreen.Pos2i convertScreenToLocal(BookEditScreen.Pos2i p_98115_) {
      return new BookEditScreen.Pos2i(p_98115_.x - (this.width - 192) / 2 - 36, p_98115_.y - 32);
   }

   private BookEditScreen.Pos2i convertLocalToScreen(BookEditScreen.Pos2i p_98146_) {
      return new BookEditScreen.Pos2i(p_98146_.x + (this.width - 192) / 2 + 36, p_98146_.y + 32);
   }

   public boolean mouseClicked(double p_98088_, double p_98089_, int p_98090_) {
      if (super.mouseClicked(p_98088_, p_98089_, p_98090_)) {
         return true;
      } else {
         if (p_98090_ == 0) {
            long i = Util.getMillis();
            BookEditScreen.DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
            int j = bookeditscreen$displaycache.getIndexAtPosition(this.font, this.convertScreenToLocal(new BookEditScreen.Pos2i((int)p_98088_, (int)p_98089_)));
            if (j >= 0) {
               if (j == this.lastIndex && i - this.lastClickTime < 250L) {
                  if (!this.pageEdit.isSelecting()) {
                     this.selectWord(j);
                  } else {
                     this.pageEdit.selectAll();
                  }
               } else {
                  this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
               }

               this.clearDisplayCache();
            }

            this.lastIndex = j;
            this.lastClickTime = i;
         }

         return true;
      }
   }

   private void selectWord(int p_98142_) {
      String s = this.getCurrentPageText();
      this.pageEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, p_98142_, false), StringSplitter.getWordPosition(s, 1, p_98142_, false));
   }

   public boolean mouseDragged(double p_98092_, double p_98093_, int p_98094_, double p_98095_, double p_98096_) {
      if (super.mouseDragged(p_98092_, p_98093_, p_98094_, p_98095_, p_98096_)) {
         return true;
      } else {
         if (p_98094_ == 0) {
            BookEditScreen.DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
            int i = bookeditscreen$displaycache.getIndexAtPosition(this.font, this.convertScreenToLocal(new BookEditScreen.Pos2i((int)p_98092_, (int)p_98093_)));
            this.pageEdit.setCursorPos(i, true);
            this.clearDisplayCache();
         }

         return true;
      }
   }

   private BookEditScreen.DisplayCache getDisplayCache() {
      if (this.displayCache == null) {
         this.displayCache = this.rebuildDisplayCache();
         this.pageMsg = new TranslatableComponent("book.pageIndicator", this.currentPage + 1, this.getNumPages());
      }

      return this.displayCache;
   }

   private void clearDisplayCache() {
      this.displayCache = null;
   }

   private void clearDisplayCacheAfterPageChange() {
      this.pageEdit.setCursorToEnd();
      this.clearDisplayCache();
   }

   private BookEditScreen.DisplayCache rebuildDisplayCache() {
      String s = this.getCurrentPageText();
      if (s.isEmpty()) {
         return BookEditScreen.DisplayCache.EMPTY;
      } else {
         int i = this.pageEdit.getCursorPos();
         int j = this.pageEdit.getSelectionPos();
         IntList intlist = new IntArrayList();
         List<BookEditScreen.LineInfo> list = Lists.newArrayList();
         MutableInt mutableint = new MutableInt();
         MutableBoolean mutableboolean = new MutableBoolean();
         StringSplitter stringsplitter = this.font.getSplitter();
         stringsplitter.splitLines(s, 114, Style.EMPTY, true, (p_98132_, p_98133_, p_98134_) -> {
            int k3 = mutableint.getAndIncrement();
            String s2 = s.substring(p_98133_, p_98134_);
            mutableboolean.setValue(s2.endsWith("\n"));
            String s3 = StringUtils.stripEnd(s2, " \n");
            int l3 = k3 * 9;
            BookEditScreen.Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(new BookEditScreen.Pos2i(0, l3));
            intlist.add(p_98133_);
            list.add(new BookEditScreen.LineInfo(p_98132_, s3, bookeditscreen$pos2i1.x, bookeditscreen$pos2i1.y));
         });
         int[] aint = intlist.toIntArray();
         boolean flag = i == s.length();
         BookEditScreen.Pos2i bookeditscreen$pos2i;
         if (flag && mutableboolean.isTrue()) {
            bookeditscreen$pos2i = new BookEditScreen.Pos2i(0, list.size() * 9);
         } else {
            int k = findLineFromPos(aint, i);
            int l = this.font.width(s.substring(aint[k], i));
            bookeditscreen$pos2i = new BookEditScreen.Pos2i(l, k * 9);
         }

         List<Rect2i> list1 = Lists.newArrayList();
         if (i != j) {
            int l2 = Math.min(i, j);
            int i1 = Math.max(i, j);
            int j1 = findLineFromPos(aint, l2);
            int k1 = findLineFromPos(aint, i1);
            if (j1 == k1) {
               int l1 = j1 * 9;
               int i2 = aint[j1];
               list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
            } else {
               int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
               list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));

               for(int j3 = j1 + 1; j3 < k1; ++j3) {
                  int j2 = j3 * 9;
                  String s1 = s.substring(aint[j3], aint[j3 + 1]);
                  int k2 = (int)stringsplitter.stringWidth(s1);
                  list1.add(this.createSelection(new BookEditScreen.Pos2i(0, j2), new BookEditScreen.Pos2i(k2, j2 + 9)));
               }

               list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
            }
         }

         return new BookEditScreen.DisplayCache(s, bookeditscreen$pos2i, flag, aint, list.toArray(new BookEditScreen.LineInfo[0]), list1.toArray(new Rect2i[0]));
      }
   }

   static int findLineFromPos(int[] p_98150_, int p_98151_) {
      int i = Arrays.binarySearch(p_98150_, p_98151_);
      return i < 0 ? -(i + 2) : i;
   }

   private Rect2i createPartialLineSelection(String p_98120_, StringSplitter p_98121_, int p_98122_, int p_98123_, int p_98124_, int p_98125_) {
      String s = p_98120_.substring(p_98125_, p_98122_);
      String s1 = p_98120_.substring(p_98125_, p_98123_);
      BookEditScreen.Pos2i bookeditscreen$pos2i = new BookEditScreen.Pos2i((int)p_98121_.stringWidth(s), p_98124_);
      BookEditScreen.Pos2i bookeditscreen$pos2i1 = new BookEditScreen.Pos2i((int)p_98121_.stringWidth(s1), p_98124_ + 9);
      return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
   }

   private Rect2i createSelection(BookEditScreen.Pos2i p_98117_, BookEditScreen.Pos2i p_98118_) {
      BookEditScreen.Pos2i bookeditscreen$pos2i = this.convertLocalToScreen(p_98117_);
      BookEditScreen.Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(p_98118_);
      int i = Math.min(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
      int j = Math.max(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
      int k = Math.min(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
      int l = Math.max(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
      return new Rect2i(i, k, j - i, l - k);
   }

   @OnlyIn(Dist.CLIENT)
   static class DisplayCache {
      static final BookEditScreen.DisplayCache EMPTY = new BookEditScreen.DisplayCache("", new BookEditScreen.Pos2i(0, 0), true, new int[]{0}, new BookEditScreen.LineInfo[]{new BookEditScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
      private final String fullText;
      final BookEditScreen.Pos2i cursor;
      final boolean cursorAtEnd;
      private final int[] lineStarts;
      final BookEditScreen.LineInfo[] lines;
      final Rect2i[] selection;

      public DisplayCache(String p_98201_, BookEditScreen.Pos2i p_98202_, boolean p_98203_, int[] p_98204_, BookEditScreen.LineInfo[] p_98205_, Rect2i[] p_98206_) {
         this.fullText = p_98201_;
         this.cursor = p_98202_;
         this.cursorAtEnd = p_98203_;
         this.lineStarts = p_98204_;
         this.lines = p_98205_;
         this.selection = p_98206_;
      }

      public int getIndexAtPosition(Font p_98214_, BookEditScreen.Pos2i p_98215_) {
         int i = p_98215_.y / 9;
         if (i < 0) {
            return 0;
         } else if (i >= this.lines.length) {
            return this.fullText.length();
         } else {
            BookEditScreen.LineInfo bookeditscreen$lineinfo = this.lines[i];
            return this.lineStarts[i] + p_98214_.getSplitter().plainIndexAtWidth(bookeditscreen$lineinfo.contents, p_98215_.x, bookeditscreen$lineinfo.style);
         }
      }

      public int changeLine(int p_98211_, int p_98212_) {
         int i = BookEditScreen.findLineFromPos(this.lineStarts, p_98211_);
         int j = i + p_98212_;
         int k;
         if (0 <= j && j < this.lineStarts.length) {
            int l = p_98211_ - this.lineStarts[i];
            int i1 = this.lines[j].contents.length();
            k = this.lineStarts[j] + Math.min(l, i1);
         } else {
            k = p_98211_;
         }

         return k;
      }

      public int findLineStart(int p_98209_) {
         int i = BookEditScreen.findLineFromPos(this.lineStarts, p_98209_);
         return this.lineStarts[i];
      }

      public int findLineEnd(int p_98219_) {
         int i = BookEditScreen.findLineFromPos(this.lineStarts, p_98219_);
         return this.lineStarts[i] + this.lines[i].contents.length();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LineInfo {
      final Style style;
      final String contents;
      final Component asComponent;
      final int x;
      final int y;

      public LineInfo(Style p_98232_, String p_98233_, int p_98234_, int p_98235_) {
         this.style = p_98232_;
         this.contents = p_98233_;
         this.x = p_98234_;
         this.y = p_98235_;
         this.asComponent = (new TextComponent(p_98233_)).setStyle(p_98232_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Pos2i {
      public final int x;
      public final int y;

      Pos2i(int p_98249_, int p_98250_) {
         this.x = p_98249_;
         this.y = p_98250_;
      }
   }
}