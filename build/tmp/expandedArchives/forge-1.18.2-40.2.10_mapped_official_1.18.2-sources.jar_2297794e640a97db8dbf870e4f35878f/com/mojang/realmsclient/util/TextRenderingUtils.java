package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextRenderingUtils {
   private TextRenderingUtils() {
   }

   @VisibleForTesting
   protected static List<String> lineBreak(String p_90249_) {
      return Arrays.asList(p_90249_.split("\\n"));
   }

   public static List<TextRenderingUtils.Line> decompose(String p_90257_, TextRenderingUtils.LineSegment... p_90258_) {
      return decompose(p_90257_, Arrays.asList(p_90258_));
   }

   private static List<TextRenderingUtils.Line> decompose(String p_90254_, List<TextRenderingUtils.LineSegment> p_90255_) {
      List<String> list = lineBreak(p_90254_);
      return insertLinks(list, p_90255_);
   }

   private static List<TextRenderingUtils.Line> insertLinks(List<String> p_90260_, List<TextRenderingUtils.LineSegment> p_90261_) {
      int i = 0;
      List<TextRenderingUtils.Line> list = Lists.newArrayList();

      for(String s : p_90260_) {
         List<TextRenderingUtils.LineSegment> list1 = Lists.newArrayList();

         for(String s1 : split(s, "%link")) {
            if ("%link".equals(s1)) {
               list1.add(p_90261_.get(i++));
            } else {
               list1.add(TextRenderingUtils.LineSegment.text(s1));
            }
         }

         list.add(new TextRenderingUtils.Line(list1));
      }

      return list;
   }

   public static List<String> split(String p_90251_, String p_90252_) {
      if (p_90252_.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> list = Lists.newArrayList();

         int i;
         int j;
         for(i = 0; (j = p_90251_.indexOf(p_90252_, i)) != -1; i = j + p_90252_.length()) {
            if (j > i) {
               list.add(p_90251_.substring(i, j));
            }

            list.add(p_90252_);
         }

         if (i < p_90251_.length()) {
            list.add(p_90251_.substring(i));
         }

         return list;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Line {
      public final List<TextRenderingUtils.LineSegment> segments;

      Line(TextRenderingUtils.LineSegment... p_167625_) {
         this(Arrays.asList(p_167625_));
      }

      Line(List<TextRenderingUtils.LineSegment> p_90264_) {
         this.segments = p_90264_;
      }

      public String toString() {
         return "Line{segments=" + this.segments + "}";
      }

      public boolean equals(Object p_90266_) {
         if (this == p_90266_) {
            return true;
         } else if (p_90266_ != null && this.getClass() == p_90266_.getClass()) {
            TextRenderingUtils.Line textrenderingutils$line = (TextRenderingUtils.Line)p_90266_;
            return Objects.equals(this.segments, textrenderingutils$line.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.segments);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineSegment {
      private final String fullText;
      @Nullable
      private final String linkTitle;
      @Nullable
      private final String linkUrl;

      private LineSegment(String p_90273_) {
         this.fullText = p_90273_;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(String p_90275_, @Nullable String p_90276_, @Nullable String p_90277_) {
         this.fullText = p_90275_;
         this.linkTitle = p_90276_;
         this.linkUrl = p_90277_;
      }

      public boolean equals(Object p_90287_) {
         if (this == p_90287_) {
            return true;
         } else if (p_90287_ != null && this.getClass() == p_90287_.getClass()) {
            TextRenderingUtils.LineSegment textrenderingutils$linesegment = (TextRenderingUtils.LineSegment)p_90287_;
            return Objects.equals(this.fullText, textrenderingutils$linesegment.fullText) && Objects.equals(this.linkTitle, textrenderingutils$linesegment.linkTitle) && Objects.equals(this.linkUrl, textrenderingutils$linesegment.linkUrl);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.fullText, this.linkTitle, this.linkUrl);
      }

      public String toString() {
         return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
      }

      public String renderedText() {
         return this.isLink() ? this.linkTitle : this.fullText;
      }

      public boolean isLink() {
         return this.linkTitle != null;
      }

      public String getLinkUrl() {
         if (!this.isLink()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.linkUrl;
         }
      }

      public static TextRenderingUtils.LineSegment link(String p_90282_, String p_90283_) {
         return new TextRenderingUtils.LineSegment((String)null, p_90282_, p_90283_);
      }

      @VisibleForTesting
      protected static TextRenderingUtils.LineSegment text(String p_90280_) {
         return new TextRenderingUtils.LineSegment(p_90280_);
      }
   }
}