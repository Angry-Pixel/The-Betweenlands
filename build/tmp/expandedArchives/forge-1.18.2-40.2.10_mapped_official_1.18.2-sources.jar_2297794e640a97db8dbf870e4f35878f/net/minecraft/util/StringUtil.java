package net.minecraft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
   private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\v");
   private static final Pattern LINE_END_PATTERN = Pattern.compile("(?:\\r\\n|\\v)$");

   public static String formatTickDuration(int p_14405_) {
      int i = p_14405_ / 20;
      int j = i / 60;
      i %= 60;
      return i < 10 ? j + ":0" + i : j + ":" + i;
   }

   public static String stripColor(String p_14407_) {
      return STRIP_COLOR_PATTERN.matcher(p_14407_).replaceAll("");
   }

   public static boolean isNullOrEmpty(@Nullable String p_14409_) {
      return StringUtils.isEmpty(p_14409_);
   }

   public static String truncateStringIfNecessary(String p_144999_, int p_145000_, boolean p_145001_) {
      if (p_144999_.length() <= p_145000_) {
         return p_144999_;
      } else {
         return p_145001_ && p_145000_ > 3 ? p_144999_.substring(0, p_145000_ - 3) + "..." : p_144999_.substring(0, p_145000_);
      }
   }

   public static int lineCount(String p_145003_) {
      if (p_145003_.isEmpty()) {
         return 0;
      } else {
         Matcher matcher = LINE_PATTERN.matcher(p_145003_);

         int i;
         for(i = 1; matcher.find(); ++i) {
         }

         return i;
      }
   }

   public static boolean endsWithNewLine(String p_145005_) {
      return LINE_END_PATTERN.matcher(p_145005_).find();
   }
}