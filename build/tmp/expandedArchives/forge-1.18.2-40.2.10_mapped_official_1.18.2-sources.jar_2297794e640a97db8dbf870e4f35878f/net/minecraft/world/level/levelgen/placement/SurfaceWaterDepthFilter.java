package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

public class SurfaceWaterDepthFilter extends PlacementFilter {
   public static final Codec<SurfaceWaterDepthFilter> CODEC = RecordCodecBuilder.create((p_191953_) -> {
      return p_191953_.group(Codec.INT.fieldOf("max_water_depth").forGetter((p_191959_) -> {
         return p_191959_.maxWaterDepth;
      })).apply(p_191953_, SurfaceWaterDepthFilter::new);
   });
   private final int maxWaterDepth;

   private SurfaceWaterDepthFilter(int p_191949_) {
      this.maxWaterDepth = p_191949_;
   }

   public static SurfaceWaterDepthFilter forMaxDepth(int p_191951_) {
      return new SurfaceWaterDepthFilter(p_191951_);
   }

   protected boolean shouldPlace(PlacementContext p_191955_, Random p_191956_, BlockPos p_191957_) {
      int i = p_191955_.getHeight(Heightmap.Types.OCEAN_FLOOR, p_191957_.getX(), p_191957_.getZ());
      int j = p_191955_.getHeight(Heightmap.Types.WORLD_SURFACE, p_191957_.getX(), p_191957_.getZ());
      return j - i <= this.maxWaterDepth;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
   }
}