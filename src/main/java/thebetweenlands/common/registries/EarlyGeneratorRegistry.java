package thebetweenlands.common.registries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.generators.FlatLandGenerator;
import thebetweenlands.common.world.gen.generators.MarshIslandsGenerator;
import thebetweenlands.common.world.gen.generators.SimplexTerrainGenerator;
import thebetweenlands.common.world.gen.generators.config.FlatLandGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.MarshIslandsGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.config.SimplexTerrainGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.ConstantHeightSelector;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.HeightmapBasedHeightSelector;

public class EarlyGeneratorRegistry {

	public static final DeferredRegister<EarlyGenerator<?>> GENERATORS = DeferredRegister.create(BLRegistries.EARLY_GENERATORS, TheBetweenlands.ID);

	public static final DeferredHolder<EarlyGenerator<?>, FlatLandGenerator> FLAT_LAND = GENERATORS.register("flat_land", () -> new FlatLandGenerator(FlatLandGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, MarshIslandsGenerator> MARSH_ISLANDS = GENERATORS.register("marsh_islands", () -> new MarshIslandsGenerator(MarshIslandsGeneratorConfiguration.CODEC));

	public static final DeferredHolder<EarlyGenerator<?>, SimplexTerrainGenerator> SIMPLEX_TERRAIN = GENERATORS.register("simplex_terrain", () -> new SimplexTerrainGenerator(SimplexTerrainGeneratorConfiguration.CODEC));
	
	public static final class Configured {
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SWAMPLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_swamplands"));
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SLUDGE_PLAINS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_sludge_plains"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> MARSH_ISLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("marsh_islands"));
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> ERODED_MARSH_ISLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("eroded_marsh_islands"));

		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> DEEP_WATERS_SIMPLEX_TERRAIN = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("deep_waters_simplex_terrain"));
	}
	
	public static void bootstrapConfigured(BootstrapContext<ConfiguredEarlyGenerator<?, ?>> context) {
		context.register(Configured.FLAT_LAND_SWAMPLANDS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(120, 8)));
		context.register(Configured.FLAT_LAND_SLUDGE_PLAINS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(120, 5)));

		context.register(Configured.MARSH_ISLANDS, new ConfiguredEarlyGenerator<>(MARSH_ISLANDS.get(), new MarshIslandsGeneratorConfiguration(120, 0.16D, 10.5D)));
		context.register(Configured.ERODED_MARSH_ISLANDS, new ConfiguredEarlyGenerator<>(MARSH_ISLANDS.get(), new MarshIslandsGeneratorConfiguration(120, 0.5D, 100.5D)));

		context.register(Configured.DEEP_WATERS_SIMPLEX_TERRAIN, new ConfiguredEarlyGenerator<>(SIMPLEX_TERRAIN.get(), new SimplexTerrainGeneratorConfiguration(new HeightmapBasedHeightSelector(Types.OCEAN_FLOOR_WG), new ConstantHeightSelector(120), 0.05D, true, false)));
	}
}
