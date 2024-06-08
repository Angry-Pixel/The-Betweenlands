package net.minecraft.util.random;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.Util;

public class WeightedRandom {
   private WeightedRandom() {
   }

   public static int getTotalWeight(List<? extends WeightedEntry> p_146313_) {
      long i = 0L;

      for(WeightedEntry weightedentry : p_146313_) {
         i += (long)weightedentry.getWeight().asInt();
      }

      if (i > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)i;
      }
   }

   public static <T extends WeightedEntry> Optional<T> getRandomItem(Random p_146321_, List<T> p_146322_, int p_146323_) {
      if (p_146323_ < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
      } else if (p_146323_ == 0) {
         return Optional.empty();
      } else {
         int i = p_146321_.nextInt(p_146323_);
         return getWeightedItem(p_146322_, i);
      }
   }

   public static <T extends WeightedEntry> Optional<T> getWeightedItem(List<T> p_146315_, int p_146316_) {
      for(T t : p_146315_) {
         p_146316_ -= t.getWeight().asInt();
         if (p_146316_ < 0) {
            return Optional.of(t);
         }
      }

      return Optional.empty();
   }

   public static <T extends WeightedEntry> Optional<T> getRandomItem(Random p_146318_, List<T> p_146319_) {
      return getRandomItem(p_146318_, p_146319_, getTotalWeight(p_146319_));
   }
}