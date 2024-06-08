package net.minecraft.util;

import javax.annotation.Nullable;

public class ExceptionCollector<T extends Throwable> {
   @Nullable
   private T result;

   public void add(T p_13654_) {
      if (this.result == null) {
         this.result = p_13654_;
      } else {
         this.result.addSuppressed(p_13654_);
      }

   }

   public void throwIfPresent() throws T {
      if (this.result != null) {
         throw this.result;
      }
   }
}