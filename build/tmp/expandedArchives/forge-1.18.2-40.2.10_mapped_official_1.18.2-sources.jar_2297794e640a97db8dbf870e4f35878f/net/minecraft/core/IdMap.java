package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
   int DEFAULT = -1;

   int getId(T p_122652_);

   @Nullable
   T byId(int p_122651_);

   default T byIdOrThrow(int p_200958_) {
      T t = this.byId(p_200958_);
      if (t == null) {
         throw new IllegalArgumentException("No value with id " + p_200958_);
      } else {
         return t;
      }
   }

   int size();
}