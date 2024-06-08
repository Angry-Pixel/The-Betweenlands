package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface WeightedEntry {
   Weight getWeight();

   static <T> WeightedEntry.Wrapper<T> wrap(T p_146291_, int p_146292_) {
      return new WeightedEntry.Wrapper<>(p_146291_, Weight.of(p_146292_));
   }

   public static class IntrusiveBase implements WeightedEntry {
      private final Weight weight;

      public IntrusiveBase(int p_146295_) {
         this.weight = Weight.of(p_146295_);
      }

      public IntrusiveBase(Weight p_146297_) {
         this.weight = p_146297_;
      }

      public Weight getWeight() {
         return this.weight;
      }
   }

   public static class Wrapper<T> implements WeightedEntry {
      private final T data;
      private final Weight weight;

      Wrapper(T p_146302_, Weight p_146303_) {
         this.data = p_146302_;
         this.weight = p_146303_;
      }

      public T getData() {
         return this.data;
      }

      public Weight getWeight() {
         return this.weight;
      }

      public static <E> Codec<WeightedEntry.Wrapper<E>> codec(Codec<E> p_146306_) {
         return RecordCodecBuilder.create((p_146309_) -> {
            return p_146309_.group(p_146306_.fieldOf("data").forGetter(WeightedEntry.Wrapper::getData), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.Wrapper::getWeight)).apply(p_146309_, WeightedEntry.Wrapper::new);
         });
      }
   }
}