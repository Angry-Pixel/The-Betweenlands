package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

@OnlyIn(Dist.CLIENT)
public class StringSplitter {
   final StringSplitter.WidthProvider widthProvider;

   public StringSplitter(StringSplitter.WidthProvider p_92335_) {
      this.widthProvider = p_92335_;
   }

   public float stringWidth(@Nullable String p_92354_) {
      if (p_92354_ == null) {
         return 0.0F;
      } else {
         MutableFloat mutablefloat = new MutableFloat();
         StringDecomposer.iterateFormatted(p_92354_, Style.EMPTY, (p_92429_, p_92430_, p_92431_) -> {
            mutablefloat.add(this.widthProvider.getWidth(p_92431_, p_92430_));
            return true;
         });
         return mutablefloat.floatValue();
      }
   }

   public float stringWidth(FormattedText p_92385_) {
      MutableFloat mutablefloat = new MutableFloat();
      StringDecomposer.iterateFormatted(p_92385_, Style.EMPTY, (p_92420_, p_92421_, p_92422_) -> {
         mutablefloat.add(this.widthProvider.getWidth(p_92422_, p_92421_));
         return true;
      });
      return mutablefloat.floatValue();
   }

   public float stringWidth(FormattedCharSequence p_92337_) {
      MutableFloat mutablefloat = new MutableFloat();
      p_92337_.accept((p_92400_, p_92401_, p_92402_) -> {
         mutablefloat.add(this.widthProvider.getWidth(p_92402_, p_92401_));
         return true;
      });
      return mutablefloat.floatValue();
   }

   public int plainIndexAtWidth(String p_92361_, int p_92362_, Style p_92363_) {
      StringSplitter.WidthLimitedCharSink stringsplitter$widthlimitedcharsink = new StringSplitter.WidthLimitedCharSink((float)p_92362_);
      StringDecomposer.iterate(p_92361_, p_92363_, stringsplitter$widthlimitedcharsink);
      return stringsplitter$widthlimitedcharsink.getPosition();
   }

   public String plainHeadByWidth(String p_92411_, int p_92412_, Style p_92413_) {
      return p_92411_.substring(0, this.plainIndexAtWidth(p_92411_, p_92412_, p_92413_));
   }

   public String plainTailByWidth(String p_92424_, int p_92425_, Style p_92426_) {
      MutableFloat mutablefloat = new MutableFloat();
      MutableInt mutableint = new MutableInt(p_92424_.length());
      StringDecomposer.iterateBackwards(p_92424_, p_92426_, (p_92407_, p_92408_, p_92409_) -> {
         float f = mutablefloat.addAndGet(this.widthProvider.getWidth(p_92409_, p_92408_));
         if (f > (float)p_92425_) {
            return false;
         } else {
            mutableint.setValue(p_92407_);
            return true;
         }
      });
      return p_92424_.substring(mutableint.intValue());
   }

   public int formattedIndexByWidth(String p_168627_, int p_168628_, Style p_168629_) {
      StringSplitter.WidthLimitedCharSink stringsplitter$widthlimitedcharsink = new StringSplitter.WidthLimitedCharSink((float)p_168628_);
      StringDecomposer.iterateFormatted(p_168627_, p_168629_, stringsplitter$widthlimitedcharsink);
      return stringsplitter$widthlimitedcharsink.getPosition();
   }

   @Nullable
   public Style componentStyleAtWidth(FormattedText p_92387_, int p_92388_) {
      StringSplitter.WidthLimitedCharSink stringsplitter$widthlimitedcharsink = new StringSplitter.WidthLimitedCharSink((float)p_92388_);
      return p_92387_.visit((p_92343_, p_92344_) -> {
         return StringDecomposer.iterateFormatted(p_92344_, p_92343_, stringsplitter$widthlimitedcharsink) ? Optional.empty() : Optional.of(p_92343_);
      }, Style.EMPTY).orElse((Style)null);
   }

   @Nullable
   public Style componentStyleAtWidth(FormattedCharSequence p_92339_, int p_92340_) {
      StringSplitter.WidthLimitedCharSink stringsplitter$widthlimitedcharsink = new StringSplitter.WidthLimitedCharSink((float)p_92340_);
      MutableObject<Style> mutableobject = new MutableObject<>();
      p_92339_.accept((p_92348_, p_92349_, p_92350_) -> {
         if (!stringsplitter$widthlimitedcharsink.accept(p_92348_, p_92349_, p_92350_)) {
            mutableobject.setValue(p_92349_);
            return false;
         } else {
            return true;
         }
      });
      return mutableobject.getValue();
   }

   public String formattedHeadByWidth(String p_168631_, int p_168632_, Style p_168633_) {
      return p_168631_.substring(0, this.formattedIndexByWidth(p_168631_, p_168632_, p_168633_));
   }

   public FormattedText headByWidth(FormattedText p_92390_, int p_92391_, Style p_92392_) {
      final StringSplitter.WidthLimitedCharSink stringsplitter$widthlimitedcharsink = new StringSplitter.WidthLimitedCharSink((float)p_92391_);
      return p_92390_.visit(new FormattedText.StyledContentConsumer<FormattedText>() {
         private final ComponentCollector collector = new ComponentCollector();

         public Optional<FormattedText> accept(Style p_92443_, String p_92444_) {
            stringsplitter$widthlimitedcharsink.resetPosition();
            if (!StringDecomposer.iterateFormatted(p_92444_, p_92443_, stringsplitter$widthlimitedcharsink)) {
               String s = p_92444_.substring(0, stringsplitter$widthlimitedcharsink.getPosition());
               if (!s.isEmpty()) {
                  this.collector.append(FormattedText.of(s, p_92443_));
               }

               return Optional.of(this.collector.getResultOrEmpty());
            } else {
               if (!p_92444_.isEmpty()) {
                  this.collector.append(FormattedText.of(p_92444_, p_92443_));
               }

               return Optional.empty();
            }
         }
      }, p_92392_).orElse(p_92390_);
   }

   public int findLineBreak(String p_168635_, int p_168636_, Style p_168637_) {
      StringSplitter.LineBreakFinder stringsplitter$linebreakfinder = new StringSplitter.LineBreakFinder((float)p_168636_);
      StringDecomposer.iterateFormatted(p_168635_, p_168637_, stringsplitter$linebreakfinder);
      return stringsplitter$linebreakfinder.getSplitPosition();
   }

   public static int getWordPosition(String p_92356_, int p_92357_, int p_92358_, boolean p_92359_) {
      int i = p_92358_;
      boolean flag = p_92357_ < 0;
      int j = Math.abs(p_92357_);

      for(int k = 0; k < j; ++k) {
         if (flag) {
            while(p_92359_ && i > 0 && (p_92356_.charAt(i - 1) == ' ' || p_92356_.charAt(i - 1) == '\n')) {
               --i;
            }

            while(i > 0 && p_92356_.charAt(i - 1) != ' ' && p_92356_.charAt(i - 1) != '\n') {
               --i;
            }
         } else {
            int l = p_92356_.length();
            int i1 = p_92356_.indexOf(32, i);
            int j1 = p_92356_.indexOf(10, i);
            if (i1 == -1 && j1 == -1) {
               i = -1;
            } else if (i1 != -1 && j1 != -1) {
               i = Math.min(i1, j1);
            } else if (i1 != -1) {
               i = i1;
            } else {
               i = j1;
            }

            if (i == -1) {
               i = l;
            } else {
               while(p_92359_ && i < l && (p_92356_.charAt(i) == ' ' || p_92356_.charAt(i) == '\n')) {
                  ++i;
               }
            }
         }
      }

      return i;
   }

   public void splitLines(String p_92365_, int p_92366_, Style p_92367_, boolean p_92368_, StringSplitter.LinePosConsumer p_92369_) {
      int i = 0;
      int j = p_92365_.length();

      StringSplitter.LineBreakFinder stringsplitter$linebreakfinder;
      for(Style style = p_92367_; i < j; style = stringsplitter$linebreakfinder.getSplitStyle()) {
         stringsplitter$linebreakfinder = new StringSplitter.LineBreakFinder((float)p_92366_);
         boolean flag = StringDecomposer.iterateFormatted(p_92365_, i, style, p_92367_, stringsplitter$linebreakfinder);
         if (flag) {
            p_92369_.accept(style, i, j);
            break;
         }

         int k = stringsplitter$linebreakfinder.getSplitPosition();
         char c0 = p_92365_.charAt(k);
         int l = c0 != '\n' && c0 != ' ' ? k : k + 1;
         p_92369_.accept(style, i, p_92368_ ? l : k);
         i = l;
      }

   }

   public List<FormattedText> splitLines(String p_92433_, int p_92434_, Style p_92435_) {
      List<FormattedText> list = Lists.newArrayList();
      this.splitLines(p_92433_, p_92434_, p_92435_, false, (p_92373_, p_92374_, p_92375_) -> {
         list.add(FormattedText.of(p_92433_.substring(p_92374_, p_92375_), p_92373_));
      });
      return list;
   }

   public List<FormattedText> splitLines(FormattedText p_92415_, int p_92416_, Style p_92417_) {
      List<FormattedText> list = Lists.newArrayList();
      this.splitLines(p_92415_, p_92416_, p_92417_, (p_92378_, p_92379_) -> {
         list.add(p_92378_);
      });
      return list;
   }

   public List<FormattedText> splitLines(FormattedText p_168622_, int p_168623_, Style p_168624_, FormattedText p_168625_) {
      List<FormattedText> list = Lists.newArrayList();
      this.splitLines(p_168622_, p_168623_, p_168624_, (p_168619_, p_168620_) -> {
         list.add(p_168620_ ? FormattedText.composite(p_168625_, p_168619_) : p_168619_);
      });
      return list;
   }

   public void splitLines(FormattedText p_92394_, int p_92395_, Style p_92396_, BiConsumer<FormattedText, Boolean> p_92397_) {
      List<StringSplitter.LineComponent> list = Lists.newArrayList();
      p_92394_.visit((p_92382_, p_92383_) -> {
         if (!p_92383_.isEmpty()) {
            list.add(new StringSplitter.LineComponent(p_92383_, p_92382_));
         }

         return Optional.empty();
      }, p_92396_);
      StringSplitter.FlatComponents stringsplitter$flatcomponents = new StringSplitter.FlatComponents(list);
      boolean flag = true;
      boolean flag1 = false;
      boolean flag2 = false;

      while(flag) {
         flag = false;
         StringSplitter.LineBreakFinder stringsplitter$linebreakfinder = new StringSplitter.LineBreakFinder((float)p_92395_);

         for(StringSplitter.LineComponent stringsplitter$linecomponent : stringsplitter$flatcomponents.parts) {
            boolean flag3 = StringDecomposer.iterateFormatted(stringsplitter$linecomponent.contents, 0, stringsplitter$linecomponent.style, p_92396_, stringsplitter$linebreakfinder);
            if (!flag3) {
               int i = stringsplitter$linebreakfinder.getSplitPosition();
               Style style = stringsplitter$linebreakfinder.getSplitStyle();
               char c0 = stringsplitter$flatcomponents.charAt(i);
               boolean flag4 = c0 == '\n';
               boolean flag5 = flag4 || c0 == ' ';
               flag1 = flag4;
               FormattedText formattedtext = stringsplitter$flatcomponents.splitAt(i, flag5 ? 1 : 0, style);
               p_92397_.accept(formattedtext, flag2);
               flag2 = !flag4;
               flag = true;
               break;
            }

            stringsplitter$linebreakfinder.addToOffset(stringsplitter$linecomponent.contents.length());
         }
      }

      FormattedText formattedtext1 = stringsplitter$flatcomponents.getRemainder();
      if (formattedtext1 != null) {
         p_92397_.accept(formattedtext1, flag2);
      } else if (flag1) {
         p_92397_.accept(FormattedText.EMPTY, false);
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class FlatComponents {
      final List<StringSplitter.LineComponent> parts;
      private String flatParts;

      public FlatComponents(List<StringSplitter.LineComponent> p_92448_) {
         this.parts = p_92448_;
         this.flatParts = p_92448_.stream().map((p_92459_) -> {
            return p_92459_.contents;
         }).collect(Collectors.joining());
      }

      public char charAt(int p_92451_) {
         return this.flatParts.charAt(p_92451_);
      }

      public FormattedText splitAt(int p_92453_, int p_92454_, Style p_92455_) {
         ComponentCollector componentcollector = new ComponentCollector();
         ListIterator<StringSplitter.LineComponent> listiterator = this.parts.listIterator();
         int i = p_92453_;
         boolean flag = false;

         while(listiterator.hasNext()) {
            StringSplitter.LineComponent stringsplitter$linecomponent = listiterator.next();
            String s = stringsplitter$linecomponent.contents;
            int j = s.length();
            if (!flag) {
               if (i > j) {
                  componentcollector.append(stringsplitter$linecomponent);
                  listiterator.remove();
                  i -= j;
               } else {
                  String s1 = s.substring(0, i);
                  if (!s1.isEmpty()) {
                     componentcollector.append(FormattedText.of(s1, stringsplitter$linecomponent.style));
                  }

                  i += p_92454_;
                  flag = true;
               }
            }

            if (flag) {
               if (i <= j) {
                  String s2 = s.substring(i);
                  if (s2.isEmpty()) {
                     listiterator.remove();
                  } else {
                     listiterator.set(new StringSplitter.LineComponent(s2, p_92455_));
                  }
                  break;
               }

               listiterator.remove();
               i -= j;
            }
         }

         this.flatParts = this.flatParts.substring(p_92453_ + p_92454_);
         return componentcollector.getResultOrEmpty();
      }

      @Nullable
      public FormattedText getRemainder() {
         ComponentCollector componentcollector = new ComponentCollector();
         this.parts.forEach(componentcollector::append);
         this.parts.clear();
         return componentcollector.getResult();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class LineBreakFinder implements FormattedCharSink {
      private final float maxWidth;
      private int lineBreak = -1;
      private Style lineBreakStyle = Style.EMPTY;
      private boolean hadNonZeroWidthChar;
      private float width;
      private int lastSpace = -1;
      private Style lastSpaceStyle = Style.EMPTY;
      private int nextChar;
      private int offset;

      public LineBreakFinder(float p_92472_) {
         this.maxWidth = Math.max(p_92472_, 1.0F);
      }

      public boolean accept(int p_92480_, Style p_92481_, int p_92482_) {
         int i = p_92480_ + this.offset;
         switch(p_92482_) {
         case 10:
            return this.finishIteration(i, p_92481_);
         case 32:
            this.lastSpace = i;
            this.lastSpaceStyle = p_92481_;
         default:
            float f = StringSplitter.this.widthProvider.getWidth(p_92482_, p_92481_);
            this.width += f;
            if (this.hadNonZeroWidthChar && this.width > this.maxWidth) {
               return this.lastSpace != -1 ? this.finishIteration(this.lastSpace, this.lastSpaceStyle) : this.finishIteration(i, p_92481_);
            } else {
               this.hadNonZeroWidthChar |= f != 0.0F;
               this.nextChar = i + Character.charCount(p_92482_);
               return true;
            }
         }
      }

      private boolean finishIteration(int p_92477_, Style p_92478_) {
         this.lineBreak = p_92477_;
         this.lineBreakStyle = p_92478_;
         return false;
      }

      private boolean lineBreakFound() {
         return this.lineBreak != -1;
      }

      public int getSplitPosition() {
         return this.lineBreakFound() ? this.lineBreak : this.nextChar;
      }

      public Style getSplitStyle() {
         return this.lineBreakStyle;
      }

      public void addToOffset(int p_92475_) {
         this.offset += p_92475_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LineComponent implements FormattedText {
      final String contents;
      final Style style;

      public LineComponent(String p_92488_, Style p_92489_) {
         this.contents = p_92488_;
         this.style = p_92489_;
      }

      public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_92493_) {
         return p_92493_.accept(this.contents);
      }

      public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_92495_, Style p_92496_) {
         return p_92495_.accept(this.style.applyTo(p_92496_), this.contents);
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface LinePosConsumer {
      void accept(Style p_92500_, int p_92501_, int p_92502_);
   }

   @OnlyIn(Dist.CLIENT)
   class WidthLimitedCharSink implements FormattedCharSink {
      private float maxWidth;
      private int position;

      public WidthLimitedCharSink(float p_92508_) {
         this.maxWidth = p_92508_;
      }

      public boolean accept(int p_92511_, Style p_92512_, int p_92513_) {
         this.maxWidth -= StringSplitter.this.widthProvider.getWidth(p_92513_, p_92512_);
         if (this.maxWidth >= 0.0F) {
            this.position = p_92511_ + Character.charCount(p_92513_);
            return true;
         } else {
            return false;
         }
      }

      public int getPosition() {
         return this.position;
      }

      public void resetPosition() {
         this.position = 0;
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface WidthProvider {
      float getWidth(int p_92516_, Style p_92517_);
   }
}