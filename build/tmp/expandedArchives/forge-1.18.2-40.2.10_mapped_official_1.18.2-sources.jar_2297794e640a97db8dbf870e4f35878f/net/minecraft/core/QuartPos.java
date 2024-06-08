package net.minecraft.core;

public final class QuartPos {
   public static final int BITS = 2;
   public static final int SIZE = 4;
   public static final int MASK = 3;
   private static final int SECTION_TO_QUARTS_BITS = 2;

   private QuartPos() {
   }

   public static int fromBlock(int p_175401_) {
      return p_175401_ >> 2;
   }

   public static int quartLocal(int p_198377_) {
      return p_198377_ & 3;
   }

   public static int toBlock(int p_175403_) {
      return p_175403_ << 2;
   }

   public static int fromSection(int p_175405_) {
      return p_175405_ << 2;
   }

   public static int toSection(int p_175407_) {
      return p_175407_ >> 2;
   }
}