package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;

public class MineshaftConfiguration implements FeatureConfiguration {
   public static final Codec<MineshaftConfiguration> CODEC = RecordCodecBuilder.create((p_67790_) -> {
      return p_67790_.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((p_160992_) -> {
         return p_160992_.probability;
      }), MineshaftFeature.Type.CODEC.fieldOf("type").forGetter((p_160990_) -> {
         return p_160990_.type;
      })).apply(p_67790_, MineshaftConfiguration::new);
   });
   public final float probability;
   public final MineshaftFeature.Type type;

   public MineshaftConfiguration(float p_67785_, MineshaftFeature.Type p_67786_) {
      this.probability = p_67785_;
      this.type = p_67786_;
   }
}