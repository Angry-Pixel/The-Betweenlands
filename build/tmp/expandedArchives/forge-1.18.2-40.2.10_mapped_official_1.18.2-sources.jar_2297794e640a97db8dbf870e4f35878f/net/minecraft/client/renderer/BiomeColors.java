package net.minecraft.client.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
   public static final ColorResolver FOLIAGE_COLOR_RESOLVER = (p_108808_, p_108809_, p_108810_) -> {
      return p_108808_.getFoliageColor();
   };
   public static final ColorResolver WATER_COLOR_RESOLVER = (p_108801_, p_108802_, p_108803_) -> {
      return p_108801_.getWaterColor();
   };

   private static int getAverageColor(BlockAndTintGetter p_108797_, BlockPos p_108798_, ColorResolver p_108799_) {
      return p_108797_.getBlockTint(p_108798_, p_108799_);
   }

   public static int getAverageGrassColor(BlockAndTintGetter p_108794_, BlockPos p_108795_) {
      return getAverageColor(p_108794_, p_108795_, GRASS_COLOR_RESOLVER);
   }

   public static int getAverageFoliageColor(BlockAndTintGetter p_108805_, BlockPos p_108806_) {
      return getAverageColor(p_108805_, p_108806_, FOLIAGE_COLOR_RESOLVER);
   }

   public static int getAverageWaterColor(BlockAndTintGetter p_108812_, BlockPos p_108813_) {
      return getAverageColor(p_108812_, p_108813_, WATER_COLOR_RESOLVER);
   }
}