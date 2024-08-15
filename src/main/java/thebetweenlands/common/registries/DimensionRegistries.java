package thebetweenlands.common.registries;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.BetweenlandsBlockTagsProvider;
import thebetweenlands.common.world.BetweenlandsSurfaceRuleData;

import java.util.List;
import java.util.OptionalLong;

public class DimensionRegistries {

	public static final ResourceLocation DIMENSION_RENDERER = TheBetweenlands.prefix("renderer");

	public static final ResourceKey<Level> DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, TheBetweenlands.prefix("the_betweenlands"));
	public static final ResourceKey<DimensionType> DIMENSION_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, TheBetweenlands.prefix("the_betweenlands"));
	public static final ResourceKey<NoiseGeneratorSettings> NOISE_SETTINGS_KEY = ResourceKey.create(Registries.NOISE_SETTINGS, TheBetweenlands.prefix("the_betweenlands"));
	public static final ResourceKey<LevelStem> LEVEL_STEM_KEY = ResourceKey.create(Registries.LEVEL_STEM, TheBetweenlands.prefix("the_betweenlands"));

	public static void bootstrapType(BootstrapContext<DimensionType> context) {
		context.register(DIMENSION_TYPE_KEY, new DimensionType(
			OptionalLong.empty(),
			true,
			false,
			false,
			true,
			1.0D,
			false,
			false,
			0,
			256,
			256,
			BetweenlandsBlockTagsProvider.BETWEENSTONE_ORE_REPLACEABLE,
			DIMENSION_RENDERER,
			0.0F,
			new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 7)
		));
	}

	public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
		context.register(NOISE_SETTINGS_KEY, new NoiseGeneratorSettings(
			NoiseSettings.create(0, 256, 2, 2),
			BlockRegistry.BETWEENSTONE.get().defaultBlockState(),
			BlockRegistry.SWAMP_WATER.get().defaultBlockState(),
			new NoiseRouter(
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero(), // TODO add a density function
				DensityFunctions.zero(),
				DensityFunctions.zero(),
				DensityFunctions.zero()
			),
			BetweenlandsSurfaceRuleData.betweenlands(),
			List.of(),
			120,
			false,
			false,
			false,
			false
		));
	}

	public static void bootstrapStem(BootstrapContext<LevelStem> context) {
		HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);
		HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
		context.register(LEVEL_STEM_KEY, new LevelStem(dimTypes.getOrThrow(DIMENSION_TYPE_KEY), new NoiseBasedChunkGenerator(new FixedBiomeSource(biome.getOrThrow(BiomeRegistry.SWAMPLANDS)), noiseGenSettings.getOrThrow(NOISE_SETTINGS_KEY))));
	}
}