package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.ExtraCodecs;

public class SimpleWeightedRandomList<E> extends WeightedRandomList<WeightedEntry.Wrapper<E>> {
   public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodecAllowingEmpty(Codec<E> p_185861_) {
      return WeightedEntry.Wrapper.<E>codec(p_185861_).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
   }

   public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodec(Codec<E> p_146265_) {
      return ExtraCodecs.nonEmptyList(WeightedEntry.Wrapper.<E>codec(p_146265_).listOf()).xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
   }

   SimpleWeightedRandomList(List<? extends WeightedEntry.Wrapper<E>> p_146262_) {
      super(p_146262_);
   }

   public static <E> SimpleWeightedRandomList.Builder<E> builder() {
      return new SimpleWeightedRandomList.Builder<>();
   }

   public static <E> SimpleWeightedRandomList<E> empty() {
      return new SimpleWeightedRandomList<>(List.of());
   }

   public static <E> SimpleWeightedRandomList<E> single(E p_185863_) {
      return new SimpleWeightedRandomList<>(List.of(WeightedEntry.wrap(p_185863_, 1)));
   }

   public Optional<E> getRandomValue(Random p_146267_) {
      return this.getRandom(p_146267_).map(WeightedEntry.Wrapper::getData);
   }

   public static class Builder<E> {
      private final ImmutableList.Builder<WeightedEntry.Wrapper<E>> result = ImmutableList.builder();

      public SimpleWeightedRandomList.Builder<E> add(E p_146272_, int p_146273_) {
         this.result.add(WeightedEntry.wrap(p_146272_, p_146273_));
         return this;
      }

      public SimpleWeightedRandomList<E> build() {
         return new SimpleWeightedRandomList<>(this.result.build());
      }
   }
}