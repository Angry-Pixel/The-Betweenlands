package net.minecraft.data.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseData {
   public static Holder<NormalNoise.NoiseParameters> bootstrap() {
      registerBiomeNoises(0, Noises.TEMPERATURE, Noises.VEGETATION, Noises.CONTINENTALNESS, Noises.EROSION);
      registerBiomeNoises(-2, Noises.TEMPERATURE_LARGE, Noises.VEGETATION_LARGE, Noises.CONTINENTALNESS_LARGE, Noises.EROSION_LARGE);
      register(Noises.RIDGE, -7, 1.0D, 2.0D, 1.0D, 0.0D, 0.0D, 0.0D);
      register(Noises.SHIFT, -3, 1.0D, 1.0D, 1.0D, 0.0D);
      register(Noises.AQUIFER_BARRIER, -3, 1.0D);
      register(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0D);
      register(Noises.AQUIFER_LAVA, -1, 1.0D);
      register(Noises.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0D);
      register(Noises.PILLAR, -7, 1.0D, 1.0D);
      register(Noises.PILLAR_RARENESS, -8, 1.0D);
      register(Noises.PILLAR_THICKNESS, -8, 1.0D);
      register(Noises.SPAGHETTI_2D, -7, 1.0D);
      register(Noises.SPAGHETTI_2D_ELEVATION, -8, 1.0D);
      register(Noises.SPAGHETTI_2D_MODULATOR, -11, 1.0D);
      register(Noises.SPAGHETTI_2D_THICKNESS, -11, 1.0D);
      register(Noises.SPAGHETTI_3D_1, -7, 1.0D);
      register(Noises.SPAGHETTI_3D_2, -7, 1.0D);
      register(Noises.SPAGHETTI_3D_RARITY, -11, 1.0D);
      register(Noises.SPAGHETTI_3D_THICKNESS, -8, 1.0D);
      register(Noises.SPAGHETTI_ROUGHNESS, -5, 1.0D);
      register(Noises.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0D);
      register(Noises.CAVE_ENTRANCE, -7, 0.4D, 0.5D, 1.0D);
      register(Noises.CAVE_LAYER, -8, 1.0D);
      register(Noises.CAVE_CHEESE, -8, 0.5D, 1.0D, 2.0D, 1.0D, 2.0D, 1.0D, 0.0D, 2.0D, 0.0D);
      register(Noises.ORE_VEININESS, -8, 1.0D);
      register(Noises.ORE_VEIN_A, -7, 1.0D);
      register(Noises.ORE_VEIN_B, -7, 1.0D);
      register(Noises.ORE_GAP, -5, 1.0D);
      register(Noises.NOODLE, -8, 1.0D);
      register(Noises.NOODLE_THICKNESS, -8, 1.0D);
      register(Noises.NOODLE_RIDGE_A, -7, 1.0D);
      register(Noises.NOODLE_RIDGE_B, -7, 1.0D);
      register(Noises.JAGGED, -16, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(Noises.SURFACE_SECONDARY, -6, 1.0D, 1.0D, 0.0D, 1.0D);
      register(Noises.CLAY_BANDS_OFFSET, -8, 1.0D);
      register(Noises.BADLANDS_PILLAR, -2, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.BADLANDS_PILLAR_ROOF, -8, 1.0D);
      register(Noises.BADLANDS_SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(Noises.ICEBERG_PILLAR, -6, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.ICEBERG_PILLAR_ROOF, -3, 1.0D);
      register(Noises.ICEBERG_SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(Noises.SWAMP, -2, 1.0D);
      register(Noises.CALCITE, -9, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.GRAVEL, -8, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.POWDER_SNOW, -6, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.PACKED_ICE, -7, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.ICE, -4, 1.0D, 1.0D, 1.0D, 1.0D);
      register(Noises.SOUL_SAND_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(Noises.GRAVEL_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(Noises.PATCH, -5, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(Noises.NETHERRACK, -3, 1.0D, 0.0D, 0.0D, 0.35D);
      register(Noises.NETHER_WART, -3, 1.0D, 0.0D, 0.0D, 0.9D);
      register(Noises.NETHER_STATE_SELECTOR, -4, 1.0D);
      return BuiltinRegistries.NOISE.holders().iterator().next();
   }

   private static void registerBiomeNoises(int p_194745_, ResourceKey<NormalNoise.NoiseParameters> p_194746_, ResourceKey<NormalNoise.NoiseParameters> p_194747_, ResourceKey<NormalNoise.NoiseParameters> p_194748_, ResourceKey<NormalNoise.NoiseParameters> p_194749_) {
      register(p_194746_, -10 + p_194745_, 1.5D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D);
      register(p_194747_, -8 + p_194745_, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      register(p_194748_, -9 + p_194745_, 1.0D, 1.0D, 2.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_194749_, -9 + p_194745_, 1.0D, 1.0D, 0.0D, 1.0D, 1.0D);
   }

   private static void register(ResourceKey<NormalNoise.NoiseParameters> p_194751_, int p_194752_, double p_194753_, double... p_194754_) {
      BuiltinRegistries.register(BuiltinRegistries.NOISE, p_194751_, new NormalNoise.NoiseParameters(p_194752_, p_194753_, p_194754_));
   }
}