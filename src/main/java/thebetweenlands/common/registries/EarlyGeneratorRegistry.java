package thebetweenlands.common.registries;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.MossyCragrockBottomBlock;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.world.gen.generators.BetweenlandsCavesGenerator;
import thebetweenlands.common.world.gen.generators.CoarseIslandsGenerator;
import thebetweenlands.common.world.gen.generators.FlatLandGenerator;
import thebetweenlands.common.world.gen.generators.MarshIslandsGenerator;
import thebetweenlands.common.world.gen.generators.SimplexTerrainGenerator;
import thebetweenlands.common.world.gen.generators.config.BetweenlandsCavesGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.CoarseIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.FlatLandGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.MarshIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.SimplexTerrainGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.ConstantHeightSelector;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.HeightmapBasedHeightSelector;
import thebetweenlands.common.world.gen.util.config.BiSimplexNoiseConfiguration;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;

public class EarlyGeneratorRegistry {

	public static final DeferredRegister<EarlyGenerator<?>> GENERATORS = DeferredRegister.create(BLRegistries.EARLY_GENERATORS, TheBetweenlands.ID);

	public static final DeferredHolder<EarlyGenerator<?>, FlatLandGenerator> FLAT_LAND = GENERATORS.register("flat_land", () -> new FlatLandGenerator(FlatLandGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, MarshIslandsGenerator> MARSH_ISLANDS = GENERATORS.register("marsh_islands", () -> new MarshIslandsGenerator(MarshIslandsGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, SimplexTerrainGenerator> SIMPLEX_TERRAIN = GENERATORS.register("simplex_terrain", () -> new SimplexTerrainGenerator(SimplexTerrainGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, CoarseIslandsGenerator> COARSE_ISLANDS = GENERATORS.register("coarse_islands", () -> new CoarseIslandsGenerator(CoarseIslandsGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, BetweenlandsCavesGenerator> BETWEENLANDS_CAVES = GENERATORS.register("bl_caves", () -> new BetweenlandsCavesGenerator(BetweenlandsCavesGeneratorConfiguration.CODEC));
	
	public static final class Configured {
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SWAMPLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_swamplands"));
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SLUDGE_PLAINS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_sludge_plains"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> MARSH_ISLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("marsh_islands"));
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> ERODED_MARSH_ISLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("eroded_marsh_islands"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> DEEP_WATERS_SIMPLEX_TERRAIN = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("deep_waters_simplex_terrain"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> COARSE_ISLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("coarse_islands"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> BETWEENLANDS_CAVES = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("bl_caves"));
	}
	
	public static void bootstrapConfigured(BootstrapContext<ConfiguredEarlyGenerator<?, ?>> context) {
		context.register(Configured.FLAT_LAND_SWAMPLANDS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(TheBetweenlands.LAYER_HEIGHT, 8)));
		context.register(Configured.FLAT_LAND_SLUDGE_PLAINS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(TheBetweenlands.LAYER_HEIGHT, 5)));

		context.register(Configured.MARSH_ISLANDS, new ConfiguredEarlyGenerator<>(MARSH_ISLANDS.get(), new MarshIslandsGeneratorConfiguration(TheBetweenlands.LAYER_HEIGHT, 0.16D, 10.5D)));
		context.register(Configured.ERODED_MARSH_ISLANDS, new ConfiguredEarlyGenerator<>(MARSH_ISLANDS.get(), new MarshIslandsGeneratorConfiguration(TheBetweenlands.LAYER_HEIGHT, 0.5D, 100.5D)));

		context.register(Configured.DEEP_WATERS_SIMPLEX_TERRAIN, new ConfiguredEarlyGenerator<>(SIMPLEX_TERRAIN.get(), new SimplexTerrainGeneratorConfiguration(new HeightmapBasedHeightSelector(Types.OCEAN_FLOOR_WG), new ConstantHeightSelector(TheBetweenlands.LAYER_HEIGHT), 0.05D, true, false)));

		context.register(Configured.COARSE_ISLANDS, new ConfiguredEarlyGenerator<>(COARSE_ISLANDS.get(),
				new CoarseIslandsGeneratorConfiguration(
						BiSimplexNoiseConfiguration.of(
								4, 0.08D * 0.6D, 1.0D / 0.9D, 2.1D,
								5, 0.1D * 4.5D, 1.0D / 2.1D, 2.0D
							),
						new ConstantHeightSelector(TheBetweenlands.LAYER_HEIGHT), new HeightmapBasedHeightSelector(Types.OCEAN_FLOOR_WG),
						1.0D, 1,
						true, false,
						BlockRegistry.CRAGROCK.get().defaultBlockState(),
						List.of(
							BlockRegistry.MOSSY_CRAGROCK_TOP.get().defaultBlockState(),
							BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get().defaultBlockState().setValue(MossyCragrockBottomBlock.IS_BOTTOM, true)
						)
					)));
		
		HolderGetter<Block> blockRegistry = context.lookup(Registries.BLOCK);
		
		context.register(Configured.BETWEENLANDS_CAVES, new ConfiguredEarlyGenerator<>(BETWEENLANDS_CAVES.get(),
				new BetweenlandsCavesGeneratorConfiguration(
						blockRegistry.getOrThrow(BLBlockTagProvider.THEBETWEENLANDS_CARVER_REPLACABLES),
						// Cave noise
						new FractalOpenSimplexNoiseSettings3D(1, true, 0.08, 0.15, 0.08, 1.0, 0.0),
						// Surface Opening noise
						new FractalOpenSimplexNoiseSettings2D(1, true, 0.05, 0.05, 0.85, 0),
						// Form noise
						new FractalOpenSimplexNoiseSettings3D(4, false, 0.5 * 0.1, 0.3 * 0.1, 0.5 * 0.1, 0.4 * 2.0, 0.0),
						// Min cave height + taper distance
						new ConstantHeightSelector(0), 10,
						// Max cave height + taper distance
						new HeightmapBasedHeightSelector(Types.OCEAN_FLOOR_WG), 20,
						// Default noise limit
						-0.3,
						// Cave water height
						15,
						// Buffer blocks
						blockRegistry.getOrThrow(BLBlockTagProvider.BL_CAVE_BUFFER_REPLACABLE), 0.25,
						// Biomes without surface openings
						HolderSet.direct(
								context.lookup(Registries.BIOME)::getOrThrow,
								BiomeRegistry.DEEP_WATERS,
								BiomeRegistry.COARSE_ISLANDS,
								BiomeRegistry.RAISED_ISLES,
								BiomeRegistry.MARSH,
								BiomeRegistry.ERODED_MARSH,
								BiomeRegistry.PATCHY_ISLANDS,
								BiomeRegistry.SLUDGE_PLAINS,
								BiomeRegistry.SWAMPLANDS_CLEARING,
								BiomeRegistry.SLUDGE_PLAINS_CLEARING
							),
						// How much to increase the noise limit near the surface of biomes without surface openings
						3.5 * 0.85
					)));
	}
}
