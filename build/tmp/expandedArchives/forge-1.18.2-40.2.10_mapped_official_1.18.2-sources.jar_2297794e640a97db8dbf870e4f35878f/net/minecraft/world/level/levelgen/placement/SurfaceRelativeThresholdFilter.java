package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

public class SurfaceRelativeThresholdFilter extends PlacementFilter {
   public static final Codec<SurfaceRelativeThresholdFilter> CODEC = RecordCodecBuilder.create((p_191929_) -> {
      return p_191929_.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((p_191944_) -> {
         return p_191944_.heightmap;
      }), Codec.INT.optionalFieldOf("min_inclusive", Integer.valueOf(Integer.MIN_VALUE)).forGetter((p_191942_) -> {
         return p_191942_.minInclusive;
      }), Codec.INT.optionalFieldOf("max_inclusive", Integer.valueOf(Integer.MAX_VALUE)).forGetter((p_191939_) -> {
         return p_191939_.maxInclusive;
      })).apply(p_191929_, SurfaceRelativeThresholdFilter::new);
   });
   private final Heightmap.Types heightmap;
   private final int minInclusive;
   private final int maxInclusive;

   private SurfaceRelativeThresholdFilter(Heightmap.Types p_191925_, int p_191926_, int p_191927_) {
      this.heightmap = p_191925_;
      this.minInclusive = p_191926_;
      this.maxInclusive = p_191927_;
   }

   public static SurfaceRelativeThresholdFilter of(Heightmap.Types p_191931_, int p_191932_, int p_191933_) {
      return new SurfaceRelativeThresholdFilter(p_191931_, p_191932_, p_191933_);
   }

   protected boolean shouldPlace(PlacementContext p_191935_, Random p_191936_, BlockPos p_191937_) {
      long i = (long)p_191935_.getHeight(this.heightmap, p_191937_.getX(), p_191937_.getZ());
      long j = i + (long)this.minInclusive;
      long k = i + (long)this.maxInclusive;
      return j <= (long)p_191937_.getY() && (long)p_191937_.getY() <= k;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
   }
}