package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class SurfaceRuleData {
   private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
   private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
   private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
   private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
   private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
   private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
   private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
   private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
   private static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
   private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
   private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
   private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
   private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
   private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
   private static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
   private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
   private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
   private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
   private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
   private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
   private static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
   private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
   private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);
   private static final SurfaceRules.RuleSource LAVA = makeStateRule(Blocks.LAVA);
   private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
   private static final SurfaceRules.RuleSource SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
   private static final SurfaceRules.RuleSource SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
   private static final SurfaceRules.RuleSource BASALT = makeStateRule(Blocks.BASALT);
   private static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
   private static final SurfaceRules.RuleSource WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
   private static final SurfaceRules.RuleSource WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
   private static final SurfaceRules.RuleSource NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
   private static final SurfaceRules.RuleSource CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
   private static final SurfaceRules.RuleSource ENDSTONE = makeStateRule(Blocks.END_STONE);

   private static SurfaceRules.RuleSource makeStateRule(Block p_194811_) {
      return SurfaceRules.state(p_194811_.defaultBlockState());
   }

   public static SurfaceRules.RuleSource overworld() {
      return overworldLike(true, false, true);
   }

   public static SurfaceRules.RuleSource overworldLike(boolean p_198381_, boolean p_198382_, boolean p_198383_) {
      SurfaceRules.ConditionSource surfacerules$conditionsource = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
      SurfaceRules.ConditionSource surfacerules$conditionsource1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
      SurfaceRules.ConditionSource surfacerules$conditionsource3 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
      SurfaceRules.ConditionSource surfacerules$conditionsource4 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource5 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource6 = SurfaceRules.waterBlockCheck(-1, 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource7 = SurfaceRules.waterBlockCheck(0, 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource8 = SurfaceRules.waterStartCheck(-6, -1);
      SurfaceRules.ConditionSource surfacerules$conditionsource9 = SurfaceRules.hole();
      SurfaceRules.ConditionSource surfacerules$conditionsource10 = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
      SurfaceRules.ConditionSource surfacerules$conditionsource11 = SurfaceRules.steep();
      SurfaceRules.RuleSource surfacerules$rulesource = SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource7, GRASS_BLOCK), DIRT);
      SurfaceRules.RuleSource surfacerules$rulesource1 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
      SurfaceRules.RuleSource surfacerules$rulesource2 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
      SurfaceRules.ConditionSource surfacerules$conditionsource12 = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
      SurfaceRules.ConditionSource surfacerules$conditionsource13 = SurfaceRules.isBiome(Biomes.DESERT);
      SurfaceRules.RuleSource surfacerules$rulesource3 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125D, 0.0125D), CALCITE), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05D, 0.05D), surfacerules$rulesource2), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE)), SurfaceRules.ifTrue(surfacerules$conditionsource12, surfacerules$rulesource1), SurfaceRules.ifTrue(surfacerules$conditionsource13, surfacerules$rulesource1), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE));
      SurfaceRules.RuleSource surfacerules$rulesource4 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45D, 0.58D), SurfaceRules.ifTrue(surfacerules$conditionsource7, POWDER_SNOW));
      SurfaceRules.RuleSource surfacerules$rulesource5 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35D, 0.6D), SurfaceRules.ifTrue(surfacerules$conditionsource7, POWDER_SNOW));
      SurfaceRules.RuleSource surfacerules$rulesource6 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5D, 0.2D), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625D, 0.025D), ICE), SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, STONE), surfacerules$rulesource4, SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(surfacerules$rulesource4, DIRT)), surfacerules$rulesource3, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), surfacerules$rulesource2), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), DIRT), surfacerules$rulesource2)), DIRT);
      SurfaceRules.RuleSource surfacerules$rulesource7 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0D, 0.2D), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0D, 0.025D), ICE), SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, STONE), surfacerules$rulesource5, SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, STONE), SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(surfacerules$rulesource5, SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK))), surfacerules$rulesource3, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5D), COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), surfacerules$rulesource2), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), surfacerules$rulesource), surfacerules$rulesource2)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95D), PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(surfacerules$conditionsource7, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM), surfacerules$rulesource);
      SurfaceRules.ConditionSource surfacerules$conditionsource14 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909D, -0.5454D);
      SurfaceRules.ConditionSource surfacerules$conditionsource15 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818D, 0.1818D);
      SurfaceRules.ConditionSource surfacerules$conditionsource16 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454D, 0.909D);
      SurfaceRules.RuleSource surfacerules$rulesource8 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(surfacerules$conditionsource, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource14, COARSE_DIRT), SurfaceRules.ifTrue(surfacerules$conditionsource15, COARSE_DIRT), SurfaceRules.ifTrue(surfacerules$conditionsource16, COARSE_DIRT), surfacerules$rulesource))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(surfacerules$conditionsource4, SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource5), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0D), WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource1, ORANGE_TERRACOTTA), SurfaceRules.ifTrue(surfacerules$conditionsource3, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource14, TERRACOTTA), SurfaceRules.ifTrue(surfacerules$conditionsource15, TERRACOTTA), SurfaceRules.ifTrue(surfacerules$conditionsource16, TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(surfacerules$conditionsource6, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource9), ORANGE_TERRACOTTA), SurfaceRules.ifTrue(surfacerules$conditionsource8, WHITE_TERRACOTTA), surfacerules$rulesource2)), SurfaceRules.ifTrue(surfacerules$conditionsource2, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource5, SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource3), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource8, WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource6, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource10, SurfaceRules.ifTrue(surfacerules$conditionsource9, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource7, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER))), surfacerules$rulesource7))), SurfaceRules.ifTrue(surfacerules$conditionsource8, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource10, SurfaceRules.ifTrue(surfacerules$conditionsource9, WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, surfacerules$rulesource6), SurfaceRules.ifTrue(surfacerules$conditionsource12, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)), SurfaceRules.ifTrue(surfacerules$conditionsource13, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), surfacerules$rulesource1), surfacerules$rulesource2)));
      Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
      if (p_198382_) {
         builder.add(SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
      }

      if (p_198383_) {
         builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
      }

      SurfaceRules.RuleSource surfacerules$rulesource9 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), surfacerules$rulesource8);
      builder.add(p_198381_ ? surfacerules$rulesource9 : surfacerules$rulesource8);
      builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
      return SurfaceRules.sequence(builder.build().toArray((p_198379_) -> {
         return new SurfaceRules.RuleSource[p_198379_];
      }));
   }

   public static SurfaceRules.RuleSource nether() {
      SurfaceRules.ConditionSource surfacerules$conditionsource = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource3 = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
      SurfaceRules.ConditionSource surfacerules$conditionsource4 = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
      SurfaceRules.ConditionSource surfacerules$conditionsource5 = SurfaceRules.hole();
      SurfaceRules.ConditionSource surfacerules$conditionsource6 = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012D);
      SurfaceRules.ConditionSource surfacerules$conditionsource7 = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012D);
      SurfaceRules.ConditionSource surfacerules$conditionsource8 = SurfaceRules.noiseCondition(Noises.PATCH, -0.012D);
      SurfaceRules.ConditionSource surfacerules$conditionsource9 = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54D);
      SurfaceRules.ConditionSource surfacerules$conditionsource10 = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17D);
      SurfaceRules.ConditionSource surfacerules$conditionsource11 = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0D);
      SurfaceRules.RuleSource surfacerules$rulesource = SurfaceRules.ifTrue(surfacerules$conditionsource8, SurfaceRules.ifTrue(surfacerules$conditionsource2, SurfaceRules.ifTrue(surfacerules$conditionsource3, GRAVEL)));
      return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK), SurfaceRules.ifTrue(surfacerules$conditionsource4, NETHERRACK), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BASALT_DELTAS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, BASALT), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(surfacerules$rulesource, SurfaceRules.ifTrue(surfacerules$conditionsource11, BASALT), BLACKSTONE)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SOUL_SAND_VALLEY), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource11, SOUL_SAND), SOUL_SOIL)), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(surfacerules$rulesource, SurfaceRules.ifTrue(surfacerules$conditionsource11, SOUL_SAND), SOUL_SOIL)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource1), SurfaceRules.ifTrue(surfacerules$conditionsource5, LAVA)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARPED_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource9), SurfaceRules.ifTrue(surfacerules$conditionsource, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource10, WARPED_WART_BLOCK), WARPED_NYLIUM)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.CRIMSON_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource9), SurfaceRules.ifTrue(surfacerules$conditionsource, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource10, NETHER_WART_BLOCK), CRIMSON_NYLIUM)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.NETHER_WASTES), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource6, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource5), SurfaceRules.ifTrue(surfacerules$conditionsource2, SurfaceRules.ifTrue(surfacerules$conditionsource3, SOUL_SAND))), NETHERRACK))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource, SurfaceRules.ifTrue(surfacerules$conditionsource3, SurfaceRules.ifTrue(surfacerules$conditionsource7, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource1, GRAVEL), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource5), GRAVEL)))))))), NETHERRACK);
   }

   public static SurfaceRules.RuleSource end() {
      return ENDSTONE;
   }

   private static SurfaceRules.ConditionSource surfaceNoiseAbove(double p_194809_) {
      return SurfaceRules.noiseCondition(Noises.SURFACE, p_194809_ / 8.25D, Double.MAX_VALUE);
   }
}