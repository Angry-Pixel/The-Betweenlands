package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WeightedPlacedFeature {
   public static final Codec<WeightedPlacedFeature> CODEC = RecordCodecBuilder.create((p_191187_) -> {
      return p_191187_.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((p_204789_) -> {
         return p_204789_.feature;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((p_191189_) -> {
         return p_191189_.chance;
      })).apply(p_191187_, WeightedPlacedFeature::new);
   });
   public final Holder<PlacedFeature> feature;
   public final float chance;

   public WeightedPlacedFeature(Holder<PlacedFeature> p_204786_, float p_204787_) {
      this.feature = p_204786_;
      this.chance = p_204787_;
   }

   public boolean place(WorldGenLevel p_191182_, ChunkGenerator p_191183_, Random p_191184_, BlockPos p_191185_) {
      return this.feature.value().place(p_191182_, p_191183_, p_191184_, p_191185_);
   }
}