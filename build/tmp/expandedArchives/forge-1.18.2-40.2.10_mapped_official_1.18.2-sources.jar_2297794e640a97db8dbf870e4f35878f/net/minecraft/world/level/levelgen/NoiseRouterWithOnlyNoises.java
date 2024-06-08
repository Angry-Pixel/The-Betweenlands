package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public record NoiseRouterWithOnlyNoises(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
   public static final Codec<NoiseRouterWithOnlyNoises> CODEC = RecordCodecBuilder.create((p_209602_) -> {
      return p_209602_.group(field("barrier", NoiseRouterWithOnlyNoises::barrierNoise), field("fluid_level_floodedness", NoiseRouterWithOnlyNoises::fluidLevelFloodednessNoise), field("fluid_level_spread", NoiseRouterWithOnlyNoises::fluidLevelSpreadNoise), field("lava", NoiseRouterWithOnlyNoises::lavaNoise), field("temperature", NoiseRouterWithOnlyNoises::temperature), field("vegetation", NoiseRouterWithOnlyNoises::vegetation), field("continents", NoiseRouterWithOnlyNoises::continents), field("erosion", NoiseRouterWithOnlyNoises::erosion), field("depth", NoiseRouterWithOnlyNoises::depth), field("ridges", NoiseRouterWithOnlyNoises::ridges), field("initial_density_without_jaggedness", NoiseRouterWithOnlyNoises::initialDensityWithoutJaggedness), field("final_density", NoiseRouterWithOnlyNoises::finalDensity), field("vein_toggle", NoiseRouterWithOnlyNoises::veinToggle), field("vein_ridged", NoiseRouterWithOnlyNoises::veinRidged), field("vein_gap", NoiseRouterWithOnlyNoises::veinGap)).apply(p_209602_, NoiseRouterWithOnlyNoises::new);
   });

   private static RecordCodecBuilder<NoiseRouterWithOnlyNoises, DensityFunction> field(String p_209606_, Function<NoiseRouterWithOnlyNoises, DensityFunction> p_209607_) {
      return DensityFunction.HOLDER_HELPER_CODEC.fieldOf(p_209606_).forGetter(p_209607_);
   }

   public NoiseRouterWithOnlyNoises mapAll(DensityFunction.Visitor p_209604_) {
      return new NoiseRouterWithOnlyNoises(this.barrierNoise.mapAll(p_209604_), this.fluidLevelFloodednessNoise.mapAll(p_209604_), this.fluidLevelSpreadNoise.mapAll(p_209604_), this.lavaNoise.mapAll(p_209604_), this.temperature.mapAll(p_209604_), this.vegetation.mapAll(p_209604_), this.continents.mapAll(p_209604_), this.erosion.mapAll(p_209604_), this.depth.mapAll(p_209604_), this.ridges.mapAll(p_209604_), this.initialDensityWithoutJaggedness.mapAll(p_209604_), this.finalDensity.mapAll(p_209604_), this.veinToggle.mapAll(p_209604_), this.veinRidged.mapAll(p_209604_), this.veinGap.mapAll(p_209604_));
   }
}