package net.minecraft.util;

import javax.annotation.Nullable;

public class MemoryReserve {
   @Nullable
   private static byte[] reserve = null;

   public static void allocate() {
      reserve = new byte[10485760];
   }

   public static void release() {
      reserve = new byte[0];
   }
}