package net.minecraft.client.gui.font;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextFieldHelper {
   private final Supplier<String> getMessageFn;
   private final Consumer<String> setMessageFn;
   private final Supplier<String> getClipboardFn;
   private final Consumer<String> setClipboardFn;
   private final Predicate<String> stringValidator;
   private int cursorPos;
   private int selectionPos;

   public TextFieldHelper(Supplier<String> p_95137_, Consumer<String> p_95138_, Supplier<String> p_95139_, Consumer<String> p_95140_, Predicate<String> p_95141_) {
      this.getMessageFn = p_95137_;
      this.setMessageFn = p_95138_;
      this.getClipboardFn = p_95139_;
      this.setClipboardFn = p_95140_;
      this.stringValidator = p_95141_;
      this.setCursorToEnd();
   }

   public static Supplier<String> createClipboardGetter(Minecraft p_95154_) {
      return () -> {
         return getClipboardContents(p_95154_);
      };
   }

   public static String getClipboardContents(Minecraft p_95170_) {
      return ChatFormatting.stripFormatting(p_95170_.keyboardHandler.getClipboard().replaceAll("\\r", ""));
   }

   public static Consumer<String> createClipboardSetter(Minecraft p_95183_) {
      return (p_95173_) -> {
         setClipboardContents(p_95183_, p_95173_);
      };
   }

   public static void setClipboardContents(Minecraft p_95156_, String p_95157_) {
      p_95156_.keyboardHandler.setClipboard(p_95157_);
   }

   public boolean charTyped(char p_95144_) {
      if (SharedConstants.isAllowedChatCharacter(p_95144_)) {
         this.insertText(this.getMessageFn.get(), Character.toString(p_95144_));
      }

      return true;
   }

   public boolean keyPressed(int p_95146_) {
      if (Screen.isSelectAll(p_95146_)) {
         this.selectAll();
         return true;
      } else if (Screen.isCopy(p_95146_)) {
         this.copy();
         return true;
      } else if (Screen.isPaste(p_95146_)) {
         this.paste();
         return true;
      } else if (Screen.isCut(p_95146_)) {
         this.cut();
         return true;
      } else if (p_95146_ == 259) {
         this.removeCharsFromCursor(-1);
         return true;
      } else {
         if (p_95146_ == 261) {
            this.removeCharsFromCursor(1);
         } else {
            if (p_95146_ == 263) {
               if (Screen.hasControlDown()) {
                  this.moveByWords(-1, Screen.hasShiftDown());
               } else {
                  this.moveByChars(-1, Screen.hasShiftDown());
               }

               return true;
            }

            if (p_95146_ == 262) {
               if (Screen.hasControlDown()) {
                  this.moveByWords(1, Screen.hasShiftDown());
               } else {
                  this.moveByChars(1, Screen.hasShiftDown());
               }

               return true;
            }

            if (p_95146_ == 268) {
               this.setCursorToStart(Screen.hasShiftDown());
               return true;
            }

            if (p_95146_ == 269) {
               this.setCursorToEnd(Screen.hasShiftDown());
               return true;
            }
         }

         return false;
      }
   }

   private int clampToMsgLength(int p_95196_) {
      return Mth.clamp(p_95196_, 0, this.getMessageFn.get().length());
   }

   private void insertText(String p_95161_, String p_95162_) {
      if (this.selectionPos != this.cursorPos) {
         p_95161_ = this.deleteSelection(p_95161_);
      }

      this.cursorPos = Mth.clamp(this.cursorPos, 0, p_95161_.length());
      String s = (new StringBuilder(p_95161_)).insert(this.cursorPos, p_95162_).toString();
      if (this.stringValidator.test(s)) {
         this.setMessageFn.accept(s);
         this.selectionPos = this.cursorPos = Math.min(s.length(), this.cursorPos + p_95162_.length());
      }

   }

   public void insertText(String p_95159_) {
      this.insertText(this.getMessageFn.get(), p_95159_);
   }

   private void resetSelectionIfNeeded(boolean p_95164_) {
      if (!p_95164_) {
         this.selectionPos = this.cursorPos;
      }

   }

   public void moveByChars(int p_169094_) {
      this.moveByChars(p_169094_, false);
   }

   public void moveByChars(int p_95151_, boolean p_95152_) {
      this.cursorPos = Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, p_95151_);
      this.resetSelectionIfNeeded(p_95152_);
   }

   public void moveByWords(int p_169096_) {
      this.moveByWords(p_169096_, false);
   }

   public void moveByWords(int p_95167_, boolean p_95168_) {
      this.cursorPos = StringSplitter.getWordPosition(this.getMessageFn.get(), p_95167_, this.cursorPos, true);
      this.resetSelectionIfNeeded(p_95168_);
   }

   public void removeCharsFromCursor(int p_95190_) {
      String s = this.getMessageFn.get();
      if (!s.isEmpty()) {
         String s1;
         if (this.selectionPos != this.cursorPos) {
            s1 = this.deleteSelection(s);
         } else {
            int i = Util.offsetByCodepoints(s, this.cursorPos, p_95190_);
            int j = Math.min(i, this.cursorPos);
            int k = Math.max(i, this.cursorPos);
            s1 = (new StringBuilder(s)).delete(j, k).toString();
            if (p_95190_ < 0) {
               this.selectionPos = this.cursorPos = j;
            }
         }

         this.setMessageFn.accept(s1);
      }

   }

   public void cut() {
      String s = this.getMessageFn.get();
      this.setClipboardFn.accept(this.getSelected(s));
      this.setMessageFn.accept(this.deleteSelection(s));
   }

   public void paste() {
      this.insertText(this.getMessageFn.get(), this.getClipboardFn.get());
      this.selectionPos = this.cursorPos;
   }

   public void copy() {
      this.setClipboardFn.accept(this.getSelected(this.getMessageFn.get()));
   }

   public void selectAll() {
      this.selectionPos = 0;
      this.cursorPos = this.getMessageFn.get().length();
   }

   private String getSelected(String p_95175_) {
      int i = Math.min(this.cursorPos, this.selectionPos);
      int j = Math.max(this.cursorPos, this.selectionPos);
      return p_95175_.substring(i, j);
   }

   private String deleteSelection(String p_95185_) {
      if (this.selectionPos == this.cursorPos) {
         return p_95185_;
      } else {
         int i = Math.min(this.cursorPos, this.selectionPos);
         int j = Math.max(this.cursorPos, this.selectionPos);
         String s = p_95185_.substring(0, i) + p_95185_.substring(j);
         this.selectionPos = this.cursorPos = i;
         return s;
      }
   }

   public void setCursorToStart() {
      this.setCursorToStart(false);
   }

   private void setCursorToStart(boolean p_95177_) {
      this.cursorPos = 0;
      this.resetSelectionIfNeeded(p_95177_);
   }

   public void setCursorToEnd() {
      this.setCursorToEnd(false);
   }

   private void setCursorToEnd(boolean p_95187_) {
      this.cursorPos = this.getMessageFn.get().length();
      this.resetSelectionIfNeeded(p_95187_);
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public void setCursorPos(int p_169099_) {
      this.setCursorPos(p_169099_, true);
   }

   public void setCursorPos(int p_95180_, boolean p_95181_) {
      this.cursorPos = this.clampToMsgLength(p_95180_);
      this.resetSelectionIfNeeded(p_95181_);
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }

   public void setSelectionPos(int p_169101_) {
      this.selectionPos = this.clampToMsgLength(p_169101_);
   }

   public void setSelectionRange(int p_95148_, int p_95149_) {
      int i = this.getMessageFn.get().length();
      this.cursorPos = Mth.clamp(p_95148_, 0, i);
      this.selectionPos = Mth.clamp(p_95149_, 0, i);
   }

   public boolean isSelecting() {
      return this.cursorPos != this.selectionPos;
   }
}