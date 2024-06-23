package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.BetweenlandsBlockTagsProvider;

import java.util.OptionalLong;

public class DimensionRegistries {

	public static final ResourceKey<Level> DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, TheBetweenlands.prefix("the_betweenlands"));
	public static final ResourceKey<DimensionType> DIMENSION_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, TheBetweenlands.prefix("the_betweenlands"));
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
			TheBetweenlands.prefix("the_betweenlands"),
			0.0F,
			new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 7)
		));
	}

	public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
	}

	public static void bootstrapStem(BootstrapContext<LevelStem> context) {
	}
}