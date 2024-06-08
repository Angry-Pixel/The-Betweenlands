package com.mojang.realmsclient;

import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Unit {
   B,
   KB,
   MB,
   GB;

   private static final int BASE_UNIT = 1024;

   public static Unit getLargest(long p_86941_) {
      if (p_86941_ < 1024L) {
         return B;
      } else {
         try {
            int i = (int)(Math.log((double)p_86941_) / Math.log(1024.0D));
            String s = String.valueOf("KMGTPE".charAt(i - 1));
            return valueOf(s + "B");
         } catch (Exception exception) {
            return GB;
         }
      }
   }

   public static double convertTo(long p_86943_, Unit p_86944_) {
      return p_86944_ == B ? (double)p_86943_ : (double)p_86943_ / Math.pow(1024.0D, (double)p_86944_.ordinal());
   }

   public static String humanReadable(long p_86946_) {
      int i = 1024;
      if (p_86946_ < 1024L) {
         return p_86946_ + " B";
      } else {
         int j = (int)(Math.log((double)p_86946_) / Math.log(1024.0D));
         String s = "" + "KMGTPE".charAt(j - 1);
         return String.format(Locale.ROOT, "%.1f %sB", (double)p_86946_ / Math.pow(1024.0D, (double)j), s);
      }
   }

   public static String humanReadable(long p_86948_, Unit p_86949_) {
      return String.format("%." + (p_86949_ == GB ? "1" : "0") + "f %s", convertTo(p_86948_, p_86949_), p_86949_.name());
   }
}