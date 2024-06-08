package net.minecraft.world.level.entity;

import javax.annotation.Nullable;

public interface EntityTypeTest<B, T extends B> {
   static <B, T extends B> EntityTypeTest<B, T> forClass(final Class<T> p_156917_) {
      return new EntityTypeTest<B, T>() {
         @Nullable
         public T tryCast(B p_156924_) {
            return (T)(p_156917_.isInstance(p_156924_) ? p_156924_ : null);
         }

         public Class<? extends B> getBaseClass() {
            return p_156917_;
         }
      };
   }

   @Nullable
   T tryCast(B p_156918_);

   Class<? extends B> getBaseClass();
}