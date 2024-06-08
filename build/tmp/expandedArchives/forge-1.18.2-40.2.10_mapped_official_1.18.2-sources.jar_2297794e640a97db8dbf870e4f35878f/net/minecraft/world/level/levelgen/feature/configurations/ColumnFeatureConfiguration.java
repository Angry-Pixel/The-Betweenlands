package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;

public class ColumnFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<ColumnFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_67563_) -> {
      return p_67563_.group(IntProvider.codec(0, 3).fieldOf("reach").forGetter((p_160722_) -> {
         return p_160722_.reach;
      }), IntProvider.codec(1, 10).fieldOf("height").forGetter((p_160719_) -> {
         return p_160719_.height;
      })).apply(p_67563_, ColumnFeatureConfiguration::new);
   });
   private final IntProvider reach;
   private final IntProvider height;

   public ColumnFeatureConfiguration(IntProvider p_160715_, IntProvider p_160716_) {
      this.reach = p_160715_;
      this.height = p_160716_;
   }

   public IntProvider reach() {
      return this.reach;
   }

   public IntProvider height() {
      return this.height;
   }
}