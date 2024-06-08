package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class RangeConfiguration implements FeatureConfiguration {
   public static final Codec<RangeConfiguration> CODEC = RecordCodecBuilder.create((p_191326_) -> {
      return p_191326_.group(HeightProvider.CODEC.fieldOf("height").forGetter((p_191328_) -> {
         return p_191328_.height;
      })).apply(p_191326_, RangeConfiguration::new);
   });
   public final HeightProvider height;

   public RangeConfiguration(HeightProvider p_191324_) {
      this.height = p_191324_;
   }
}