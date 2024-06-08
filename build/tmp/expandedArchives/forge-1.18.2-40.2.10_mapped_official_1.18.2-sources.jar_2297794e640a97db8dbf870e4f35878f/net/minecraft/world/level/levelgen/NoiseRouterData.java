package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
   private static final float ORE_THICKNESS = 0.08F;
   private static final double VEININESS_FREQUENCY = 1.5D;
   private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5D;
   private static final double SURFACE_DENSITY_THRESHOLD = 1.5625D;
   private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0D);
   private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
   private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
   private static final ResourceKey<DensityFunction> Y = createKey("y");
   private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
   private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE = createKey("overworld/base_3d_noise");
   private static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
   private static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
   private static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
   private static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
   private static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
   private static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
   private static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
   private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
   private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = createKey("end/sloped_cheese");
   private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
   private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
   private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
   private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

   protected static NoiseRouterWithOnlyNoises overworld(NoiseSettings p_212278_, boolean p_212279_) {
      return overworldWithNewCaves(p_212278_, p_212279_);
   }

   private static ResourceKey<DensityFunction> createKey(String p_209537_) {
      return ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation(p_209537_));
   }

   public static Holder<? extends DensityFunction> bootstrap() {
      register(ZERO, DensityFunctions.zero());
      int i = DimensionType.MIN_Y * 2;
      int j = DimensionType.MAX_Y * 2;
      register(Y, DensityFunctions.yClampedGradient(i, j, (double)i, (double)j));
      DensityFunction densityfunction = register(SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(getNoise(Noises.SHIFT)))));
      DensityFunction densityfunction1 = register(SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(getNoise(Noises.SHIFT)))));
      register(BASE_3D_NOISE, BlendedNoise.UNSEEDED);
      DensityFunction densityfunction2 = register(CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS))));
      DensityFunction densityfunction3 = register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION))));
      DensityFunction densityfunction4 = register(RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.RIDGE))));
      DensityFunction densityfunction5 = DensityFunctions.noise(getNoise(Noises.JAGGED), 1500.0D, 0.0D);
      DensityFunction densityfunction6 = splineWithBlending(densityfunction2, densityfunction3, densityfunction4, DensityFunctions.TerrainShaperSpline.SplineType.OFFSET, -0.81D, 2.5D, DensityFunctions.blendOffset());
      DensityFunction densityfunction7 = register(FACTOR, splineWithBlending(densityfunction2, densityfunction3, densityfunction4, DensityFunctions.TerrainShaperSpline.SplineType.FACTOR, 0.0D, 8.0D, BLENDING_FACTOR));
      DensityFunction densityfunction8 = register(DEPTH, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D), densityfunction6));
      register(SLOPED_CHEESE, slopedCheese(densityfunction2, densityfunction3, densityfunction4, densityfunction7, densityfunction8, densityfunction5));
      DensityFunction densityfunction9 = register(CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS_LARGE))));
      DensityFunction densityfunction10 = register(EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION_LARGE))));
      DensityFunction densityfunction11 = splineWithBlending(densityfunction9, densityfunction10, densityfunction4, DensityFunctions.TerrainShaperSpline.SplineType.OFFSET, -0.81D, 2.5D, DensityFunctions.blendOffset());
      DensityFunction densityfunction12 = register(FACTOR_LARGE, splineWithBlending(densityfunction9, densityfunction10, densityfunction4, DensityFunctions.TerrainShaperSpline.SplineType.FACTOR, 0.0D, 8.0D, BLENDING_FACTOR));
      DensityFunction densityfunction13 = register(DEPTH_LARGE, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D), densityfunction11));
      register(SLOPED_CHEESE_LARGE, slopedCheese(densityfunction9, densityfunction10, densityfunction4, densityfunction12, densityfunction13, densityfunction5));
      register(SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), getFunction(BASE_3D_NOISE)));
      register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction());
      register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_THICKNESS), 2.0D, 1.0D, -0.6D, -1.3D)));
      register(SPAGHETTI_2D, spaghetti2D());
      register(ENTRANCES, entrances());
      register(NOODLE, noodle());
      register(PILLARS, pillars());
      return BuiltinRegistries.DENSITY_FUNCTION.holders().iterator().next();
   }

   private static DensityFunction register(ResourceKey<DensityFunction> p_209545_, DensityFunction p_209546_) {
      return new DensityFunctions.HolderHolder(BuiltinRegistries.register(BuiltinRegistries.DENSITY_FUNCTION, p_209545_, p_209546_));
   }

   private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> p_209543_) {
      return BuiltinRegistries.NOISE.getHolderOrThrow(p_209543_);
   }

   private static DensityFunction getFunction(ResourceKey<DensityFunction> p_209553_) {
      return new DensityFunctions.HolderHolder(BuiltinRegistries.DENSITY_FUNCTION.getHolderOrThrow(p_209553_));
   }

   private static DensityFunction slopedCheese(DensityFunction p_209482_, DensityFunction p_209483_, DensityFunction p_209484_, DensityFunction p_209485_, DensityFunction p_209486_, DensityFunction p_209487_) {
      DensityFunction densityfunction = splineWithBlending(p_209482_, p_209483_, p_209484_, DensityFunctions.TerrainShaperSpline.SplineType.JAGGEDNESS, 0.0D, 1.28D, BLENDING_JAGGEDNESS);
      DensityFunction densityfunction1 = DensityFunctions.mul(densityfunction, p_209487_.halfNegative());
      DensityFunction densityfunction2 = noiseGradientDensity(p_209485_, DensityFunctions.add(p_209486_, densityfunction1));
      return DensityFunctions.add(densityfunction2, getFunction(BASE_3D_NOISE));
   }

   private static DensityFunction spaghettiRoughnessFunction() {
      DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_ROUGHNESS));
      DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0D, -0.1D);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction1, DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.4D))));
   }

   private static DensityFunction entrances() {
      DensityFunction densityfunction = DensityFunctions.cacheOnce(DensityFunctions.noise(getNoise(Noises.SPAGHETTI_3D_RARITY), 2.0D, 1.0D));
      DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_3D_THICKNESS), -0.065D, -0.088D);
      DensityFunction densityfunction2 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction densityfunction3 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction densityfunction4 = DensityFunctions.add(DensityFunctions.max(densityfunction2, densityfunction3), densityfunction1).clamp(-1.0D, 1.0D);
      DensityFunction densityfunction5 = getFunction(SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction densityfunction6 = DensityFunctions.noise(getNoise(Noises.CAVE_ENTRANCE), 0.75D, 0.5D);
      DensityFunction densityfunction7 = DensityFunctions.add(DensityFunctions.add(densityfunction6, DensityFunctions.constant(0.37D)), DensityFunctions.yClampedGradient(-10, 30, 0.3D, 0.0D));
      return DensityFunctions.cacheOnce(DensityFunctions.min(densityfunction7, DensityFunctions.add(densityfunction5, densityfunction4)));
   }

   private static DensityFunction noodle() {
      DensityFunction densityfunction = getFunction(Y);
      int i = -64;
      int j = -60;
      int k = 320;
      DensityFunction densityfunction1 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE), 1.0D, 1.0D), -60, 320, -1);
      DensityFunction densityfunction2 = yLimitedInterpolatable(densityfunction, DensityFunctions.mappedNoise(getNoise(Noises.NOODLE_THICKNESS), 1.0D, 1.0D, -0.05D, -0.1D), -60, 320, 0);
      double d0 = 2.6666666666666665D;
      DensityFunction densityfunction3 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 2.6666666666666665D, 2.6666666666666665D), -60, 320, 0);
      DensityFunction densityfunction4 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 2.6666666666666665D, 2.6666666666666665D), -60, 320, 0);
      DensityFunction densityfunction5 = DensityFunctions.mul(DensityFunctions.constant(1.5D), DensityFunctions.max(densityfunction3.abs(), densityfunction4.abs()));
      return DensityFunctions.rangeChoice(densityfunction1, -1000000.0D, 0.0D, DensityFunctions.constant(64.0D), DensityFunctions.add(densityfunction2, densityfunction5));
   }

   private static DensityFunction pillars() {
      double d0 = 25.0D;
      double d1 = 0.3D;
      DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.PILLAR), 25.0D, 0.3D);
      DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_RARENESS), 0.0D, -2.0D);
      DensityFunction densityfunction2 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_THICKNESS), 0.0D, 1.1D);
      DensityFunction densityfunction3 = DensityFunctions.add(DensityFunctions.mul(densityfunction, DensityFunctions.constant(2.0D)), densityfunction1);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction3, densityfunction2.cube()));
   }

   private static DensityFunction spaghetti2D() {
      DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_2D_MODULATOR), 2.0D, 1.0D);
      DensityFunction densityfunction1 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
      DensityFunction densityfunction2 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_ELEVATION), 0.0D, (double)Math.floorDiv(-64, 8), 8.0D);
      DensityFunction densityfunction3 = getFunction(SPAGHETTI_2D_THICKNESS_MODULATOR);
      DensityFunction densityfunction4 = DensityFunctions.add(densityfunction2, DensityFunctions.yClampedGradient(-64, 320, 8.0D, -40.0D)).abs();
      DensityFunction densityfunction5 = DensityFunctions.add(densityfunction4, densityfunction3).cube();
      double d0 = 0.083D;
      DensityFunction densityfunction6 = DensityFunctions.add(densityfunction1, DensityFunctions.mul(DensityFunctions.constant(0.083D), densityfunction3));
      return DensityFunctions.max(densityfunction6, densityfunction5).clamp(-1.0D, 1.0D);
   }

   private static DensityFunction underground(DensityFunction p_209470_) {
      DensityFunction densityfunction = getFunction(SPAGHETTI_2D);
      DensityFunction densityfunction1 = getFunction(SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction densityfunction2 = DensityFunctions.noise(getNoise(Noises.CAVE_LAYER), 8.0D);
      DensityFunction densityfunction3 = DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction2.square());
      DensityFunction densityfunction4 = DensityFunctions.noise(getNoise(Noises.CAVE_CHEESE), 0.6666666666666666D);
      DensityFunction densityfunction5 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27D), densityfunction4).clamp(-1.0D, 1.0D), DensityFunctions.add(DensityFunctions.constant(1.5D), DensityFunctions.mul(DensityFunctions.constant(-0.64D), p_209470_)).clamp(0.0D, 0.5D));
      DensityFunction densityfunction6 = DensityFunctions.add(densityfunction3, densityfunction5);
      DensityFunction densityfunction7 = DensityFunctions.min(DensityFunctions.min(densityfunction6, getFunction(ENTRANCES)), DensityFunctions.add(densityfunction, densityfunction1));
      DensityFunction densityfunction8 = getFunction(PILLARS);
      DensityFunction densityfunction9 = DensityFunctions.rangeChoice(densityfunction8, -1000000.0D, 0.03D, DensityFunctions.constant(-1000000.0D), densityfunction8);
      return DensityFunctions.max(densityfunction7, densityfunction9);
   }

   private static DensityFunction postProcess(NoiseSettings p_212275_, DensityFunction p_212276_) {
      DensityFunction densityfunction = DensityFunctions.slide(p_212275_, p_212276_);
      DensityFunction densityfunction1 = DensityFunctions.blendDensity(densityfunction);
      return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction1), DensityFunctions.constant(0.64D)).squeeze();
   }

   private static NoiseRouterWithOnlyNoises overworldWithNewCaves(NoiseSettings p_212283_, boolean p_212284_) {
      DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.AQUIFER_BARRIER), 0.5D);
      DensityFunction densityfunction1 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
      DensityFunction densityfunction2 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
      DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.AQUIFER_LAVA));
      DensityFunction densityfunction4 = getFunction(SHIFT_X);
      DensityFunction densityfunction5 = getFunction(SHIFT_Z);
      DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(p_212284_ ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
      DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(p_212284_ ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
      DensityFunction densityfunction8 = getFunction(p_212284_ ? FACTOR_LARGE : FACTOR);
      DensityFunction densityfunction9 = getFunction(p_212284_ ? DEPTH_LARGE : DEPTH);
      DensityFunction densityfunction10 = noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
      DensityFunction densityfunction11 = getFunction(p_212284_ ? SLOPED_CHEESE_LARGE : SLOPED_CHEESE);
      DensityFunction densityfunction12 = DensityFunctions.min(densityfunction11, DensityFunctions.mul(DensityFunctions.constant(5.0D), getFunction(ENTRANCES)));
      DensityFunction densityfunction13 = DensityFunctions.rangeChoice(densityfunction11, -1000000.0D, 1.5625D, densityfunction12, underground(densityfunction11));
      DensityFunction densityfunction14 = DensityFunctions.min(postProcess(p_212283_, densityfunction13), getFunction(NOODLE));
      DensityFunction densityfunction15 = getFunction(Y);
      int i = p_212283_.minY();
      int j = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_212286_) -> {
         return p_212286_.minY;
      }).min().orElse(i);
      int k = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_212281_) -> {
         return p_212281_.maxY;
      }).max().orElse(i);
      DensityFunction densityfunction16 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEININESS), 1.5D, 1.5D), j, k, 0);
      float f = 4.0F;
      DensityFunction densityfunction17 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_A), 4.0D, 4.0D), j, k, 0).abs();
      DensityFunction densityfunction18 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_B), 4.0D, 4.0D), j, k, 0).abs();
      DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant((double)-0.08F), DensityFunctions.max(densityfunction17, densityfunction18));
      DensityFunction densityfunction20 = DensityFunctions.noise(getNoise(Noises.ORE_GAP));
      return new NoiseRouterWithOnlyNoises(densityfunction, densityfunction1, densityfunction2, densityfunction3, densityfunction6, densityfunction7, getFunction(p_212284_ ? CONTINENTS_LARGE : CONTINENTS), getFunction(p_212284_ ? EROSION_LARGE : EROSION), getFunction(p_212284_ ? DEPTH_LARGE : DEPTH), getFunction(RIDGES), densityfunction10, densityfunction14, densityfunction16, densityfunction19, densityfunction20);
   }

   private static NoiseRouterWithOnlyNoises noNewCaves(NoiseSettings p_212288_) {
      DensityFunction densityfunction = getFunction(SHIFT_X);
      DensityFunction densityfunction1 = getFunction(SHIFT_Z);
      DensityFunction densityfunction2 = DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.TEMPERATURE));
      DensityFunction densityfunction3 = DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.VEGETATION));
      DensityFunction densityfunction4 = noiseGradientDensity(DensityFunctions.cache2d(getFunction(FACTOR)), getFunction(DEPTH));
      DensityFunction densityfunction5 = postProcess(p_212288_, getFunction(SLOPED_CHEESE));
      return new NoiseRouterWithOnlyNoises(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction2, densityfunction3, getFunction(CONTINENTS), getFunction(EROSION), getFunction(DEPTH), getFunction(RIDGES), densityfunction4, densityfunction5, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   protected static NoiseRouterWithOnlyNoises overworldWithoutCaves(NoiseSettings p_209549_) {
      return noNewCaves(p_209549_);
   }

   protected static NoiseRouterWithOnlyNoises nether(NoiseSettings p_209556_) {
      return noNewCaves(p_209556_);
   }

   protected static NoiseRouterWithOnlyNoises end(NoiseSettings p_209559_) {
      DensityFunction densityfunction = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
      DensityFunction densityfunction1 = postProcess(p_209559_, getFunction(SLOPED_CHEESE_END));
      return new NoiseRouterWithOnlyNoises(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction, densityfunction1, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   private static NormalNoise seedNoise(PositionalRandomFactory p_209525_, Registry<NormalNoise.NoiseParameters> p_209526_, Holder<NormalNoise.NoiseParameters> p_209527_) {
      return Noises.instantiate(p_209525_, p_209527_.unwrapKey().flatMap(p_209526_::getHolder).orElse(p_209527_));
   }

   public static NoiseRouter createNoiseRouter(NoiseSettings p_209503_, long p_209504_, Registry<NormalNoise.NoiseParameters> p_209505_, WorldgenRandom.Algorithm p_209506_, NoiseRouterWithOnlyNoises p_209507_) {
      boolean flag = p_209506_ == WorldgenRandom.Algorithm.LEGACY;
      PositionalRandomFactory positionalrandomfactory = p_209506_.newInstance(p_209504_).forkPositional();
      Map<DensityFunction, DensityFunction> map = new HashMap<>();
      DensityFunction.Visitor densityfunction$visitor = (p_209535_) -> {
         if (p_209535_ instanceof DensityFunctions.Noise) {
            DensityFunctions.Noise densityfunctions$noise = (DensityFunctions.Noise)p_209535_;
            Holder<NormalNoise.NoiseParameters> holder2 = densityfunctions$noise.noiseData();
            return new DensityFunctions.Noise(holder2, seedNoise(positionalrandomfactory, p_209505_, holder2), densityfunctions$noise.xzScale(), densityfunctions$noise.yScale());
         } else if (p_209535_ instanceof DensityFunctions.ShiftNoise) {
            DensityFunctions.ShiftNoise densityfunctions$shiftnoise = (DensityFunctions.ShiftNoise)p_209535_;
            Holder<NormalNoise.NoiseParameters> holder3 = densityfunctions$shiftnoise.noiseData();
            NormalNoise normalnoise1;
            if (flag) {
               normalnoise1 = NormalNoise.create(positionalrandomfactory.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0D));
            } else {
               normalnoise1 = seedNoise(positionalrandomfactory, p_209505_, holder3);
            }

            return densityfunctions$shiftnoise.withNewNoise(normalnoise1);
         } else if (p_209535_ instanceof DensityFunctions.ShiftedNoise) {
            DensityFunctions.ShiftedNoise densityfunctions$shiftednoise = (DensityFunctions.ShiftedNoise)p_209535_;
            if (flag) {
               Holder<NormalNoise.NoiseParameters> holder = densityfunctions$shiftednoise.noiseData();
               if (Objects.equals(holder.unwrapKey(), Optional.of(Noises.TEMPERATURE))) {
                  NormalNoise normalnoise2 = NormalNoise.createLegacyNetherBiome(p_209506_.newInstance(p_209504_), new NormalNoise.NoiseParameters(-7, 1.0D, 1.0D));
                  return new DensityFunctions.ShiftedNoise(densityfunctions$shiftednoise.shiftX(), densityfunctions$shiftednoise.shiftY(), densityfunctions$shiftednoise.shiftZ(), densityfunctions$shiftednoise.xzScale(), densityfunctions$shiftednoise.yScale(), holder, normalnoise2);
               }

               if (Objects.equals(holder.unwrapKey(), Optional.of(Noises.VEGETATION))) {
                  NormalNoise normalnoise = NormalNoise.createLegacyNetherBiome(p_209506_.newInstance(p_209504_ + 1L), new NormalNoise.NoiseParameters(-7, 1.0D, 1.0D));
                  return new DensityFunctions.ShiftedNoise(densityfunctions$shiftednoise.shiftX(), densityfunctions$shiftednoise.shiftY(), densityfunctions$shiftednoise.shiftZ(), densityfunctions$shiftednoise.xzScale(), densityfunctions$shiftednoise.yScale(), holder, normalnoise);
               }
            }

            Holder<NormalNoise.NoiseParameters> holder1 = densityfunctions$shiftednoise.noiseData();
            return new DensityFunctions.ShiftedNoise(densityfunctions$shiftednoise.shiftX(), densityfunctions$shiftednoise.shiftY(), densityfunctions$shiftednoise.shiftZ(), densityfunctions$shiftednoise.xzScale(), densityfunctions$shiftednoise.yScale(), holder1, seedNoise(positionalrandomfactory, p_209505_, holder1));
         } else if (p_209535_ instanceof DensityFunctions.WeirdScaledSampler) {
            DensityFunctions.WeirdScaledSampler densityfunctions$weirdscaledsampler = (DensityFunctions.WeirdScaledSampler)p_209535_;
            return new DensityFunctions.WeirdScaledSampler(densityfunctions$weirdscaledsampler.input(), densityfunctions$weirdscaledsampler.noiseData(), seedNoise(positionalrandomfactory, p_209505_, densityfunctions$weirdscaledsampler.noiseData()), densityfunctions$weirdscaledsampler.rarityValueMapper());
         } else if (p_209535_ instanceof BlendedNoise) {
            return flag ? new BlendedNoise(p_209506_.newInstance(p_209504_), p_209503_.noiseSamplingSettings(), p_209503_.getCellWidth(), p_209503_.getCellHeight()) : new BlendedNoise(positionalrandomfactory.fromHashOf(new ResourceLocation("terrain")), p_209503_.noiseSamplingSettings(), p_209503_.getCellWidth(), p_209503_.getCellHeight());
         } else if (p_209535_ instanceof DensityFunctions.EndIslandDensityFunction) {
            return new DensityFunctions.EndIslandDensityFunction(p_209504_);
         } else if (p_209535_ instanceof DensityFunctions.TerrainShaperSpline) {
            DensityFunctions.TerrainShaperSpline densityfunctions$terrainshaperspline = (DensityFunctions.TerrainShaperSpline)p_209535_;
            TerrainShaper terrainshaper = p_209503_.terrainShaper();
            return new DensityFunctions.TerrainShaperSpline(densityfunctions$terrainshaperspline.continentalness(), densityfunctions$terrainshaperspline.erosion(), densityfunctions$terrainshaperspline.weirdness(), terrainshaper, densityfunctions$terrainshaperspline.spline(), densityfunctions$terrainshaperspline.minValue(), densityfunctions$terrainshaperspline.maxValue());
         } else if (p_209535_ instanceof DensityFunctions.Slide) {
            DensityFunctions.Slide densityfunctions$slide = (DensityFunctions.Slide)p_209535_;
            return new DensityFunctions.Slide(p_209503_, densityfunctions$slide.input());
         } else {
            return p_209535_;
         }
      };
      DensityFunction.Visitor densityfunction$visitor1 = (p_209541_) -> {
         return map.computeIfAbsent(p_209541_, densityfunction$visitor);
      };
      NoiseRouterWithOnlyNoises noiserouterwithonlynoises = p_209507_.mapAll(densityfunction$visitor1);
      PositionalRandomFactory positionalrandomfactory1 = positionalrandomfactory.fromHashOf(new ResourceLocation("aquifer")).forkPositional();
      PositionalRandomFactory positionalrandomfactory2 = positionalrandomfactory.fromHashOf(new ResourceLocation("ore")).forkPositional();
      return new NoiseRouter(noiserouterwithonlynoises.barrierNoise(), noiserouterwithonlynoises.fluidLevelFloodednessNoise(), noiserouterwithonlynoises.fluidLevelSpreadNoise(), noiserouterwithonlynoises.lavaNoise(), positionalrandomfactory1, positionalrandomfactory2, noiserouterwithonlynoises.temperature(), noiserouterwithonlynoises.vegetation(), noiserouterwithonlynoises.continents(), noiserouterwithonlynoises.erosion(), noiserouterwithonlynoises.depth(), noiserouterwithonlynoises.ridges(), noiserouterwithonlynoises.initialDensityWithoutJaggedness(), noiserouterwithonlynoises.finalDensity(), noiserouterwithonlynoises.veinToggle(), noiserouterwithonlynoises.veinRidged(), noiserouterwithonlynoises.veinGap(), (new OverworldBiomeBuilder()).spawnTarget());
   }

   private static DensityFunction splineWithBlending(DensityFunction p_209489_, DensityFunction p_209490_, DensityFunction p_209491_, DensityFunctions.TerrainShaperSpline.SplineType p_209492_, double p_209493_, double p_209494_, DensityFunction p_209495_) {
      DensityFunction densityfunction = DensityFunctions.terrainShaperSpline(p_209489_, p_209490_, p_209491_, p_209492_, p_209493_, p_209494_);
      DensityFunction densityfunction1 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), p_209495_, densityfunction);
      return DensityFunctions.flatCache(DensityFunctions.cache2d(densityfunction1));
   }

   private static DensityFunction noiseGradientDensity(DensityFunction p_212272_, DensityFunction p_212273_) {
      DensityFunction densityfunction = DensityFunctions.mul(p_212273_, p_212272_);
      return DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction.quarterNegative());
   }

   private static DensityFunction yLimitedInterpolatable(DensityFunction p_209472_, DensityFunction p_209473_, int p_209474_, int p_209475_, int p_209476_) {
      return DensityFunctions.interpolated(DensityFunctions.rangeChoice(p_209472_, (double)p_209474_, (double)(p_209475_ + 1), p_209473_, DensityFunctions.constant((double)p_209476_)));
   }

   protected static double applySlide(NoiseSettings p_209499_, double p_209500_, double p_209501_) {
      double d0 = (double)((int)p_209501_ / p_209499_.getCellHeight() - p_209499_.getMinCellY());
      p_209500_ = p_209499_.topSlideSettings().applySlide(p_209500_, (double)p_209499_.getCellCountY() - d0);
      return p_209499_.bottomSlideSettings().applySlide(p_209500_, d0);
   }

   protected static double computePreliminarySurfaceLevelScanning(NoiseSettings p_209509_, DensityFunction p_209510_, int p_209511_, int p_209512_) {
      for(int i = p_209509_.getMinCellY() + p_209509_.getCellCountY(); i >= p_209509_.getMinCellY(); --i) {
         int j = i * p_209509_.getCellHeight();
         double d0 = -0.703125D;
         double d1 = p_209510_.compute(new DensityFunction.SinglePointContext(p_209511_, j, p_209512_)) + -0.703125D;
         double d2 = Mth.clamp(d1, -64.0D, 64.0D);
         d2 = applySlide(p_209509_, d2, (double)j);
         if (d2 > 0.390625D) {
            return (double)j;
         }
      }

      return 2.147483647E9D;
   }

   protected static final class QuantizedSpaghettiRarity {
      protected static double getSphaghettiRarity2D(double p_209564_) {
         if (p_209564_ < -0.75D) {
            return 0.5D;
         } else if (p_209564_ < -0.5D) {
            return 0.75D;
         } else if (p_209564_ < 0.5D) {
            return 1.0D;
         } else {
            return p_209564_ < 0.75D ? 2.0D : 3.0D;
         }
      }

      protected static double getSpaghettiRarity3D(double p_209566_) {
         if (p_209566_ < -0.5D) {
            return 0.75D;
         } else if (p_209566_ < 0.0D) {
            return 1.0D;
         } else {
            return p_209566_ < 0.5D ? 1.5D : 2.0D;
         }
      }
   }
}