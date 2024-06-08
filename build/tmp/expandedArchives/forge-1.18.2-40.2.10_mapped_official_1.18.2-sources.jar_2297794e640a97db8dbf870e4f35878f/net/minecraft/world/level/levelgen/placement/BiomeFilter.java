package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class BiomeFilter extends PlacementFilter {
   private static final BiomeFilter INSTANCE = new BiomeFilter();
   public static Codec<BiomeFilter> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });

   private BiomeFilter() {
   }

   public static BiomeFilter biome() {
      return INSTANCE;
   }

   protected boolean shouldPlace(PlacementContext p_191563_, Random p_191564_, BlockPos p_191565_) {
      PlacedFeature placedfeature = p_191563_.topFeature().orElseThrow(() -> {
         return new IllegalStateException("Tried to biome check an unregistered feature");
      });
      Biome biome = p_191563_.getLevel().getBiome(p_191565_).value();
      return biome.getGenerationSettings().hasFeature(placedfeature);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.BIOME_FILTER;
   }
}