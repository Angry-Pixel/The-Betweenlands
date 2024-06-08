package net.minecraft.util;

public class FastColor {
   public static class ARGB32 {
      public static int alpha(int p_13656_) {
         return p_13656_ >>> 24;
      }

      public static int red(int p_13666_) {
         return p_13666_ >> 16 & 255;
      }

      public static int green(int p_13668_) {
         return p_13668_ >> 8 & 255;
      }

      public static int blue(int p_13670_) {
         return p_13670_ & 255;
      }

      public static int color(int p_13661_, int p_13662_, int p_13663_, int p_13664_) {
         return p_13661_ << 24 | p_13662_ << 16 | p_13663_ << 8 | p_13664_;
      }

      public static int multiply(int p_13658_, int p_13659_) {
         return color(alpha(p_13658_) * alpha(p_13659_) / 255, red(p_13658_) * red(p_13659_) / 255, green(p_13658_) * green(p_13659_) / 255, blue(p_13658_) * blue(p_13659_) / 255);
      }
   }
}