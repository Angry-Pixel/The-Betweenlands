package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class EnvironmentScanPlacement extends PlacementModifier {
   private final Direction directionOfSearch;
   private final BlockPredicate targetCondition;
   private final BlockPredicate allowedSearchCondition;
   private final int maxSteps;
   public static final Codec<EnvironmentScanPlacement> CODEC = RecordCodecBuilder.create((p_191650_) -> {
      return p_191650_.group(Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter((p_191672_) -> {
         return p_191672_.directionOfSearch;
      }), BlockPredicate.CODEC.fieldOf("target_condition").forGetter((p_191670_) -> {
         return p_191670_.targetCondition;
      }), BlockPredicate.CODEC.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue()).forGetter((p_191668_) -> {
         return p_191668_.allowedSearchCondition;
      }), Codec.intRange(1, 32).fieldOf("max_steps").forGetter((p_191652_) -> {
         return p_191652_.maxSteps;
      })).apply(p_191650_, EnvironmentScanPlacement::new);
   });

   private EnvironmentScanPlacement(Direction p_191645_, BlockPredicate p_191646_, BlockPredicate p_191647_, int p_191648_) {
      this.directionOfSearch = p_191645_;
      this.targetCondition = p_191646_;
      this.allowedSearchCondition = p_191647_;
      this.maxSteps = p_191648_;
   }

   public static EnvironmentScanPlacement scanningFor(Direction p_191658_, BlockPredicate p_191659_, BlockPredicate p_191660_, int p_191661_) {
      return new EnvironmentScanPlacement(p_191658_, p_191659_, p_191660_, p_191661_);
   }

   public static EnvironmentScanPlacement scanningFor(Direction p_191654_, BlockPredicate p_191655_, int p_191656_) {
      return scanningFor(p_191654_, p_191655_, BlockPredicate.alwaysTrue(), p_191656_);
   }

   public Stream<BlockPos> getPositions(PlacementContext p_191663_, Random p_191664_, BlockPos p_191665_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_191665_.mutable();
      WorldGenLevel worldgenlevel = p_191663_.getLevel();
      if (!this.allowedSearchCondition.test(worldgenlevel, blockpos$mutableblockpos)) {
         return Stream.of();
      } else {
         int i = 0;

         while(true) {
            if (i < this.maxSteps) {
               if (this.targetCondition.test(worldgenlevel, blockpos$mutableblockpos)) {
                  return Stream.of(blockpos$mutableblockpos);
               }

               blockpos$mutableblockpos.move(this.directionOfSearch);
               if (worldgenlevel.isOutsideBuildHeight(blockpos$mutableblockpos.getY())) {
                  return Stream.of();
               }

               if (this.allowedSearchCondition.test(worldgenlevel, blockpos$mutableblockpos)) {
                  ++i;
                  continue;
               }
            }

            if (this.targetCondition.test(worldgenlevel, blockpos$mutableblockpos)) {
               return Stream.of(blockpos$mutableblockpos);
            }

            return Stream.of();
         }
      }
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.ENVIRONMENT_SCAN;
   }
}