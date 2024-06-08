package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements FeatureConfiguration {
   public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0D, 1.0D);
   public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create((p_160842_) -> {
      return p_160842_.group(GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter((p_160868_) -> {
         return p_160868_.geodeBlockSettings;
      }), GeodeLayerSettings.CODEC.fieldOf("layers").forGetter((p_160866_) -> {
         return p_160866_.geodeLayerSettings;
      }), GeodeCrackSettings.CODEC.fieldOf("crack").forGetter((p_160864_) -> {
         return p_160864_.geodeCrackSettings;
      }), CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35D).forGetter((p_160862_) -> {
         return p_160862_.usePotentialPlacementsChance;
      }), CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0D).forGetter((p_160860_) -> {
         return p_160860_.useAlternateLayer0Chance;
      }), Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter((p_160858_) -> {
         return p_160858_.placementsRequireLayer0Alternate;
      }), IntProvider.codec(1, 20).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter((p_160856_) -> {
         return p_160856_.outerWallDistance;
      }), IntProvider.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter((p_160854_) -> {
         return p_160854_.distributionPoints;
      }), IntProvider.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter((p_160852_) -> {
         return p_160852_.pointOffset;
      }), Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter((p_160850_) -> {
         return p_160850_.minGenOffset;
      }), Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter((p_160848_) -> {
         return p_160848_.maxGenOffset;
      }), CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05D).forGetter((p_160846_) -> {
         return p_160846_.noiseMultiplier;
      }), Codec.INT.fieldOf("invalid_blocks_threshold").forGetter((p_160844_) -> {
         return p_160844_.invalidBlocksThreshold;
      })).apply(p_160842_, GeodeConfiguration::new);
   });
   public final GeodeBlockSettings geodeBlockSettings;
   public final GeodeLayerSettings geodeLayerSettings;
   public final GeodeCrackSettings geodeCrackSettings;
   public final double usePotentialPlacementsChance;
   public final double useAlternateLayer0Chance;
   public final boolean placementsRequireLayer0Alternate;
   public final IntProvider outerWallDistance;
   public final IntProvider distributionPoints;
   public final IntProvider pointOffset;
   public final int minGenOffset;
   public final int maxGenOffset;
   public final double noiseMultiplier;
   public final int invalidBlocksThreshold;

   public GeodeConfiguration(GeodeBlockSettings p_160828_, GeodeLayerSettings p_160829_, GeodeCrackSettings p_160830_, double p_160831_, double p_160832_, boolean p_160833_, IntProvider p_160834_, IntProvider p_160835_, IntProvider p_160836_, int p_160837_, int p_160838_, double p_160839_, int p_160840_) {
      this.geodeBlockSettings = p_160828_;
      this.geodeLayerSettings = p_160829_;
      this.geodeCrackSettings = p_160830_;
      this.usePotentialPlacementsChance = p_160831_;
      this.useAlternateLayer0Chance = p_160832_;
      this.placementsRequireLayer0Alternate = p_160833_;
      this.outerWallDistance = p_160834_;
      this.distributionPoints = p_160835_;
      this.pointOffset = p_160836_;
      this.minGenOffset = p_160837_;
      this.maxGenOffset = p_160838_;
      this.noiseMultiplier = p_160839_;
      this.invalidBlocksThreshold = p_160840_;
   }
}