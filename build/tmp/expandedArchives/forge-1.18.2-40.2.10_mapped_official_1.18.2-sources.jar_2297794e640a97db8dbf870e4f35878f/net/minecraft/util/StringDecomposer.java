package net.minecraft.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class StringDecomposer {
   private static final char REPLACEMENT_CHAR = '\ufffd';
   private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);

   private static boolean feedChar(Style p_14333_, FormattedCharSink p_14334_, int p_14335_, char p_14336_) {
      return Character.isSurrogate(p_14336_) ? p_14334_.accept(p_14335_, p_14333_, 65533) : p_14334_.accept(p_14335_, p_14333_, p_14336_);
   }

   public static boolean iterate(String p_14318_, Style p_14319_, FormattedCharSink p_14320_) {
      int i = p_14318_.length();

      for(int j = 0; j < i; ++j) {
         char c0 = p_14318_.charAt(j);
         if (Character.isHighSurrogate(c0)) {
            if (j + 1 >= i) {
               if (!p_14320_.accept(j, p_14319_, 65533)) {
                  return false;
               }
               break;
            }

            char c1 = p_14318_.charAt(j + 1);
            if (Character.isLowSurrogate(c1)) {
               if (!p_14320_.accept(j, p_14319_, Character.toCodePoint(c0, c1))) {
                  return false;
               }

               ++j;
            } else if (!p_14320_.accept(j, p_14319_, 65533)) {
               return false;
            }
         } else if (!feedChar(p_14319_, p_14320_, j, c0)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateBackwards(String p_14338_, Style p_14339_, FormattedCharSink p_14340_) {
      int i = p_14338_.length();

      for(int j = i - 1; j >= 0; --j) {
         char c0 = p_14338_.charAt(j);
         if (Character.isLowSurrogate(c0)) {
            if (j - 1 < 0) {
               if (!p_14340_.accept(0, p_14339_, 65533)) {
                  return false;
               }
               break;
            }

            char c1 = p_14338_.charAt(j - 1);
            if (Character.isHighSurrogate(c1)) {
               --j;
               if (!p_14340_.accept(j, p_14339_, Character.toCodePoint(c1, c0))) {
                  return false;
               }
            } else if (!p_14340_.accept(j, p_14339_, 65533)) {
               return false;
            }
         } else if (!feedChar(p_14339_, p_14340_, j, c0)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateFormatted(String p_14347_, Style p_14348_, FormattedCharSink p_14349_) {
      return iterateFormatted(p_14347_, 0, p_14348_, p_14349_);
   }

   public static boolean iterateFormatted(String p_14307_, int p_14308_, Style p_14309_, FormattedCharSink p_14310_) {
      return iterateFormatted(p_14307_, p_14308_, p_14309_, p_14309_, p_14310_);
   }

   public static boolean iterateFormatted(String p_14312_, int p_14313_, Style p_14314_, Style p_14315_, FormattedCharSink p_14316_) {
      int i = p_14312_.length();
      Style style = p_14314_;

      for(int j = p_14313_; j < i; ++j) {
         char c0 = p_14312_.charAt(j);
         if (c0 == 167) {
            if (j + 1 >= i) {
               break;
            }

            char c1 = p_14312_.charAt(j + 1);
            ChatFormatting chatformatting = ChatFormatting.getByCode(c1);
            if (chatformatting != null) {
               style = chatformatting == ChatFormatting.RESET ? p_14315_ : style.applyLegacyFormat(chatformatting);
            }

            ++j;
         } else if (Character.isHighSurrogate(c0)) {
            if (j + 1 >= i) {
               if (!p_14316_.accept(j, style, 65533)) {
                  return false;
               }
               break;
            }

            char c2 = p_14312_.charAt(j + 1);
            if (Character.isLowSurrogate(c2)) {
               if (!p_14316_.accept(j, style, Character.toCodePoint(c0, c2))) {
                  return false;
               }

               ++j;
            } else if (!p_14316_.accept(j, style, 65533)) {
               return false;
            }
         } else if (!feedChar(style, p_14316_, j, c0)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateFormatted(FormattedText p_14329_, Style p_14330_, FormattedCharSink p_14331_) {
      return !p_14329_.visit((p_14302_, p_14303_) -> {
         return iterateFormatted(p_14303_, 0, p_14302_, p_14331_) ? Optional.empty() : STOP_ITERATION;
      }, p_14330_).isPresent();
   }

   public static String filterBrokenSurrogates(String p_14305_) {
      StringBuilder stringbuilder = new StringBuilder();
      iterate(p_14305_, Style.EMPTY, (p_14343_, p_14344_, p_14345_) -> {
         stringbuilder.appendCodePoint(p_14345_);
         return true;
      });
      return stringbuilder.toString();
   }

   public static String getPlainText(FormattedText p_14327_) {
      StringBuilder stringbuilder = new StringBuilder();
      iterateFormatted(p_14327_, Style.EMPTY, (p_14323_, p_14324_, p_14325_) -> {
         stringbuilder.appendCodePoint(p_14325_);
         return true;
      });
      return stringbuilder.toString();
   }
}