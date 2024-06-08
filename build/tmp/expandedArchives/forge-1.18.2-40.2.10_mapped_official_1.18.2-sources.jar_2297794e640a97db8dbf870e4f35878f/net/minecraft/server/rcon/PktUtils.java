package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class PktUtils {
   public static final int MAX_PACKET_SIZE = 1460;
   public static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String stringFromByteArray(byte[] p_11489_, int p_11490_, int p_11491_) {
      int i = p_11491_ - 1;

      int j;
      for(j = p_11490_ > i ? i : p_11490_; 0 != p_11489_[j] && j < i; ++j) {
      }

      return new String(p_11489_, p_11490_, j - p_11490_, StandardCharsets.UTF_8);
   }

   public static int intFromByteArray(byte[] p_11486_, int p_11487_) {
      return intFromByteArray(p_11486_, p_11487_, p_11486_.length);
   }

   public static int intFromByteArray(byte[] p_11493_, int p_11494_, int p_11495_) {
      return 0 > p_11495_ - p_11494_ - 4 ? 0 : p_11493_[p_11494_ + 3] << 24 | (p_11493_[p_11494_ + 2] & 255) << 16 | (p_11493_[p_11494_ + 1] & 255) << 8 | p_11493_[p_11494_] & 255;
   }

   public static int intFromNetworkByteArray(byte[] p_11497_, int p_11498_, int p_11499_) {
      return 0 > p_11499_ - p_11498_ - 4 ? 0 : p_11497_[p_11498_] << 24 | (p_11497_[p_11498_ + 1] & 255) << 16 | (p_11497_[p_11498_ + 2] & 255) << 8 | p_11497_[p_11498_ + 3] & 255;
   }

   public static String toHexString(byte p_11484_) {
      return "" + HEX_CHAR[(p_11484_ & 240) >>> 4] + HEX_CHAR[p_11484_ & 15];
   }
}