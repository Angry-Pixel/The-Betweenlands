package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration implements FeatureConfiguration {
   public static final Codec<OceanRuinConfiguration> CODEC = RecordCodecBuilder.create((p_67832_) -> {
      return p_67832_.group(OceanRuinFeature.Type.CODEC.fieldOf("biome_temp").forGetter((p_161004_) -> {
         return p_161004_.biomeTemp;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((p_161002_) -> {
         return p_161002_.largeProbability;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter((p_161000_) -> {
         return p_161000_.clusterProbability;
      })).apply(p_67832_, OceanRuinConfiguration::new);
   });
   public final OceanRuinFeature.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfiguration(OceanRuinFeature.Type p_67826_, float p_67827_, float p_67828_) {
      this.biomeTemp = p_67826_;
      this.largeProbability = p_67827_;
      this.clusterProbability = p_67828_;
   }
}