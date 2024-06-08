package net.minecraft.world;

import javax.annotation.Nullable;

public interface Clearable {
   void clearContent();

   static void tryClear(@Nullable Object p_18909_) {
      if (p_18909_ instanceof Clearable) {
         ((Clearable)p_18909_).clearContent();
      }

   }
}