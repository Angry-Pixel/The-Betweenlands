package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph {
   private Graph() {
   }

   public static <T> boolean depthFirstSearch(Map<T, Set<T>> p_184557_, Set<T> p_184558_, Set<T> p_184559_, Consumer<T> p_184560_, T p_184561_) {
      if (p_184558_.contains(p_184561_)) {
         return false;
      } else if (p_184559_.contains(p_184561_)) {
         return true;
      } else {
         p_184559_.add(p_184561_);

         for(T t : p_184557_.getOrDefault(p_184561_, ImmutableSet.of())) {
            if (depthFirstSearch(p_184557_, p_184558_, p_184559_, p_184560_, t)) {
               return true;
            }
         }

         p_184559_.remove(p_184561_);
         p_184558_.add(p_184561_);
         p_184560_.accept(p_184561_);
         return false;
      }
   }
}