package net.minecraft.world.level.levelgen;

import java.util.List;
import net.minecraft.world.level.biome.Climate;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, PositionalRandomFactory aquiferPositionalRandomFactory, PositionalRandomFactory oreVeinsPositionalRandomFactory, DensityFunction temperature, DensityFunction humidity, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, List<Climate.ParameterPoint> spawnTarget) {
}