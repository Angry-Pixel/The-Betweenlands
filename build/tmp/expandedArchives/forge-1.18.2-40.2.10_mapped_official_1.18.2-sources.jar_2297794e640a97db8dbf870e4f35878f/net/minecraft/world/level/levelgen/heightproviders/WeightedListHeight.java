package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WeightedListHeight extends HeightProvider {
   public static final Codec<WeightedListHeight> CODEC = RecordCodecBuilder.create((p_191539_) -> {
      return p_191539_.group(SimpleWeightedRandomList.wrappedCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((p_191541_) -> {
         return p_191541_.distribution;
      })).apply(p_191539_, WeightedListHeight::new);
   });
   private final SimpleWeightedRandomList<HeightProvider> distribution;

   public WeightedListHeight(SimpleWeightedRandomList<HeightProvider> p_191536_) {
      this.distribution = p_191536_;
   }

   public int sample(Random p_191543_, WorldGenerationContext p_191544_) {
      return this.distribution.getRandomValue(p_191543_).orElseThrow(IllegalStateException::new).sample(p_191543_, p_191544_);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.WEIGHTED_LIST;
   }
}