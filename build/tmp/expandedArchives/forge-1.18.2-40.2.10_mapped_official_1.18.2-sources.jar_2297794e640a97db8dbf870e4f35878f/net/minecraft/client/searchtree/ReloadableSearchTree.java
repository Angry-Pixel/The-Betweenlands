package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
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
public class ReloadableSearchTree<T> extends ReloadableIdSearchTree<T> {
   protected SuffixArray<T> tree = new SuffixArray<>();
   private final Function<T, Stream<String>> filler;

   public ReloadableSearchTree(Function<T, Stream<String>> p_119923_, Function<T, Stream<ResourceLocation>> p_119924_) {
      super(p_119924_);
      this.filler = p_119923_;
   }

   public void refresh() {
      this.tree = new SuffixArray<>();
      super.refresh();
      this.tree.generate();
   }

   protected void index(T p_119932_) {
      super.index(p_119932_);
      this.filler.apply(p_119932_).forEach((p_119927_) -> {
         this.tree.add(p_119932_, p_119927_.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> search(String p_119929_) {
      int i = p_119929_.indexOf(58);
      if (i < 0) {
         return this.tree.search(p_119929_);
      } else {
         List<T> list = this.namespaceTree.search(p_119929_.substring(0, i).trim());
         String s = p_119929_.substring(i + 1).trim();
         List<T> list1 = this.pathTree.search(s);
         List<T> list2 = this.tree.search(s);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator<>(list.iterator(), new ReloadableSearchTree.MergingUniqueIterator<>(list1.iterator(), list2.iterator(), this::comparePosition), this::comparePosition));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingUniqueIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> firstIterator;
      private final PeekingIterator<T> secondIterator;
      private final Comparator<T> orderT;

      public MergingUniqueIterator(Iterator<T> p_119937_, Iterator<T> p_119938_, Comparator<T> p_119939_) {
         this.firstIterator = Iterators.peekingIterator(p_119937_);
         this.secondIterator = Iterators.peekingIterator(p_119938_);
         this.orderT = p_119939_;
      }

      protected T computeNext() {
         boolean flag = !this.firstIterator.hasNext();
         boolean flag1 = !this.secondIterator.hasNext();
         if (flag && flag1) {
            return this.endOfData();
         } else if (flag) {
            return this.secondIterator.next();
         } else if (flag1) {
            return this.firstIterator.next();
         } else {
            int i = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (i == 0) {
               this.secondIterator.next();
            }

            return (T)(i <= 0 ? this.firstIterator.next() : this.secondIterator.next());
         }
      }
   }
}