package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class LayerConfiguration implements FeatureConfiguration {
   public static final Codec<LayerConfiguration> CODEC = RecordCodecBuilder.create((p_67777_) -> {
      return p_67777_.group(Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter((p_160988_) -> {
         return p_160988_.height;
      }), BlockState.CODEC.fieldOf("state").forGetter((p_160986_) -> {
         return p_160986_.state;
      })).apply(p_67777_, LayerConfiguration::new);
   });
   public final int height;
   public final BlockState state;

   public LayerConfiguration(int p_67772_, BlockState p_67773_) {
      this.height = p_67772_;
      this.state = p_67773_;
   }
}