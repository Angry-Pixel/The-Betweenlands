package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.CavesBetweenlands;

public class CarverRegistry {
	public static final DeferredRegister<WorldCarver<?>> CARVER_TYPES = DeferredRegister.create(Registries.CARVER, TheBetweenlands.ID);

	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CaveCarverConfiguration>> BETWEENLANDS_CAVES = CARVER_TYPES.register("betweenlands_caves", () -> new CaveWorldCarver(CaveCarverConfiguration.CODEC));
	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CanyonCarverConfiguration>> BETWEENLANDS_RAVINES = CARVER_TYPES.register("betweenlands_ravines", () -> new CanyonWorldCarver(CanyonCarverConfiguration.CODEC));
	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CaveCarverConfiguration>> CAVES_BETWEENLANDS = CARVER_TYPES.register("caves_betweenlands", () -> new CavesBetweenlands(CaveCarverConfiguration.CODEC));


	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_BETWEENLANDS_CAVES = makeKey("betweenlands_caves");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_BETWEENLANDS_RAVINES = makeKey("betweenlands_ravines");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_CAVES_BETWEENLANDS = makeKey("caves_betweenlands");

	private static ResourceKey<ConfiguredWorldCarver<?>> makeKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_CARVER, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {

	}
}
