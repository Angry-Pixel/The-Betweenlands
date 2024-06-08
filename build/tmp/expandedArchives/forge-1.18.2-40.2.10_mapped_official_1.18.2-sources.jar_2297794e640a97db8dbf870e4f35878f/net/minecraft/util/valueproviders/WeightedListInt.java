package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

public class WeightedListInt extends IntProvider {
   public static final Codec<WeightedListInt> CODEC = RecordCodecBuilder.create((p_185920_) -> {
      return p_185920_.group(SimpleWeightedRandomList.wrappedCodec(IntProvider.CODEC).fieldOf("distribution").forGetter((p_185918_) -> {
         return p_185918_.distribution;
      })).apply(p_185920_, WeightedListInt::new);
   });
   private final SimpleWeightedRandomList<IntProvider> distribution;
   private final int minValue;
   private final int maxValue;

   public WeightedListInt(SimpleWeightedRandomList<IntProvider> p_185915_) {
      this.distribution = p_185915_;
      List<WeightedEntry.Wrapper<IntProvider>> list = p_185915_.unwrap();
      int i = Integer.MAX_VALUE;
      int j = Integer.MIN_VALUE;

      for(WeightedEntry.Wrapper<IntProvider> wrapper : list) {
         int k = wrapper.getData().getMinValue();
         int l = wrapper.getData().getMaxValue();
         i = Math.min(i, k);
         j = Math.max(j, l);
      }

      this.minValue = i;
      this.maxValue = j;
   }

   public int sample(Random p_185922_) {
      return this.distribution.getRandomValue(p_185922_).orElseThrow(IllegalStateException::new).sample(p_185922_);
   }

   public int getMinValue() {
      return this.minValue;
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.WEIGHTED_LIST;
   }
}