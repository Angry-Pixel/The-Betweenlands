package thebetweenlands.common.registries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.generators.FlatLandGenerator;
import thebetweenlands.common.world.gen.generators.config.FlatLandGeneratorConfiguration;

public class EarlyGeneratorRegistry {

	public static final DeferredRegister<EarlyGenerator<?>> GENERATORS = DeferredRegister.create(BLRegistries.EARLY_GENERATORS, TheBetweenlands.ID);

	public static final DeferredHolder<EarlyGenerator<?>, FlatLandGenerator> FLAT_LAND = GENERATORS.register("flat_land", () -> new FlatLandGenerator(FlatLandGeneratorConfiguration.CODEC));
	
	public static final class Configured {
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SWAMPLANDS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_swamplands"));
		public static final ResourceKey<ConfiguredEarlyGenerator<?, ?>> FLAT_LAND_SLUDGE_PLAINS = ResourceKey.create(BLRegistries.Keys.CONFIGURED_GENERATORS, TheBetweenlands.prefix("flat_land_sludge_plains"));
	}
	
	public static void bootstrapConfigured(BootstrapContext<ConfiguredEarlyGenerator<?, ?>> context) {
		context.register(Configured.FLAT_LAND_SWAMPLANDS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(120, 8)));
		context.register(Configured.FLAT_LAND_SLUDGE_PLAINS, new ConfiguredEarlyGenerator<>(FLAT_LAND.get(), new FlatLandGeneratorConfiguration(120, 5)));
	}
}
