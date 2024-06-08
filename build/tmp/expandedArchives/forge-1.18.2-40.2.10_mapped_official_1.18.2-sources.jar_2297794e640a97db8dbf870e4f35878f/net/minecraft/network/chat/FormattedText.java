package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
   Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
   FormattedText EMPTY = new FormattedText() {
      public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130779_) {
         return Optional.empty();
      }

      public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130781_, Style p_130782_) {
         return Optional.empty();
      }
   };

   <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130770_);

   <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130771_, Style p_130772_);

   static FormattedText of(final String p_130776_) {
      return new FormattedText() {
         public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130787_) {
            return p_130787_.accept(p_130776_);
         }

         public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130789_, Style p_130790_) {
            return p_130789_.accept(p_130790_, p_130776_);
         }
      };
   }

   static FormattedText of(final String p_130763_, final Style p_130764_) {
      return new FormattedText() {
         public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130797_) {
            return p_130797_.accept(p_130763_);
         }

         public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130799_, Style p_130800_) {
            return p_130799_.accept(p_130764_.applyTo(p_130800_), p_130763_);
         }
      };
   }

   static FormattedText composite(FormattedText... p_130774_) {
      return composite(ImmutableList.copyOf(p_130774_));
   }

   static FormattedText composite(final List<? extends FormattedText> p_130769_) {
      return new FormattedText() {
         public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130805_) {
            for(FormattedText formattedtext : p_130769_) {
               Optional<T> optional = formattedtext.visit(p_130805_);
               if (optional.isPresent()) {
                  return optional;
               }
            }

            return Optional.empty();
         }

         public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130807_, Style p_130808_) {
            for(FormattedText formattedtext : p_130769_) {
               Optional<T> optional = formattedtext.visit(p_130807_, p_130808_);
               if (optional.isPresent()) {
                  return optional;
               }
            }

            return Optional.empty();
         }
      };
   }

   default String getString() {
      StringBuilder stringbuilder = new StringBuilder();
      this.visit((p_130767_) -> {
         stringbuilder.append(p_130767_);
         return Optional.empty();
      });
      return stringbuilder.toString();
   }

   public interface ContentConsumer<T> {
      Optional<T> accept(String p_130810_);
   }

   public interface StyledContentConsumer<T> {
      Optional<T> accept(Style p_130811_, String p_130812_);
   }
}