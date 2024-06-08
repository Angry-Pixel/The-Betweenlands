package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class WeightedRandomList<E extends WeightedEntry> {
   private final int totalWeight;
   private final ImmutableList<E> items;

   WeightedRandomList(List<? extends E> p_146327_) {
      this.items = ImmutableList.copyOf(p_146327_);
      this.totalWeight = WeightedRandom.getTotalWeight(p_146327_);
   }

   public static <E extends WeightedEntry> WeightedRandomList<E> create() {
      return new WeightedRandomList<>(ImmutableList.of());
   }

   @SafeVarargs
   public static <E extends WeightedEntry> WeightedRandomList<E> create(E... p_146331_) {
      return new WeightedRandomList<>(ImmutableList.copyOf(p_146331_));
   }

   public static <E extends WeightedEntry> WeightedRandomList<E> create(List<E> p_146329_) {
      return new WeightedRandomList<>(p_146329_);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public Optional<E> getRandom(Random p_146336_) {
      if (this.totalWeight == 0) {
         return Optional.empty();
      } else {
         int i = p_146336_.nextInt(this.totalWeight);
         return WeightedRandom.getWeightedItem(this.items, i);
      }
   }

   public List<E> unwrap() {
      return this.items;
   }

   public static <E extends WeightedEntry> Codec<WeightedRandomList<E>> codec(Codec<E> p_146334_) {
      return p_146334_.listOf().xmap(WeightedRandomList::create, WeightedRandomList::unwrap);
   }
}