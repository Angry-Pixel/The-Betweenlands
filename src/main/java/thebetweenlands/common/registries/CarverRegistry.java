package thebetweenlands.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.carvers.*;

public class CarverRegistry {
	public static final DeferredRegister<WorldCarver<?>> CARVER_TYPES = DeferredRegister.create(Registries.CARVER, TheBetweenlands.ID);

	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CaveCarverConfiguration>> BETWEENLANDS_CAVES = CARVER_TYPES.register("betweenlands_caves", () -> new BetweenlandsCaveCarver(CaveCarverConfiguration.CODEC));
	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CanyonCarverConfiguration>> BETWEENLANDS_RAVINES = CARVER_TYPES.register("betweenlands_ravines", () -> new BetweenlandsRavineCarver(CanyonCarverConfiguration.CODEC));
	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CarverConfiguration>> CAVES_BETWEENLANDS = CARVER_TYPES.register("caves_betweenlands", () -> new CavesBetweenlands(CaveCarverConfiguration.CODEC));
	public static final DeferredHolder<WorldCarver<?>, WorldCarver<CarverConfiguration>> GIANT_ROOTS_BETWEENLANDS = CARVER_TYPES.register("giant_roots_betweenlands", () -> new GiantRoots(CaveCarverConfiguration.CODEC));


	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_BETWEENLANDS_CAVES = makeKey("betweenlands_caves");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_BETWEENLANDS_RAVINES = makeKey("betweenlands_ravines");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_CAVES_BETWEENLANDS = makeKey("caves_betweenlands");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_GIANT_ROOTS_BETWEENLANDS = makeKey("giant_roots_betweenlands");

	private static ResourceKey<ConfiguredWorldCarver<?>> makeKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_CARVER, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {

	}
}
