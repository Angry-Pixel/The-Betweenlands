package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class NoiseBasedCountPlacement extends RepeatingPlacement {
   public static final Codec<NoiseBasedCountPlacement> CODEC = RecordCodecBuilder.create((p_191736_) -> {
      return p_191736_.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((p_191746_) -> {
         return p_191746_.noiseToCountRatio;
      }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((p_191744_) -> {
         return p_191744_.noiseFactor;
      }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((p_191738_) -> {
         return p_191738_.noiseOffset;
      })).apply(p_191736_, NoiseBasedCountPlacement::new);
   });
   private final int noiseToCountRatio;
   private final double noiseFactor;
   private final double noiseOffset;

   private NoiseBasedCountPlacement(int p_191728_, double p_191729_, double p_191730_) {
      this.noiseToCountRatio = p_191728_;
      this.noiseFactor = p_191729_;
      this.noiseOffset = p_191730_;
   }

   public static NoiseBasedCountPlacement of(int p_191732_, double p_191733_, double p_191734_) {
      return new NoiseBasedCountPlacement(p_191732_, p_191733_, p_191734_);
   }

   protected int count(Random p_191740_, BlockPos p_191741_) {
      double d0 = Biome.BIOME_INFO_NOISE.getValue((double)p_191741_.getX() / this.noiseFactor, (double)p_191741_.getZ() / this.noiseFactor, false);
      return (int)Math.ceil((d0 + this.noiseOffset) * (double)this.noiseToCountRatio);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.NOISE_BASED_COUNT;
   }
}