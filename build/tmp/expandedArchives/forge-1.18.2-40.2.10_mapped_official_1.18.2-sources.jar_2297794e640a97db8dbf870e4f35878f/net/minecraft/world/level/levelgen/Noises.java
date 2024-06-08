package net.minecraft.world.level.levelgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class Noises {
   public static final ResourceKey<NormalNoise.NoiseParameters> TEMPERATURE = createKey("temperature");
   public static final ResourceKey<NormalNoise.NoiseParameters> VEGETATION = createKey("vegetation");
   public static final ResourceKey<NormalNoise.NoiseParameters> CONTINENTALNESS = createKey("continentalness");
   public static final ResourceKey<NormalNoise.NoiseParameters> EROSION = createKey("erosion");
   public static final ResourceKey<NormalNoise.NoiseParameters> TEMPERATURE_LARGE = createKey("temperature_large");
   public static final ResourceKey<NormalNoise.NoiseParameters> VEGETATION_LARGE = createKey("vegetation_large");
   public static final ResourceKey<NormalNoise.NoiseParameters> CONTINENTALNESS_LARGE = createKey("continentalness_large");
   public static final ResourceKey<NormalNoise.NoiseParameters> EROSION_LARGE = createKey("erosion_large");
   public static final ResourceKey<NormalNoise.NoiseParameters> RIDGE = createKey("ridge");
   public static final ResourceKey<NormalNoise.NoiseParameters> SHIFT = createKey("offset");
   public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_BARRIER = createKey("aquifer_barrier");
   public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = createKey("aquifer_fluid_level_floodedness");
   public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_LAVA = createKey("aquifer_lava");
   public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = createKey("aquifer_fluid_level_spread");
   public static final ResourceKey<NormalNoise.NoiseParameters> PILLAR = createKey("pillar");
   public static final ResourceKey<NormalNoise.NoiseParameters> PILLAR_RARENESS = createKey("pillar_rareness");
   public static final ResourceKey<NormalNoise.NoiseParameters> PILLAR_THICKNESS = createKey("pillar_thickness");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_2D = createKey("spaghetti_2d");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_2D_ELEVATION = createKey("spaghetti_2d_elevation");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_2D_MODULATOR = createKey("spaghetti_2d_modulator");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_2D_THICKNESS = createKey("spaghetti_2d_thickness");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_3D_1 = createKey("spaghetti_3d_1");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_3D_2 = createKey("spaghetti_3d_2");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_3D_RARITY = createKey("spaghetti_3d_rarity");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_3D_THICKNESS = createKey("spaghetti_3d_thickness");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_ROUGHNESS = createKey("spaghetti_roughness");
   public static final ResourceKey<NormalNoise.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = createKey("spaghetti_roughness_modulator");
   public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_ENTRANCE = createKey("cave_entrance");
   public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_LAYER = createKey("cave_layer");
   public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_CHEESE = createKey("cave_cheese");
   public static final ResourceKey<NormalNoise.NoiseParameters> ORE_VEININESS = createKey("ore_veininess");
   public static final ResourceKey<NormalNoise.NoiseParameters> ORE_VEIN_A = createKey("ore_vein_a");
   public static final ResourceKey<NormalNoise.NoiseParameters> ORE_VEIN_B = createKey("ore_vein_b");
   public static final ResourceKey<NormalNoise.NoiseParameters> ORE_GAP = createKey("ore_gap");
   public static final ResourceKey<NormalNoise.NoiseParameters> NOODLE = createKey("noodle");
   public static final ResourceKey<NormalNoise.NoiseParameters> NOODLE_THICKNESS = createKey("noodle_thickness");
   public static final ResourceKey<NormalNoise.NoiseParameters> NOODLE_RIDGE_A = createKey("noodle_ridge_a");
   public static final ResourceKey<NormalNoise.NoiseParameters> NOODLE_RIDGE_B = createKey("noodle_ridge_b");
   public static final ResourceKey<NormalNoise.NoiseParameters> JAGGED = createKey("jagged");
   public static final ResourceKey<NormalNoise.NoiseParameters> SURFACE = createKey("surface");
   public static final ResourceKey<NormalNoise.NoiseParameters> SURFACE_SECONDARY = createKey("surface_secondary");
   public static final ResourceKey<NormalNoise.NoiseParameters> CLAY_BANDS_OFFSET = createKey("clay_bands_offset");
   public static final ResourceKey<NormalNoise.NoiseParameters> BADLANDS_PILLAR = createKey("badlands_pillar");
   public static final ResourceKey<NormalNoise.NoiseParameters> BADLANDS_PILLAR_ROOF = createKey("badlands_pillar_roof");
   public static final ResourceKey<NormalNoise.NoiseParameters> BADLANDS_SURFACE = createKey("badlands_surface");
   public static final ResourceKey<NormalNoise.NoiseParameters> ICEBERG_PILLAR = createKey("iceberg_pillar");
   public static final ResourceKey<NormalNoise.NoiseParameters> ICEBERG_PILLAR_ROOF = createKey("iceberg_pillar_roof");
   public static final ResourceKey<NormalNoise.NoiseParameters> ICEBERG_SURFACE = createKey("iceberg_surface");
   public static final ResourceKey<NormalNoise.NoiseParameters> SWAMP = createKey("surface_swamp");
   public static final ResourceKey<NormalNoise.NoiseParameters> CALCITE = createKey("calcite");
   public static final ResourceKey<NormalNoise.NoiseParameters> GRAVEL = createKey("gravel");
   public static final ResourceKey<NormalNoise.NoiseParameters> POWDER_SNOW = createKey("powder_snow");
   public static final ResourceKey<NormalNoise.NoiseParameters> PACKED_ICE = createKey("packed_ice");
   public static final ResourceKey<NormalNoise.NoiseParameters> ICE = createKey("ice");
   public static final ResourceKey<NormalNoise.NoiseParameters> SOUL_SAND_LAYER = createKey("soul_sand_layer");
   public static final ResourceKey<NormalNoise.NoiseParameters> GRAVEL_LAYER = createKey("gravel_layer");
   public static final ResourceKey<NormalNoise.NoiseParameters> PATCH = createKey("patch");
   public static final ResourceKey<NormalNoise.NoiseParameters> NETHERRACK = createKey("netherrack");
   public static final ResourceKey<NormalNoise.NoiseParameters> NETHER_WART = createKey("nether_wart");
   public static final ResourceKey<NormalNoise.NoiseParameters> NETHER_STATE_SELECTOR = createKey("nether_state_selector");

   private static ResourceKey<NormalNoise.NoiseParameters> createKey(String p_189310_) {
      return ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(p_189310_));
   }

   public static NormalNoise instantiate(Registry<NormalNoise.NoiseParameters> p_189306_, PositionalRandomFactory p_189307_, ResourceKey<NormalNoise.NoiseParameters> p_189308_) {
      Holder<NormalNoise.NoiseParameters> holder = p_189306_.getHolderOrThrow(p_189308_);
      return instantiate(p_189307_, holder);
   }

   public static NormalNoise instantiate(PositionalRandomFactory p_209648_, Holder<NormalNoise.NoiseParameters> p_209649_) {
      return NormalNoise.create(p_209648_.fromHashOf(p_209649_.unwrapKey().orElseThrow().location()), p_209649_.value());
   }
}