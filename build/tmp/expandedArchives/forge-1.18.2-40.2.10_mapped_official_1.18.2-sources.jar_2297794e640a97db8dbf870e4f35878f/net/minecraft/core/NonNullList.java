package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E> extends AbstractList<E> {
   private final List<E> list;
   @Nullable
   private final E defaultValue;

   public static <E> NonNullList<E> create() {
      return new NonNullList<>(Lists.newArrayList(), (E)null);
   }

   public static <E> NonNullList<E> createWithCapacity(int p_182648_) {
      return new NonNullList<>(Lists.newArrayListWithCapacity(p_182648_), (E)null);
   }

   public static <E> NonNullList<E> withSize(int p_122781_, E p_122782_) {
      Validate.notNull(p_122782_);
      Object[] aobject = new Object[p_122781_];
      Arrays.fill(aobject, p_122782_);
      return new NonNullList<>(Arrays.asList((E[])aobject), p_122782_);
   }

   @SafeVarargs
   public static <E> NonNullList<E> of(E p_122784_, E... p_122785_) {
      return new NonNullList<>(Arrays.asList(p_122785_), p_122784_);
   }

   protected NonNullList(List<E> p_122777_, @Nullable E p_122778_) {
      this.list = p_122777_;
      this.defaultValue = p_122778_;
   }

   @Nonnull
   public E get(int p_122791_) {
      return this.list.get(p_122791_);
   }

   public E set(int p_122795_, E p_122796_) {
      Validate.notNull(p_122796_);
      return this.list.set(p_122795_, p_122796_);
   }

   public void add(int p_122787_, E p_122788_) {
      Validate.notNull(p_122788_);
      this.list.add(p_122787_, p_122788_);
   }

   public E remove(int p_122793_) {
      return this.list.remove(p_122793_);
   }

   public int size() {
      return this.list.size();
   }

   public void clear() {
      if (this.defaultValue == null) {
         super.clear();
      } else {
         for(int i = 0; i < this.size(); ++i) {
            this.set(i, this.defaultValue);
         }
      }

   }
}