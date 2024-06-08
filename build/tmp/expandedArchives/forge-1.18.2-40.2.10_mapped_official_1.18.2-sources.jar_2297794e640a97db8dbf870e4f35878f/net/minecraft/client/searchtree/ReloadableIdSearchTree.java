package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReloadableIdSearchTree<T> implements MutableSearchTree<T> {
   protected SuffixArray<T> namespaceTree = new SuffixArray<>();
   protected SuffixArray<T> pathTree = new SuffixArray<>();
   private final Function<T, Stream<ResourceLocation>> idGetter;
   private final List<T> contents = Lists.newArrayList();
   private final Object2IntMap<T> orderT = new Object2IntOpenHashMap<>();

   public ReloadableIdSearchTree(Function<T, Stream<ResourceLocation>> p_119876_) {
      this.idGetter = p_119876_;
   }

   public void refresh() {
      this.namespaceTree = new SuffixArray<>();
      this.pathTree = new SuffixArray<>();

      for(T t : this.contents) {
         this.index(t);
      }

      this.namespaceTree.generate();
      this.pathTree.generate();
   }

   public void add(T p_119879_) {
      this.orderT.put(p_119879_, this.contents.size());
      this.contents.add(p_119879_);
      this.index(p_119879_);
   }

   public void clear() {
      this.contents.clear();
      this.orderT.clear();
   }

   protected void index(T p_119889_) {
      this.idGetter.apply(p_119889_).forEach((p_119885_) -> {
         this.namespaceTree.add(p_119889_, p_119885_.getNamespace().toLowerCase(Locale.ROOT));
         this.pathTree.add(p_119889_, p_119885_.getPath().toLowerCase(Locale.ROOT));
      });
   }

   protected int comparePosition(T p_119881_, T p_119882_) {
      return Integer.compare(this.orderT.getInt(p_119881_), this.orderT.getInt(p_119882_));
   }

   public List<T> search(String p_119887_) {
      int i = p_119887_.indexOf(58);
      if (i == -1) {
         return this.pathTree.search(p_119887_);
      } else {
         List<T> list = this.namespaceTree.search(p_119887_.substring(0, i).trim());
         String s = p_119887_.substring(i + 1).trim();
         List<T> list1 = this.pathTree.search(s);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator<>(list.iterator(), list1.iterator(), this::comparePosition));
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected static class IntersectionIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> firstIterator;
      private final PeekingIterator<T> secondIterator;
      private final Comparator<T> orderT;

      public IntersectionIterator(Iterator<T> p_119894_, Iterator<T> p_119895_, Comparator<T> p_119896_) {
         this.firstIterator = Iterators.peekingIterator(p_119894_);
         this.secondIterator = Iterators.peekingIterator(p_119895_);
         this.orderT = p_119896_;
      }

      protected T computeNext() {
         while(this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
            int i = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (i == 0) {
               this.secondIterator.next();
               return this.firstIterator.next();
            }

            if (i < 0) {
               this.firstIterator.next();
            } else {
               this.secondIterator.next();
            }
         }

         return this.endOfData();
      }
   }
}