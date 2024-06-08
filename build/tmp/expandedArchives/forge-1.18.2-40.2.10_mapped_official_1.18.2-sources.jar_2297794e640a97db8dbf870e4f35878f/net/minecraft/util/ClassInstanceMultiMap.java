package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(Class<T> p_13531_) {
      this.baseClass = p_13531_;
      this.byClass.put(p_13531_, this.allInstances);
   }

   public boolean add(T p_13536_) {
      boolean flag = false;

      for(Entry<Class<?>, List<T>> entry : this.byClass.entrySet()) {
         if (entry.getKey().isInstance(p_13536_)) {
            flag |= entry.getValue().add(p_13536_);
         }
      }

      return flag;
   }

   public boolean remove(Object p_13543_) {
      boolean flag = false;

      for(Entry<Class<?>, List<T>> entry : this.byClass.entrySet()) {
         if (entry.getKey().isInstance(p_13543_)) {
            List<T> list = entry.getValue();
            flag |= list.remove(p_13543_);
         }
      }

      return flag;
   }

   public boolean contains(Object p_13540_) {
      return this.find(p_13540_.getClass()).contains(p_13540_);
   }

   public <S> Collection<S> find(Class<S> p_13534_) {
      if (!this.baseClass.isAssignableFrom(p_13534_)) {
         throw new IllegalArgumentException("Don't know how to search for " + p_13534_);
      } else {
         List<? extends T> list = this.byClass.computeIfAbsent(p_13534_, (p_13538_) -> {
            return this.allInstances.stream().filter(p_13538_::isInstance).collect(Collectors.toList());
         });
         return (Collection<S>)Collections.unmodifiableCollection(list);
      }
   }

   public Iterator<T> iterator() {
      return (Iterator<T>)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List<T> getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   public int size() {
      return this.allInstances.size();
   }
}